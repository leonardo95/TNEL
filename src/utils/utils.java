package utils;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class utils {

	public static int getFirstorSecondHighestValue(int[] proposals, boolean first)
	{
		int highest = Integer.MIN_VALUE;
		int highest2nd = Integer.MIN_VALUE;
		for(int i = 0; i < proposals.length; i++) { 
			if (proposals[i] >= highest) 
			{ 
				highest2nd = highest;
				highest = proposals[i];
			} else if (proposals[i] >= highest2nd)
				highest2nd = proposals[i];
		}
		if(first)
			return highest;
		else
			return highest2nd;
	}

	public static void main(String[] args) throws StaleProxyException {
		
		/*int b[] = {0,10,2,4,7};
		System.out.println(getFirstorSecondHighestValue(b, true));
		System.out.println(getFirstorSecondHighestValue(b, false));*/

		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();

		//AMS and DF Agents 
		ContainerController mainContainer = rt.createMainContainer(p);

		//System.out.println("Registered Bidders:");
		int totalBidders = 3;
		
		for (int i = 0; i < totalBidders; i++){
			Object[] bidderArgs = new Object[1];
			bidderArgs[0] = "bidder " + i;

			mainContainer.createNewAgent("Bidder " + i, "ContractNetResponderAgent", bidderArgs).start();
		}
	
		//System.out.println("Registered Auctions:");
		Object[] auctioneerArgs = new Object[totalBidders];
		
		for (int i = 0; i < totalBidders; i++){
			auctioneerArgs[i] = "Bidder " + i;
		}
		
		mainContainer.createNewAgent("Auctioneer 0", "ContractNetInitiatorAgent", auctioneerArgs).start();
		
		AgentController gui = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
		gui.start();
	}
}


