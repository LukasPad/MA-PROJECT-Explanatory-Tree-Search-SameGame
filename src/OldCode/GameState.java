package OldCode;

public class GameState
{
	public byte[] position;
	public byte[] colors;
	public int xDim, yDim;
	public long hash;
		
	public GameState()
	{}
	
	public GameState(byte[] p,int xDim,int yDim, int nrOfColors)
	{
		colors = new byte[nrOfColors];
		position = new byte[p.length];
		
		for (int i=0;i<position.length;i++)
		{
			colors[p[i]]++;
			position[i]=p[i];
		}
		
		this.xDim=xDim;
		this.yDim=yDim;
	}
	
	public void copy(GameState g)
	{
		if (position==null || position.length!=g.position.length)
		{
			position = new byte[g.position.length];
			colors = new byte[g.colors.length];
		}
		
		for (int i=0;i<position.length;i++)
		{
			colors[g.position[i]]++;
			position[i]=g.position[i];
		} 
		
		xDim=g.xDim;
		yDim=g.yDim;
	}
}
