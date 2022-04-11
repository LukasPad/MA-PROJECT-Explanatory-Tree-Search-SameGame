import java.util.Arrays;
import java.util.Random;

//import com.sun.corba.se.spi.ior.MakeImmutable;
//import com.sun.org.apache.bcel.internal.generic.GETSTATIC;


public class Evaluation 
{
	private static byte[] p = new byte[0];

	public static int estimate(byte[] position, int mode, int colors[], int xDim, int yDim, int function)
	{
		if (function==1) return overEstimate(position, mode, colors, xDim, yDim);
		if (function==2) 
		{
			if (p.length!=position.length) p = new byte[position.length];
			for (int i=0;i<p.length;i++) p[i] = position[i];
			return underEstimate(p, colors, mode, xDim, yDim);
		}
		else return -1;
	}
	
	public static int overEstimate(byte[] position, int mode, int[] colors, int xDim, int yDim)
	{	
		if (!SameGameBoard.canMove(position, xDim, yDim)) return 0;
		
		if (mode==BoardPanel.CLICKOMANIA)
		{
			int total=0;
			for (int i=0;i<colors.length;i++)
				if (colors[i]>1) total+=colors[i];
				                                       
			return total;
		}
		else if (mode==BoardPanel.SAMEGAME)
		{	
			int total=0;
			boolean bonusPossible=true;
			for (int i=0;i<colors.length;i++)
			{
				int number = colors[i];
				if (number>1) total+=(number-2)*(number-2);
				if (number==1) bonusPossible=false;
			}
			
			if (bonusPossible) total+=1000;
			return total;
		}
		else	//BubbleBreaker
		{
			int total=0;
			for (int i=0;i<colors.length;i++)
			{
				int number = colors[i];
				if (number>1) total+=number*(number-1);
			}
			return total;
		}
	}
	
	public static int overEstimate2(byte[] position, int mode, int[] colors, int xDim, int yDim)
	{	
		if (!SameGameBoard.canMove(position, xDim, yDim)) return 0;
		
		if (mode==BoardPanel.CLICKOMANIA)
		{
			int total=0;
			for (int i=0;i<colors.length;i++)
				if (colors[i]>1) total+=colors[i];
				                                       
			return total;
		}
		else if (mode==BoardPanel.SAMEGAME)
		{	
			int total=0;
			double max=0;
			int number;
			boolean bonusPossible=true;
			for (int i=0;i<colors.length;i++)
			{
				number=colors[i];
				total+=5*number;
				if (number>max) max = number;
				if (number==1) bonusPossible=false;
			}
			
			//max=(max*9)/10.0;
			total+=(max-2)*(max-2);
			if (bonusPossible) total+=1000;
			return total;
		}
		else	//BubbleBreaker
		{
			int total=0;
			for (int i=0;i<colors.length;i++)
			{
				int number = colors[i];
				if (number>1) total+=number*(number-1);
			}
			return total;
		}
	}
	
	//private static Random r = new Random();
	private static FastRandom r = new FastRandom();
	
	private final static byte BYTEZERO=(byte)0;
	private final static byte BYTEMINUSONE=(byte)-1;
	private final static byte BYTEMINUSTWO=(byte)-2;
	
	private static int underEstimate(byte[] position, int colors[], int scoreMode, int xDim, int yDim)
	{
		int score=0;
		for (int y=0;y<yDim;y++)
		{
			for (int x=0;x<xDim;x++)
			{
				if (position[x+y*xDim]!=-1)
				{
					int color=position[x+y*xDim];
					int blocks = SameGameBoard.makeMove(position, xDim, yDim, x, y, color, BYTEMINUSONE);
					if (scoreMode==BoardPanel.SAMEGAME) score += (blocks-2)*(blocks-2);
					if (scoreMode==BoardPanel.BUBBLEBREAKER) score += blocks*(blocks-1);
					if (scoreMode==BoardPanel.CLICKOMANIA) score += blocks;
					colors[color]-=blocks;
					SameGameBoard.dropDownStones(position, xDim, yDim);
				}
			}
		}
		if (scoreMode==BoardPanel.SAMEGAME)
		{
			if (SameGameBoard.isEmpty(position, xDim, yDim)) score+=1000;
			else
			{
				for (int i=0;i<colors.length;i++)
					if (colors[i]>2) score-= (colors[i]-2)*(colors[i]-2);
			}
		}
		return score;
	}
	
