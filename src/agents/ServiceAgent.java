package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ServiceAgent extends Agent {
		
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
}
