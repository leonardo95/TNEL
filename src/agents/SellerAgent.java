package agents;

import java.util.Date;
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
	
	ServiceAgent serviceAgent = new ServiceAgent();
	String productName = new String();
	double productReservePrice = 2.56;
	
	public void setup() {
		System.out.println("SELLER AGENT " + getLocalName() + " INITIATED");
		System.out.println("SELLING PRODUCT WITH RESERVE PRICE " + productReservePrice);
				
		addBehaviour(new ContractNetInitiator(this, null) {
			
			public Vector<ACLMessage> prepareCFPs(ACLMessage cfp) {
				
				cfp = new ACLMessage(ACLMessage.CFP);
				System.out.println("CFP SENT");
				
				Vector<ACLMessage> responses = new Vector<ACLMessage>();

				AID[] agents = serviceAgent.getAllAgents(getAgent(), null, null);
				
				for(int i=0; i<agents.length; ++i){
					System.out.println(agents[i].getName());
					cfp.addReceiver(new AID(agents[i].getLocalName(), AID.ISLOCALNAME));
				}

				cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
				cfp.setContent(productName);

				responses.addElement(cfp);

				return responses;
			}
			
			
		});
	}
}
