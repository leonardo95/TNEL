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

		//String num = new String();
		String name = new String();
		String price = new String();
		String min = new String();
		String max = new String();
		int softBidders = Integer.MIN_VALUE;
		int hardBidders =  Integer.MIN_VALUE;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
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

		log.enterMinPrice();

		//Reading the minimum bid price of the product in auction
		try {
			min = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.enterMaxPrice();

		//Reading the maximum bid price of the product in auction
		try {
			max = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.enterSoftBidders();
		
		//Reading the number of soft bidders in auction
		try {
			softBidders = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.enterHardBidders();
		
		//Reading the number of hard bidders in auction
		try {
			hardBidders = Integer.parseInt(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();

		ContainerController mainContainer = rt.createMainContainer(p);

		int totalBidders = softBidders + hardBidders;

		Object[] auctioneerArgs = new Object[totalBidders+2];

		auctioneerArgs[0]=name;
		auctioneerArgs[1]=price;

		//Initiating the auction Bidders
		for (int i = 0; i < totalBidders; i++){
			if(i < softBidders){
				auctioneerArgs[i+2] = "Soft_Bidder" + i;
			}
			else{
				auctioneerArgs[i+2] = "Hard_Bidder" + i;
			}
		}

		//Launching the auctioneer
		mainContainer.createNewAgent("Auction", "agents.SellerAgent", auctioneerArgs).start();

		//Launching the bidders
		for (int i = 0; i < totalBidders; i++){
			Object[] bidderArgs = new Object[3];

			if(i < softBidders){
				bidderArgs[0] = "Soft_Bidder" + i;
				bidderArgs[1] = min;
				bidderArgs[2] = max;
				mainContainer.createNewAgent("Soft_Bidder" + i, "agents.BuyerAgent", bidderArgs).start();
			}
			else{
				bidderArgs[0] = "Hard_Bidder" + i;
				bidderArgs[1] = min;
				bidderArgs[2] = max;
				mainContainer.createNewAgent("Hard_Bidder" + i, "agents.BuyerAgent", bidderArgs).start();
			}

		}

		Utils.gui = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
		Utils.gui.start();
	}
}
