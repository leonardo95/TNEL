package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.Logs;
import utils.Utils;

public class Main {
	
	static Logs log = new Logs();
	
	public static void main(String[] args) throws StaleProxyException {
		
		String num = new String();
		String name = new String();
		String price = new String();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    log.enterNumberOfBids();
	    
	    //Reading the number of bidders that will participate in the auction
	    try {
			num = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    log.enterProductName();
	    
	    //Reading the name of the product in auction
	    try {
			name = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    log.enterReservePrice();
	    
	    
	    //Reading the reserve price of the product for auction
	    try {
			price = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();

		ContainerController mainContainer = rt.createMainContainer(p);

		int totalBidders = Integer.parseInt(num);
			
		Object[] auctioneerArgs = new Object[totalBidders+2];
		
		auctioneerArgs[0]=name;
		auctioneerArgs[1]=price;
		
		//Initiating the auction Bidders
		for (int i = 0; i < totalBidders; i++){
			auctioneerArgs[i+2] = "Bidder" + i;
		}
		
		//Launching the auctioneer
		mainContainer.createNewAgent("Auction", "agents.SellerAgent", auctioneerArgs).start();
		
		//Launching the bidders
		for (int i = 0; i < totalBidders; i++){
			Object[] bidderArgs = new Object[1];
			bidderArgs[0] = "Bidder" + i;

			mainContainer.createNewAgent("Bidder" + i, "agents.BuyerAgent", bidderArgs).start();
		}
		
		Utils.gui = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
		Utils.gui.start();
	}
}
