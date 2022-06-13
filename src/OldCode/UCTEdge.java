package OldCode;

public class UCTEdge
{
	public UCTNode parent;
	public UCTNode child;
	public UCTEdge sibling;
	public int move;
	public int simulations;
	public float average=Float.MIN_VALUE;
	public short topScore=Short.MIN_VALUE;	
	public int movePoints;
	public long sumOfSquaredResults = 0;
}
