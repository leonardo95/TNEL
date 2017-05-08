package agents;

import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class BuyerAgent extends Agent{
	
	public void setup() {
		System.out.println("BUYER AGENT " + getLocalName() + " INITIATED");
		
		MessageTemplate messageTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET), MessageTemplate.MatchPerformative(ACLMessage.CFP));
		addBehaviour(new ContractNetResponder(this, messageTemplate){
			
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("CFP " + getLocalName() + "->" + cfp.getSender().getName() + " - CONTENT: "+cfp.getContent());
				ACLMessage ret = evaluateAction(cfp);
				if (ret != null) {
					return ret;
				}
				else {
					System.out.println("AGENT " + getLocalName() + "- WILL NOT PROVIDE A PROPOSAL");
					throw new RefuseException("evaluation-failed");
				}
			}
			
			/* THE EVALUATION IS A SIMULATION. NUMBERS HIGHER THEN 5 WILL BID. LOWER OR EQUAL WILL NOT.*/
			public ACLMessage evaluateAction(ACLMessage cfp){
				int result = (int) (Math.random() * 10);
				if(result > 5){
					System.out.println("AGENT " + getLocalName() + "- PROPOSING " + result);
					
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(String.valueOf(result));
					return propose;
				}
				return null;
			}
		});
	}

}
