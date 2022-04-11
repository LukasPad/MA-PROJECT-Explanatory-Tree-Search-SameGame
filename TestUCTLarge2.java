public class TestUCTLarge2 
{
	private static int millisecondsPerMove=Integer.MAX_VALUE;
	private static int mode = BoardPanel.SAMEGAME;
	
	private static int nrOfNodes=500000;
	
	public static void main(String[] args) 
	{
		int run=1;
		for (int z = 5;z<8;z++)
		{
			run=(int)Math.pow(2, z);
			System.out.println("Starting Test Run "+run+"x"+nrOfNodes/run+" nodes");
			
			UCTPlayer bot = new UCTPlayer();
			//MCTSPlayer bot = new MCTSPlayer();
			bot.output=false;
			bot.maxNumberOfNodes=nrOfNodes/run;
			
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
			System.out.println("Top Score weight: "+bot.topScoreWeight);
			System.out.println("Number of nodes: "+bot.maxNumberOfNodes);
			
			//bot.topScoreWeight=j*0.01;
			//bot.numberOfVisitsBeforeExpanding=j;
			//bot.UCTConstant=j*0.01;
			//bot.DeviationConstant=(int)Math.pow(2, j);
			//bot.MCTSConstant=Math.pow(2, j);
			//bot.MCTSConstant=1000000000;
			
		
			
			
			long startTime = System.currentTimeMillis();
			int scoreTotal=0;
			for (int j=0;j<run;j++)
			{
				int scoreRun = 0;
				for (int i=0;i<set.getNumberOfPositions();i++)
				//for (int i=0;i<1;i++)
				{
		
					//long heapFreeSize = Runtime.getRuntime().freeMemory();
					//System.out.println("Free: "+heapFreeSize);
					
					
					byte[] p = set.getPosition(i);
					
					bot.getMove(p, 15, 15, BoardPanel.SAMEGAME, millisecondsPerMove);
					
					averageaveragedepth=(i*averageaveragedepth+bot.averageDepth)/(i+1.0);
					averagedeepestnode=(i*averagedeepestnode+bot.deepestNode)/(i+1.0);
					
					//System.out.println("Position nr. "+(i+1)+"  Score: "+bot.bestScore);
					//avrg[i]=((avrg[i]*j)+bot.bestScore)/(j+1.0);
					if (bot.bestScore>max[i]) max[i]=bot.bestScore;
					scoreTotal+=bot.bestScore;
					scoreRun+=bot.bestScore;
					//max[i]=bot.bestScore;
					Runtime.getRuntime().gc();
					//System.exit(0);
					
					if (scoreTotal>bestRun) bestRun=scoreRun;
					
					//System.out.println("MCTS Constant:"+Math.pow(2, j));
					//System.out.println("Deviation Constant:"+Math.pow(2, j));
					//System.out.println("Total score: "+scoreTotal);
					
					//knowledgeParameter+=0.0025;
				}
			}
			
			long endTime = System.currentTimeMillis();
			System.out.println("Total time: "+(int)((endTime-startTime)/1000.0)+" seconds");
			double averageTotal=0;
			double bestCombinedRun=0;
			
			for (int k=0;k<set.getNumberOfPositions();k++) bestCombinedRun+=max[k];
			
			
			/*for (int i=0;i<20;i++)
			{
				System.out.println("Position nr. "+(i+1)+"  Average Score: "+avrg[i]+"  Best Score: "+max[i]);
				averageTotal+=avrg[i];
				bestCombinedRun+=max[i];
			}*/
			double average = scoreTotal/(set.getNumberOfPositions()*run);
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
			System.out.println();
			System.out.println();
			
			
			run*=2;
		}
	}
}
