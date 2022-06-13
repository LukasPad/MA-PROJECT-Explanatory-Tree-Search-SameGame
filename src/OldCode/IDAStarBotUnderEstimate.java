package OldCode;

public class IDAStarBotUnderEstimate implements SameGameBot
{
	
	private int xDim;
	private int yDim;
	
	private int scoreMode;
	
	private int colors;
	
	private int evalFunction=1;
	
	private int[][] colorsLeftArray;
	private TranspositionTable transposition;
	
	private boolean useTT=!true;
	private int maximumTTdepth=5;
	
	private boolean stop=false;
	private boolean timeIsUp=false;
	
	private int bestMove=0;
	private int bestValue=0;
	
	private long leafNodes=0;
	private long totalDepthLeafNodes=0;
	private int deepestLeaf=0;
	private int oldBestMove=0;
	public boolean output=!true;
	private int[] scores;
	private int threshold;
	//private int[] position=new int[0];

	public int getMove(byte[] position, int xDimension, int yDimension, int mode, int time) 
	{	
		//if (evalFunction==2) return getMove2(position, xDimension, yDimension, mode, time);
		WatchTimeThread clock = new WatchTimeThread(time,this);
		clock.start();
		
		//if (position.length!=aPosition.length) position = new int[aPosition.length];
		//for (int i=0;i<position.length;i++) position[i]=aPosition[i];
		
		long started=System.currentTimeMillis();
		stop=false;
		timeIsUp=false;
		xDim=xDimension;
		yDim=yDimension;
		scoreMode=mode;
		
		for (int i=0;i<position.length;i++)
			if (position[i]>colors) colors=position[i];
				colors++;
				
		if (useTT) transposition=new TranspositionTable(xDimension,yDimension,colors,16);		
		colorsLeftArray=new int[100][colors];
		
		for (int i=0;i<position.length;i++)
			if (position[i]>=0) colorsLeftArray[0][position[i]]++;
		
		
		//if (!reUseThreshold) threshold = Evaluation.estimate(position, scoreMode, colorsLeftArray[0], xDim, yDim,evalFunction);
		//else threshold= oldThreshold;
		threshold = 2;
		
		int[] moves = SameGameBoard.generateMoves1(position, xDim, yDim);
		scores = new int[moves.length];
		nodes=0;
		
		int[] moveScores = new int[moves.length];
		
		while (true)
		{
			if (stop)
			{
				if (timeIsUp)
				{
					return oldBestMove;
				}
				clock.stopIt();
				return -1;
			}
			
			bestMove=0;
			bestValue=0;
			leafNodes=0;
			totalDepthLeafNodes=0;
			deepestLeaf=0;
			
			if (output) System.out.print("Threshold: "+threshold);
			
			int i=0;
			while(moves[i]!=-1)
			{
				byte[] p = new byte[position.length];
				for (int j=position.length-1;j>=0;j--)
					p[j]=position[j];
				int playedColor = p[moves[i]];
				
				int bloks = SameGameBoard.makeMove(p, xDim, yDim, moves[i]%xDim, moves[i]/xDim, p[moves[i]],(byte)-1);
				int score = bloks;
				for (int j=0;j<colorsLeftArray[1].length;j++)
					colorsLeftArray[1][j] = colorsLeftArray[0][j];
				colorsLeftArray[1][playedColor]-=score;
				p=SameGameBoard.dropDownStones(p, xDim, yDim);
				
				if (scoreMode==BoardPanel.SAMEGAME) score=(score-2)*(score-2);
				if (scoreMode==BoardPanel.BUBBLEBREAKER) score=(score-1)*score;
				scores[i]=score;
				moveScores[i]=IDAStar(p, score, bloks,1);
				
				if (moveScores[i]>bestValue)
				{
					bestValue=moveScores[i];
					bestMove=i;
				}
				i++;
			}
			
			long ended = System.currentTimeMillis();
			if (output) System.out.println("  NPS: "+(int)((nodes*1000)/((ended+1.0)-started))+" Search Depths: "+(((int)(((totalDepthLeafNodes+0.0)/(leafNodes))*100))+0.0)/100+"/"+deepestLeaf+" Best Score: "+bestValue);
			threshold += 2;
			oldBestMove=moves[bestMove];
			if (threshold==xDim*yDim) return oldBestMove;
		}
	}
	
