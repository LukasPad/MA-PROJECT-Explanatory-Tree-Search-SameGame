public class TestUCTLarge 
{
	private static int millisecondsPerMove=Integer.MAX_VALUE;
	private static int mode = BoardPanel.SAMEGAME;
	
	public static void main(String[] args) 
	{
		System.out.println("Starting Test Run");
		
		//UCTPlayer bot = new UCTPlayer();
		MCTSPlayer bot = new MCTSPlayer();
		bot.output=false;
		
		//long totalNodes=0;
		
		TestSet set = new TestSet();
		set.loadTestSet();
		
		double averageaveragedepth = 0;
		double averagedeepestnode = 0;
		
		//double[] avrg = new double[20]; 
		double[] max = new double[set.getNumberOfPositions()];
		double bestRun=0;
		double knowledgeParameter=0;
		Evaluation.chanceOfPlayingChosenColor=knowledgeParameter;
		
		System.out.println("Settings:");
		System.out.println("UCT Constant: "+bot.UCTConstant);
		System.out.println("Deviation Constant: "+bot.DeviationConstant);
		System.out.println("Number of visited nodes before expanding: "+bot.numberOfVisitsBeforeExpanding);
		System.out.println("Chance of playing chosen Color: 0");//"+knowledgeParameter);
		//System.out.println("Top Score weight: "+bot.topScoreWeight);
		System.out.println("Number of nodes: "+bot.maxNumberOfNodes);
		
		//bot.topScoreWeight=j*0.01;
		//bot.numberOfVisitsBeforeExpanding=j;
		//bot.UCTConstant=j*0.01;
		//bot.DeviationConstant=(int)Math.pow(2, j);
		//bot.MCTSConstant=Math.pow(2, j);
		//bot.MCTSConstant=1000000000;
		
		
		
		int scoreTotal=0;
		long startTime = System.currentTimeMillis();
		for (int i=0;i<set.getNumberOfPositions();i++)
		//for (int i=0;i<1;i++)
		{

			//long heapFreeSize = Runtime.getRuntime().freeMemory();
			//System.out.println("Free: "+heapFreeSize);
			
			
			byte[] p = set.getPosition(i);
			
			//bot.getMove(p, 15, 15, BoardPanel.SAMEGAME, millisecondsPerMove);
			bot.playGame(p, 15, 15, BoardPanel.SAMEGAME, 30000);
			
			averageaveragedepth=(i*averageaveragedepth+bot.averageDepth)/(i+1.0);
			averagedeepestnode=(i*averagedeepestnode+bot.deepestNode)/(i+1.0);
			
			System.out.println("Position nr. "+(i+1)+"  Score: "+bot.gameScore);
			//avrg[i]=((avrg[i]*j)+bot.bestScore)/(j+1.0);
			//if (bot.bestScore>max[i]) max[i]=bot.bestScore;
			scoreTotal+=bot.gameScore;
			max[i]=bot.gameScore;
			Runtime.getRuntime().gc();
			//System.exit(0);
			
			if (scoreTotal>bestRun) bestRun=scoreTotal;
			
			//System.out.println("MCTS Constant:"+Math.pow(2, j));
			//System.out.println("Deviation Constant:"+Math.pow(2, j));
			//System.out.println("Total score: "+scoreTotal);
			
			//knowledgeParameter+=0.0025;
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total time: "+(int)((endTime-startTime)/1000.0)+" seconds");
		double averageTotal=0;
		double bestCombinedRun=0;
		/*for (int i=0;i<20;i++)
		{
			System.out.println("Position nr. "+(i+1)+"  Average Score: "+avrg[i]+"  Best Score: "+max[i]);
			averageTotal+=avrg[i];
			bestCombinedRun+=max[i];
		}*/
		System.out.println();
		double average = scoreTotal/set.getNumberOfPositions();
		System.out.println("Average: "+average+"  Best Run: "+bestRun+"  Best Combined Score: "+bestCombinedRun);
		
		double deviation=0;
		for (int i=0;i<set.getNumberOfPositions();i++)
		{
			deviation+=(max[i]-average)*(max[i]-average);
		}
		deviation/=(set.getNumberOfPositions()-1);
		deviation=Math.sqrt(deviation);
		System.out.println("Deviation: "+deviation);
		
		System.out.println("Average Depth: "+averageaveragedepth);
		System.out.println("Average Deepest Node: "+averagedeepestnode);
		
		System.exit(0);
	}
}
