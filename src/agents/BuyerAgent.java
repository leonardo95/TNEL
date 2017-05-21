package agents;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSIteratedContractNetResponder;
import jade.proto.SSResponderDispatcher;
import utils.Logs;

public class BuyerAgent extends Agent{

	private static final long serialVersionUID = 1L;
	Logs log = new Logs();
	
	//FIPA Iterated Contract Net Protocol Setup
	protected void setup() {
		final String IP = FIPANames.InteractionProtocol.FIPA_ITERATED_CONTRACT_NET;
		MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(IP),
				MessageTemplate.MatchPerformative(ACLMessage.CFP));

		SequentialBehaviour sequential = new SequentialBehaviour();
		addBehaviour(sequential);
		ParallelBehaviour parallel = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		sequential.addSubBehaviour(parallel);
		parallel.addSubBehaviour(new CustomContractNetResponder(this, template));
	}
	
	//FIPA Iterated Contract Net Protocol Behaviour
	private class CustomContractNetResponder extends SSResponderDispatcher {

		private CustomContractNetResponder(Agent agent, MessageTemplate template) {
			super(agent, template);
		}

		protected Behaviour createResponder(ACLMessage message) {
			return new SSIteratedContractNetResponder(myAgent, message) {

				protected ACLMessage handleCfp(ACLMessage cfp) {
					log.receiveCFP(getAID().getLocalName());
					
					boolean reservedpriceflag = true;
					
					//Get reserve price flag truthfull value
					try {
						reservedpriceflag = Boolean.parseBoolean((cfp.getContent().substring(cfp.getContent().lastIndexOf("|") + 1)));

					} catch (Exception e) {
						log.unableToReadProductPrice(getAID().getLocalName());
					}					
					
					//Reply to CFP
					ACLMessage response = cfp.createReply();
					
					doWait(2000);
					Random rand = new Random();
					int  n = rand.nextInt(50) + 1;

					if ((n & 1) == 0) { 
						//Propose
						response.setPerformative(ACLMessage.PROPOSE);
						
						//Generating bid
						Random rand2 = new Random();
						double randomValue;
						if(reservedpriceflag){
							randomValue = 6 + (15 - 6) * rand2.nextDouble();
						}
						else{
							randomValue = 4 + (10 - 4) * rand2.nextDouble();
						}
						String bid = String.format( "%.2f", randomValue);
						
						log.printBid(getAgent().getLocalName(), bid);

						response.setContent(String.valueOf(bid)); 
					} 
					else { 
						//Refuse
						log.refuseToBid(getAgent().getLocalName());
						response.setPerformative(ACLMessage.REFUSE);
					}

					return response;
				}

				//HandleProposal
				protected ACLMessage handleAcceptProposal(ACLMessage msg, ACLMessage propose, ACLMessage accept) {
					if (msg != null) {
						String productName = null;
						double cost = 0;
						try {
							productName = accept.getContent().substring(0, accept.getContent().indexOf("|"));
							cost = Double.parseDouble(accept.getContent().substring(accept.getContent().lastIndexOf("|") + 1));
						} catch (Exception e) {}
						
						log.receiveAcceptance(getAID().getLocalName(), productName, productName, cost);
						ACLMessage inform = accept.createReply();
						inform.setPerformative(ACLMessage.INFORM);
						return inform;
					} else {
						ACLMessage failure = accept.createReply();
						failure.setPerformative(ACLMessage.FAILURE);
						return failure;
					}
				}
				
				//HandleRejection
				protected void handleRejectProposal(ACLMessage msg, ACLMessage propose, ACLMessage reject) {
					log.handleRejection(getAID().getLocalName());
				}
			};
		}
	}

}
