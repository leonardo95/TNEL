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
import utils.utils;

public class main {
	
	public static void main(String[] args) throws StaleProxyException {
		
		/* Generate double values with 2 decimals places. The user must provide the min and max range that a bidder can bid.
		Random r = new Random();
		double randomValue = 5 + (15 - 5) * r.nextDouble();
		System.out.println(String.format( "%.2f", randomValue));
		*/
		String num = new String();
		String name = new String();
		String price = new String();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    System.out.print("Enter number of bidders: ");
	    try {
			num = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    System.out.print("Enter Product name: ");
	    try {
			name = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    System.out.print("Enter Product Reserve Price: ");
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
		
		for (int i = 0; i < totalBidders; i++){
			auctioneerArgs[i+2] = "Bidder" + i;
		}
		
		mainContainer.createNewAgent("Auction", "agents.SellerAgent", auctioneerArgs).start();
		

		for (int i = 0; i < totalBidders; i++){
			Object[] bidderArgs = new Object[1];
			bidderArgs[0] = "Bidder" + i;

			mainContainer.createNewAgent("Bidder" + i, "agents.BuyerAgent", bidderArgs).start();
		}
		
		utils.gui = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
		utils.gui.start();
	}
}
