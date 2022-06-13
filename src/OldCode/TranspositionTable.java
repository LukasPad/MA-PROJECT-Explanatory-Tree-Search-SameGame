package OldCode;

import java.util.Random;


public class TranspositionTable 
{
	public static long[][] hashCodes;
	private Random random;
	//private int codeLength;
	
	public int codePart=0;
	
	public UCTNode[] table;

	
	private static boolean output=!true;
	
	public TranspositionTable(int xDim, int yDim, int colors, int codeLength)
	{
		hashCodes=new long[xDim*yDim][colors+1];
		random = new Random();
		for (int i=0;i<hashCodes.length;i++)
			for (int j=0;j<colors;j++)
			hashCodes[i][j]=random.nextLong();
		
		for (int i=0;i<codeLength;i++)
			codePart|=(1<<i);
		
		table=new UCTNode[1<<codeLength];
	}
	
	public long computeHash(byte[] position)
	{
		long hash=0;
		
		for (int i=0;i<position.length;i++)
		{
			hash^=hashCodes[i][position[i]+1];
		}
		
		return hash;
	}
	
	public UCTNode getTT(long hash)
	{
		//if (output) System.out.println("Search key: "+index+" Position: "+(index&codePart)+" Stored key: "+ tableKeys[index&codePart] +" Threshold: "+tableThresholds[index&codePart]+" Gvalue: "+gScore[index&codePart]);
		
		UCTNode n = table[(int)(hash&codePart)];
		
		while (n!=null)
		{
			if (n.hash==hash) return n;
			n=n.ttNext;
		}
		
		return null;
	}
	
	public void addToTable(UCTNode node)
	{
		int index = (int)(node.hash&codePart);
		
		node.ttNext=table[index];
		table[index]=node;
	}
	
	/*public void addToTable(int hashKey, int threshold, int g)
	{
		if (output) System.out.println("Adding key: "+hashKey+" Position: "+(hashKey&codePart)+" Threshold: "+threshold+" Gvalue: "+g);
		int index=hashKey&codePart;
		tableKeys[index]=hashKey;
		tableThresholds[index]=threshold;
		gScore[index]=g;
	}
	
	public int getThreshold(int index)
	{
		return tableThresholds[index&codePart];
	}
	
	public int getGScore(int index)
	{
		return gScore[index&codePart];
	}*/
	
}
