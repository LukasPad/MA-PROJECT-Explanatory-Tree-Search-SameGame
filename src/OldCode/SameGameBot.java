package OldCode;

public interface SameGameBot
{
	public void setEvaluationFunction(int function);
	public int getMove(byte[] position, int xDim, int yDim, int scoreMode, int time);
	public void stopCalculation();
	public void reUseEarlierThreshold(boolean value);
	public void timeIsUp();
}
