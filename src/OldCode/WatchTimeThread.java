package OldCode;

public class WatchTimeThread extends Thread
{
	private int ms=0;
	private SameGameBot b;
	private boolean stop=false;
	public WatchTimeThread(int milliseconds, SameGameBot bot)
	{
		ms=milliseconds;
		b=bot;
	}
	
	public void run()
	{
		
		try {
			long start = System.currentTimeMillis();
			while ((System.currentTimeMillis()-start<ms)&&(!stop)) 
				sleep(5);
			if (!stop) b.timeIsUp();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopIt()
	{
		stop=true;
	}
}