	public static int monteCarlo(byte[] position, int xDim, int yDim, int games, int scoreMode)
	{
		int bestScore = Integer.MIN_VALUE;
		if (monteCarloPosition.length!=position.length) monteCarloPosition = new byte[position.length];
		monteCarloXDim=xDim;
		monteCarloYDim=yDim;
		monteCarloXDimMinus1 = monteCarloXDim-1;
		monteCarloYDimMinus1 = monteCarloYDim-1;
		monteCarloScoreMode=scoreMode;
		monteCarloXDimTimesMonteCarloYDim=monteCarloXDim*monteCarloYDim;
		monteCarloYDimMinus1TimesMonteCarloXDim=monteCarloYDimMinus1*monteCarloXDim;
		for (int i=0;i<games;i++)
		{
			for (int j=0;j<position.length;j++) monteCarloPosition[j]=position[j];
			SameGameBoard.setMakeMoveVariables(monteCarloPosition, monteCarloXDim, monteCarloYDim, BYTEMINUSONE);
			int result = playRandomGame();
			if (result>bestScore) bestScore=result;
		}
		return bestScore;
	}
	
	public static byte[] monteCarloPosition = new byte[0];
	private static int monteCarloXDim = 0;
	private static int monteCarloYDim = 0;
	private static int monteCarloXDimMinus1 = 0;
	private static int monteCarloYDimMinus1 = 0;
	private static int monteCarloScoreMode =0;
	public static byte[] monteCarloColors = new byte[10];
	private static int monteCarloNumberOfColors;
	private static int monteCarloYDimMinus1TimesMonteCarloXDim;
	private static int monteCarloXDimTimesMonteCarloYDim;
	
	private static int[] squares = new int[100];
	
	static
	{
		for (int i=0;i<100;i++)	squares[i]=i*i;
	}
	
	//public static int numberOfBlocksToStartPlayingRandom=40;
	public static int[] gameRecord = new int[225];
	public static int gameRecordPosition = 0;
	public static int firstRandomMove = -1;
	public static short score = 0;
	
	public static short playRandomGame()
	{
		int blocks = 0;
		boolean sameGame = (monteCarloScoreMode==BoardPanel.SAMEGAME);
		boolean bubbleBreaker = (monteCarloScoreMode==BoardPanel.BUBBLEBREAKER);
	
		int move;
		
		boolean playingMixedStrategy = true;
		if (r.nextDouble()<Parameters.chanceOnPlayingWithSameStrategy)
			playingMixedStrategy = false;
		
		int style =0;
		if (!(playingMixedStrategy)) style = choosePlayoutStyle();
		
		if (playingMixedStrategy)
			move = getMixedPlayoutStrategyMove();
		else
			move=getMoveOfStyle(style);
		
		terminal=(move==-1);
		
		firstRandomMove = move;
		
		while (move!=-1)
		{
			gameRecord[gameRecordPosition]=move;
			gameRecordPosition++;
			
			int moveColor = monteCarloPosition[move];
			
			blocks = SameGameBoard.makeMove(monteCarloPosition,move,move%15, move/15, moveColor);
		
			//System.out.println("bloks: "+blocks);
			
			if (sameGame) score+=squares[blocks-2];
			else if (bubbleBreaker) score+=blocks*(blocks-1);
			else score+=blocks;
			
			monteCarloColors[moveColor]-=blocks;
			
			SameGameBoard.dropTheStones2(monteCarloPosition, monteCarloXDim, monteCarloYDim);
			SameGameBoard.shiftToTheLeft(monteCarloPosition, monteCarloXDim, monteCarloYDim);
			
			if (!playingMixedStrategy)
				move = getMixedPlayoutStrategyMove();
			else
				move=getMoveOfStyle(style);
			
		}
		
		gameRecord[gameRecordPosition]=-1;
		
		if (sameGame)
		{
			score+=sameGameFinalScore();
		}
		
		return score;
	}
	
	
	private static byte[] gameCopy;
	private static byte[] emptyBoard;
	private static byte[] moveCategoryBoard;
	private static int counter=1;
	private static int[] categoryFound;
	
