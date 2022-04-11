import java.util.Random;

//import com.sun.corba.se.spi.ior.MakeImmutable;


public class MoveGeneratorTest {

	
	public static void main(String[] args) 
	{
		//singleTest();
		multiTest();
		
	}
	
	private static void multiTest()
	{
		System.out.println("Starting multiple move generator tests...");
		System.out.println();
		Random r = new Random();
	
		byte[] position = new byte[225];
		byte[] oldPosition = new byte[225];
		int[] windowCopy = new int[3];
		int[] windowAfterMoving = new int[3];
		int[] changedColumns = new int[15];
		for (int game=0;game<1000000;game++)
		{
			System.out.println("Game nr. "+game);
			
			for (int i=0;i<225;i++) position[i]= (byte)r.nextInt(5);
			
			int[] earlierMoves = SameGameBoard.generateMoves1(position, 15, 15);
			
			int numberOfMoves=0;
			while (earlierMoves[numberOfMoves]!=-1) numberOfMoves++;
			
			int move = earlierMoves[r.nextInt(numberOfMoves)];
			
			SameGameBoard.makeMove(position, 15, 15, move%15, move/15, position[move], (byte)-1);
			SameGameBoard.dropDownStones(position, 15, 15);
			int[] window = SameGameBoard.getChangedWindow();
			
			while (SameGameBoard.canMove(position, 15, 15))
			{
				//SameGameBoard.println(position, 15, 15);
				//System.out.println();
				//System.out.println();
				
				boolean movesAreSame = true;
				
				windowCopy = window.clone();
				int[] iterativeMoves = SameGameBoard.generateMovesIteratively(position, earlierMoves, window, 15, 15);
				int[] normalMoves = SameGameBoard.generateMoves1(position, 15, 15);
				
				earlierMoves=normalMoves.clone();
				
				int numberOfIterativeMoves=0;
				while (iterativeMoves[numberOfIterativeMoves]!=-1) numberOfIterativeMoves++;
				
				int numberOfNormalMoves=0;
				while (normalMoves[numberOfNormalMoves]!=-1) numberOfNormalMoves++;
				
				if (numberOfIterativeMoves!=numberOfNormalMoves) movesAreSame=false;
				
				if (!movesAreSame) 
				{
					System.out.println("Old position: ");
					SameGameBoard.println(oldPosition, 15, 15);
					System.out.println("New Position: ");
					SameGameBoard.println(position, 15, 15);
					System.out.print("ChangedColumns: ");
					for (int i=0;i<15;i++) System.out.print(changedColumns[i]+" ");
					System.out.println();
					System.out.println("MostLeftColum: "+windowCopy[0]+" MostRightColumn: "+windowCopy[1]+" DeepestChange: "+windowCopy[2]);
					System.out.println("After moving: MostLeftColum: "+windowAfterMoving[0]+" MostRightColumn: "+windowAfterMoving[1]+" DeepestChange: "+windowAfterMoving[2]);
					System.out.print("Normal: ");
					for (int i=0;i<numberOfNormalMoves;i++) System.out.print(normalMoves[i]+" ");
					System.out.println();
					System.out.print("Iterative: ");
					for (int i=0;i<numberOfIterativeMoves;i++) System.out.print(iterativeMoves[i]+" ");
					System.out.println();
					
					System.out.println("Checking for differences...");
					System.out.print("In iterative array but not in normal: ");
					for (int j=0;j<iterativeMoves.length;j++) if ((iterativeMoves[j]!=-2)&&(!isInArray(iterativeMoves[j], normalMoves)))
						System.out.print(iterativeMoves[j]+"["+iterativeMoves[j]%15+","+iterativeMoves[j]/15+"] ");
					System.out.println();
						
					System.out.print("In normal array but not in iterative: ");
					for (int j=0;j<normalMoves.length;j++) if (!isInArray(normalMoves[j], iterativeMoves))
						System.out.print(normalMoves[j]+"["+normalMoves[j]%15+","+normalMoves[j]/15+"] ");
					System.out.println();
					System.out.println("Found a position where the moves are not the same!!! Normal: "+numberOfNormalMoves+" Iterative: "+numberOfIterativeMoves);
					
					SameGameBoard.changedRows=changedColumns;
					SameGameBoard.getChangedWindow();
					
					System.exit(0);
				}
				
				byte[] normalEmptyBoard = playAllMoves(position, normalMoves, 15, 15);
				byte[] iterativeEmptyBoard = playAllMoves(position, iterativeMoves, 15, 15);
				
				if (!SameGameBoard.isSame(normalEmptyBoard, iterativeEmptyBoard))
				{
					System.out.println("Played all moves, but the boards are not the same!!!!");
				}
				
				int randomMove = normalMoves[r.nextInt(numberOfNormalMoves)];
				//System.out.println("Playing: "+randomMove);
				for (int i=0;i<225;i++) oldPosition[i]=position[i];
				SameGameBoard.makeMove(position, 15, 15, randomMove%15, randomMove/15, position[randomMove], (byte)-1);
				windowAfterMoving = SameGameBoard.getChangedWindow();
				position = SameGameBoard.dropDownStones(position, 15, 15);
				window = SameGameBoard.getChangedWindow();
				changedColumns=SameGameBoard.changedRows.clone();	
			}
			
		}
	}
	
