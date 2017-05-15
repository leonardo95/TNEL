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

public class BuyerAgent extends Agent{

	private static final long serialVersionUID = 1L;
	/**
	 * Registers the agent with the Directory Facilitator as a Carrier, 
	 * and prepares the agent for an incoming message.
	 */
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

	private class CustomContractNetResponder extends SSResponderDispatcher {

		private CustomContractNetResponder(Agent agent, MessageTemplate template) {
			super(agent, template);
		}

		protected Behaviour createResponder(ACLMessage message) {
			return new SSIteratedContractNetResponder(myAgent, message) {

				/**
				 * Responds to the CFP message from the initiator with either a PROPOSE/REFUSE message.
				 * If the payment is too low for the agent, it declines with a REFUSE message, 
				 * otherwise, the agent will respond with a PROPOSE message.
				 */
				protected ACLMessage handleCfp(ACLMessage cfp) {
					System.out.println("Bidder Side: " + getAID().getName() + " received a cfp!");
					boolean reservedpriceflag = true;
					try {
						reservedpriceflag = Boolean.parseBoolean((cfp.getContent().substring(cfp.getContent().lastIndexOf("|") + 1)));

					} catch (Exception e) {
						System.out.println("Bidder Side: " + getAID().getName() + " couldn't read the price.");
					}
					//System.out.println(reservedpriceflag);

					ACLMessage response = cfp.createReply();


					Random rand = new Random();
					int  n = rand.nextInt(50) + 1;

					if ((n & 1) == 0) { 
						response.setPerformative(ACLMessage.PROPOSE);

						Random rand2 = new Random();
						double randomValue = 4 + (10 - 4) * rand2.nextDouble();
						String bid = String.format( "%.2f", randomValue);
						System.out.println("Bidder Side: " + getAgent().getLocalName() + " is going to bid: " + bid);

						response.setContent(String.valueOf(bid)); 
					} 
					else { 

						System.out.println("Bidder Side: " + getAgent().getLocalName() + " is not going to bid!");
						response.setPerformative(ACLMessage.REFUSE);
					}

					return response;
				}

				/**
				 * The agent received an ACCEPT_PROPOSAL message, so it won the auction.
				 */
				protected ACLMessage handleAcceptProposal(ACLMessage msg, ACLMessage propose, ACLMessage accept) {
					if (msg != null) {
						String productName = null;
						double cost = 0;
						try {
							productName = accept.getContent().substring(0, accept.getContent().indexOf("|"));
							cost = Double.parseDouble(accept.getContent().substring(accept.getContent().lastIndexOf("|") + 1));
						} catch (Exception e) {}

						System.out.println("Bidder Side: " + getAID().getName() + " won the auction: \"" + productName + "\" from "
								+ accept.getSender().getName() + ", and will pay $" + cost + " to acquire it.");
						ACLMessage inform = accept.createReply();
						inform.setPerformative(ACLMessage.INFORM);
						return inform;
					} else {
						ACLMessage failure = accept.createReply();
						failure.setPerformative(ACLMessage.FAILURE);
						return failure;
					}
				}

				protected void handleRejectProposal(ACLMessage msg, ACLMessage propose, ACLMessage reject) {
					System.out.println("Bidder Side: " + getAID().getName() + " can't win auction because the value offered is lower than the reserved price!");
				}
			};
		}
	}

}
