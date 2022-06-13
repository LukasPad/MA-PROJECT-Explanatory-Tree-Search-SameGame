package OldCode;

public class MCTSPlayer implements SameGameBot
{

	private boolean stop=false;
	private boolean timeUp=false;
	
	private boolean sameGame = false;
	private boolean bubbleBreaker = false;
	
	public static int xDim=0;
	public static int yDim=0;
	private int totalLength=0;
	
	//private static byte[] simulationPosition;
	//private static byte[] simulationPositionColors; 
	//public static short simulationScore;
	
	public int topScore = Integer.MIN_VALUE;
	public int[] primaryVariant = new int[225];
	
	public double UCTConstant=Parameters.UCTConstant;
	public double DeviationConstant=Parameters.DeviationConstant;
	public static int numberOfVisitsBeforeExpanding = Parameters.numberOfVisitsBeforeExpanding;
	public int maxNumberOfNodes=Parameters.maxNumberOfNodes;
	public int maxSimulation = Integer.MAX_VALUE;
	
	public boolean output=true;
	public int totalSimulations=0;
	public int deepestNode=0;
	public double averageDepth=0;
	//public int totalNodes=0;
	public int bestScore;
	
	public boolean useTranspositions = !true;
	
	private UCTEdge[] simulatedMoves= new UCTEdge[1000];
	private int movesInTree = 0;
	
	public TranspositionTable tt;
	
	public final int TOPSCORE = 1;
	public final int AVERAGE = 2;
	public final int SIMULATIONS = 3;
	
	public int finalMoveSelection = TOPSCORE;
	
