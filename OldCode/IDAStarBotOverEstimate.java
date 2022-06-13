package OldCode;//import com.sun.xml.internal.ws.transport.local.LocalMessage;


public class IDAStarBotOverEstimate implements SameGameBot 
{
	
	private int xDim;
	private int yDim;
	
	private int scoreMode;
	
	private int colors;
	
	//private int evalFunction=1;
	
	private int[][] colorsLeftArray;
	private int[][] possibleMoves;
	private TranspositionTable transposition;
	
	private boolean useTT=!true;
	private int maximumTTdepth=5;
	
	private boolean stop=false;
	private boolean timeIsUp=false;
	
	private int bestMove=0;
	private int bestGValue=0;
	private int bestLocalGValue=0;
	
	private long leafNodes=0;
	private long totalDepthLeafNodes=0;
	private int deepestLeaf=0;
	
	private int oldBestMove=0;
	
	public boolean output=true;
	
	private boolean reUseThreshold=!true;
	private int oldThreshold=Integer.MAX_VALUE;
	private int[] scores;

	public int getMove(byte[] position, int xDimension, int yDimension, int mode, int time) 
	{	
		//if (evalFunction==2) return getMove2(position, xDimension, yDimension, mode, time);
		WatchTimeThread clock = new WatchTimeThread(time,this);
		clock.start();
		
		long started=System.currentTimeMillis();
		stop=false;
		timeIsUp=false;
		xDim=xDimension;
		yDim=yDimension;
		scoreMode=mode;
		
		for (int i=0;i<position.length;i++)
			if (position[i]>colors) colors=position[i];
				colors++;
				
		if ((useTT)&&(transposition==null)) transposition=new TranspositionTable(xDimension,yDimension,colors,16);		
		colorsLeftArray=new int[100][colors];
		
		for (int i=0;i<position.length;i++)
			if (position[i]>=0) colorsLeftArray[0][position[i]]++;
		
		possibleMoves = new int[100][xDim*yDim];
		
		int threshold;
		if (!reUseThreshold) threshold = Evaluation.overEstimate(position, scoreMode, colorsLeftArray[0], xDim, yDim);
		else threshold= oldThreshold;
		
		//threshold=2;
		
		possibleMoves[0] = SameGameBoard.generateMoves1(position, xDim, yDim);
		scores = new int[possibleMoves[0].length];
		nodes=0;
		while (true)
		{
			if (stop)
			{
				if (timeIsUp)
				{
					//System.out.println("Return because out of time.");
					//System.out.println("Oldest Best Move: "+oldBestMove+" Color of this: "+position[oldBestMove]);
					return oldBestMove;
				}
				clock.stopIt();
				return -1;
			}
			
			bestMove=0;
			bestGValue=0;
			leafNodes=0;
			totalDepthLeafNodes=0;
			deepestLeaf=0;
			
			maximumValueOfLeaveNode=Integer.MIN_VALUE;
			if (output) System.out.print("Threshold: "+threshold);
			
			//long startedlocal = System.currentTimeMillis();
			
			int i=0;
			int[] moves = possibleMoves[0];
			while(moves[i]!=-1)
			{
				bestLocalGValue=0;
				byte[] p = new byte[position.length];
				for (int j=position.length-1;j>=0;j--)
					p[j]=position[j];
				int playedColor = p[moves[i]];
				//SameGameBoard.setMakeMoveVariables(xDim, yDim);
				int score = SameGameBoard.makeMove(p, xDim, yDim, moves[i]%xDim, moves[i]/xDim, p[moves[i]],(byte)-1);
				for (int j=0;j<colorsLeftArray[1].length;j++)
					colorsLeftArray[1][j] = colorsLeftArray[0][j];
				colorsLeftArray[1][playedColor]-=score;
				p=SameGameBoard.dropDownStones(p, xDim, yDim);
				int[] changedWindow = SameGameBoard.getChangedWindow();
				//System.out.println();
				//SameGameBoard.println(p, xDim, yDim);
				if (scoreMode==BoardPanel.SAMEGAME) score=(score-2)*(score-2);
				if (scoreMode==BoardPanel.BUBBLEBREAKER) score=(score-1)*score;
				scores[i]=score;
				if (IDAStar(p, score, threshold,1,changedWindow))
				{
					if (output) System.out.println();
					if (output) System.out.println();
					oldThreshold=threshold-score;
					clock.stopIt();
					return moves[i];
				}
				if (bestLocalGValue>bestGValue)
				{
					bestGValue=bestLocalGValue;
					bestMove=i;
				}
				i++;
			}
			long ended = System.currentTimeMillis();
			if (output) System.out.println("  NPS: "+(int)((nodes*1000)/((ended+1.0)-started))+" Search Depths: "+(((int)(((totalDepthLeafNodes+0.0)/(leafNodes))*100))+0.0)/100+"/"+deepestLeaf);
			threshold = maximumValueOfLeaveNode;
			oldBestMove=moves[bestMove];
	
			if (ended-started>time)
			{
				oldThreshold=threshold-scores[bestMove];
				return moves[bestMove];
			}
		}
	}
	
