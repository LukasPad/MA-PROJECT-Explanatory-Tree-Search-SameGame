package OldCode;

import java.util.Random;

public class TestUCT 
{
	private static int millisecondsPerMove=100000;
	private static int mode = BoardPanel.SAMEGAME;
	private static double learningRate = 1; 
	private static double oldC = UCTPlayer.UCTConstant;
	private static double oldD = UCTPlayer.DeviationConstant;
	private static double oldScore = -1;
	private static double newScore = 0;
	private static Random r = new Random();
	
	public static void main(String[] args) 
	{
		//RealisationProbabilities.initialize();
		System.out.println("Starting Test Run");

		Random r = new Random();
		
		MCTSPlayer bot = new MCTSPlayer();
		//UCTPlayer bot = new UCTPlayer();
		
		bot.output=false;
		bot.maxNumberOfNodes=500000;
		
		
		double[] avrg = new double[20]; 
		double[] max = new double[20];
		int[][] primaryVariation = new int[20][225];
		double bestRun=0;
		
		for (int i=0;i<20;i++) max[i]=Integer.MIN_VALUE;
		
		System.out.println("Settings:");
		System.out.println("UCT Constant: "+bot.UCTConstant);
		System.out.println("Deviation Constant: "+bot.DeviationConstant);
		System.out.println("Number of visited nodes before expanding: "+bot.numberOfVisitsBeforeExpanding);
		System.out.println("Chance of playing chosen Color: "+Parameters.chanceOfPlayingChosenColor);
		System.out.println("Top Score weight: "+bot.topScoreWeight);
		System.out.println("Number of nodes: "+UCTNode.totalNodes);
		
		long startTime = System.currentTimeMillis();
		int runscore=0;
		for (int i=0;i<positions.length;i++)
		//for (int i=0;i<1;i++)
		{

			int runsPerPosition = 50;
			int average=0;
			
			//System.out.println("Position:");
			//SameGameBoard.println(positions[i], 15, 15);
			
			for (int j=0;j<runsPerPosition;j++)
			{

				byte[] p = positions[i];
				
				int[] g = bot.playGame(p, 15, 15, BoardPanel.SAMEGAME, millisecondsPerMove);
				int score = bot.gameScore;
				
				System.out.print("Position nr. "+(i+1)+"  Score: "+score);
				int counter=1;
				/*while (bot.primaryVariant[counter]!=-1)
				{
					System.out.print(" ["+bot.primaryVariant[counter]%15+","+bot.primaryVariant[counter]/15+"]");
					counter++;
				}*/
				System.out.println();
				avrg[i]=((avrg[i]*j)+score)/(j+1.0);
				if (score>max[i])
				{
					max[i]=score;
					System.arraycopy(g, 0, primaryVariation[i], 0, 225);
					//primareVariation[i]=bot.primaryVariant;
				}
				runscore+=score;
				Runtime.getRuntime().gc();
		
			}
			if (runscore>bestRun) bestRun=runscore;
			

		}
		long endTime = System.currentTimeMillis();
		
		
		double averageTotal=0;
		double bestCombinedRun=0;
		for (int i=0;i<20;i++)
		{
			System.out.print("Position nr. "+(i+1)+"  Average Score: "+avrg[i]+"  Best Score: "+max[i]);
			int counter=0;
			System.out.print("  Primary Variation:");
			while (counter<225 && primaryVariation[i][counter]!=-1)
			{
				System.out.print(" ["+primaryVariation[i][counter]%15+","+primaryVariation[i][counter]/15+"]");
				counter++;
			}
			System.out.println();
			averageTotal+=avrg[i];
			bestCombinedRun+=max[i];
		}
		System.out.println();
		System.out.println("Total: "+averageTotal+"  Best Run: "+bestRun+"  Best Combined Score: "+bestCombinedRun);
		System.out.println("Total time: "+(int)((endTime-startTime)/1000.0)+" seconds");
		System.exit(0);
	}
	
