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
}


