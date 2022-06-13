package OldCode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TrainWeights 
{
	private static int millisecondsPerMove=1000;
	private static int mode = BoardPanel.SAMEGAME;
	private static double learningRate = 1; 
	private static double oldC = UCTPlayer.UCTConstant;
	private static double oldD = UCTPlayer.DeviationConstant;
	private static double oldScore = -1;
	private static double newScore = 0;
	private static Random r = new Random();
	private static int[] beginGroupOccured = new int[225];
	private static int[] beginGroupPlayed = new int[225];
	private static int[] middleGroupOccured = new int[225];
	private static int[] middleGroupPlayed = new int[225];
	private static int[] endGroupOccured = new int[225];
	private static int[] endGroupPlayed = new int[225];
	
	public static void main(String[] args) 
	{
		System.out.println("Starting Training");
		
		UCTPlayer bot = new UCTPlayer();
		bot.output=false;
		
		//long time = System.currentTimeMillis();
		long time = (new Random()).nextLong();
		
		double bestRun=0;
		double knowledgeParameter=0.001;
		Evaluation.chanceOfPlayingChosenColor=knowledgeParameter;
	
		byte[] gameCopy = new byte[225];
		
		System.out.println("Settings:");
		System.out.println("UCT Constant: "+bot.UCTConstant);
		System.out.println("Deviation Constant: "+bot.DeviationConstant);
		System.out.println("Number of visited nodes before expanding: "+bot.numberOfVisitsBeforeExpanding);
		System.out.println("Chance of playing chosen Color: "+knowledgeParameter);
		System.out.println("Top Score weight: "+bot.topScoreWeight);
		System.out.println("Number of nodes: "+bot.maxNumberOfNodes);
	
		int gameCounter = 0;
		int score = 0;
		while (gameCounter<1000)
		{
			score=0;
			gameCounter++;
			System.out.println("Game nr. "+gameCounter);
			
				
			byte[] p = new byte[225];
			for (int i=0;i<p.length;i++) p[i] = (byte) r.nextInt(5);
			/*byte[] p ={
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					4,1,0,0,0,0,1,0,0,0,0,0,0,0,0,
					3,1,0,0,0,0,5,0,0,0,0,0,0,0,0,
					1,4,0,3,0,0,4,5,0,0,0,0,0,0,0,
					3,4,3,5,1,3,3,3,3,5,4,0,0,0,0		
			};
			for (int i=0;i<p.length;i++) p[i] -= 1;*/
			
			
			//SameGameBoard.println(p, 15, 15);
			//System.out.println("Score: "+score);
			//System.out.println();
			//System.out.println();
			int bloksLeft=225;
			while (true)
			{				
				System.arraycopy(p, 0, gameCopy, 0, 225);
				int[] groupsThisPos = new int[225];
				SameGameBoard.setMakeMoveVariables(gameCopy, 15, 15, (byte)-1);
				int[] allMoves = SameGameBoard.generateMoves(gameCopy);
				int i=0;
				while (allMoves[i]!=-1)
				{
					short bloks = SameGameBoard.makeMove(gameCopy, allMoves[i], allMoves[i]%15, allMoves[i]/15, gameCopy[allMoves[i]]);
					if (groupsThisPos[bloks]==0) groupsThisPos[bloks]++;
					i++;
				}
				if (bloksLeft>150) for (i=0;i<225;i++) beginGroupOccured[i]+=groupsThisPos[i];
				else if (bloksLeft>75) for (i=0;i<225;i++) middleGroupOccured[i]+=groupsThisPos[i];
				else for (i=0;i<225;i++) endGroupOccured[i]+=groupsThisPos[i];
				
				
				int move = bot.getMove(p, 15, 15, BoardPanel.SAMEGAME, millisecondsPerMove);
				
				System.arraycopy(p, 0, gameCopy, 0, 225);
				short bloks = SameGameBoard.makeMove(p, move, move%15, move/15, p[move]);
				
				
				if (bloks==1)
				{
					System.out.println("Move with only one blok???");
					SameGameBoard.println(gameCopy, 15, 15);
					System.out.println("Move: ["+move%15+"/"+move/15+"]");
					System.exit(0);
				}
				
				if (bloksLeft>150) beginGroupPlayed[bloks]++;
				else if (bloksLeft>75) middleGroupPlayed[bloks]++;
				else endGroupPlayed[bloks]++;
				
				bloksLeft-=bloks;
				
				
				SameGameBoard.dropDownStones(p, 15, 15);
			
				score+=(bloks-2)*(bloks-2);
				
				/*SameGameBoard.println(p, 15, 15);
				System.out.println("Score: "+score);
				System.out.println();
				System.out.println();*/
				
				int[] moves =SameGameBoard.generateMoves(p);
				
				/*for (i=0;i<25;i++)
				{
					System.out.println("Size: "+i+" Occured: "+groupOccured[i]+" Played: "+groupPlayed[i]+" Ratio: "+(groupPlayed[i]/(groupOccured[i]+0.0)));
				}*/
				
				if (moves[0]==-1) break;
		
			}
			
			
			try {
				FileWriter fstream = new FileWriter(time+".txt");
				BufferedWriter out = new BufferedWriter(fstream);
				
				out.write("After "+gameCounter+" games:");
				out.newLine();
				out.write("Begin Game: ");
				for (int i=0;i<100;i++)
				{
					out.write("Size: "+i+" Occured: "+beginGroupOccured[i]+" Played: "+beginGroupPlayed[i]+" Ratio: "+(beginGroupPlayed[i]/(beginGroupOccured[i]+0.0)));
					out.newLine();
				}
				out.write("Middle Game: ");
				for (int i=0;i<100;i++)
				{
					out.write("Size: "+i+" Occured: "+middleGroupOccured[i]+" Played: "+middleGroupPlayed[i]+" Ratio: "+(middleGroupPlayed[i]/(middleGroupOccured[i]+0.0)));
					out.newLine();
				}
				out.write("End Game: ");
				for (int i=0;i<100;i++)
				{
					out.write("Size: "+i+" Occured: "+endGroupOccured[i]+" Played: "+endGroupPlayed[i]+" Ratio: "+(endGroupPlayed[i]/(endGroupOccured[i]+0.0)));
					out.newLine();
				}
				out.newLine();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Runtime.getRuntime().gc();
		}
	
		System.exit(0);
	}
	
}
