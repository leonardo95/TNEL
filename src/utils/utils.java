package utils;

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

	public static void main(String[] args) {
		int b[] = {0,10,2,4,7};
		System.out.println(getFirstorSecondHighestValue(b, true));
		System.out.println(getFirstorSecondHighestValue(b, false));
	}
}