	private static byte[][] positions = {{3,1,1,4,1,0,4,0,4,4,1,1,0,2,3,
		  3,3,2,0,4,4,1,3,1,2,0,0,4,0,4,
	      0,2,3,4,3,0,3,0,0,3,4,4,1,1,1,
	      2,3,4,0,2,3,0,2,4,4,4,3,0,2,3,
	      1,2,1,3,1,2,0,1,2,1,0,3,4,0,1,
	      0,4,4,3,0,3,4,2,2,2,0,2,3,4,0,
	      2,4,3,4,2,3,1,1,1,3,4,1,0,3,1,
	      1,0,0,4,0,3,1,2,1,0,4,1,3,3,1,
	      1,3,3,2,0,4,3,1,3,0,4,1,0,0,3,
	      0,3,3,4,2,3,0,0,2,1,2,3,4,0,1,
	      0,4,1,2,0,1,3,4,3,3,4,1,4,0,4,
	      2,2,3,1,0,4,0,1,2,4,1,3,3,0,1,
	      3,3,0,2,3,2,1,4,3,1,3,0,2,1,3,
	      1,0,3,2,1,4,4,4,4,0,4,2,1,3,4,
	      1,0,1,0,1,1,2,2,1,0,0,1,4,3,2},
		 
	      {3,3,0,1,0,2,1,2,3,2,3,1,1,1,0,
		  4,1,3,4,0,3,3,2,2,4,0,2,4,0,0,
		  2,3,2,2,0,3,1,0,4,4,0,2,4,0,4,
		  0,3,4,4,2,2,1,3,3,1,3,0,3,3,4,
		  0,0,2,1,2,1,3,4,3,2,1,2,3,1,4,
		  1,2,4,2,0,0,0,1,1,1,0,0,2,4,4,
		  1,0,3,3,3,2,1,0,4,2,4,1,4,3,0,
		  4,4,3,3,0,2,3,3,4,3,0,3,0,0,4,
		  3,3,3,1,4,3,3,3,0,4,2,0,3,2,0,
		  2,4,1,1,1,1,4,0,0,3,0,4,0,4,3,
		  3,3,0,1,4,1,2,1,1,0,3,4,2,1,0,
		  2,2,3,3,2,0,4,3,3,4,0,4,3,3,1,
		  0,1,3,2,1,2,1,1,0,2,4,1,4,0,3,
		  4,1,4,0,2,1,3,1,3,1,4,0,1,0,3,
		  1,3,2,3,2,2,4,2,2,4,3,0,3,1,1},
		  
		  {4,2,4,3,1,0,3,3,2,2,4,3,1,4,2,
		  3,0,3,4,0,3,3,3,2,4,4,3,1,3,3,
		  2,0,4,4,0,1,2,2,2,3,4,0,4,4,0,
		  0,4,3,0,0,2,4,2,1,2,0,3,2,4,2,
		  0,2,0,2,0,1,1,3,2,1,1,2,3,4,0,
		  1,0,1,0,4,3,3,3,4,2,2,2,3,4,1,
		  2,3,4,3,4,2,2,4,2,4,3,4,4,0,1,
		  4,2,3,2,2,0,1,2,4,3,3,0,0,2,1,
		  3,4,4,3,0,4,3,4,1,0,0,2,1,4,3,
		  4,0,1,3,1,0,2,3,0,2,0,2,3,0,1,
		  4,2,0,0,0,2,2,1,0,2,3,1,1,3,1,
		  0,3,1,1,3,3,2,1,2,0,0,4,2,4,1,
		  2,1,4,4,4,0,3,3,4,2,0,0,2,0,0,
		  1,0,4,4,0,1,3,2,4,0,4,2,0,0,1,
		  2,2,2,2,3,3,0,4,3,3,4,0,4,1,2},
		  
		  {4,2,2,4,1,3,3,2,4,0,4,2,3,4,2,
		  2,0,2,1,2,1,0,1,2,1,1,3,0,4,2,
		  0,2,3,2,0,0,4,1,0,4,3,0,0,3,2,
		  2,2,3,1,1,0,0,1,0,1,1,4,3,0,0,
		  4,2,0,4,2,2,0,3,0,0,2,2,1,4,2,
		  1,4,3,3,2,3,0,4,4,0,0,2,2,3,0,
		  2,1,1,4,1,0,1,0,4,4,1,0,4,1,3,
		  3,3,0,2,1,3,1,1,4,0,2,3,3,3,3,
		  2,3,3,1,3,1,0,4,1,0,1,2,3,0,4,
		  3,2,1,1,3,4,0,2,4,2,4,2,0,2,0,
		  0,3,0,1,4,0,0,0,4,2,1,0,2,4,0,
		  2,0,1,4,2,3,1,4,2,0,1,0,3,4,2,
		  0,4,2,0,3,4,4,3,1,1,3,4,2,1,4,
		  4,2,4,0,4,3,0,2,2,4,1,4,3,4,1,
		  4,3,2,2,2,1,1,2,3,3,1,2,0,3,2},
		  
		  {3,4,4,3,2,3,2,1,3,4,1,2,3,3,2,
		  2,0,2,0,3,1,0,3,1,1,2,1,4,3,4,
		  1,3,1,0,3,1,3,2,3,4,0,0,1,4,1,
		  0,2,1,0,2,2,2,4,1,0,4,4,3,3,2,
		  2,3,1,3,0,4,0,2,3,0,1,4,4,2,3,
		  3,1,3,3,2,3,0,1,0,4,3,4,0,1,4,
		  4,4,4,2,2,3,0,0,0,1,0,1,2,1,3,
		  2,1,3,4,4,0,4,1,0,4,0,1,2,1,3,
		  3,4,3,1,2,0,1,3,3,0,1,4,2,0,0,
		  2,3,0,1,2,4,3,3,0,1,1,2,2,3,3,
		  4,4,1,0,3,3,4,4,2,2,4,2,0,3,0,
		  3,1,0,4,3,2,0,2,3,1,4,3,1,2,2,
		  2,2,3,0,2,4,1,3,0,3,2,1,3,4,2,
		  2,4,3,1,3,0,3,2,0,4,3,2,2,3,4,
		  0,4,2,2,2,3,2,0,1,1,4,0,1,3,3},
		  
		  {2,4,2,0,4,2,2,3,1,0,1,3,4,2,0,
		  2,3,3,2,3,1,3,3,0,1,4,1,0,0,1,
		  0,4,3,0,3,1,3,3,3,1,0,2,4,2,1,
		  3,0,1,0,1,2,3,0,0,2,1,1,1,4,4,
		  0,1,1,1,2,0,2,1,3,4,2,0,3,1,0,
		  1,1,1,4,1,1,0,0,1,1,4,1,1,2,1,
		  3,3,0,1,1,3,2,0,0,0,0,1,2,0,1,
		  0,3,0,3,4,0,1,1,2,1,4,2,1,0,2,
		  1,2,2,2,2,3,4,1,3,1,4,2,4,1,1,
		  2,2,0,3,3,0,2,2,3,3,2,2,1,0,3,
		  2,4,0,0,4,0,4,3,4,4,3,4,1,4,4,
		  2,1,2,3,1,1,2,2,1,0,3,1,4,4,0,
		  2,3,2,2,1,1,4,0,1,4,4,0,4,3,3,
		  1,1,3,0,3,1,4,3,4,1,0,4,1,1,4,
		  0,4,4,4,2,2,4,3,1,1,3,2,4,4,1},
		  
		  {3,4,0,3,1,2,0,1,3,1,2,4,1,1,3,
	      3,1,4,3,0,0,1,3,0,2,0,4,4,4,4,
	      0,4,3,2,1,1,0,2,2,1,3,4,0,2,3,
	      2,4,0,1,3,3,3,2,2,2,2,0,2,2,0,
	      0,4,0,0,2,1,0,1,4,3,3,3,1,0,2,
	      1,0,4,1,2,4,4,2,2,0,0,0,3,4,4,
	      4,2,1,3,1,2,0,1,3,4,2,2,1,3,2,
	      1,1,1,0,3,0,3,1,3,3,1,1,2,3,0,
	      1,2,4,3,1,4,1,1,1,0,2,3,0,3,3,
	      0,4,1,3,4,0,4,1,4,0,4,2,3,0,1,
	      0,4,3,4,2,4,1,3,1,3,0,4,3,0,0,
	      3,1,1,1,0,4,2,0,3,0,4,4,2,4,4,
	      4,0,4,3,1,4,1,3,2,3,0,1,0,1,1,
	      3,3,4,2,4,4,2,0,3,4,3,0,1,0,3,
	      0,2,3,4,4,2,4,1,0,0,0,4,2,4,0},
	      
	      {3,1,3,1,4,4,2,2,0,4,0,2,2,3,1,
	      1,1,2,3,3,1,0,2,2,2,0,2,4,1,1,
	      4,4,1,2,4,2,1,4,1,2,3,3,2,1,4,
	      1,0,2,2,3,4,1,3,2,2,1,3,4,3,2,
	      3,1,1,0,0,1,2,0,3,2,4,3,4,3,1,
	      1,1,3,0,4,2,1,3,0,1,2,4,4,0,3,
	      0,1,1,1,0,1,2,3,3,1,0,1,0,0,3,
	      2,3,2,3,1,1,1,2,4,0,2,1,2,3,3,
	      0,1,3,0,4,3,1,1,4,0,1,3,0,3,0,
	      1,3,3,0,3,0,0,0,3,4,1,3,0,0,0,
	      4,4,2,1,3,1,0,1,1,3,1,3,2,4,3,
	      0,3,0,2,3,1,1,1,3,3,1,2,3,2,2,
	      3,2,2,0,3,0,3,1,0,0,3,3,2,4,2,
	      0,1,2,2,0,2,4,4,1,3,4,3,1,1,4,
	      4,4,3,0,4,3,3,3,4,1,3,4,4,3,1},

	      {1,3,4,0,2,1,4,3,0,0,1,2,3,1,1,
		  0,0,3,0,3,2,3,0,1,4,0,3,3,3,2,
		  2,4,1,2,0,1,2,1,0,0,3,1,0,2,2,
		  0,2,1,2,1,1,0,0,0,3,3,0,1,1,3,
		  1,4,2,3,1,3,3,0,4,2,3,1,0,4,4,
		  2,1,1,4,1,1,4,0,4,4,2,0,0,4,0,
		  3,4,4,3,0,0,2,0,4,1,2,4,0,3,3,
		  1,4,0,4,0,0,3,3,4,4,0,2,2,4,4,
		  0,1,0,4,2,3,3,0,0,2,0,4,3,4,1,
		  3,1,1,4,2,4,0,0,2,0,3,1,2,4,3,
		  0,0,4,2,4,1,2,0,0,0,3,0,3,3,3,
		  0,0,1,0,1,2,2,0,3,4,3,2,4,3,4,
		  1,1,0,2,0,4,3,3,1,1,4,3,2,4,1,
		  0,1,2,2,3,4,0,3,1,4,0,0,3,1,1,
		  0,3,0,0,1,0,1,1,1,3,1,2,0,0,0},
		  
		  {0,1,3,3,4,3,4,3,2,4,4,0,3,2,1,
		  4,0,1,1,0,0,0,1,2,0,3,0,0,2,1,
		  1,2,4,3,0,2,0,2,3,4,3,1,2,2,3,
		  3,4,3,0,1,3,3,2,3,1,1,0,3,4,2,
		  2,0,0,3,2,0,2,3,3,3,0,1,1,1,1,
		  2,4,2,2,1,4,3,2,1,4,0,1,4,4,1,
		  0,0,0,2,2,3,4,3,2,3,0,3,4,3,4,
		  1,2,0,4,1,2,2,4,0,2,4,2,4,0,3,
		  3,4,3,3,1,1,0,4,4,2,1,0,0,1,3,
		  1,2,2,2,4,3,2,0,2,1,0,1,0,1,3,
		  2,3,4,2,1,0,1,2,3,2,4,0,2,4,3,
		  1,3,2,4,3,0,4,4,1,1,4,1,2,4,0,
		  3,0,2,2,1,4,3,4,1,2,2,1,1,3,1,
		  2,0,2,1,0,4,1,4,0,3,2,3,0,2,4,
		  0,3,1,1,0,1,4,1,4,1,1,1,0,4,2},
		  
		  {4,1,2,0,2,3,4,1,4,4,1,4,3,1,3,
		  1,3,1,3,4,0,3,4,2,3,3,2,3,4,1,
		  1,3,2,2,3,4,2,3,4,0,3,4,1,2,3,
		  1,3,2,4,0,2,0,0,1,2,1,3,4,4,2,
		  4,0,2,2,0,1,1,0,0,1,0,2,3,2,4,
		  2,2,0,3,4,1,0,4,3,4,4,2,3,3,4,
		  4,4,0,2,0,3,4,1,1,4,4,2,0,1,1,
		  3,1,0,4,1,1,1,3,2,4,1,3,2,0,2,
		  0,2,0,0,1,1,2,0,4,1,1,0,2,2,4,
		  3,1,0,4,3,4,3,1,1,0,0,3,2,3,4,
		  4,4,1,2,4,0,4,2,0,3,2,3,4,0,0,
		  2,4,3,0,1,3,1,3,1,0,1,0,0,1,4,
		  1,2,1,2,0,0,3,0,1,1,0,2,3,1,2,
		  3,2,0,1,3,0,2,4,3,4,4,4,0,3,0,
		  2,3,3,0,2,2,4,3,0,2,1,2,3,2,0},
		  
		  {1,2,2,4,2,3,4,2,4,1,2,2,3,3,4,
		  3,1,1,4,1,1,1,1,1,2,1,1,4,1,0,
		  1,4,1,4,4,2,1,4,0,3,4,0,2,3,3,
		  3,3,1,2,0,3,3,3,2,4,0,1,2,3,0,
		  4,3,4,1,3,0,4,4,3,4,0,4,0,0,2,
		  2,0,3,1,2,4,4,4,0,0,2,3,0,0,3,
		  0,4,0,3,4,2,1,1,0,3,3,3,2,2,1,
		  0,2,0,3,1,4,0,0,1,2,0,3,4,1,2,
		  3,2,2,2,1,1,1,4,3,2,0,2,4,2,2,
		  4,3,3,0,3,0,0,4,0,0,2,2,3,3,1,
		  4,2,3,4,1,2,3,1,3,0,4,4,4,0,2,
		  0,1,3,1,2,3,2,4,3,3,1,2,4,0,1,
		  4,1,3,3,1,0,3,2,0,1,4,0,2,0,2,
		  4,0,2,4,1,0,0,4,2,0,0,4,4,3,0,
		  1,1,1,3,4,2,3,2,1,2,0,1,4,1,0},
		  
		  {4,0,1,4,3,3,1,4,1,2,4,1,0,0,2,
		  0,1,4,0,3,0,0,2,4,2,2,3,3,2,4,
		  0,2,1,0,3,3,3,0,0,4,4,3,1,1,4,
		  4,4,2,1,0,2,4,3,3,2,2,4,2,4,0,
		  3,0,0,4,4,2,2,1,3,4,3,2,4,2,0,
		  0,4,1,4,4,4,4,4,1,2,3,4,2,3,3,
		  0,1,2,0,0,2,2,1,3,4,2,0,0,4,1,
		  4,3,3,2,0,0,1,0,1,4,3,2,3,1,1,
		  3,4,2,2,0,2,3,3,3,0,0,1,2,1,3,
		  1,3,2,1,2,2,4,1,1,1,2,3,1,3,1,
		  0,0,2,1,2,1,1,4,1,1,0,2,1,2,0,
		  4,1,2,1,0,3,1,0,3,4,0,4,3,3,2,
		  4,3,0,0,3,4,3,3,3,3,1,1,3,2,1,
		  0,1,1,3,0,1,1,0,4,0,4,0,2,0,4,
		  2,2,1,4,4,2,2,0,3,4,3,0,2,4,3},
		  
		  {2,2,4,0,2,4,0,0,1,4,0,3,4,3,3,
		  0,4,3,1,0,3,2,0,1,2,2,1,4,4,0,
		  2,1,2,3,3,2,1,2,3,3,0,4,2,1,0,
		  4,4,3,3,2,4,1,0,1,4,4,0,4,2,1,
		  3,3,0,1,2,2,3,1,3,0,1,3,2,3,3,
		  1,2,0,3,4,0,4,2,2,2,1,3,3,3,1,
		  4,0,0,1,1,1,1,4,3,3,2,1,3,2,0,
		  4,1,4,4,1,0,0,2,0,3,2,2,0,2,3,
		  2,3,3,1,4,3,0,1,0,4,4,0,0,2,1,
		  0,1,2,2,4,3,1,1,4,4,2,4,4,2,4,
		  2,4,1,1,0,3,3,3,0,4,4,0,0,2,0,
		  3,2,1,3,0,4,4,2,3,0,2,1,1,3,1,
		  0,4,3,3,1,2,0,2,2,1,2,3,0,0,1,
		  4,3,4,2,1,1,3,0,4,1,4,1,4,2,0,
		  2,1,3,2,0,1,4,0,1,4,0,4,0,4,3},
		  
		  {0,1,2,1,3,4,3,2,1,2,1,2,2,3,4,
		  4,0,0,1,3,0,4,2,0,4,4,4,2,1,1,
		  3,0,0,1,2,1,1,3,0,0,3,2,4,0,0,
		  4,2,1,4,4,1,4,0,0,3,2,0,2,2,0,
		  3,3,4,2,1,2,4,1,3,4,0,4,2,3,0,
		  0,4,4,1,2,2,1,4,4,2,3,3,4,4,1,
		  3,1,1,3,2,2,0,3,2,3,4,4,3,2,0,
		  2,4,1,3,2,0,2,4,4,4,1,4,4,0,0,
		  1,4,2,1,2,0,3,3,0,1,3,3,2,4,3,
		  2,2,3,2,1,1,0,0,1,1,3,1,2,4,3,
		  2,1,0,2,2,0,3,2,2,1,4,1,1,4,0,
		  0,1,3,2,1,0,4,0,0,3,3,0,3,0,4,
		  1,2,1,3,4,3,1,1,3,0,0,4,3,1,4,
		  0,3,3,3,1,1,4,0,0,4,2,4,1,0,3,
		  3,0,3,2,1,4,0,3,3,1,2,2,0,4,2},

		  {2,0,1,4,4,3,1,4,2,0,4,0,4,0,1,
		  3,3,0,2,1,1,1,4,2,4,3,4,2,1,0,
		  4,1,4,4,1,2,1,1,1,2,3,1,0,3,3,
		  4,4,2,3,3,0,2,0,3,2,1,4,4,1,4,
		  1,4,1,3,3,3,1,0,2,2,2,2,2,3,0,
		  0,4,2,3,0,3,1,0,1,1,3,1,3,2,1,
		  2,0,2,4,1,1,2,1,3,1,1,1,2,2,2,
		  1,3,3,3,1,1,0,0,3,3,0,2,1,1,1,
		  0,0,0,4,4,1,3,2,4,1,0,0,3,3,0,
		  4,3,2,3,1,3,3,3,4,3,1,2,2,1,1,
		  1,1,1,1,2,0,2,1,4,1,3,1,1,1,2,
		  0,1,2,1,0,4,0,2,3,1,0,0,0,1,0,
		  0,1,1,4,3,3,4,4,0,0,1,0,1,2,4,
		  3,2,2,1,1,4,0,2,1,0,1,0,4,4,4,
		  4,0,1,1,2,2,4,3,4,1,2,4,4,3,4},

		  {0,2,2,2,4,1,2,0,4,0,2,3,0,2,2,
		  4,4,4,2,2,2,1,2,3,2,3,0,0,2,1,
		  3,2,0,1,2,3,2,4,3,1,0,4,2,0,2,
		  2,1,2,0,0,2,2,3,4,3,2,2,2,1,3,
		  0,2,0,3,2,0,2,1,2,2,2,3,3,0,2,
		  3,1,0,4,3,0,1,1,0,3,0,0,2,3,4,
		  0,3,4,1,3,4,3,1,1,3,3,1,2,1,3,
		  4,2,3,1,1,0,3,3,4,4,1,1,4,4,3,
		  3,0,4,1,1,1,3,3,1,4,1,1,4,4,2,
		  2,1,3,0,2,2,4,2,4,2,1,1,2,2,0,
		  0,1,3,2,4,4,0,0,0,4,2,2,4,2,2,
		  3,0,1,2,4,1,0,3,3,1,0,4,0,2,2,
		  4,2,1,4,2,2,2,2,0,2,1,0,4,3,0,
		  0,4,3,2,0,2,3,2,4,2,1,1,1,3,4,
		  4,2,4,4,0,2,0,1,3,4,2,4,2,3,1},
		  
		  {0,2,2,4,4,3,3,3,3,0,4,3,0,2,3,
		  4,1,4,4,4,1,3,1,4,1,0,3,0,2,1,
		  0,4,1,3,0,3,1,3,3,2,4,0,4,3,2,
		  1,2,3,3,4,2,1,0,2,3,3,3,2,3,4,
		  0,4,1,4,1,1,2,3,0,2,1,3,1,0,2,
		  3,1,3,4,1,3,3,1,4,3,3,2,4,4,0,
		  0,2,4,4,1,0,0,3,2,3,2,3,3,3,2,
		  0,1,4,3,3,1,2,1,3,2,3,1,2,0,2,
		  0,2,0,2,3,1,3,4,1,1,0,2,1,4,1,
		  0,3,4,0,0,2,3,2,4,3,3,0,0,0,3,
		  2,4,0,2,2,0,3,1,0,2,3,2,3,2,3,
		  4,3,1,1,4,3,1,1,3,1,3,0,4,1,3,
		  4,2,1,1,3,3,0,3,0,4,0,3,4,3,0,
		  1,2,4,4,2,4,3,4,1,4,3,0,0,0,2,
		  4,3,3,2,3,3,0,0,1,1,2,3,3,4,2},
		  
		  {4,4,1,4,1,1,2,4,0,3,3,0,1,2,0,
		  4,1,1,4,3,1,1,2,2,3,0,2,4,0,3,
		  3,2,3,4,0,1,4,0,2,4,4,1,2,3,0,
		  0,4,3,4,0,2,4,0,4,4,4,1,4,3,4,
		  4,0,1,4,2,0,1,2,4,3,0,1,4,4,3,
		  3,1,4,2,2,1,2,1,2,2,4,2,1,2,2,
		  2,4,2,0,4,0,3,4,3,0,2,3,3,3,0,
		  3,4,3,4,0,0,0,1,1,4,1,2,1,3,3,
		  4,4,4,1,2,4,2,4,0,1,4,1,4,4,3,
		  0,4,2,2,4,0,3,1,3,2,4,1,4,4,4,
		  0,0,2,1,4,0,2,4,3,4,0,0,4,3,0,
		  4,2,1,0,2,2,4,2,2,2,3,3,1,0,0,
		  4,1,3,3,4,3,1,3,2,1,1,3,1,4,2,
		  1,1,4,0,4,3,3,2,0,2,4,3,1,4,0,
		  3,3,1,4,1,4,0,4,0,4,3,0,4,4,0},
		  
		  {3,0,1,3,3,0,0,1,0,0,2,4,0,0,1,
		  1,2,2,3,2,2,0,4,0,2,3,2,2,2,1,
		  3,1,0,0,0,0,4,4,1,3,1,3,2,0,4,
		  0,1,0,2,0,3,4,3,2,3,0,2,0,3,4,
		  2,3,2,2,0,3,3,0,0,3,0,3,4,1,1,
		  0,3,3,2,0,4,1,2,4,1,2,4,4,1,0,
		  3,2,4,0,4,1,4,3,2,1,1,4,0,0,2,
		  1,4,1,3,0,4,0,3,2,3,2,0,0,0,1,
		  0,0,0,1,4,2,1,0,4,4,4,3,1,0,4,
		  3,3,3,1,0,3,1,2,0,2,4,3,4,1,1,
		  1,1,1,3,0,2,2,3,0,4,3,4,4,1,1,
		  0,2,0,0,2,0,0,1,3,0,2,3,0,2,4,
		  4,3,3,2,4,0,0,0,4,3,1,0,4,1,2,
		  2,2,3,2,0,4,2,0,0,4,1,4,4,0,1,
		  3,4,1,4,4,0,0,0,0,1,0,2,1,0,0}};
}