	public static double RWeight = Parameters.RWeight;
	public static double TRWeight = Parameters.TRWeight; 
	public static double TCRWeight = Parameters.TCRWeight;
	public static double RPWeight = Parameters.RPWeight;
	public static double FRPWeight = Parameters.FRPWeight;
	public static double CMWeight = Parameters.CMWeight;
	
	private static int getMixedPlayoutStrategyMove()
	{
		/*int max = 0;
		int chosenColor=0;
		for (int i=0;i<monteCarloColors.length;i++)
		{
			if (monteCarloColors[i]>max)
			{
				chosenColor=i;
				max=monteCarloColors[i];
			}	
		}
		return getRandomKnowledgeMove(chosenColor);*/
		
		double total = RWeight + TRWeight + TCRWeight + RPWeight + FRPWeight + CMWeight;
		RWeight/=total;
		TRWeight/=total;
		TCRWeight/=total;
		RPWeight/=total;
		FRPWeight/=total;
		CMWeight/=total;
		
		double choice = r.nextDouble();
		if (choice<RWeight) return getRandomMove();
		choice-=RWeight;
		if (choice<TRWeight) return getRandomKnowledgeMove(r.nextInt(monteCarloNumberOfColors));
		choice-=TRWeight;
		if (choice<TCRWeight)
		{	
			int max = 0;
			int chosenColor=0;
			for (int i=0;i<monteCarloColors.length;i++)
			{
				if (monteCarloColors[i]>max)
				{
					chosenColor=i;
					max=monteCarloColors[i];
				}	
			}
			return getRandomKnowledgeMove(chosenColor);
		}
		choice-=TCRWeight;
		if (choice<RPWeight) return getRealisationProbabilityMove();
		choice-=RPWeight;
		if (choice<FRPWeight) return getFastRealisationProbabilityMove();
		choice-=FRPWeight;
		return getConnectionMaximizationMove();
	}
	
	private static int choosePlayoutStyle()
	{
		double total = Parameters.RWeight2 + Parameters.TRWeight2 + Parameters.TCRWeight2 + Parameters.RPWeight2 + Parameters.FRPWeight2 + Parameters.CMWeight2;
		double RWeight = Parameters.RWeight2 / total;
		double TRWeight = Parameters.TRWeight2 / total;
		double TCRWeight = Parameters.TCRWeight2 / total;
		double RPWeight =Parameters.RPWeight2 / total;
		double FRPWeight = Parameters.FRPWeight2 / total;
		double CMWeight = Parameters. CMWeight2 / total;
		
		double choice = r.nextDouble();
		if (choice<RWeight) return 1;
		choice-=RWeight;
		if (choice<TRWeight) return 2;
		choice-=TRWeight;
		if (choice<TCRWeight) return 3;
		choice-=TCRWeight;
		if (choice<RPWeight) return 4;
		choice-=RPWeight;
		if (choice<FRPWeight) return 5;
		choice-=FRPWeight;
		return 6;
	}
	
	private static int getMoveOfStyle(int style)
	{
		if (style == 1) return getRandomMove();
		if (style == 2) return getRandomKnowledgeMove(r.nextInt(monteCarloNumberOfColors));
		if (style == 3) 
		{
			int max = 0;
			int chosenColor=0;
			for (int i=0;i<monteCarloColors.length;i++)
			{
				if (monteCarloColors[i]>max)
				{
					chosenColor=i;
					max=monteCarloColors[i];
				}	
			}
			return getRandomKnowledgeMove(chosenColor);
		}
		if (style == 4) return getRealisationProbabilityMove();
		if (style == 5) return getFastRealisationProbabilityMove();
		if (style == 6) return getConnectionMaximizationMove();
		return -1;
	}
	
	private static byte[] localGameCopy;
	private static int[] bestMoves = new int[225];
	private static int bestMoveCounter=0;
	
	public static int getConnectionMaximizationMove()
	{
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1) return -1;
		if (gameCopy==null) gameCopy = new byte[monteCarloXDim*monteCarloYDim];
		bestMoveCounter=0;
		System.arraycopy(monteCarloPosition, 0, gameCopy, 0, monteCarloXDimTimesMonteCarloYDim);
		
