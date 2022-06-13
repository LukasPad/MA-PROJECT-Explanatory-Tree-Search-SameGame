package OldCode;

public class UCTNode
	{
		public byte nrOfTimesVisited=0;		//1
		public int simulations=0;				//4
		public float average=Float.MIN_VALUE;	//4
		public short topScore=Short.MIN_VALUE;	//2
		//public byte[] position;
		public GameState state;
		public boolean isInternal=false;		//1
		public boolean isTerminal=false;		//1
		public long sumOfSquaredResults = 0;
		//public UCTNode sibling;
		public UCTEdge child;
		public UCTEdge parent;
		public long hash = 0;
		public UCTNode ttNext = null;
		
		public static int totalNodes = 0;
		//public static byte[] simulationPosition;
		
		public UCTNode()
		{
			totalNodes++;
		}
		
		/*private short doLeafSimulation()
		{
			int xDim=MCTSPlayer.xDim;
			int yDim=MCTSPlayer.yDim;
			
			if (isTerminal)
			{
				simulations++;
				return topScore;
			}
	
			if (nrOfTimesVisited<MCTSPlayer.numberOfVisitsBeforeExpanding)
			{
				nrOfTimesVisited++;
				int move = Evaluation.getRandomMove(simulationPosition);
				
				if (move!=-1)
				{
					short blocks = SameGameBoard.makeMove(simulationPosition, move, move%xDim, move/xDim, simulationPosition[move]);
					SameGameBoard.dropDownStones(simulationPosition, xDim, yDim);
					
					long newHash = TranspositionTable.computeHashKey(simulationPosition, xDim, yDim);
					
					UCTNode existing = child;
					while ((existing!=null)&&(existing.hash!=newHash)) existing=existing.sibling;

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
		}*/
		
		public short playRandomGame()
		{		
			//Evaluation.monteCarloPosition=simulationPosition;
			//Evaluation.monteCarloColors=simulationPositionColors;
			/*if (Evaluation.monteCarloPosition.length!=simulationPosition.length)
				Evaluation.monteCarloPosition = new byte[simulationPosition.length];
			for (int i=0;i<simulationPosition.length;i++)
				Evaluation.monteCarloPosition[i]=simulationPosition[i];*/
			/*for (int i=0;i<state.colors.length;i++)
				Evaluation.monteCarloColors[i]=state.colors[i];*/
			//Evaluation.monteCarloPosition=simulationPosition;
			//Evaluation.monteCarloColors=simulationsPositionColors;
			short points = Evaluation.playRandomGame();
			average=(float)((average*simulations+points)/(simulations+1.0));
			//simulations++;
			sumOfSquaredResults+=points*points;
			//if (points>topScore) topScore=(short)(MCTSPlayer.simulationScore+points);
			
			return points;
		}
		
		public boolean hasEdgeWithMove(int m)
		{
			UCTEdge loop = child;
			while (loop!=null) 
			{
				if (loop.move==m) return true;
				loop=loop.sibling;
			}
			return false;
		}
	}