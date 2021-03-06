package OldCode;

public class TestClass
{
	private static int millisecondsPerMove=60000;
	private static int mode = BoardPanel.SAMEGAME;
	
	public static void main(String[] args) 
	{
		TestSet set = new TestSet();
		set.loadTestSet();
		
		
		System.out.println("Starting Test Run");
		long startTime = System.currentTimeMillis();
		
		//IDAStarBotOverEstimate bot = new IDAStarBotOverEstimate();
		UCTPlayer bot = new UCTPlayer();
		bot.output=!false;
		int scoreTotal=0;
		long totalNodes=0;
		
		for (int i=0;i<set.getNumberOfPositions();i++)
		{
			byte[] p = set.getPosition(i);
			int scoreThisGame=0;
			long nodesThisGame=0;
			
			//while (SameGameBoard.canMove(p, 15, 15))
			{
				int move = bot.getMove(p, 15, 15, mode, millisecondsPerMove);
				nodesThisGame+=bot.totalSimulations;
				int score = SameGameBoard.makeMove(p, 15, 15, move%15, move/15, p[move], (byte)-1);
				
				if (mode == BoardPanel.SAMEGAME) score=(score-2)*(score-2);
				if (mode == BoardPanel.BUBBLEBREAKER) score = score*(score-1);
				
				SameGameBoard.dropDownStones(p, 15, 15);
				
				System.out.println(i+"\t"+bot.averageDepth+"\t"+bot.deepestNode);
				
				//SameGameBoard.println(p, 15, 15);
				scoreThisGame+=score;
			}
			
			if (mode == BoardPanel.SAMEGAME)
			{
				if (SameGameBoard.isEmpty(p, 15, 15)) scoreThisGame+=1000;
				else
				{
					int[] colors = new int[5];
					for (int j=0;j<p.length;j++)
						if (p[j]>=0) colors[p[j]]++;
					
					for (int j=0;j<5;j++)
						if (colors[j]>2) scoreThisGame-=(colors[j]-2)*(colors[j]-2);
				}
			}
			//System.out.println("Position nr. "+(i+1)+"  Score: "+scoreThisGame+"  Nodes: "+nodesThisGame);
			scoreTotal+=scoreThisGame;
			totalNodes+=nodesThisGame;
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Total score: "+scoreTotal);
		System.out.println("Total nodes: "+totalNodes);
		System.out.println("Total time: "+(int)((endTime-startTime)/1000.0)+" seconds");
		System.out.println("Speed: "+(int)(totalNodes/((endTime-startTime)/1000.0)));

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