		if (localGameCopy==null) localGameCopy = new byte[gameCopy.length];
		
		int bestImprovement=Integer.MIN_VALUE;
		
		for (int pos=0;pos<monteCarloPosition.length;pos++)
		{
			if (gameCopy[pos]!=-1)
			{
				System.arraycopy(monteCarloPosition, 0, localGameCopy, 0, monteCarloXDimTimesMonteCarloYDim);
			
				int b = SameGameBoard.makeMove(gameCopy, pos, pos%monteCarloXDim, pos/monteCarloXDim, gameCopy[pos]);
				
				if (b>1)
				{
					SameGameBoard.makeMove(localGameCopy, pos, pos%monteCarloXDim, pos/monteCarloXDim, localGameCopy[pos]);
					
					//SameGameBoard.println(localGameCopy, 15, 15);
					
					int connectionsBefore = countConnections(); 
					
					//System.out.println("ConnectionsBefore: "+connectionsBefore);
					SameGameBoard.dropDownStones(localGameCopy, monteCarloXDim, monteCarloYDim);
					int connectionsAfter = countConnections();
					//SameGameBoard.println(localGameCopy, 15, 15);
					//System.out.println("ConnectionsAfter: "+connectionsAfter);
					//System.out.println("-------------------------------");
					int diff=connectionsAfter-(connectionsBefore+(b-1));
					if (diff>bestImprovement)
					{
						bestMoveCounter=1;
						bestMoves[0]=pos;
						bestImprovement=diff;
					}
					else if (diff==bestImprovement)
					{
						bestMoves[bestMoveCounter]=pos;
						bestMoveCounter++;
					}
				}
			}
		}

