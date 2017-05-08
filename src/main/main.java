package main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.utils;

public class main {
	
	public static void main(String[] args) throws StaleProxyException {
		
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();

		ContainerController mainContainer = rt.createMainContainer(p);

		int totalBidders = 3;
			
		Object[] auctioneerArgs = new Object[totalBidders];
		
		for (int i = 0; i < totalBidders; i++){
			auctioneerArgs[i] = "B" + i;
		}
		
		mainContainer.createNewAgent("S1", "agents.SellerAgent", auctioneerArgs).start();
		

		for (int i = 0; i < totalBidders; i++){
			Object[] bidderArgs = new Object[1];
			bidderArgs[0] = "B" + i;

			mainContainer.createNewAgent("B" + i, "agents.BuyerAgent", bidderArgs).start();
		}
		
		AgentController gui = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
		gui.start();
	}
}
