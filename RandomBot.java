import java.util.Random;

public class RandomBot implements SameGameBot 
{
	Random r = new Random();
	private boolean stop=false;
	
	public int getMove(byte[] position, int xDim, int yDim, int scoreFunction, int time)
	{
		stop=false;
		while (true)
		{
			int xChoice = r.nextInt(xDim);
			int yChoice = r.nextInt(yDim);
			if (SameGameBoard.legalMove(position, xDim, yDim, xChoice, yChoice)) 
			{
				try
				{
					Thread.sleep(time/2);
				}
				catch (Exception e) {}
				if (stop) return -1;
				return xChoice+(yChoice*xDim);
			}
		}
	}
	
	public void setEvaluationFunction(int function)
	{
		
	}
	public void stopCalculation()
	{
		stop=true;
	}
	
	public void reUseEarlierThreshold(boolean value)
	{
		//reUseThreshold=value;
	}
	
	public void timeIsUp()
	{
		
	}
}
