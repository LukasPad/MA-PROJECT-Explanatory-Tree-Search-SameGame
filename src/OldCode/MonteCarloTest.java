package OldCode;

import java.util.Random;


public class MonteCarloTest 
{	
	public static void main(String[] args) 
	{
		System.out.println("Monte Carlo Test");
		
		byte[] position = new byte[255];
		Random r = new Random();
		for (int i=0;i<255;i++) position[i] = (byte)r.nextInt(5);
		long start, end;
		
		System.out.print("Best Score after 1 game: ");
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 1,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(1/((end-start)/1000.0)));
		
		System.out.print("Best Score after 10 games: "); 
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 10,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(10/((end-start)/1000.0)));
		
		System.out.print("Best Score after 100 games: "); 
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 100,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(100/((end-start)/1000.0)));
		
		System.out.print("Best Score after 1000 games: "); 
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 1000,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(1000/((end-start)/1000.0)));
		
		System.out.print("Best Score after 10000 games: ");
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 10000,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(10000/((end-start)/1000.0)));
		
		System.out.print("Best Score after 100000 games: ");
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 100000,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(100000/((end-start)/1000.0)));
		
		System.out.print("Best Score after 1000000 games: ");
		start = System.currentTimeMillis();
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 1000000,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(1000000/((end-start)/1000.0)));
		
		System.out.print("Best Score after 10000000 games: "); 
		System.out.print(Evaluation.monteCarlo(position, 15, 15, 10000000,BoardPanel.SAMEGAME)+" ");
		end = System.currentTimeMillis();
		System.out.println("Games per second: "+(10000000/((end-start)/1000.0)));

	}
}
