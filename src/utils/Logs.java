package utils;

import java.util.ArrayList;

import jade.core.AID;

//Logs used throughout the auction
public class Logs {

	public void enterNumberOfBids(){
		System.out.print("Enter number of bidders: ");
	}
	
	public void enterProductName(){
		System.out.print("Enter Product name: ");
	}
	
	public void enterReservePrice(){
		System.out.print("Enter Product Reserve Price: ");
	}
	
	public void enterMinPrice(){
		System.out.print("Enter Minimum Bid Price: ");
	}
	
	public void enterMaxPrice(){
		System.out.print("Enter Maximum Bid Price: ");
	}
	
	public void autionHeader(){
		System.out.println(new String("GSP Auction Mechanism in Sales").toUpperCase());
		System.out.println();
	}
	
	public void noBiddersInAuction(){
		System.out.println("No bidders in the auction");
	}
	
	public void listBidders(ArrayList<String> agents){
		System.out.print("Auction founded with the following agents: ");
		for (int i = 0; i < agents.size(); i++) {
			System.out.print(agents.get(i).toString() + " ");
		}
	}
	
	public void startingRound(int rounds){
		System.out.println();
		System.out.println();
		System.out.println("Starting Round: " + rounds);
		System.out.println();
	}
	
	public void sendingCFP(){
		System.out.println("AUCTIONEER: Sending CFP");
		System.out.println();
	}
	
	public void handlePropose(String name, String content, String productName){
		System.out.println("AUTIONEER: " + name  + " proposes " + content + "$ for the product: \"" + productName + "\".");
	}
	
	public void handleRefuse(String name){
		System.out.println();
		System.out.println("AUCTIONEER: " + name + " refused to bid on the product");
	}
	
	public void handleFaliure(String name){
		System.out.println("AUTIONEER: " + name  + " failed to reply.");
	}
	
	public void handleInform(String name){
		System.out.println("\n" + name + " has sold the product!");
	}
	
	public void handlingBids(String name, int num){
		System.out.println("\n" + "AUTIONEER: " +  name + " is handling " + num + " bids. \n");
	}
	
	public void highestBid(String name, double highestProposal){
		System.out.println("AUTIONEER: the bidder with the highest bid is "+ name + " with value " + highestProposal + "\n");
	}
	
	public void proposalListing(String name, Double double1){
		System.out.println("Name: " + name + " with value - " + double1); 
	}
	
	public void noBidsWereMade(){
		System.out.println("AUCTIONEER: No bids for the product were made");
	}
	
	public void winner(int type, double highest, String name, double highest2nd){
		if(type==0){
			System.out.println("The highest bid is: " + highest + " from " + name + ". Since he is the only that bidded, he is going to pay " + highest);
		}
		else{
			System.out.println("The highest bid is: " + highest + " from " + name + ". The winner is going to pay the second biggest bid: " + highest2nd);
		}
	}
	
	public void reservePriceNotMet(){
		System.out.println("The Auctionneer decided not to sell the product because the product reserved price was not achieved!");
	}
	
	public void proceedToNextRound(String name, String remainingbidders, double price){
		System.out.println("The agents: " + remainingbidders + " are going to proceed to the next round. \n");
		System.out.println(name + " is issuing CFP's with a reserved price of $" + price + ".\n");
	}
	
	public void roundUpdate(int round){
		System.out.println("Round: " + round);
		System.out.println();
	}
	
	public void receiveCFP(String name){
		System.out.println("BIDDER: " + name+ " received CFP");
	}
	
	public void unableToReadProductPrice(String name){
		System.out.println("BIDDER: " + name + " unable to read product price.");
	}
	
	public void printBid(String name, String bid){
		System.out.println("BIDDER: " + name + " will bid " + bid);
	}
	
	public void refuseToBid(String name){
		System.out.println("BIDDER: " + name + " decided not to bid on the product");
	}
	
	public void receiveAcceptance(String name, String product, String sender, double cost){
		System.out.println("BIDDER: " + name + " won the auction: \"" + product + "\" from "+ sender + ", and will pay $" + cost + " to acquire it.");
	}
	
	public void handleRejection(String name){
		System.out.println("Bidder Side: " + name + " can't win auction because the value offered is lower than the reserved price!");
	}
}