	//private static int minimumValueOfLeaveNode=0;
	//private static int minimumValueOfLeaveNode=Integer.MAX_VALUE;
	public static long nodes=0;
	private static int[] c;
	
	
	private int IDAStar(byte[] position, int g, int bloks, int depth)
	{
		if (stop) return Integer.MIN_VALUE;
		nodes++;
		/*int hashKey=0;
		if ((useTT)&&(depth<=maximumTTdepth))
		{
			hashKey = transposition.computeHashKey(position,xDim,yDim);
			if (transposition.isInTable(hashKey)) 
				if ((transposition.getThreshold(hashKey)==threshold)&&(transposition.getGScore(hashKey)<=g))
					return true;
		}*/
		
		//Game ended
		if (!SameGameBoard.canMove(position, xDim, yDim))
		{
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
						
			/*if ((useTT)&&(depth<=maximumTTdepth))
			{
				if ((transposition.getKey(hashKey)==0)||(threshold>transposition.getThreshold(hashKey))||(g>transposition.getGScore(hashKey)))
					transposition.addToTable(hashKey, threshold,g);
			}*/
			leafNodes++;
			totalDepthLeafNodes+=depth;
			if (depth>deepestLeaf) deepestLeaf=depth;
			return g;
		}
		
		if (bloks>=threshold)
		{
			//int h=Evaluation.estimate(position, scoreMode, colorsLeftArray[depth], xDim, yDim,evalFunction);
			int h=Evaluation.monteCarlo(position, xDim, yDim, 100, scoreMode);
			int f = g+h;
			
			/*if ((useTT)&&(depth<=maximumTTdepth))
			{
				if ((transposition.getKey(hashKey)==0)||(threshold>transposition.getThreshold(hashKey))||(g>transposition.getGScore(hashKey)))
					transposition.addToTable(hashKey, threshold,g);
			}*/
			leafNodes++;
			totalDepthLeafNodes+=depth;
			if (depth>deepestLeaf) deepestLeaf=depth;
			return f;
		}
		
		int[] localmoves = SameGameBoard.generateMoves1(position, xDim, yDim);
		
		int i=0;
		byte[] p = new byte[position.length];
		
		int bestScore=0;
		
		while (localmoves[i]!=-1)
		{
			for (int j=position.length-1;j>=0;j--)
				p[j]=position[j];
			
			int playedColor = p[localmoves[i]];
			int localBloks = SameGameBoard.makeMove(p, xDim, yDim, localmoves[i]%xDim, localmoves[i]/xDim, p[localmoves[i]],(byte)-1);
			int score = localBloks;
			for (int j=colorsLeftArray[depth].length-1;j>=0;j--)
				colorsLeftArray[depth+1][j] = colorsLeftArray[depth][j];
			colorsLeftArray[depth+1][playedColor]-=score;
			p = SameGameBoard.dropDownStones(p, xDim, yDim);
			
			if (scoreMode==BoardPanel.SAMEGAME) score=(score-2)*(score-2);
			if (scoreMode==BoardPanel.BUBBLEBREAKER) score=(score-1)*score;
			
			int s = IDAStar(p, g+score, bloks+localBloks,depth+1);
			
			if (s>bestScore)
				bestScore=s;
			
			i++;
		}
		/*if ((useTT)&&((depth<maximumTTdepth)))
		{
			if ((transposition.getKey(hashKey)==0)||(threshold>transposition.getThreshold(hashKey))||(g>transposition.getGScore(hashKey)))
				transposition.addToTable(hashKey, threshold,g);
		}*/
		return bestScore;
	}
	
	public void setEvaluationFunction(int function)
	{
		evalFunction=function;
	}

	public void stopCalculation()
	{
		stop=true;
	}
	
	public void reUseEarlierThreshold(boolean value)
	{
		//reUseThreshold=value;
	}
	
	public void timeIsUp()
	{
		timeIsUp=true;
		stop=true;
	}
}
