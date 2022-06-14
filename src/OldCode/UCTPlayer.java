package OldCode;


public class UCTPlayer implements SameGameBot 
{

	private boolean stop=false;
	private boolean timeUp=false;
	//private int[] moves;
	//private int[][] positions;
	//private int[] scores;
	
	//private double[] average;
	//private int[] topScore;
	//private int[] simulations;
	//private Random r = new Random();
	
	private boolean sameGame = false;
	private boolean bubbleBreaker = false;
	
	private int xDim=0;
	private int yDim=0;
	private int totalLength=0;
	
	private static byte[] simulationPosition;
	private static byte[] simulationsPositionColors; 
	private static short simulationScore;
	
	//Normal Settings
	//public static double UCTConstant=0.5;
	//public static int DeviationConstant=10000;
	//public final int numberOfVisitsBeforeExpanding = 5;
	//public static double topScoreWeight = 0.0;
	
	//Exploration Settings
	//public final double UCTConstant=1;
	//public final int DeviationConstant=20000;
	//public final int numberOfVisitsBeforeExpanding = 5;
	//public static double topScoreWeight = 0.0;
	
	//Fast Settings
	//public static double UCTConstant=0.1;
	//public static  double DeviationConstant=32;
	//public final int numberOfVisitsBeforeExpanding = 10;
	//public static double topScoreWeight = 0.02;
	
	//Test Settings
	//public static double UCTConstant=0.1;
	//public static double DeviationConstant=0.1;
	//public final int numberOfVisitsBeforeExpanding = 0;
	//public static double topScoreWeight = 0.02;
	
	public static double UCTConstant = Parameters.UCTConstant;
	public static double DeviationConstant = Parameters.DeviationConstant;
	public static int numberOfVisitsBeforeExpanding = Parameters.numberOfVisitsBeforeExpanding;
	public static double topScoreWeight = Parameters.topScoreWeight;
	
	
	public int maxNumberOfNodes=Parameters.maxNumberOfNodes;
	
	
	
	public boolean output=true;
	//public boolean useTT=!true;
	public int totalSimulations=0;
	public int deepestNode=0;
	public double averageDepth=0;
	public int totalNodes=0;
	public int bestScore;
	public int terminalNodesFound=0;
	//public double numberOfNodeBeforeUsingNormalUCTConstant = 1000.0;
	
	public static int[] primaryVariant = new int[225];
	private static int[] localVariant = new int[225];
	
	//private static int[] exit = new int[5];
	
	//private static TranspositionTable tt;
	