	public int getMove(byte[] position, int xDim, int yDim, int scoreMode, int time) 
	{	
		stop=false;
		timeUp=false;
		
		this.xDim=xDim;
		this.yDim=yDim;
		totalLength=xDim*yDim;
		Evaluation.monteCarloPosition = new byte[totalLength];
		
		int colors = 0;
		for (int i=0;i<position.length;i++) if (position[i]>colors) colors=position[i];
		colors++;
		byte[] positionColors = new byte[colors];
		Evaluation.monteCarloColors = new byte[colors];
		for (int i=0;i<position.length;i++) if (position[i]>=0) positionColors[position[i]]++;
		
		Evaluation.setVariables(xDim, yDim, scoreMode,colors);
		SameGameBoard.setMakeMoveVariables(Evaluation.monteCarloPosition, xDim, yDim, (byte)-1);
		
		if (useTranspositions)
		{
			tt = new TranspositionTable(xDim,yDim,colors,16);
		}
		
		if (scoreMode==BoardPanel.SAMEGAME) sameGame=true;
		else if (scoreMode==BoardPanel.BUBBLEBREAKER) bubbleBreaker=true;
		
		WatchTimeThread clock = new WatchTimeThread(time,this);
		clock.start();
		//---------------------------------------------------------------
		/*moves = SameGameBoard.generateMoves1(position, xDim, yDim);
		int nrOfMoves=0;
		for (;moves[nrOfMoves]>=0;nrOfMoves++);
		positions=new int[nrOfMoves][position.length];
		scores=new int[nrOfMoves];
		average = new double[nrOfMoves];
		topScore = new int[nrOfMoves];
		for (int i=0;i<nrOfMoves;i++) topScore[i]=Integer.MIN_VALUE;
		simulations = new int[nrOfMoves];
		
		for (int i=0;i<nrOfMoves;i++)
		{
			for (int j=0;j<position.length;j++) positions[i][j]=position[j];
			scores[i]= SameGameBoard.makeMove(positions[i], xDim, yDim, moves[i]%xDim, moves[i]/xDim, position[moves[i]], -1);
			SameGameBoard.dropDownStones(positions[i], xDim, yDim);
			
			int tmp = scores[i]-2;
			if (sameGame) if (tmp>0) scores[i]=squares[i];
			else if (bubbleBreaker) scores[i]*=scores[i]-2;
		}
		
		
		
		while ((!stop)&&(!timeUp))
		{
			int moveIndex=selectMove();
			//System.out.println("Move Index: "+moveIndex);
			int mcScore = Evaluation.monteCarlo(positions[moveIndex], xDim, yDim, 1, scoreMode);
			if (mcScore>topScore[moveIndex]) topScore[moveIndex]=mcScore;
			average[moveIndex]=(average[moveIndex]*simulations[moveIndex]+mcScore)/(simulations[moveIndex]+1.0);
			simulations[moveIndex]++;
			totalSimulations++;
		}*/
		
		totalSimulations=0;
		UCTNode.totalNodes=0;
		topScore=Integer.MIN_VALUE;
		
		UCTNode root = new UCTNode();
		UCTEdge rootEdge = new UCTEdge();
		rootEdge.child=root;
		//root.position=position;
		//root.state = new GameState(position,xDim,yDim,colors);
		
		OutputThread out=null;
		if (output)
		{
			out = new OutputThread(root);
			out.start();
		}
		
		simulatedMoves[0]=rootEdge;
		
		while ((!stop)&&(!timeUp)&&(UCTNode.totalNodes<maxNumberOfNodes)&&(!root.isTerminal)&&(totalSimulations<maxSimulation))
		{
			movesInTree=0;
			
			//System.out.println("=============================================");
			System.arraycopy(position, 0, Evaluation.monteCarloPosition, 0, totalLength);
			System.arraycopy(positionColors, 0, Evaluation.monteCarloColors, 0, colors);
			UCTNode leaf = selectNode(root);
			System.arraycopy(Evaluation.monteCarloPosition, 0, leafPosition, 0, 225);
			short simulationScore=leaf.playRandomGame();
			expansionStrategy(leaf);
			//short score = leaf.doLeafSimulation();
			backPropagate(simulationScore);
			totalSimulations++;
			
			if (simulationScore>topScore)
			{
				topScore=simulationScore;
				System.arraycopy(Evaluation.gameRecord, 0, primaryVariant, 0, 225);
			}
			
			//printTree(root,0);
			//System.out.println("-*--------------");
		}
		
		if (output) out.stopIt();
		
		clock.stopIt();
		
		
		
		/*UCTEdge best = root.child;
		UCTEdge loop = best.sibling;
		while (loop!=null)
		{
			if (loop.topScore>best.topScore) best=loop;
			loop=loop.sibling;
		}
		
		bestScore=best.topScore;
		
		return best.move;*/
		
		/*if (primaryVariant[0]==0)
		{
			System.out.println("got null move");
			
			System.out.println(stop);
			System.out.println(timeUp);
			System.out.println(UCTNode.totalNodes);
			System.out.println(totalSimulations);
			
			SameGameBoard.println(position, 15, 15);
			
			System.out.println("Primary Variant: ");
			for (int i=0;i<225 && primaryVariant[i]!=-1;i++)
				System.out.print(primaryVariant[i]+" ");
			System.out.println();
			
			System.out.println(root.simulations);
			System.out.println("Printing root children...");
			UCTEdge loop = root.child;
			while (loop!=null)
			{
				double deviation = Math.sqrt((loop.sumOfSquaredResults - (loop.simulations*loop.average*loop.average) + DeviationConstant)/loop.simulations);
				System.out.println("X:"+loop.move%xDim+" Y:"+loop.move/xDim+" Sims: "+loop.simulations+" Avrg: "+loop.average+" Top: "+loop.topScore+" UCT: "+(UCTConstant * Math.sqrt(Math.log10(root.simulations)/loop.simulations))+" Dev: "+deviation);
				loop=loop.sibling;
			}
		}*/
		
		
		
		if (finalMoveSelection==TOPSCORE) return primaryVariant[0];
		if (finalMoveSelection==AVERAGE)
		{
			UCTEdge n = root.child;
			float bestAverage = Float.MIN_VALUE;
			int bestMove = n.move;
			
			while (n!=null)
			{
				if (n.average>bestAverage)
				{
					bestAverage = n.average;
					bestMove = n.move;
				}
				n=n.sibling;
			}
			
			return bestMove;
		}
		return 0;
	}
	
	private static byte[] leafPosition = new byte[225];
	
	
	public static double topScoreWeight = 0.03;
	//public static double topScoreWeight = 0;
	
	public static double MCTSConstant=0.5;
	
