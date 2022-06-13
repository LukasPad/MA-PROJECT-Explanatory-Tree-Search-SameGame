package OldCode;

import java.util.Random;


public class FastRandom 
{

	private static long seed;
	private final static long MULTIPLIER = 0x5DEECE66DL;
    private final static long APPEND = 0xBL;
    private final static long MASK = (1L << 48) - 1;
    private static final int RANGE = 7;
	
    
	public final static int nextMarkInt() 
	{   
        final long nextseed = (seed * MULTIPLIER + APPEND) & MASK;
        seed = nextseed;
        return (int) (nextseed >>> (48 - RANGE)) - (1 << (RANGE - 1)); 
    }  
	
	
	
	public final synchronized int nextInt(int max) 
	{   
		c++;
		if (c==v.length)c=0;
		return v[c]%max;
    }  
	
	public final  int nextInt() 
	{   
		c++;
		if (c==v.length)c=0;
		return v[c];
    } 
	
	
	private static int[] v = new int[100000];
	private static int c = 0;
	private static final Random r = new Random();
	private static double[] d = new double[100000];
	private static int c2 = 0;
	
	static
	{
		for (int i=0;i<v.length;i++) 
		{
			v[i]=r.nextInt(Integer.MAX_VALUE);
			d[i]=r.nextDouble();
		}
	}
	
	public final synchronized double nextDouble()
	{
		c2++;
		if (c2==d.length)c2=0;
		return d[c2];
		//return r.nextDouble();
	}
	
	
}