	public int getMove(byte[] position, int xDim, int yDim, int scoreMode, int time) 
	{
		averageDepth=0;
		deepestNode=0;
		UCTConstant = Parameters.UCTConstant;
		DeviationConstant = Parameters.DeviationConstant;
		numberOfVisitsBeforeExpanding = Parameters.numberOfVisitsBeforeExpanding;
		topScoreWeight = Parameters.topScoreWeight;
		maxNumberOfNodes=Parameters.maxNumberOfNodes;
		Evaluation.RWeight = Parameters.RWeight;
		Evaluation.TRWeight = Parameters.TRWeight; 
		Evaluation.TCRWeight = Parameters.TCRWeight;
		Evaluation.RPWeight = Parameters.RPWeight;
		Evaluation.FRPWeight = Parameters.FRPWeight;
		Evaluation.CMWeight = Parameters.CMWeight;
		Evaluation.chanceOfPlayingChosenColor = Parameters.chanceOfPlayingChosenColor;
		
		
		primaryVariant[224]=-1;
		localVariant[224]=-1;
		//exit = new int[5];
		stop=false;
		timeUp=false;
		TimeThread t = new TimeThread(time);
		t.start();
		bestScore=Integer.MIN_VALUE;
		
		this.xDim=xDim;
		this.yDim=yDim;
		totalLength=xDim*yDim;
		simulationPosition = new byte[totalLength];
		
		int colors = 0;
		for (int i=0;i<position.length;i++) if (position[i]>colors) colors=position[i];
		colors++;
		byte[] positionColors = new byte[colors];
		simulationsPositionColors = new byte[colors];
		for (int i=0;i<position.length;i++) if (position[i]!=-1) positionColors[position[i]]++;
		//for (int i=0;i<positionColors.length;i++) System.out.println(positionColors[i]);
		
		Evaluation.setVariables(xDim, yDim, scoreMode,colors);
		Evaluation.gameRecord=localVariant;
		SameGameBoard.setMakeMoveVariables(simulationPosition, xDim, yDim, (byte)-1);
		
		//tt = new TranspositionTable(xDim,yDim,colors,16);
		
		if (scoreMode==BoardPanel.SAMEGAME) sameGame=true;
		else if (scoreMode==BoardPanel.BUBBLEBREAKER) bubbleBreaker=true;
		
		totalSimulations=0;
		totalNodes=0;
		terminalNodesFound=0;
		
		UCTNode root = new UCTNode();
		
		OutputThread out=null;
		if (output)
		{
			out = new OutputThread(root);
			out.start();
		}
		
		while ((!stop)&&(!timeUp)&&(totalNodes<maxNumberOfNodes)&&(!root.isTerminal))
		{
			System.arraycopy(position, 0, simulationPosition, 0, totalLength);
			System.arraycopy(positionColors, 0, simulationsPositionColors, 0, colors);
			simulationScore=0;
			depth=1;
			UCTNode leaf = selectNode(root);
			leaf.doLeafSimulation();
			if (simulationScore>bestScore)
			{
				bestScore=simulationScore;
				System.arraycopy(localVariant, 0, primaryVariant, 0, 225);
			}
			backPropagate(leaf,simulationScore);
			totalSimulations++;
		}
		
		//System.out.println("Total Simulations: "+totalSimulations);
		//System.out.println("Terminal Nodes: "+terminalNodesFound);
		//System.out.println("Exit 0: "+exit[0]);
		//System.out.println("Exit 1: "+exit[1]);
		//System.out.println("Exit 2: "+exit[2]);
		//System.out.println("Exit 3: "+exit[3]);
		//System.out.println("Exit 4: "+exit[4]);
		
		if (output) out.stopIt();
		
		//return primaryVariant[0];
		
		//UCTNode best = root.child;
		
		/*System.out.println("Root has children? "+(root.child!=null));
		System.out.print("Primary variant: ");
		for (int i=0;i<20;i++) System.out.print(primaryVariant[i]+" ");*/
		t.stopIt();
		return primaryVariant[1];
		/*
		UCTNode loop = best.sibling;
		while (loop!=null)
		{
			if (loop.topScore>best.topScore) best=loop;
			loop=loop.sibling;
		}
		
		return best.move;*/
	}
	
