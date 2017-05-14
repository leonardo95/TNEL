package main;

import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.utils;

public class main {
	
	public static void main(String[] args) throws StaleProxyException {
		
		/* Generate double values with 2 decimals places. The user must provide the min and max range that a bidder can bid.
		Random r = new Random();
		double randomValue = 5 + (15 - 5) * r.nextDouble();
		System.out.println(String.format( "%.2f", randomValue));
		*/
		
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();

		ContainerController mainContainer = rt.createMainContainer(p);

		int totalBidders = 10;
			
		Object[] auctioneerArgs = new Object[totalBidders];
		
		for (int i = 0; i < totalBidders; i++){
			auctioneerArgs[i] = "Bidder" + i;
		}
		
		mainContainer.createNewAgent("S1", "agents.SellerAgent", auctioneerArgs).start();
		

		for (int i = 0; i < totalBidders; i++){
			Object[] bidderArgs = new Object[1];
			bidderArgs[0] = "Bidder" + i;

			mainContainer.createNewAgent("Bidder" + i, "agents.BuyerAgent", bidderArgs).start();
		}
		
		AgentController gui = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
		gui.start();
	}
}