	private UCTNode selectNode(UCTNode n)
	{
		Evaluation.gameRecordPosition=0;
		Evaluation.score=0;
	
		while (n.isInternal && !n.isTerminal)
		{
			UCTEdge best = null;
			UCTEdge loop = n.child;

			double bestScore=Double.NEGATIVE_INFINITY;
			double moveScore = 0;
			
			int parentSims = n.simulations;
			if (useTranspositions)
			{
				for (UCTEdge loop2=n.child;loop2!=null;loop2=loop2.sibling)
					parentSims+=loop2.child.simulations;
			}
			
			while (loop!=null)
			{
				if (loop.simulations<=0)
				{	
					best = loop;
					break;
				}
				
				/*if (loop.child.isTerminal)
				{
					loop=loop.sibling;
					continue;
				}*/
				
				int childSims = loop.simulations;
				
				if (useTranspositions) childSims=loop.child.simulations;
				
				
				moveScore = loop.average;
				moveScore += UCTConstant * Math.sqrt(Math.log10(parentSims)/childSims);
				moveScore += Math.sqrt(Math.abs(loop.sumOfSquaredResults - (childSims*loop.average*loop.average) + DeviationConstant)/childSims);
				if (moveScore>bestScore)
				{
					bestScore=moveScore;
					best=loop;
				}
				loop=loop.sibling;
			}
			
			if (best==null)
			{
				System.out.println("best child == null");
				loop = n.child;
				System.out.println(bestScore);
				while (loop!=null)
				{
					double deviation = Math.sqrt((loop.sumOfSquaredResults - (loop.simulations*loop.average*loop.average) + DeviationConstant)/loop.simulations);
					System.out.println("X:"+loop.move%xDim+" Y:"+loop.move/xDim+" Sims: "+loop.simulations+" Avrg: "+loop.average+" Top: "+loop.topScore+" UCT: "+(UCTConstant * Math.sqrt(Math.log10(n.simulations)/loop.simulations))+" Dev: "+deviation);
					loop=loop.sibling;
				}
			}
			
			Evaluation.score+=best.movePoints;
			Evaluation.gameRecord[Evaluation.gameRecordPosition]=best.move;
			
			int color = Evaluation.monteCarloPosition[best.move];
			short bloks = SameGameBoard.makeMove(Evaluation.monteCarloPosition, best.move, best.move%xDim, best.move/xDim, Evaluation.monteCarloPosition[best.move]);
			Evaluation.monteCarloColors[color]-=bloks;
			SameGameBoard.dropDownStones(Evaluation.monteCarloPosition, xDim, yDim);
			
			
			n=best.child;
			
			Evaluation.gameRecordPosition++;
			movesInTree++;
			simulatedMoves[movesInTree]=best;
		}
		
		if (Evaluation.gameRecordPosition>deepestNode) deepestNode=Evaluation.gameRecordPosition;
		averageDepth=((averageDepth*totalSimulations)+Evaluation.gameRecordPosition)/(totalSimulations+1.0);
		
		return n;
	}
	
	private byte[] tempPos = new byte[225];
	
	private void expansionStrategy(UCTNode leaf)
	{
		if (leaf.isInternal || leaf.isTerminal) return;
		
		if (leaf.simulations>=numberOfVisitsBeforeExpanding)
		{
			leaf.isInternal=true;
			
			
			int[] moves =SameGameBoard.generateMoves(leafPosition);
			
			if (moves[0]==-1)
			{
				leaf.isTerminal=true;
				return;
			}
			
			int i=0;
			
			for (i=0;moves[i]!=-1;i++)
			{
					UCTEdge e = new UCTEdge();
					e.parent=leaf;
					e.move=moves[i];
					e.movePoints=(SameGameBoard.moveSize[i]-2)*(SameGameBoard.moveSize[i]-2);
					//System.out.println(e.movePoints);
					e.sibling=leaf.child;
					leaf.child=e;
					
					if (useTranspositions)
					{
						System.arraycopy(leafPosition, 0, tempPos, 0, 225);
						
						SameGameBoard.makeMove(tempPos, moves[i], moves[i]%15, moves[i]/15, tempPos[moves[i]]);
						SameGameBoard.dropDownStones(tempPos, 15, 15);
						
						long hash = tt.computeHash(tempPos);
						
						UCTNode tableNode = tt.getTT(hash);
						if (tableNode==null)
						{
							UCTNode n = new UCTNode();
							n.hash=hash;
							e.child=n;
							tt.addToTable(n);
						}
						else
						{
							e.child=tableNode;
						}
					}
					else
					{
						UCTNode n = new UCTNode();
						e.child=n;
					}
			}
			
			if (i==0)
			{
				System.out.println("dasfs");
				System.exit(0);
			}
			
			for (UCTEdge loop=leaf.child;loop!=null;loop=loop.sibling)
			if (loop!=null && loop.child==null)
			{
				System.out.println("now...");
				SameGameBoard.println(Evaluation.monteCarloPosition, 15, 15);
				System.exit(0);
			}
			
			//int playedRandomMove = Evaluation.firstRandomMove;
		}
	}
	