	private int depth=1;
	private UCTNode selectNode(UCTNode n)
	{
		
		//System.out.println("Internal? "+(n.isInternal));
		//System.out.println("Terminal? "+(n.isTerminal));
		
		while (n.isInternal&&!n.isTerminal)
		{
			//System.out.println("Is Internal or not Terminal");
			//Transposition Table update
			/*UCTNode loop = n.child;
			while (loop!=null)
			{
				if (tt.isInTable(loop.hashKey))
				{
					int pos = loop.hashKey&tt.codePart;
					if (loop.simulations<tt.simulations[pos])
					{
						loop.average=tt.average[pos];
						loop.simulations=tt.simulations[pos];
						loop.sumOfSquaredResults=tt.sumOfSquaredResults[pos];
					}
				}
				loop=loop.sibling;
			}*/
			
			
			UCTNode best=n.child;
			
			double deviation=0;
			double bestScore;
			if (best.isTerminal) bestScore=best.topScore;
			else
			{
				double average = (topScoreWeight*best.topScore)+((1-topScoreWeight)*best.average);
				deviation = Math.sqrt((best.sumOfSquaredResults-best.simulations*average*average+DeviationConstant)/(best.simulations+1));
				bestScore = average+UCTConstant*Math.sqrt(Math.sqrt(n.simulations)/best.simulations)*deviation;
			}
			UCTNode loop = best.sibling;
		
			while (loop!=null)
			{
				deviation=0;
				if (loop.simulations==0)
				{
					if (depth>deepestNode) deepestNode=depth;
					localVariant[depth]=loop.move;
					averageDepth=((averageDepth*totalSimulations)+depth)/(totalSimulations+1.0);
					int color = simulationPosition[loop.move];
					short bloks = SameGameBoard.makeMove(simulationPosition, loop.move, loop.move%xDim, loop.move/xDim, simulationPosition[loop.move]);
					simulationsPositionColors[color]-=bloks;
					SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
					if (sameGame) simulationScore+=squares[bloks-2];
					else if (bubbleBreaker) simulationScore=(short)(simulationScore+(bloks*(bloks-1)));
					else simulationScore=(short)(simulationScore+bloks);
					depth++;
					//System.out.println("Found a child without simulations.");
					//System.out.println("Move: "+(loop.move%15)+" "+(loop.move/15));
					return loop;
				}
				double tmpScore;
				if (loop.isTerminal) tmpScore=loop.topScore;
				else
				{
					deviation = Math.sqrt((loop.sumOfSquaredResults-loop.simulations*loop.average*loop.average+DeviationConstant)/(loop.simulations+1));
					tmpScore = loop.average+UCTConstant*Math.sqrt(Math.sqrt(n.simulations)/loop.simulations)*deviation;
				}
				if (tmpScore>bestScore)
				{
					best=loop;
					bestScore=tmpScore;
				}
				loop=loop.sibling;
			}
		
			
		//SameGameBoard.println(simulationPosition, 15, 15);
		//System.out.println("Move: "+best.move+" X:"+best.move%xDim+" Y:"+best.move/xDim);
		
			int color = simulationPosition[best.move];
			localVariant[depth]=best.move;
			short bloks = SameGameBoard.makeMove(simulationPosition, best.move, best.move%xDim, best.move/xDim, simulationPosition[best.move]);
			simulationsPositionColors[color]-=bloks;
			SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
			if (sameGame) simulationScore+=squares[bloks-2];
			else if (bubbleBreaker) simulationScore=(short)(simulationScore+(bloks*(bloks-1)));
			else simulationScore=(short)(simulationScore+bloks);
			n=best;
			//System.out.println("Selected move based on UCT.");
			//System.out.println("Move: "+(best.move%15)+" "+(best.move/15));
			depth++;
		}
		//System.out.println("After loop.");
		if (depth>deepestNode) deepestNode=depth;
		averageDepth=((averageDepth*totalSimulations)+depth)/(totalSimulations+1.0);
		
		return n;
	}
	
	private void backPropagate(UCTNode n, short score)
	{
		while (n!=null)
		{
			n.average=(float)(((n.average*n.simulations)+score)/(n.simulations+1.0));
			n.simulations++;
			n.sumOfSquaredResults+=score*score;
			if (score>n.topScore)n.topScore=score;
			
			// Transposition Table
			/*int pos = n.hashKey&tt.codePart;
			tt.average[pos]=n.average;
			tt.simulations[pos]=n.simulations;
			tt.sumOfSquaredResults[pos]=n.sumOfSquaredResults;*/
			
			
			n=n.parent;
		}
	}
	
	private void removeTerminalNode(UCTNode n)
	{
		if (n.parent!=null)
		{
			UCTNode loop = n.parent.child;
			
			if (n==loop)
			{	
				n.parent.child=loop.sibling;
				if (loop.sibling==null) removeTerminalNode(n.parent);
				return;
			}
			
			UCTNode before = loop;
			loop = loop.sibling;
			
			while (loop!=n)
			{
				before=loop;
				loop=loop.sibling;
			}
			before.sibling=loop.sibling;
		}
		else n.isTerminal=true;
	}

	
	/*private int selectMove()
	{
		int nrOfMoves=0;
		for (nrOfMoves=0;(nrOfMoves<moves.length)&&(moves[nrOfMoves]>=0);nrOfMoves++)
			if (simulations[nrOfMoves]==0) return nrOfMoves;
		
		double bestValue=Double.MIN_NORMAL;
		int bestIndex=0;
		
		for (int i=0;i<nrOfMoves;i++)
		{
			double value = average[i] + Math.sqrt(2*Math.log(totalSimulations)/simulations[i]);
			//System.out.print(value);
			if (value>bestValue)
			{
				bestValue=value;
				bestIndex=i;
			}
		}
		//System.out.println();
		return bestIndex;
	}*/