	private static int maximumValueOfLeaveNode=0;
	//private static int minimumValueOfLeaveNode=Integer.MAX_VALUE;
	public static long nodes=0;
	private static int[] c;
	
	
	private boolean IDAStar(byte[] position, int g, int threshold, int depth, int[] changedWindow)
	{
		if (stop) return false;
		nodes++;
		int hashKey=0;
		
		/*if ((useTT)&&(depth<=maximumTTdepth))
		{
			hashKey = transposition.computeHashKey(position,xDim,yDim);
			if (transposition.tableKeys[hashKey&transposition.codePart]==hashKey)
				if ((transposition.tableThresholds[hashKey&transposition.codePart]==threshold)&&(transposition.gScore[hashKey&transposition.codePart]>=g))
					return false;
		}*/
		
		//Game ended
		if (!SameGameBoard.canMove(position, xDim, yDim))
		{
			leafNodes++;
			totalDepthLeafNodes+=depth;
			if (depth>deepestLeaf) deepestLeaf=depth;
			
			if (scoreMode==BoardPanel.SAMEGAME)
			{
				if (SameGameBoard.isEmpty(position, xDim, yDim)) g+=1000;
				else
				{
					c = colorsLeftArray[depth];
					for (int i=c.length-1;i>=0;i--)
						if (c[i]>2) g-=(c[i]-2)*(c[i]-2);
				}
			}
			if (g>=threshold) return true;
			if (g>bestLocalGValue) bestLocalGValue=g;
			if (g>maximumValueOfLeaveNode) maximumValueOfLeaveNode=g;
			
			/*if ((useTT)&&(depth<=maximumTTdepth))
			{
				if ((transposition.tableKeys[hashKey&transposition.codePart]==0)||(threshold<transposition.tableThresholds[hashKey&transposition.codePart])||(g>transposition.gScore[hashKey&transposition.codePart]))
					transposition.addToTable(hashKey, threshold,g);
			}*/
			
			return false;
		}
		
		//System.out.println("Estimate");
		//Estimate how much we can get
		int h=Evaluation.overEstimate(position, scoreMode, colorsLeftArray[depth], xDim, yDim);
		
		if (g+h<threshold)
		{
			if (g+h>maximumValueOfLeaveNode) maximumValueOfLeaveNode=g+h;
			if (g>bestLocalGValue) bestLocalGValue=g;
			/*if ((useTT)&&(depth<=maximumTTdepth))
			{
				if ((transposition.tableKeys[hashKey&transposition.codePart]==0)||(threshold<transposition.tableThresholds[hashKey&transposition.codePart])||(g>transposition.gScore[hashKey&transposition.codePart]))
					transposition.addToTable(hashKey, threshold,g);
			}*/
			leafNodes++;
			totalDepthLeafNodes+=depth;
			if (depth>deepestLeaf) deepestLeaf=depth;
			return false;
		}
		
		//System.out.println("Make Moves");
		//Make moves
		possibleMoves[depth] = SameGameBoard.generateMovesIteratively(position, possibleMoves[depth-1], changedWindow, xDim, yDim);
		//possibleMoves[depth] = SameGameBoard.generateMoves1(position, xDim, yDim);
		int[] localmoves = possibleMoves[depth];
		
		int i=0;
		byte[] p = new byte[position.length];
		
		while (localmoves[i]!=-1)
		{
			for (int j=position.length-1;j>=0;j--)
				p[j]=position[j];
			
			int playedColor = p[localmoves[i]];
			int score = SameGameBoard.makeMove(p, xDim, yDim, localmoves[i]%xDim, localmoves[i]/xDim, p[localmoves[i]],(byte)-1);
			for (int j=colorsLeftArray[depth].length-1;j>=0;j--)
				colorsLeftArray[depth+1][j] = colorsLeftArray[depth][j];
			colorsLeftArray[depth+1][playedColor]-=score;
			p = SameGameBoard.dropDownStones(p, xDim, yDim);
			int[] newChangedWindow = SameGameBoard.getChangedWindow();
			
			if (scoreMode==BoardPanel.SAMEGAME) score=(score-2)*(score-2);
			if (scoreMode==BoardPanel.BUBBLEBREAKER) score=(score-1)*score;
			
			if (IDAStar(p, g+score, threshold,depth+1, newChangedWindow)) return true;
			i++;
		}
		/*if ((useTT)&&((depth<maximumTTdepth)))
		{
			if ((transposition.tableKeys[hashKey&transposition.codePart]==0)||(threshold<transposition.tableThresholds[hashKey&transposition.codePart])||(g>transposition.gScore[hashKey&transposition.codePart]))
				transposition.addToTable(hashKey, threshold,g);
		}*/
		return false;
	}
	
	public void setEvaluationFunction(int function)
	{
		//evalFunction=function;
	}

	public void stopCalculation()
	{
		stop=true;
	}
	
	public void reUseEarlierThreshold(boolean value)
	{
		reUseThreshold=value;
	}
	
	public void timeIsUp()
	{
		timeIsUp=true;
		stop=true;
	}
}