	private void backPropagate(short score)
	{
		for (int i=movesInTree;i>=0;i--)
		{	
			UCTNode n = simulatedMoves[i].child;
			n.average=(float)(((n.average*n.simulations)+score)/(n.simulations+1.0));
			n.simulations++;
			n.sumOfSquaredResults+=score*score;
			if (score>n.topScore)n.topScore=score;
			
			if (n.isInternal)
			{
				n.isTerminal=true;
				for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
				{
					if (!loop.child.isTerminal) n.isTerminal=false;
				}
			}
			
			UCTEdge e = simulatedMoves[i];
			e.average=(float)(((e.average*e.simulations)+score)/(e.simulations+1.0));
			e.simulations++;
			e.sumOfSquaredResults+=score*score;
			if (score>e.topScore)e.topScore=score;
			
		}
		
		
		/*while (n!=null)
		{
			n.average=(float)(((n.average*n.simulations)+score)/(n.simulations+1.0));
			n.simulations++;
			n.sumOfSquaredResults+=score*score;
			if (score>n.topScore)n.topScore=score;
			n=n.parent;
		}*/
	}
	
	public int[] playGame(byte[] position, int xDim, int yDim, int scoreMode, int time)
	{
		int[] game = new int[225];
		byte[] gamePos = new byte[225];
		int[] gamePosColors = new int[10];
		System.arraycopy(position, 0, gamePos, 0, 225);
		for (int i=0;i<225;i++) if (gamePos[i]>=0) gamePosColors[gamePos[i]]++;
		
		int oldVariationScore = Integer.MIN_VALUE;
		int[] oldPrimaryVariation = new int[225];
		
		int moveCounter=0;
		gameScore=0;
		
		while (SameGameBoard.canMove(gamePos, xDim, yDim))
		{
			primaryVariant = new int[225];
			Evaluation.gameRecordPosition=0;
			topScore=Integer.MIN_VALUE;
			int m = getMove(gamePos, xDim, yDim, scoreMode, time/35);
			
			
			/*System.out.print("Primary Variant: ");
			for (int i=0;i<225;i++) System.out.print("["+primaryVariant[i]%15+","+primaryVariant[i]/15+"]");
			System.out.println();*/
			
			//if (oldVariationScore<topScore)
			//{
				game[moveCounter] = m;
			/*}
			else
			{
				game[moveCounter] = oldPrimaryVariation[0];
			}*/
			
			int colorPlayed=gamePos[game[moveCounter]];
			int bloks = SameGameBoard.makeMove(gamePos, game[moveCounter], game[moveCounter]%15, game[moveCounter]/15, colorPlayed);
			SameGameBoard.dropDownStones(gamePos, 15, 15);
			gamePosColors[colorPlayed]-=bloks;
			
			/*System.out.println();
			SameGameBoard.println(gamePos, 15, 15);
			System.out.println();*/
			
			gameScore+=(bloks-2)*(bloks-2);
			
			/*if (oldVariationScore<topScore)
			{
				oldVariationScore=topScore-(bloks-2)*(bloks-2);
				System.arraycopy(primaryVariant, 1, oldPrimaryVariation, 0, 224);
			}
			else
			{
				oldVariationScore-=(bloks-2)*(bloks-2);
				for (int i=0;i<224;i++) oldPrimaryVariation[i]=oldPrimaryVariation[i+1];
			}*/
			
			moveCounter++;
		}
		
		game[moveCounter]=-1;
		if (SameGameBoard.isEmpty(gamePos,15,15)) gameScore+=1000;
		else
		{
			for (int i=4;i>=0;i--) 
			{
				int amount = gamePosColors[i]-2;
				if (amount>0) gameScore-=amount*amount;
			}
		}
		
		return game;
	}
	
	public int gameScore=0;
	
	private void printTree(UCTNode n, int depth)
	{
		if (n==null) return;
		for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
		{
			for (int i=0;i<depth;i++) System.out.print("    ");
			System.out.println(loop.move+" "+loop.child);
			
			printTree(loop.child,depth+1);
		}
	}
	
	private boolean treeOK(UCTNode n, int depth)
	{
		if (n.child==null && !n.isInternal) return true;
		if (n.child!=null && n.isTerminal) return true;
		
		for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
		{
			for (int i=0;i<depth;i++) System.out.print("    ");
			System.out.println(loop.move);
			if (loop.child==null) return false;
			if (!treeOK(loop.child,depth+1)) return false;
		}
		return true;
	}
	

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
	