	public void reUseEarlierThreshold(boolean value) 
	{
		

	}

	public void setEvaluationFunction(int function) 
	{
	

	}

	public void stopCalculation() 
	{
		stop=true;
	}

	public void timeIsUp() 
	{
		//System.out.println("Time up.");
		timeUp=true;
	}

private static int[] squares = new int[100];
	
	static
	{
		for (int i=0;i<100;i++)	squares[i]=i*i;
	}
	
	private class UCTNode
	{
		private byte nrOfTimesVisited=0;		//1
		private int simulations=0;				//4
		//private float average=Float.MIN_VALUE;	//4
		private float average=0;	//4
		private short topScore=Short.MIN_VALUE;	//2
		public short move=-1;					//2						
		public boolean isInternal=false;		//1
		public boolean isTerminal=false;		//1
		private long sumOfSquaredResults = 0;
		//private int hashKey=Integer.MIN_VALUE;
		private UCTNode sibling;
		public UCTNode child;
		public UCTNode parent;
		
		private UCTNode()
		{
			totalNodes++;
		}
		
		private void doLeafSimulation()
		{
			//if (nrOfTimesVisited==0) hashKey = tt.computeHashKey(simulationPosition, xDim, yDim);
				
			if (nrOfTimesVisited<numberOfVisitsBeforeExpanding)
			{
				nrOfTimesVisited++;
				int move = Evaluation.getRandomMove(simulationPosition);
				
				if (move==-1)
				{
					foundTerminalNode();
					//exit[0]++;
					return;
				}
				
				int color = simulationPosition[move]; 
				short blocks = SameGameBoard.makeMove(simulationPosition, move, move%xDim, move/xDim, simulationPosition[move]);
				SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
				simulationsPositionColors[color]-=blocks;
				localVariant[depth]=move;
				depth++;
				if (sameGame) simulationScore+=squares[blocks-2]; 
				else if (bubbleBreaker) simulationScore=(short)(simulationScore+(blocks*(blocks-1)));
				else simulationScore=(short)(simulationScore+blocks);
				
				
				UCTNode existing = child;
				while ((existing!=null)&&(existing.move!=move)) existing=existing.sibling;

				if (existing!=null)
				{
					existing.doLeafSimulation();
					//exit[1]++;
					return;
				}
				
				UCTNode newChild = new UCTNode();
				newChild.move=(short)move;
				newChild.parent=this;
				newChild.sibling=child;
				child=newChild;
				child.playRandomGame();
				//exit[2]++;
				return;
			}
			
	
			isInternal=true;
			int[] moves =SameGameBoard.generateMoves(simulationPosition);
			
			if (moves[0]==-1) foundTerminalNode();
			
			int i=0;
			UCTNode newChild=null;
			while (moves[i]!=-1)
			{
				UCTNode c = child;
				
				while ((c!=null)&&(c.move!=moves[i]))
					c=c.sibling;
				
				if (c==null)
				{
					newChild = new UCTNode();
					newChild.move=(short)moves[i];
					newChild.parent=this;
					newChild.sibling=child;
					child=newChild;
				}
				
				i++;
			}
			
			if (newChild!=null)
			{
				int color = simulationPosition[newChild.move];
				int blocks = SameGameBoard.makeMove(simulationPosition, newChild.move, newChild.move%xDim, newChild.move/xDim, simulationPosition[newChild.move]);
				simulationsPositionColors[color]-=blocks;
				SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
				if (sameGame) simulationScore+=squares[blocks-2]; 
				else if (bubbleBreaker) simulationScore=(short)(simulationScore+(blocks*(blocks-1)));
				else simulationScore=(short)(simulationScore+blocks);
				localVariant[depth]=newChild.move;
				depth++;
				newChild.playRandomGame();
				//exit[3]++;
				return;
			}
			
			//System.out.println("Before selecting node:");
			//SameGameBoard.println(simulationPosition, 15, 15);
			UCTNode c = selectNode(this);
			//System.out.println("After selecting node:");
			//SameGameBoard.println(simulationPosition, 15, 15);
			c.doLeafSimulation();
			
			//exit[4]++;
			return;
		}
		
