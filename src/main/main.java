package main;

import utils.utils;

public class main {
	public static void main(String[] args) {
		int b[] = {0,10,2,4,7};
		System.out.println(utils.getFirstorSecondHighestValue(b, true));
		System.out.println(utils.getFirstorSecondHighestValue(b, false));
	}
}
