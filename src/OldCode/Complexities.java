package OldCode;

import java.util.Random;


public class Complexities {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		//State-space complexity
		
		/*long positionPerColumn=0;
		for (int i=0;i<=15;i++)	
		{
			positionPerColumn+=Math.pow(5, i);
		}
		
		System.out.println("Positions per Column: "+positionPerColumn);
		System.exit(0);*/
		//Game-tree complexity
		Random r = new Random();
		long totalMoves=0;
		long totalBranching=0;
		int nrOfGames=1000000;
		for (int games=0;games<nrOfGames;games++)
		{
			System.out.println(games);
			byte[] position = new byte[255];
			for (int i=0;i<255;i++) position[i] = (byte)r.nextInt(5);
			
			SameGameBoard.setMakeMoveVariables(position, 15, 15, (byte)-1);
			
			int[] moves = SameGameBoard.generateMoves1(position,15,15);
		
			while (moves[0]!=-1)
			{
				//SameGameBoard.println(position, 15, 15);
				totalMoves++;
				int nrOfMoves=0;
				for (nrOfMoves=0;moves[nrOfMoves]!=-1;nrOfMoves++);
					//System.out.print(moves[nrOfMoves]+" ");
				//System.out.println(moves[nrOfMoves]);
				totalBranching+=nrOfMoves;
			
				int move = moves[r.nextInt(nrOfMoves)];
				//System.out.println(move+"  "+move%15+"  "+move/15);
				SameGameBoard.makeMove(position, move, move%15, move/15, position[move]);
				SameGameBoard.dropDownStones(position, 15, 15);
				
				moves = SameGameBoard.generateMoves1(position,15,15);
				//System.out.println();
			}
		}
		
		System.out.println("Total Moves: "+totalMoves);
		System.out.println("Total Move Choices: "+totalBranching);

		System.out.println();
		System.out.println("Average Game Length: "+totalMoves/(nrOfGames+0.0));
		System.out.println("Average Branching Factor: "+totalBranching/(totalMoves+0.0));
	}

}