		public short playRandomGame()
		{
			Evaluation.monteCarloPosition=simulationPosition;
			/*for (int i=0;i<simulationsPositionColors.length;i++)
			{
				System.out.println(simulationsPositionColors[i]);
			}*/
				
			Evaluation.monteCarloColors=simulationsPositionColors;
			Evaluation.gameRecordPosition=depth;
			short points = Evaluation.playRandomGame();
			simulationScore+=points;
			nrOfTimesVisited++;
			
			average=((simulations*average)+simulationScore)/(simulations+1);
			simulations++;
			
			
			sumOfSquaredResults=simulationScore*simulationScore;
			
			if (points>topScore) topScore=(short)simulationScore;
			if (Evaluation.terminal) removeTerminalNode(this);
			return points;
		}
		
		private void foundTerminalNode()
		{
			//System.out.println("TERMINAL!!");
			terminalNodesFound++;
			Evaluation.monteCarloPosition=simulationPosition;
			simulationScore+=Evaluation.sameGameFinalScore();
			removeTerminalNode(this);
		}
	}
	
	private class OutputThread extends Thread
	{
		private UCTNode root;
		private boolean stop=false;
		
		public OutputThread(UCTNode root)
		{
			this.root=root;
		}
		
		public void run()
		{
			int lastTotal=0;
			
			try {
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			while (!stop)
			{
				System.out.println("Total Nodes: "+totalNodes);
				System.out.println("Total Simulations: "+totalSimulations);
				System.out.println("Simulations per second: "+((totalSimulations-lastTotal)*2));
				System.out.println("Average depth: "+averageDepth);
				System.out.println("Deepest Node: "+deepestNode);
				System.out.println("Top Score: "+root.topScore);
				lastTotal=totalSimulations;
				
				System.out.print("Primary variant: ");
				/*UCTNode n = root;
				while (n.child!=null)
				{
					UCTNode bestChild = n.child;
					int bestScore = bestChild.topScore;
					UCTNode loop = bestChild.sibling;
					while (loop!=null)
					{
						if (loop.topScore>bestScore)
						{
							bestChild=loop;
							bestScore=loop.topScore;
						}
						loop=loop.sibling;
					}
					System.out.print("["+bestChild.move%xDim+","+bestChild.move/xDim+"]");
					n=bestChild;
				}
				System.out.println();*/
				int i=1;
				while (primaryVariant[i]!=-1)
				{
					System.out.print("["+primaryVariant[i]%15+","+primaryVariant[i]/15+"] ");
					i++;
				}
				System.out.println();
				
				
				UCTNode loop = root.child;
				while (loop!=null)
				{
					double deviation = Math.sqrt((loop.sumOfSquaredResults-loop.simulations*loop.average*loop.average+DeviationConstant)/(loop.simulations+1));
					double tmpScore = loop.average+UCTConstant*Math.sqrt(Math.sqrt(root.simulations)/loop.simulations)*deviation;
					System.out.println("X:"+loop.move%xDim+" Y:"+loop.move/xDim+" Sims: "+loop.simulations+" Avrg: "+loop.average+" Top: "+loop.topScore+" UCT/Dev: "+tmpScore);
					//System.out.println(" Dev: "+Math.sqrt((loop.sumOfSquaredResults-loop.simulations*loop.average*loop.average+DeviationConstant)/(loop.simulations+1)));
					//System.out.println("Sum of Squares:"+loop.sumOfSquaredResults);
					loop=loop.sibling;
				}
				
				System.out.println("----------------------------------------------------------");
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopIt()
		{
			stop=true;
		}
	}
	
	private class TimeThread extends Thread
	{
		private int ms;
		private boolean stop=false;
		public TimeThread(int ms)
		{
			this.ms=ms;
		}
		public void run()
		{
			try {
				sleep(ms);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!stop) timeIsUp();
		}
		public void stopIt()
		{
			stop=true;
		}
	}
}
