package agents;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ServiceAgent extends Agent {
	
	private ArrayList<Agent> registeredAgents = new ArrayList<Agent>();
		
	public AID[] getAllAgents(Agent agent, DFAgentDescription description, SearchConstraints constraints){
		
		try {
			DFAgentDescription[] ret = DFService.search(agent, description, constraints);
			
			AID[] agents = new AID[ret.length];
			for (int i = 0; i < ret.length; ++i) {
				agents[i] = ret[i].getName();
			}
			return agents;
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void register(Agent agent, ServiceDescription description) {
		
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		agentDescription.addServices(description);

		try {
			registeredAgents.add(agent);
			DFService.register(agent, agentDescription);
			
			System.out.println("NEW REGESTRY: " + agent.getName() + " - " + description.getType());
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}
