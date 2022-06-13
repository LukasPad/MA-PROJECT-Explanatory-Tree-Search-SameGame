package OldCode;

import java.util.Random;


public class RandomGeneratorSpeedTest 
{

	public static void main(String[] args) 
	{
		/*System.out.println("Array copy speed test.");
		int[] array = new int[225];
		for (int i=0;i<225;i++) array[i]=i;
		
		System.out.println("Normal copy");
		long start = System.currentTimeMillis();
		for (int i=0;i<100000000;i++)
		{
			int[] newA = new int[225];
			for (int j=0;j<225;j++) newA[j]=array[j];
		}
		long end = System.currentTimeMillis();
		System.out.println("Time(ms): "+(end-start));
		
		System.out.println("Array copy");
		start = System.currentTimeMillis();
		for (int i=0;i<100000000;i++)
		{
			int[] newA = new int[225];
			System.arraycopy(array, 0, newA, 0, 225);
		}
		end = System.currentTimeMillis();
		System.out.println("Time(ms): "+(end-start));*/
		
		System.out.println("Starting Java Random Generator.");
		Random r = new Random();
		long start = System.currentTimeMillis();
		for (int i=0;i<100000000;i++) r.nextInt();
		long end = System.currentTimeMillis();
		System.out.println("Time(ms): "+(end-start));
		
		System.out.println("Starting Marks Random Generator.");
		
		start = System.currentTimeMillis();
		seed = System.currentTimeMillis();
		for (int i=0;i<100000000;i++) randomIntValue();
		end = System.currentTimeMillis();
		System.out.println("Time(ms): "+(end-start));
	}
	
	private static long seed;
	private final static long MULTIPLIER = 0x5DEECE66DL;
    private final static long APPEND = 0xBL;
    private final static long MASK = (1L << 48) - 1;
    private static final int RANGE = 7;
	
	public final static int randomIntValue() 
	{   
        final long nextseed = (seed * MULTIPLIER + APPEND) & MASK;
        seed = nextseed;
        return (int) (nextseed >>> (48 - RANGE)) - (1 << (RANGE - 1)); 
    }  

}
