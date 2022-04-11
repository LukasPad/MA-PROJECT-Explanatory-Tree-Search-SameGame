
public class Maximax implements SameGameBot 
{
	
	private int evaluationFunction=1;
	private boolean stop=false;
	private boolean timeup=false;
	private boolean reUseThreshold=false;
	private int xDim;
	private int yDim;
	private int mode;
	
	private byte[][] positions;
	private int[][] colorsLeftArray;
	
	private long nodes=0;
	
	private boolean useTT = true;
	private int dontUseTTonLastPlies=0;
	private TranspositionTable transposition;
	
	private int colors;
	private int alpha=Integer.MIN_VALUE;
	
	private boolean finished=false;
	
	public int getMove(byte[] position, int xDim, int yDim, int scoreMode, int time) 
	{
		WatchTimeThread clock = new WatchTimeThread(time,this);
		clock.start();
		
		finished=false;
		stop=false;
		timeup=false;
		this.xDim=xDim;
		this.yDim=yDim;
		mode=scoreMode;
		
		positions=new byte[100][position.length];
		positions[0]=position;
		
		for (int i=0;i<position.length;i++)
			if (position[i]>colors) colors=position[i];
				colors++;
		colorsLeftArray=new int[100][colors];
				
		for (int i=0;i<position.length;i++)
			if (position[i]>=0) colorsLeftArray[0][position[i]]++;
		
		if ((useTT)&&(transposition==null)) transposition=new TranspositionTable(xDim,yDim,colors,16);
		
		int depth=1;
		int[] moves = SameGameBoard.generateMoves1(position, xDim, yDim);
		
		int bestScore;
		int bestIndex=0;
		int oldBestIndex=0;
		
		while ((!timeup)&&(!finished))
		{	
			nodes=0;
			finished=true;
			long start = System.currentTimeMillis();
			alpha=Integer.MIN_VALUE;
			bestScore=Integer.MIN_VALUE;
			bestIndex=0;
			int move=0;
			while (moves[move]!=-1)
			{
				for (int i=0;i<position.length;i++) positions[1][i]=positions[0][i];
				int playedColor=positions[0][moves[move]];
				int score = SameGameBoard.makeMove(positions[1], xDim, yDim, moves[move]%xDim, moves[move]/xDim, position[moves[move]], (byte)-1);
				//SameGameBoard.println(positions[1], xDim, yDim);
				for (int j=0;j<colorsLeftArray[1].length;j++) 
					colorsLeftArray[1][j] = colorsLeftArray[0][j];
				colorsLeftArray[1][playedColor]-=score;
				if (mode==BoardPanel.SAMEGAME) score=(score-2)*(score-2);
				if (mode==BoardPanel.BUBBLEBREAKER) score=(score-1)*score;
				SameGameBoard.dropDownStones(positions[1], xDim, yDim);
				//SameGameBoard.println(positions[1], xDim, yDim);
				
				score += max(score, 1,depth-1);
				if (score>bestScore)
				{	
					bestScore=score;
					bestIndex=move;
				}
				move++;
			}
			oldBestIndex=bestIndex;
			long end = System.currentTimeMillis();
			
			if (!timeup) System.out.println("Searched Depth: "+depth+" NPS: "+(nodes*1000)/(((end-start+0.0))+1)+" BestScore: "+bestScore);
			
			depth++;
		}
		
		clock.stopIt();
		System.out.println();
		return moves[oldBestIndex];
	}
	
	private int max(int score, int level, int depth)
	{
		nodes++;
		if (timeup||stop) return Integer.MIN_VALUE;
		
		//SameGameBoard.println(positions[level], xDim, yDim);
		
		if (!SameGameBoard.canMove(positions[level], xDim, yDim))
		{
			if (mode==BoardPanel.SAMEGAME)
			{
				if (SameGameBoard.isEmpty(positions[level], xDim, yDim)) 
					return 1000;
				else
				{
					int s=0;
					for (int i=0;i<colorsLeftArray[level].length;i++)
					{
						int n=colorsLeftArray[level][i];
						if (n>2) s-=(n-2)*(n-2);
					}
					return s;
				}
			}
			return 0;
		}
		
		int eval = Evaluation.estimate(positions[level], mode, colorsLeftArray[level], xDim, yDim, evaluationFunction); 
		
		if (depth==0)
		{
			finished=false;
			return eval;
		}
		
		if (score+eval<alpha) return score+eval;
		
		int hashKey=0;
		if ((useTT)&&(depth>=dontUseTTonLastPlies))
		{
			hashKey = transposition.computeHashKey(positions[level],xDim,yDim);
			if (transposition.tableKeys[hashKey&transposition.codePart]==hashKey)
				if ((transposition.tableThresholds[hashKey&transposition.codePart]>=depth))
				{
					finished=false;
					return transposition.gScore[hashKey&transposition.codePart];
				}
		}
		
		int[] moves = SameGameBoard.generateMoves1(positions[level], xDim, yDim);
		int max = Integer.MIN_VALUE;
		
		int move=0;
		while (moves[move]!=-1)
		{
			for (int i=0;i<positions[level].length;i++) positions[level+1][i]=positions[level][i];
			int playedColor=positions[level][moves[move]];
			int s = SameGameBoard.makeMove(positions[level+1], xDim, yDim, moves[move]%xDim, moves[move]/xDim, positions[level][moves[move]], (byte)-1);
			for (int j=0;j<colorsLeftArray[level].length;j++)
				colorsLeftArray[level+1][j] = colorsLeftArray[level][j];
			colorsLeftArray[level+1][playedColor]-=s;
			if (mode==BoardPanel.SAMEGAME) s=(s-2)*(s-2);
			if (mode==BoardPanel.BUBBLEBREAKER) s=(s-1)*s;
			SameGameBoard.dropDownStones(positions[level+1], xDim, yDim);
			//SameGameBoard.dropDownStones(positions[level+1], xDim, yDim);
			s += max(score+s,level+1,depth-1);
			if (s>max) max=s;
			if (score>alpha) alpha=score;
			move++;
		}
		
		if ((useTT)&&(depth>=dontUseTTonLastPlies))
		{
			//if ((transposition.tableThresholds[hashKey&transposition.codePart]<=depth))
				transposition.addToTable(hashKey, depth,max);
		}
		
		return max;
	}

	public void reUseEarlierThreshold(boolean value) 
	{
		reUseThreshold=value;
	}

	public void setEvaluationFunction(int function) 
	{
		evaluationFunction=function;
	}

	public void stopCalculation() 
	{
		stop=true;
	}

	public void timeIsUp() 
	{
		timeup=true;
	}

}