		if (bestImprovement==Integer.MIN_VALUE) return -1;
		return bestMoves[r.nextInt(bestMoveCounter)];
	}
	
	private static int countConnections()
	{
		int connections = 0;
		int[] changedRows = SameGameBoard.changedRows;
		int mostLeftRow = -1;
		int mostRightRow = -1;
		int deepestChange = 0;
		for (int i=0;i<changedRows.length;i++)
		{
			if (changedRows[i]>deepestChange) deepestChange=changedRows[i];
			if (changedRows[i]>0)
			{
				mostRightRow=i;
				if (mostLeftRow==-1)
				{
					mostLeftRow=i-1;
					if (mostLeftRow==-1) mostLeftRow=0;
				}
			}
		}
		if (deepestChange<monteCarloYDimMinus1) deepestChange++;
		
		for (int i=mostLeftRow;i<=mostRightRow;i++)
		{
			for (int pos = i+(deepestChange*monteCarloXDim);pos>=0;pos-=monteCarloXDim)
			{
				int c = localGameCopy[pos];
				if (c!=-1)
				{
					if ((pos>=monteCarloXDim)&&(localGameCopy[pos-monteCarloXDim]==c)) connections++;
					if ((i<monteCarloXDimMinus1)&&(localGameCopy[pos+1]==c)) connections++;
				}
			}
		}
		return connections;
	}
	
	public static boolean terminal=false;
	//public static boolean useTabuColorRandom = !true;
	//public static boolean useRealisationProbabilities = true;
	
	public static double RPTCRratio = 0.1;  
	
	private static final boolean useRandomRP = !true;
	private static final boolean useTabuColorRandomRP = true;
	private static final boolean useGamePhases = !true;
	
	private static int getRealisationProbabilityMove()
	{
		/*if ((useRandomRP)&&(useRealisationProbabilities))
		{
			Exception e =  new Exception("Do not know which Realisation Probabilities to use.");
			e.printStackTrace();
			System.exit(0);
		}*/
		
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1) return -1;
		if (gameCopy==null) gameCopy = new byte[monteCarloXDim*monteCarloYDim];
		if (emptyBoard==null)
		{
			emptyBoard = new byte[monteCarloXDim*monteCarloYDim];
			for (int i=0;i<emptyBoard.length;i++) emptyBoard[i]=-1;
		}
		if (moveCategoryBoard==null) moveCategoryBoard = new byte[monteCarloXDim*monteCarloYDim];
		System.arraycopy(emptyBoard, 0, moveCategoryBoard, 0, monteCarloXDimTimesMonteCarloYDim);
		if (categoryFound==null) categoryFound= new int[monteCarloXDimTimesMonteCarloYDim];
		System.arraycopy(monteCarloPosition, 0, gameCopy, 0, monteCarloXDimTimesMonteCarloYDim);
		
		int bloksLeft=0;
		
		//System.out.println("Position:");
		//SameGameBoard.println(monteCarloPosition, 15, 15);
		
		int highestCategory = 0;
		boolean canMove = false;
		
		for (int i=0;i<monteCarloXDimTimesMonteCarloYDim;i++)
		{
			if (gameCopy[i]!=-1)
			{
				//System.out.println("Position before :");
				//SameGameBoard.println(gameCopy, 15, 15);
				int b = SameGameBoard.makeMove(gameCopy, i, i%monteCarloXDim, i/monteCarloXDim, gameCopy[i]);
				moveCategoryBoard[i]=(byte)b;
				categoryFound[b]=counter;
				bloksLeft+=b;
				if (b>highestCategory) highestCategory=b;
				if (b>1) canMove=true;
				//System.out.println("Position after :");
				//SameGameBoard.println(gameCopy, 15, 15);
			}
		}
		
		if (!canMove) return -1;
		
		//System.out.println("Bloks left: "+bloksLeft);
		
		/*System.out.println("Categories:");
		for (int i=0;i<10;i++)
		{
			System.out.println(i+" "+categoryFound[i]);
		}
		System.out.println("Categories found at:");
		SameGameBoard.println(moveCategoryBoard, 15, 15);*/
		
		
		double total=0;
		for (int i=2;i<=highestCategory;i++)
		{
			if (categoryFound[i]==counter)
			{
				if (useGamePhases)
				{
					if (useRandomRP)
					{
						if (bloksLeft>150) total+=RealisationProbabilities.beginGameRandom[i];
						else if (bloksLeft>75) total+=RealisationProbabilities.middleGameRandom[i];
						else total+=RealisationProbabilities.endGameRandom[i];
					}
					if (useTabuColorRandomRP)
					{
						if (bloksLeft>150) total+=RealisationProbabilities.beginGameTabuColorRandom[i];
						else if (bloksLeft>75) total+=RealisationProbabilities.middleGameTabuColorRandom[i];
						else total+=RealisationProbabilities.endGameTabuColorRandom[i];
					}
				}
				else
				{
					if (useRandomRP)
						total+=RealisationProbabilities.totalRandom[i];
					if (useTabuColorRandomRP)
						total+=RealisationProbabilities.totalTabuColorRandom[i];
				}
			}
		}
		
		//System.out.println("Total: "+total);
		
		double choice = r.nextDouble()*total;
		
		//System.out.println("Random Choice: "+choice);
		
		int chosenCategory=0;
		for (int i=2;(i<=highestCategory)&&(choice>0);i++)
		{
			if (categoryFound[i]==counter)
			{
				if (useGamePhases)
				{
					if (useRandomRP)
					{
						if (bloksLeft>150) choice-=RealisationProbabilities.beginGameRandom[i];
						else if (bloksLeft>75) choice-=RealisationProbabilities.middleGameRandom[i];
						else choice-=RealisationProbabilities.endGameRandom[i];
					}
					if (useTabuColorRandomRP)
					{
						if (bloksLeft>150) choice-=RealisationProbabilities.beginGameTabuColorRandom[i];
						else if (bloksLeft>75) choice-=RealisationProbabilities.middleGameTabuColorRandom[i];
						else choice-=RealisationProbabilities.endGameTabuColorRandom[i];
					}
				}
				else
				{
					if (useRandomRP)
						choice-=RealisationProbabilities.totalRandom[i];
					if (useTabuColorRandomRP)
						choice-=RealisationProbabilities.totalTabuColorRandom[i];
				}
				
				if (choice<0) chosenCategory=i;
			}
		}
		
		//System.out.println("Chosen Category: "+chosenCategory);
		
		int pos;
		randomMoveX=r.nextInt(monteCarloXDim);
		randomMoveY=r.nextInt(monteCarloYDim);
		pos = randomMoveX+(randomMoveY*monteCarloXDim);
		
		/*System.out.println("Board: ");
		SameGameBoard.println(monteCarloPosition, 15, 15);
		System.out.println("Category Board: ");
		SameGameBoard.println(moveCategoryBoard, 15, 15);
		System.out.println("Looking for: "+(chosenCategory+1));*/
		
		while (true)
		{
			if (moveCategoryBoard[pos]==chosenCategory) break;
			
			randomMoveY--;
			pos-=monteCarloXDim;
			
			if ((pos<0)||(monteCarloPosition[pos]==-1))
			{
				randomMoveX++;
				if (randomMoveX==monteCarloXDim) randomMoveX=0;
				randomMoveY=monteCarloYDimMinus1;
				pos = randomMoveX+(randomMoveY*monteCarloXDim);
			}
		}
		counter++;
		
		//System.out.println("Returning: ["+pos%15+"/"+pos/15+"]");
		//System.exit(0);
		return pos;
	}
	
	private static int getFastRealisationProbabilityMove()
	{
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1) return -1;
		//if (SameGameBoard.isEmpty(monteCarloPosition, monteCarloXDim, monteCarloYDim)) return -1;
		
		if (gameCopy==null) gameCopy = new byte[monteCarloXDim*monteCarloYDim];
		System.arraycopy(monteCarloPosition, 0, gameCopy, 0, monteCarloXDimTimesMonteCarloYDim);
		
		int pos, startPos=-1;
		randomMoveX=r.nextInt(monteCarloXDim);
		randomMoveY=r.nextInt(monteCarloYDim);
		pos = randomMoveX+(randomMoveY*monteCarloXDim);
		startPos=-1;
		
		int move = -1;
		
		while (true)
		{
			if (gameCopy[pos]!=-1)
			{
				startPos=pos;
				int b = SameGameBoard.makeMove(gameCopy, pos, randomMoveX, randomMoveY, gameCopy[pos]);
				
				double chance = 0;
				
				if (useRandomRP) chance=RealisationProbabilities.totalRandom[b];
				else chance=RealisationProbabilities.totalTabuColorRandom[b];
			
				if ((b>1)&&(r.nextDouble()<chance)) return pos;
				
				if ((move==-1)&&(b>1)) move=pos;
			}
			
			randomMoveY--;
			pos-=monteCarloXDim;
			
			if ((pos<0)||(monteCarloPosition[pos]==-1))
			{
				randomMoveX++;
				if (randomMoveX==monteCarloXDim) randomMoveX=0;
				randomMoveY=monteCarloYDimMinus1;
				pos = randomMoveX+(randomMoveY*monteCarloXDim);
			}
			if (startPos==-1) startPos=pos;
			else if (startPos==pos)
			{
				return move;
			}
		}
	}
	
	private static double determineChanceToPlayChosenColor(int stonesLeft)
	{
		//return (1/(stonesLeft+1.0));
		return 0;
		/*if (stonesLeft > 100) return 0;
		if (stonesLeft > 75) return 0.01;
		if (stonesLeft > 50) return 0.1;
		if (stonesLeft > 25) return 0.5;
		return 1;*/
	}
	
	public static short sameGameFinalScore()
	{
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1)
			return 1000;
		
		short score=0;
		
		/*SameGameBoard.println(monteCarloPosition, 15, 15);
		System.out.println("MonteCarloColors:");
		for (int i=0;i<monteCarloColors.length;i++)
			System.out.println("Color "+i+": "+monteCarloColors[i]);*/
		
		for (int i=monteCarloColors.length-1;i>=0;i--) 
		{
			int amount = monteCarloColors[i]-2;
			if (amount>0) score-=squares[amount];
		}
		return score;
	}
	
	public static int randomMoveX=0;
	public static int randomMoveY=0;
	public static int randomMoveColor;
	
	private static int getRandomMove()
	{
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1) return -1;
		//if (SameGameBoard.isEmpty(monteCarloPosition, monteCarloXDim, monteCarloYDim)) return -1;
		
		int pos, startPos=-1;
		randomMoveX=r.nextInt(monteCarloXDim);
		randomMoveY=r.nextInt(monteCarloYDim);
		pos = randomMoveX+(randomMoveY*monteCarloXDim);
		
		randomMoveColor = monteCarloPosition[pos];
		if (randomMoveColor!=-1)
		{
			if (randomMoveX>=1) if (randomMoveColor==monteCarloPosition[pos-1]) return pos;
			if (randomMoveX<monteCarloXDimMinus1) if (randomMoveColor==monteCarloPosition[pos+1]) return pos;
			if (randomMoveY>=1) if (randomMoveColor==monteCarloPosition[pos-monteCarloXDim]) return pos;
			if (randomMoveY<monteCarloYDimMinus1) if (randomMoveColor==monteCarloPosition[pos+monteCarloXDim]) return pos;
		}
		
		int count = 0;
		
		while (true)
		{
			count++;
			/*if (count>260)
			{
				SameGameBoard.println(monteCarloPosition, monteCarloXDim, monteCarloYDim);
				count=count+1;
			}*/
			//System.out.println("RandomX: "+randomX+" RandomY: "+randomY+" Pos: "+pos);
			
			randomMoveY--;
			pos-=monteCarloXDim;
			
			if ((pos<0)||(monteCarloPosition[pos]==-1))
			{
				randomMoveX++;
				if (randomMoveX==monteCarloXDim) randomMoveX=0;
				randomMoveY=monteCarloYDimMinus1;
				pos = randomMoveX+(randomMoveY*monteCarloXDim);
			}
			randomMoveColor = monteCarloPosition[pos];
			
			if (randomMoveColor!=-1)
			{
				if (startPos==-1)
				{
					startPos=pos;
				}
				else
				if (startPos==pos)
				{
					return -1;
				}
				
				if (randomMoveX>=1) if (randomMoveColor==monteCarloPosition[pos-1]) return pos;
				if (randomMoveX<monteCarloXDimMinus1) if (randomMoveColor==monteCarloPosition[pos+1]) return pos;
				if (randomMoveY>=1) if (randomMoveColor==monteCarloPosition[pos-monteCarloXDim]) return pos;
				if (randomMoveY<monteCarloYDimMinus1) if (randomMoveColor==monteCarloPosition[pos+monteCarloXDim]) return pos;
			}
			
		}
	}
	
	
	
	public static double chanceOfPlayingChosenColor=Parameters.chanceOfPlayingChosenColor;
	private static int getRandomKnowledgeMove(int chosenColor)
	{
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1) return -1;
		
		int moveInChosenColor=-1;
		
		int pos, startPos=-1;
		randomMoveX=r.nextInt(monteCarloXDim);
		randomMoveY=r.nextInt(monteCarloYDim);
		//pos = randomMoveX+(randomMoveY*monteCarloXDim);
		pos=positionArray[randomMoveX][randomMoveY];
		
		randomMoveColor = monteCarloPosition[pos];
		
		if (randomMoveColor!=-1)
		{
			if (((randomMoveX>=1)&&(randomMoveColor==monteCarloPosition[pos-1]))||
			((randomMoveX<monteCarloXDimMinus1)&&(randomMoveColor==monteCarloPosition[pos+1]))||
			((randomMoveY>=1)&&(randomMoveColor==monteCarloPosition[pos-monteCarloXDim]))||
			((randomMoveY<monteCarloYDimMinus1)&&(randomMoveColor==monteCarloPosition[pos+monteCarloXDim])))
			{
				if (randomMoveColor!=chosenColor) return pos;
				else
				{
					if (r.nextDouble()<chanceOfPlayingChosenColor) return pos;
					else moveInChosenColor=pos;
				}
			}
		}
		
		int count = 0;
		
		while (true)
		{
			count++;
			
			randomMoveY--;
			pos-=monteCarloXDim;
			
			if ((pos<0)||(monteCarloPosition[pos]==-1))
			{
				randomMoveX++;
				if (randomMoveX==monteCarloXDim) randomMoveX=0;
				randomMoveY=monteCarloYDimMinus1;
				pos=positionArray[randomMoveX][randomMoveY];
				//pos = randomMoveX+(randomMoveY*monteCarloXDim);
			}
			randomMoveColor = monteCarloPosition[pos];

			if (randomMoveColor!=-1)
			{
				if (startPos==-1)
				{
					startPos=pos;
				}
				else
				if (startPos==pos)
				{
					randomMoveX=moveInChosenColor%monteCarloXDim;
					randomMoveY=moveInChosenColor/monteCarloXDim;
					randomMoveColor=chosenColor;
					//if (randomMoveColor==44) System.out.println("HERE 3");
					return moveInChosenColor;
				}
				
				if (((randomMoveX>=1)&&(randomMoveColor==monteCarloPosition[pos-1]))||
				((randomMoveX<monteCarloXDimMinus1)&&(randomMoveColor==monteCarloPosition[pos+1]))||
				((randomMoveY>=1)&&(randomMoveColor==monteCarloPosition[pos-monteCarloXDim]))||
				((randomMoveY<monteCarloYDimMinus1)&&(randomMoveColor==monteCarloPosition[pos+monteCarloXDim])))
				{
					if (randomMoveColor!=chosenColor) return pos;
					else
					{
						if (r.nextDouble()<chanceOfPlayingChosenColor) return pos;
						else moveInChosenColor=pos;
					}
				}
			}
			
		}
	}
	
	private static int[][] positionArray = new int[15][15];
	
	public static void setVariables(int xDim, int yDim, int scoreMode, int colors)
	{
		monteCarloNumberOfColors=colors;
		monteCarloXDim=xDim;
		monteCarloYDim=yDim;
		monteCarloXDimMinus1 = monteCarloXDim-1;
		monteCarloYDimMinus1 = monteCarloYDim-1;
		monteCarloScoreMode=scoreMode;
		monteCarloXDimTimesMonteCarloYDim=monteCarloXDim*monteCarloYDim;
		monteCarloYDimMinus1TimesMonteCarloXDim=monteCarloYDimMinus1*monteCarloXDim;
		
		for (int i=0;i<15;i++)
			for (int j=0;j<15;j++)
				positionArray[i][j]=i+(j*monteCarloXDim);
	}
	
	public static int getRandomMove(byte[] monteCarloPosition)
	{
		if (monteCarloPosition[monteCarloYDimMinus1TimesMonteCarloXDim]==-1) return -1;
		//if (SameGameBoard.isEmpty(monteCarloPosition, monteCarloXDim, monteCarloYDim)) return -1;
		
		int pos, startPos=-1;
		randomMoveX=r.nextInt(monteCarloXDim);
		randomMoveY=r.nextInt(monteCarloYDim);
		pos = randomMoveX+(randomMoveY*monteCarloXDim);
		
		randomMoveColor = monteCarloPosition[pos];
		if (randomMoveColor!=-1)
		{
			if (randomMoveX>=1) if (randomMoveColor==monteCarloPosition[pos-1]) return pos;
			if (randomMoveX<monteCarloXDimMinus1) if (randomMoveColor==monteCarloPosition[pos+1]) return pos;
			if (randomMoveY>=1) if (randomMoveColor==monteCarloPosition[pos-monteCarloXDim]) return pos;
			if (randomMoveY<monteCarloYDimMinus1) if (randomMoveColor==monteCarloPosition[pos+monteCarloXDim]) return pos;
		}
		
		int count = 0;
		
		while (true)
		{
			count++;
			randomMoveY--;
			pos-=monteCarloXDim;
			
			if ((pos<0)||(monteCarloPosition[pos]==-1))
			{
				randomMoveX++;
				if (randomMoveX==monteCarloXDim) randomMoveX=0;
				randomMoveY=monteCarloYDimMinus1;
				pos = randomMoveX+(randomMoveY*monteCarloXDim);
			}
			randomMoveColor = monteCarloPosition[pos];
			if (randomMoveColor!=-1)
			{
				if (startPos==-1)
				{
					startPos=pos;
				}
				else
				if (startPos==pos)
				{
					return -1;
				}
				
				if (randomMoveX>=1) if (randomMoveColor==monteCarloPosition[pos-1]) return pos;
				if (randomMoveX<monteCarloXDimMinus1) if (randomMoveColor==monteCarloPosition[pos+1]) return pos;
				if (randomMoveY>=1) if (randomMoveColor==monteCarloPosition[pos-monteCarloXDim]) return pos;
				if (randomMoveY<monteCarloYDimMinus1) if (randomMoveColor==monteCarloPosition[pos+monteCarloXDim]) return pos;
			}
			
		}
	}
	
}