	/*public class UCTNode
	{
		private byte nrOfTimesVisited=0;		//1
		private int simulations=0;				//4
		private float average=Float.MIN_VALUE;	//4
		private short topScore=Short.MIN_VALUE;	//2
		public short move=-1;					//2						
		public boolean isInternal=false;		//1
		public boolean isTerminal=false;		//1
		private long sumOfSquaredResults = 0;
		private UCTNode sibling;
		public UCTNode child;
		public UCTNode parent;
		
		private UCTNode()
		{
			totalNodes++;
		}
		
		private short doLeafSimulation()
		{
			if (isTerminal)
			{
				simulations++;
				return topScore;
			}
	
			if (nrOfTimesVisited<numberOfVisitsBeforeExpanding)
			{
				nrOfTimesVisited++;
				int move = Evaluation.getRandomMove(simulationPosition);
				
				if (move!=-1)
				{
					short blocks = SameGameBoard.makeMove(simulationPosition, move, move%xDim, move/xDim, simulationPosition[move]);
					SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
					
					UCTNode existing = child;
					while ((existing!=null)&&(existing.move!=move)) existing=existing.sibling;

					if (existing!=null) return existing.playRandomGame();
					
					UCTNode newChild = new UCTNode();
					newChild.move=(short)move;
					newChild.parent=this;
					if (sameGame) simulationScore+=squares[blocks-2]; 
					else if (bubbleBreaker) simulationScore=(short)(simulationScore+(blocks*(blocks-1)));
					else simulationScore=(short)(simulationScore+blocks);
					newChild.sibling=child;
					child=newChild;
					return child.playRandomGame();
				}
				else
				{
					System.out.println("TERMINAL!!");
					isTerminal=true;
					Evaluation.monteCarloPosition=simulationPosition;
					topScore=Evaluation.sameGameFinalScore();
					simulations++;
					removeTerminalNode(this);
					return topScore;
				}
			}
			
	
			isInternal=true;
			int[] moves =SameGameBoard.generateMoves(simulationPosition);
			
			if (moves[0]==-1)
			{
				System.out.println("TERMINAL!!");
				isTerminal=true;
				Evaluation.monteCarloPosition=simulationPosition;
				topScore=Evaluation.sameGameFinalScore();
				simulations++;
				removeTerminalNode(this);
				return topScore;
			}
			
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
				int blocks = SameGameBoard.makeMove(simulationPosition, newChild.move, newChild.move%xDim, newChild.move/xDim, simulationPosition[newChild.move]);
				SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
				if (sameGame) simulationScore+=squares[blocks-2]; 
				else if (bubbleBreaker) simulationScore=(short)(simulationScore+(blocks*(blocks-1)));
				else simulationScore=(short)(simulationScore+blocks);
				return newChild.playRandomGame();
			}
			
			UCTNode c = selectNode(this);
			return c.playRandomGame();
		}
		
		public short playRandomGame()
		{
			Evaluation.monteCarloPosition=simulationPosition;
			Evaluation.monteCarloColors=simulationsPositionColors;
			short points = Evaluation.playRandomGame();
			average=(float)((average*simulations+points)/(simulations+1.0));
			simulations++;
			sumOfSquaredResults+=points*points;
			if (points>topScore) topScore=(short)(simulationScore+points);
			return points;
		}
	}*/
	
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
			while (!stop)
			{
				System.out.println("Total Nodes: "+UCTNode.totalNodes);
				System.out.println("Total Simulations: "+totalSimulations);
				System.out.println("Simulations per second: "+((totalSimulations-lastTotal)*2));
				System.out.println("Average depth: "+averageDepth);
				System.out.println("Deepest Node: "+deepestNode);
				System.out.println("Top Score: "+topScore);
				lastTotal=totalSimulations;
				
				System.out.print("Primary variant: ");
				int i=0;
				while (i<225 && primaryVariant[i]!=-1)
				{
					System.out.print("["+primaryVariant[i]%xDim+","+primaryVariant[i]/xDim+"] ");
					i++;
				}
				/*UCTNode n = root;
				while (n.child!=null)
				{
					UCTEdge bestEdge = n.child;
					int bestScore = bestEdge.topScore;
					UCTEdge loop = bestEdge.sibling;
					while (loop!=null)
					{
						if (loop.topScore>bestScore)
						{
							bestEdge=loop;
							bestScore=loop.topScore;
						}
						loop=loop.sibling;
					}
					System.out.print("["+bestEdge.move%xDim+","+bestEdge.move/xDim+"]");
					n=bestEdge.child;
				}*/
				System.out.println();
				UCTEdge loop = root.child;
				while (loop!=null)
				{
					double deviation = Math.sqrt((loop.sumOfSquaredResults - (loop.simulations*loop.average*loop.average) + DeviationConstant)/loop.simulations);
					System.out.println("X:"+loop.move%xDim+" Y:"+loop.move/xDim+" Sims: "+loop.simulations+" Avrg: "+loop.average+" Top: "+loop.topScore+" UCT: "+(UCTConstant * Math.sqrt(Math.log10(root.simulations)/loop.simulations))+" Dev: "+deviation);
					
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
}
