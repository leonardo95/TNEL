package agents;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.ContractNetInitiator;

public class SellerAgent extends Agent {
	
	String productName = new String();
	double productReservePrice = 7.56;
	boolean reservePriceMet = false;
	
	public void setup() {
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			System.out.println("SELLER AGENT " + getLocalName() + " INITIATED");
			System.out.println("SELLING PRODUCT WITH RESERVE PRICE " + productReservePrice);
			
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			System.out.println("CFP SENT");
			
			Vector<ACLMessage> responses = new Vector<ACLMessage>();
			
			for (int i = 0; i < args.length; ++i) {
				cfp.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
	  		}
			
			cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			cfp.setContent(productName);
			
			addBehaviour(new ContractNetInitiator(this, cfp) {
				
				protected void handleAllResponses(Vector responses, Vector acceptances) {
					
					Enumeration e = responses.elements();
					
					while (e.hasMoreElements()) {
						
						ACLMessage msg = (ACLMessage) e.nextElement();
						if (msg.getPerformative() == ACLMessage.PROPOSE) {
							
							System.out.println("PROPOSAL RECEIVED WITH VALUE - " + msg.getContent());
							
							ACLMessage reply = msg.createReply();
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							
							if(Integer.parseInt(msg.getContent()) >= productReservePrice){
								reservePriceMet=true;
							}
							
							if(reservePriceMet){
								System.out.println("RESERVE PRICE HAS BEEN MET");
							}
							else{
								System.out.println("RESERVE PRICE STILL HAS NOT BEEN MET");
							}
							
							acceptances.addElement(reply);
						}
					}					
				}
			});
		}
	}
}
