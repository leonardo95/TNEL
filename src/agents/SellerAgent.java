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
import utils.Logs;

public class SellerAgent extends Agent {

	double productReservePrice;
	double highestProposal = 0.0;
	boolean reservePriceMet = false;
	private static final long serialVersionUID = 1L;
	String productName = new String();
	ACLMessage winnerAuction = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
	Logs log = new Logs();

	private ArrayList<String> agents = new ArrayList<String>();
	private HashMap<AID, Double> proposals = new LinkedHashMap<AID, Double>();

	//FIPA Iterated Contract Net Protocol Setup
	protected void setup() {
		log.autionHeader();

		Object[] args = getArguments();
		productName = (String) args[0];
		productReservePrice = Double.parseDouble((String) args[1]);

		if(args.length <= 2)
			log.noBiddersInAuction();
		else {
			for (int i = 2; i < args.length; i++)
				agents.add((String)args[i]);
		}

		//FIPA Iterated Contract Net Protocol Behaviour
		addBehaviour(new ContractNetInitiator(this, null) {
			private int globalResponses = 0;

			//FIPA Iterated Contract Net Protocol PrepareCFPs
			public Vector<ACLMessage> prepareCfps(ACLMessage init) {
				init = new ACLMessage(ACLMessage.CFP);
				Vector<ACLMessage> messages = new Vector<ACLMessage>();

				doWait(3000);

				log.listBidders(agents);

				//Initiating the BDI Bidder Agents
				for (int i = 0; i < agents.size(); i++) {
					AID agent = new AID((String) agents.get(i), AID.ISLOCALNAME);
					proposals.put(agent, (double) 0);
					init.addReceiver(agent);
				}

				doWait(2000);

				log.startingRound(utils.Utils.rounds);
				log.sendingCFP();

				//Sending CFP
				init.setProtocol(FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET);
				init.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
				init.setContent(productName + "|" + reservePriceMet);

				messages.addElement(init);
				return messages;
			}

			//FIPA Iterated Contract Net Protocol HandlePropose
			protected void handlePropose(ACLMessage propose, Vector v) {
				log.handlePropose(propose.getSender().getLocalName(), propose.getContent(), productName);
			}

			//FIPA Iterated Contract Net Protocol HandleRefuse
			protected void handleRefuse(ACLMessage refuse) {
				globalResponses++;
				log.handleRefuse(refuse.getSender().getLocalName());
			}

			//FIPA Iterated Contract Net Protocol HandleFaliure
			protected void handleFailure(ACLMessage failure) {
				globalResponses++;
				log.handleFaliure(failure.getSender().getLocalName());
			}

			//FIPA Iterated Contract Net Protocol HandleInform
			protected void handleInform(ACLMessage inform) {
				globalResponses++;
				log.handleInform(getAID().getLocalName() );

				//Auction GUI Termination
				try {
					utils.Utils.gui.kill();
				} catch (StaleProxyException e) {
					e.printStackTrace();
				}
			}

			//FIPA Iterated Contract Net Protocol HandleAllResponses
			protected void handleAllResponses(Vector responses, Vector acceptances) {

				int agentsLeft = responses.size() - globalResponses;
				globalResponses = 0;

				log.handlingBids(getAID().getLocalName(), agentsLeft);

				doWait(2000);

				//Proposals verification
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

				//CFP for the new round
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
							log.highestBid(msg.getSender().getLocalName(), highestProposal);
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

				//Proposal Listing
				Iterator<AID> keySetIterator2 = proposals.keySet().iterator(); 
				while(keySetIterator2.hasNext()){ 
					AID key = keySetIterator2.next(); 
					log.proposalListing(key.getLocalName(), proposals.get(key));
				}

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

					}

					if(noOne)
						log.noBidsWereMade();
					else
					{
						//Winner Accept Proposal
						System.out.println();
						if(highest > productReservePrice)
						{
							reply = winnerAuction.createReply();
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);

							if(highest2nd == 0)
							{
								log.winner(0, highest, winner.getLocalName(), highest2nd);
								reply.setContent(productName + "|" + (Double) highest);
							}
							else
							{
								log.winner(1, highest, winner.getLocalName(), highest2nd);
								reply.setContent(productName + "|" + (Double) highest2nd);
							}

							reply.setReplyByDate(new Date(System.currentTimeMillis() + 20000));
							acceptances.addElement(reply);
						}
						else
						{
							log.reservePriceNotMet();
						}
					}
				}
				else
				{
					//Issuing a new CFP
					for (int i = 0; i < responderList.size(); i++) {
						responderList.get(i).setContent(productName + "|" + reservePriceMet);
						cfpVector.set(i, responderList.get(i));
					}

					String remainingbidders = "";
					for (int i = 0; i < biddersvec.size(); i++) {
						remainingbidders += new String(biddersvec.get(i).getLocalName() + " ");

					}
					log.proceedToNextRound(getAID().getLocalName(), remainingbidders, productReservePrice);
					newIteration(cfpVector);
					utils.Utils.rounds++;

					if(utils.Utils.rounds != 0)
						log.roundUpdate(utils.Utils.rounds);

				}
			}
		});
	}	
}
