package OldCode;

import javax.swing.JOptionPane;

public class ComputerPlayThread extends Thread 
{
	boolean stop=true;
	boolean makeMove=false;
	private BoardPanel boardPanel;
	private ScorePanel scorePanel;
	private HistoryPanel history;
	private SameGameBot bot;
	private byte[] position;
	private int xDim,yDim;
	//private int score;
	private int sgScore;
	private int bbScore;
	private int cmScore;
	private int numberOfColors;
	private boolean ended=false;
	
	public ComputerPlayThread(SameGameBot abot,BoardPanel b,ScorePanel s, int sg, int bb, int cm)
	{
		bot=abot;
		position=b.getPosition();
		xDim=b.getXDim();
		yDim=b.getYDim();
		boardPanel=b;
		scorePanel=s;
		sgScore=sg;
		bbScore=bb;
		cmScore=cm;
		numberOfColors=b.getNumberofColors();
		history=b.getHistoryPanel();
	}
	
	public void stopIt()
	{
		stop=true;
		bot.stopCalculation();
	}
	
	public void startIt()
	{
		stop=false;
	}
	
	public int getScore(int mode)
	{
		if (mode == boardPanel.SAMEGAME) return sgScore;
		if (mode == boardPanel.BUBBLEBREAKER) return bbScore;
		return cmScore;
		
	}
	
	public void setScore(int s, int mode)
	{
		if (mode == boardPanel.SAMEGAME) sgScore = s;
		if (mode == boardPanel.BUBBLEBREAKER) bbScore = s;
		if (mode == boardPanel.CLICKOMANIA) cmScore = s;
	}
	
	public void setPosition(byte[] pos)
	{
		byte[] p=new byte[pos.length];
		for (int i=0;i<pos.length;i++) p[i]=pos[i];
		position=p;
		bot.reUseEarlierThreshold(false);
	}
	
	public byte[] getPosition()
	{
		return position;
	}
	
	public void setEnded(boolean b)
	{
		ended=b;
	}
	
	public void setBot(SameGameBot b)
	{
		bot=b;
	}
	
	public void setBoardPanel(BoardPanel b)
	{
		xDim=b.getXDim();
		yDim=b.getYDim();
		boardPanel=b;
		sgScore=b.getSGScore();
		bbScore=b.getBBScore();
		cmScore=b.getCMScore();
		numberOfColors=b.getNumberofColors();
		history=b.getHistoryPanel();
	}
	
	public void run()
	{
		int[] colorsLeft=null;
		while (true)
		{
			if (!stop)
			{
				boardPanel.removeMouseListener();
				int xDim = boardPanel.getXDim();
				int yDim = boardPanel.getYDim();
				
				//SameGameBoard.setMakeMoveVariables(xDim, yDim);
				if (!ended)
				{
				//if (SameGameBoard.canMove(position, xDim, yDim))
				//{
					//if (!SameGameBoard.canMove(position, xDim, yDim)) System.out.println("Trying to get a move from an impossible position.");
					//SameGameBoard.println(position, xDim, yDim);
					int move = bot.getMove(position, xDim, yDim,boardPanel.getScoreMode(),boardPanel.getMainFrame().getSettings().getTimePerMove());
					//SameGameBoard.println(position, xDim, yDim);

					// interrupt mcts for explanations
					if (!makeMove && boardPanel.getXaiPlay()) {
						stopIt();
						boardPanel.explanationPanel.updateFeatures();
						history.enableContinueButton();
						boardPanel.explanationPanel.congregateMoves();
						boardPanel.setHumanPlay(true);
						System.out.println("MCTS Finished");
						makeMove = true;
					}


					if ((!stop)&&(move!=-1))
					{
						int blocks = SameGameBoard.makeMove(position, xDim, yDim, move%xDim, move/xDim, position[move],(byte)-1);
						
						sgScore+= (blocks-2)*(blocks-2);
						bbScore+= blocks*(blocks-1);
						cmScore+= blocks;
							
						SameGameBoard.dropDownStones(position, xDim, yDim);
						boardPanel.setPosition(position, xDim, yDim, boardPanel.getNumberofColors());
			
						colorsLeft = new int[numberOfColors];
						for (int i=0;i<position.length;i++)
							if (position[i]>=0) colorsLeft[position[i]]++;
						
						if (!SameGameBoard.canMove(position, xDim, yDim))
						{
							if (!ended)
							{
								ended=true;
								stop=true;
								if (position[(yDim-1)*xDim]==-1)
									sgScore+=1000;
								else
								{
									for (int i=0;i<colorsLeft.length;i++)
										if (colorsLeft[i]>2) sgScore-=(colorsLeft[i]-2)*(colorsLeft[i]-2);
								}
								//scorePanel.updateNumbers(colorsLeft,score);
							}
						}
						
						history.addMove(position,boardPanel.numberToString(move%xDim)+" "+((move/xDim)+1),sgScore,bbScore,cmScore,true);
						
						if (boardPanel.getScoreMode()==boardPanel.SAMEGAME) scorePanel.updateNumbers(colorsLeft, sgScore);
						if (boardPanel.getScoreMode()==boardPanel.BUBBLEBREAKER) scorePanel.updateNumbers(colorsLeft, bbScore);
						if (boardPanel.getScoreMode()==boardPanel.CLICKOMANIA) scorePanel.updateNumbers(colorsLeft, cmScore);
						bot.reUseEarlierThreshold(true);
						if (ended)
						{
							JOptionPane.showMessageDialog(null, "Game Over");
							//System.exit(0);
						}

						if (boardPanel.getXaiPlay()) makeMove=false;
					}
				}
			}
			boardPanel.addMouseListener();
			try
			{
				sleep(5);
			}
			catch (Exception e) {}
		}
	}
}
