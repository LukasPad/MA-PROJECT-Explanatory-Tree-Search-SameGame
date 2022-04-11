import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public class TestSet {

	/**
	 * @param args
	 */
	private String fileName = "TestSet.txt";
	private byte[][] testSet;
	
	private final int xDim=15;
	private final int yDim=15;
	private final int positions=250;
	private final int colors=5;
	
	public static void main(String[] args) 
	{
		TestSet s = new TestSet();
		s.generateTestSet();
		s.loadTestSet();
		s.printTestSet();
	}
	
	public void loadTestSet()
	{
		testSet=new byte[positions][xDim*yDim];
		
		try 
		{
			FileReader input = new FileReader(fileName);
			BufferedReader bufRead = new BufferedReader(input);
			
			for (int i=0;i<positions;i++)
			{
				for (int j=0;j<yDim;j++)
				{
					String line = bufRead.readLine();
					String[] l = line.split(" ");
					for (int k=0;k<xDim;k++)
						testSet[i][k+(j*xDim)]=Byte.parseByte(l[k]);
				}
				bufRead.readLine();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void generateTestSet()
	{
		System.out.println("Generating Testset...");
		
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			Random r = new Random();
			
			for (int i=0;i<positions;i++)
			{
				for (int j=0;j<yDim;j++)
				{
					for (int k=0;k<xDim;k++) out.write(r.nextInt(5)+" ");
					out.newLine();
				}
				out.newLine();
			}
			
			out.close();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public int getNumberOfPositions()
	{
		return positions;
	}
	
	public byte[] getPosition(int nr)
	{
		return testSet[nr];
	}
	
	private void printTestSet()
	{
		for (int i=0;i<positions;i++)
		{
			SameGameBoard.println(testSet[i], xDim, yDim);
			System.out.println();
		}
	}
	
}
