package agents;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.wrapper.StaleProxyException;

public class SellerAgent extends Agent {

	private static final long serialVersionUID = 1L;
	String productName = new String("iogurte");
	double productReservePrice = 7.56;
	double highestProposal = 0.0;
	ACLMessage winnerAuction = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
	boolean reservePriceMet = false;

	private ArrayList<String> agents = new ArrayList<String>();
	private HashMap<AID, Double> proposals = new LinkedHashMap<AID, Double>();

	protected void setup() {
		System.out.println(new String("GSP Auction Mechanism in Sales").toUpperCase());
		System.out.println();

		Object[] args = getArguments();
		if(args.length == 0)
			System.out.println("No bidders in the auction");
		else {
			for (int i = 0; i < args.length; i++)
				agents.add((String)args[i]);
		}

		addBehaviour(new ContractNetInitiator(this, null) {
			private int globalResponses = 0;

			public Vector<ACLMessage> prepareCfps(ACLMessage init) {
				init = new ACLMessage(ACLMessage.CFP);
				Vector<ACLMessage> messages = new Vector<ACLMessage>();

				doWait(3000);
				System.out.print("Auction founded with the following agents: ");
				for (int i = 0; i < agents.size(); i++) {
					System.out.print(agents.get(i).toString() + " ");
					AID agent = new AID((String) agents.get(i), AID.ISLOCALNAME);
					proposals.put(agent, (double) 0);
					init.addReceiver(agent);
				}

				doWait(2000);
				
				System.out.println();
				System.out.println();
				System.out.println("Starting Round: " + utils.utils.rounds);
				System.out.println();
				
				System.out.println("AUCTIONEER: Sending CFP");
				System.out.println();
				
				init.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
				init.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
				init.setContent(productName + "|" + reservePriceMet);

				messages.addElement(init);

				return messages;
			}

			protected void handlePropose(ACLMessage propose, Vector v) {
				System.out.println("AUTIONEER: " + propose.getSender().getLocalName()  + " proposes " + propose.getContent() + "$ for the product: \"" + productName + "\".");
			}

			protected void handleRefuse(ACLMessage refuse) {
				globalResponses++;
				System.out.println();
				System.out.println("AUCTIONEER: " + refuse.getSender().getLocalName() + " refused to bid on the product");
			}

			protected void handleFailure(ACLMessage failure) {
				globalResponses++;
				System.out.println("AUTIONEER: " + failure.getSender().getLocalName()  + " failed to reply.");
			}

			protected void handleInform(ACLMessage inform) {
				globalResponses++;
				System.out.println("\n" + getAID().getLocalName() + " has sold the product!");
				try {
					utils.utils.gui.kill();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
			}

			protected void handleAllResponses(Vector responses, Vector acceptances) {
		
				int agentsLeft = responses.size() - globalResponses;
				globalResponses = 0;

				System.out.println("\n" + "AUTIONEER: " +  getAID().getLocalName() + " is handling " + agentsLeft + " bids. \n");

				//System.out.println(responses.size());
				doWait(2000);
				Enumeration<?> t = responses.elements();
			
				while (t.hasMoreElements()) {
					ACLMessage msg = (ACLMessage) t.nextElement();

					if (msg.getPerformative() == ACLMessage.PROPOSE) {

						NumberFormat nf = NumberFormat.getInstance();
						double proposal = 0;
						try {
							proposal = nf.parse(msg.getContent()).doubleValue();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						if(proposal > highestProposal)
						{
							highestProposal = proposal;
							winnerAuction = msg;
						}
					}
				}
				
				ACLMessage reply = new ACLMessage(ACLMessage.CFP);
				Vector<ACLMessage> cfpVector = new Vector<ACLMessage>();
				Vector<AID> biddersvec = new Vector<AID>();
				Enumeration<?> e = responses.elements();
				ArrayList<ACLMessage> responderList = new ArrayList<ACLMessage>();
				while (e.hasMoreElements()) {
					ACLMessage msg = (ACLMessage) e.nextElement();

					if (msg.getPerformative() == ACLMessage.PROPOSE) {

						NumberFormat nf = NumberFormat.getInstance();
						double proposal = 0;
						try {
							proposal = nf.parse(msg.getContent()).doubleValue();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						proposals.replace(msg.getSender(), proposal);

						if(winnerAuction == msg){
							System.out.println("AUTIONEER: the bidder with the highest bid is "+ msg.getSender().getLocalName() + " with value " + highestProposal + "\n");
						}
						else
						{
							reply = msg.createReply();
							reply.setPerformative(ACLMessage.CFP);
						}

						if(proposal > productReservePrice)
							reservePriceMet = true;
						else{
							responderList.add(reply);
							cfpVector.addElement(reply);
							biddersvec.add(msg.getSender());
						}
					}
				}

				Iterator<AID> keySetIterator2 = proposals.keySet().iterator(); 
				while(keySetIterator2.hasNext()){ 
					AID key = keySetIterator2.next(); 
					
					System.out.println("key: " + key + " value: " + proposals.get(key)); 
				}
				//System.out.println(reply);

				System.out.println();
				if(responderList.size() == 0)
				{
					boolean noOne = true;

					Double highest = 0.0;
					Double highest2nd = 0.0;
					AID winner = new AID();

					Iterator<AID> keySetIterator = proposals.keySet().iterator(); 
					while(keySetIterator.hasNext()){ 
						AID key = keySetIterator.next(); 
						if (proposals.get(key) != 0)
							noOne = false;

						if (proposals.get(key) >= highest) 
						{ 
							highest2nd = highest;
							highest = proposals.get(key);
							winner = key;
						} 
						else if (proposals.get(key) >= highest2nd)
							highest2nd = proposals.get(key);

						//System.out.println("key: " + key + " value: " + proposals.get(key)); 
					}

					if(noOne)
						System.out.println("Auctionneer Side: No one wants to bid for the product!");
					else
					{
						System.out.println();
						if(highest > productReservePrice)
						{
							reply = winnerAuction.createReply();
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

							if(highest2nd == 0)
							{
								System.out.println("The biggest bid is: " + highest + " from " + winner + ". Since he is the only that bidded, he is going to pay " + highest);
								reply.setContent(productName + "|" + (Double) highest);
							}
							else
							{
								System.out.println("The biggest bid is: " + highest + " from " + winner + ". The winner is going to pay the second biggest bid: " + highest2nd);
								reply.setContent(productName + "|" + (Double) highest2nd);
							}

							reply.setReplyByDate(new Date(System.currentTimeMillis() + 20000));
							//System.out.println(reply);
							acceptances.addElement(reply);
						}
						else
						{
							System.out.println("The Auctionneer decided not to sell the product because the product reserved price was not achieved!");
						}

					}

				}
				else
				{
					for (int i = 0; i < responderList.size(); i++) {
						responderList.get(i).setContent(productName + "|" + reservePriceMet);
						cfpVector.set(i, responderList.get(i));
					}

					String remainingbidders = "";
					for (int i = 0; i < biddersvec.size(); i++) {
						remainingbidders += new String(biddersvec.get(i).getLocalName() + " ");

					}

					System.out.println("The agents: " + remainingbidders + " are going to proceed to the next round. \n");
					System.out.println(getAID().getLocalName() + " is issuing CFP's with a reserved price of $" + productReservePrice + ".\n");
					newIteration(cfpVector);
					utils.utils.rounds++;
					
					if(utils.utils.rounds != 0)
						System.out.println("Round: " + utils.utils.rounds);
					System.out.println();
					
				}
			}
		});
	}	
}