	private static byte[] playAllMoves(byte[] pos, int[] moves, int xDim, int yDim)
	{
		byte[] p = new byte[pos.length];
		
		for (int i=0;i<pos.length;i++) p[i]=pos[i];
		
		int i=0;
		while (moves[i]!=-1)
		{
			int color=p[moves[i]];
			SameGameBoard.makeMove(p, xDim, yDim, moves[i]%xDim, moves[i]/xDim, color, (byte)-1);
				
			i++;
		}
		
		return p;
	}
	
	private static void singleTest()
	{
		System.out.println("Testing iterative move generator...");
		System.out.println("Creating test position...");
		Random r = new Random();
			//int[] position = new int[225];
			byte[] position = {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,2,0,0,0,0,0,0,0,0,0,0,2,
					0,0,0,5,0,0,0,0,2,0,0,0,0,0,4,
					5,0,0,1,0,0,0,0,3,0,5,0,0,0,3,
					4,0,0,4,0,0,0,0,2,0,2,0,3,5,1,
					3,4,0,1,0,0,0,0,4,2,1,1,5,2,3,
					5,5,0,4,2,0,0,0,3,5,1,3,2,2,2,
					1,3,5,3,5,2,0,0,5,4,1,5,1,2,1,
					3,1,4,2,2,4,4,2,2,2,1,2,1,4,2,
					5,1,2,1,2,4,4,5,3,1,2,1,2,1,3};
			for (int i=0;i<225;i++) position[i]--; 
			//for (int i=0;i<225;i++) position[i]= r.nextInt(5);
			SameGameBoard.println(position, 15, 15);
			System.out.println("Creating all moves for this position normally...");
			int[] moves = SameGameBoard.generateMoves1(position, 15, 15);
			int i=0; while (moves[i]!=-1) i++;
			System.out.println("Found "+i+" possible moves.");
			for (int j=0;j<moves.length;j++) System.out.print(moves[j]+" ");
			System.out.println();
			System.out.println("Playing a random move...");
			int move = moves[r.nextInt(i)];
			/*int move=0;
			for (int j=0;(j<moves.length)&&(move==0);j++)
				if (moves[j]/15==0) move=moves[j];*/
			move = 214;
			System.out.println("Playing move: "+move);
			SameGameBoard.makeMove(position, 15, 15, move%15, move/15, position[move], (byte)-1);
			int[] window = SameGameBoard.getChangedWindow();
			SameGameBoard.dropDownStones(position, 15, 15);
			SameGameBoard.println(position, 15, 15);
			System.out.println("Creating moves iteratively");
			int[] newMoves = SameGameBoard.generateMovesIteratively(position, moves, window, 15, 15);
			int numberOfMoves=0;
			i=0;
			while (newMoves[i]!=-1)
			{
				if (newMoves[i]!=-2) numberOfMoves++;
				i++;
			}
			for (int j=0;j<newMoves.length;j++) System.out.print(newMoves[j]+" ");
			System.out.println();
			System.out.println("Found "+numberOfMoves+" moves iteratively.");
			moves = SameGameBoard.generateMoves1(position, 15, 15);
			i=0; while (moves[i]!=-1) i++;
			System.out.println("Found "+i+" possible moves normally");
			for (int j=0;j<moves.length;j++) System.out.print(moves[j]+" ");
			
			System.out.println("Checking for differences...");
			System.out.print("In iterative array but not in normal: ");
			for (int j=0;j<newMoves.length;j++) if ((newMoves[j]!=-2)&&(!isInArray(newMoves[j], moves)))
				System.out.print(newMoves[j]+"["+newMoves[j]%15+","+newMoves[j]/15+"] ");
			System.out.println();
				
			System.out.print("In normal array but not in iterative: ");
			for (int j=0;j<moves.length;j++) if (!isInArray(moves[j], newMoves))
				System.out.print(moves[j]+"["+moves[j]%15+","+moves[j]/15+"] ");
			System.out.println();
	}

	private static boolean isInArray(int value, int[] array)
	{
		for (int i=0;i<array.length;i++) if (array[i]==value) return true;
		return false;
	}
	
}
