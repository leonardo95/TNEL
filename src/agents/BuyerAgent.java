package agents;

import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

public class BuyerAgent extends Agent{
	ServiceAgent serviceAgent = new ServiceAgent();
	
	public void setup() {
		System.out.println("BUYER AGENT " + getLocalName() + " INITIATED");
				
		
	}

}
