import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BoardPanel extends JPanel
{
	private static int xSizePanel;
	private static int ySizePanel;
	private static byte[] board;
	private static int boardXSize;
	private static int boardYSize;
	private static int fieldWidth;
	private static int fieldHeight;
	private static int numberOfColors;
	private Graphics2D g2;
	private boolean rectangles=true;
	private boolean letters=false;
	
	private BoardMouseMotionListener mouseMovementListener;
	
	ScorePanel scorePanel;
	JFrame mainFrame;
	private HistoryPanel history;
	
	private boolean humanPlay=true;
	
	//private static int score=0;
	private static int sgScore=0;
	private static int bbScore=0;
	private static int cmScore=0; 
	
	public static final int SAMEGAME=1;
	public static final int BUBBLEBREAKER=2;
	public static final int CLICKOMANIA=3;
	private static int currentMode=1;
	
	public BoardPanel(byte[] b, int bXSize, int bYSize, int colors, ScorePanel score, JFrame f, HistoryPanel h) 
	{
		this.setBackground(Color.BLACK);
		this.setForeground(Color.BLACK);
		scorePanel=score;
		mainFrame=f;
		board=b;
		setPreferredSize(new Dimension(xSizePanel,ySizePanel));
		boardXSize=bXSize;
		boardYSize=bYSize;
		numberOfColors=colors;
		history=h;
		addMouseListener(new BoardMouseListener(this));
		mouseMovementListener = new BoardMouseMotionListener(score,board);;
		this.addMouseMotionListener(mouseMovementListener);
	}
	
	public void paintComponent(Graphics g) 
	{
		//super.paintComponent(g);
		g2 = (Graphics2D) g;
		
		if (rectangles)
		{
			g.setColor(new Color(236,233,216));
			g.fillRect(0,0,this.getWidth(),this.getHeight());
		}
		else
		{
			g.setColor(Color.BLACK);
			g.fillRect(0,0,this.getWidth(),this.getHeight());
		}
		
		xSizePanel=this.getWidth();
		ySizePanel=this.getHeight();
		fieldWidth=(int)((xSizePanel/(boardXSize+0.0)));
		fieldHeight=(int)((ySizePanel/(boardYSize+0.0)));
		
		Rectangle2D.Double blok=null;
		Ellipse2D.Double ellipse=null;
		Point2D.Double p1=null;
		Point2D.Double p2=null;
		Line2D.Double line=null;
		
		int curX=0;
		int curY=0;
		
		Font f = new Font("Arial", Font.BOLD, 20);
		g.setFont(f);
		
		for (int i=0;i<board.length;i++)
		{
			if (rectangles)
			{
				if (blok==null) blok = new Rectangle2D.Double(curX*fieldWidth, curY*fieldHeight+1, fieldWidth, fieldHeight-1);
				else blok.setFrame(curX*fieldWidth, curY*fieldHeight+1, fieldWidth, fieldHeight-1);
				if (board[i]==-1) g.setColor(Color.black);
				else g.setColor(MainFrame.colors[board[i]]);
				g2.draw(blok);
				g2.fill(blok);
				
				if (p1==null) p1 = new Point2D.Double(curX*fieldWidth,0);
				else p1.setLocation(curX*fieldWidth,0);
				if (p2==null) p2 = new Point2D.Double(curX*fieldWidth,boardYSize*fieldHeight);
				else p2.setLocation(curX*fieldWidth,boardYSize*fieldHeight);
				if (line==null) line = new Line2D.Double(p1,p2);
				else line.setLine(p1, p2);
				g2.setColor(Color.black);
				g2.draw(line);
			}
			else
			{
				if (ellipse==null) ellipse = new Ellipse2D.Double(curX*fieldWidth, curY*fieldHeight, fieldWidth-2, fieldHeight-2);
				else ellipse.setFrame(curX*fieldWidth, curY*fieldHeight, fieldWidth-2, fieldHeight-2);
				if (board[i]==-1) g.setColor(Color.black);
				else g.setColor(MainFrame.colors[board[i]]);
				g2.draw(ellipse);
				g2.fill(ellipse);
			}
			
			if (letters)
			{
				int letterXPos=curX*fieldWidth+(fieldWidth/2)-6;
				int letterYPos=(curY+1)*fieldHeight-(fieldHeight/2)+7;
				g2.setColor(Color.black);
				g.drawString(""+(char)(board[i]+65),letterXPos , letterYPos);	
			}
			
			if (curX==boardXSize-1)
			{
				curX=0;
				curY++;
				if(rectangles){
				p1.setLocation(0,curY*fieldHeight);
				p2.setLocation(boardXSize*fieldWidth,curY*fieldHeight);
				line.setLine(p1, p2);
				g2.draw(line);}
			}
			else curX++;
		}
	}
	
	public void setPosition(byte[] position, int xDim, int yDim, int colors)
	{
		byte[] p=new byte[position.length];
		for (int i=0;i<position.length;i++) p[i]=position[i];
		board = p;
		boardXSize=xDim;
		boardYSize=yDim;
		numberOfColors=colors;
		int[] colorsLeft = new int[colors];
		for (int i=0;i<position.length;i++)
			if (position[i]>=0) colorsLeft[position[i]]++;
		scorePanel.setFieldsLeft(colorsLeft);
		mouseMovementListener.setPosition(p);
		
		this.repaint();
	}
	
	public void setScoreMode(int mode)
	{
		currentMode=mode;
		history.update();
		ComputerPlayThread t = ((MainFrame)mainFrame).getComputerPlayThreat();
		sgScore = t.getScore(SAMEGAME);
		bbScore = t.getScore(BUBBLEBREAKER);
		cmScore = t.getScore(CLICKOMANIA);
		if (mode==SAMEGAME) scorePanel.setScore(sgScore);
		if (mode==BUBBLEBREAKER) scorePanel.setScore(bbScore);
		if (mode==CLICKOMANIA) scorePanel.setScore(cmScore);
	}
	
	public int getScoreMode()
	{
		return currentMode;
	}
	
	public void setHumanPlay(boolean mode)
	{
		humanPlay=mode;
	}
	
	public byte[] getPosition()
	{
		return board;
	}
	
	public int getXDim()
	{
		return boardXSize;
	}
	
	public int getYDim()
	{
		return boardYSize;
	}
	
	public void setLetters(boolean mode)
	{
		letters=mode;
	}
	
	public int getScore()
	{
		if (currentMode==SAMEGAME) return sgScore;
		if (currentMode==BUBBLEBREAKER) return bbScore;
		if (currentMode==CLICKOMANIA) return cmScore;
		return 0;
	}
	
	public int getSGScore()
	{
		return sgScore;
	}
	
	public int getBBScore()
	{
		return bbScore;
	}
	
	public int getCMScore()
	{
		return cmScore;
	}
	
	public void setScore(int sg, int bb, int cm)
	{
		sgScore=sg;
		bbScore=bb;
		cmScore=cm;
	}
	
	public int getNumberofColors()
	{
		return numberOfColors;
	}
	
	public MainFrame getMainFrame()
	{
		return (MainFrame) mainFrame;
	}
	
	public ScorePanel getScorePanel()
	{
		return scorePanel;
	}
	
	public HistoryPanel getHistoryPanel()
	{
		return history;
	}
	
	public static String numberToString(int anumber)
	{
		String s = ""; 
		int number=anumber+1;
		while (number>0)
		{
			int n = number%26;
			if (n==1) s="A"+s;
			if (n==2) s="B"+s;
			if (n==3) s="C"+s;
			if (n==4) s="D"+s;
			if (n==5) s="E"+s;
			if (n==6) s="F"+s;
			if (n==7) s="G"+s;
			if (n==8) s="H"+s;
			if (n==9) s="I"+s;
			if (n==10) s="J"+s;
			if (n==11) s="K"+s;
			if (n==12) s="L"+s;
			if (n==13) s="M"+s;
			if (n==14) s="N"+s;
			if (n==15) s="O"+s;
			if (n==16) s="P"+s;
			if (n==17) s="Q"+s;
			if (n==18) s="R"+s;
			if (n==19) s="S"+s;
			if (n==20) s="T"+s;
			if (n==21) s="U"+s;
			if (n==22) s="V"+s;
			if (n==23) s="W"+s;
			if (n==24) s="X"+s;
			if (n==25) s="Y"+s;
			if (n==26) s="Z"+s;
		
			number=number/26;
		}
		return s;
	}
	
	private class BoardMouseListener implements MouseListener
	{
		BoardPanel b;
		public BoardMouseListener(BoardPanel bpanel)
		{
			b= bpanel;
		}
		
		public void mouseClicked(MouseEvent e)
		{
			
		}
		
		public void mouseReleased(MouseEvent e)
		{
			
		}
		
		public void mouseEntered(MouseEvent e)
		{
			
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (humanPlay)
			{
				int mouseX = e.getX();
				int mouseY = e.getY();
				int[] colorsLeft=new int[0];
				
				if (mouseY<boardYSize*fieldHeight)
				{
					int boardX = mouseX/fieldWidth;
					int boardY = mouseY/fieldHeight;
					if (SameGameBoard.legalMove(board, boardXSize, boardYSize, boardX, boardY))
					{
						int movePoints = SameGameBoard.makeMove(board, boardXSize, boardYSize, boardX, boardY,board[boardX+(boardY*boardXSize)],(byte)-1);
						sgScore+= (movePoints-2)*(movePoints-2);
						bbScore+= movePoints*(movePoints-1);
						cmScore+= movePoints;
						
						ComputerPlayThread t = ((MainFrame)mainFrame).getComputerPlayThreat();
						t.setScore(sgScore, BoardPanel.SAMEGAME);
						t.setScore(bbScore, BoardPanel.BUBBLEBREAKER);
						t.setScore(cmScore, BoardPanel.CLICKOMANIA);
			
						board=SameGameBoard.dropDownStones(board, boardXSize, boardYSize);
						
						colorsLeft = new int[numberOfColors];
						for (int i=0;i<board.length;i++)
							if (board[i]>=0) colorsLeft[board[i]]++;
						if (currentMode==SAMEGAME) scorePanel.updateNumbers(colorsLeft,sgScore);
						if (currentMode==BUBBLEBREAKER) scorePanel.updateNumbers(colorsLeft,bbScore);
						if (currentMode==CLICKOMANIA) scorePanel.updateNumbers(colorsLeft,cmScore);
						
						boolean showDialog=false;
						
						if (!SameGameBoard.canMove(board, boardXSize, boardYSize))
						{
							if (board[(boardYSize-1)*boardXSize]==-1)
								sgScore+=1000;
							else
							{
								for (int i=0;i<colorsLeft.length;i++)
									if (colorsLeft[i]>2) sgScore-=(colorsLeft[i]-2)*(colorsLeft[i]-2);
							}
							scorePanel.updateNumbers(colorsLeft,sgScore);
							
							showDialog=true;
							((MainFrame)mainFrame).setEnded(true);
						}
						
						history.addMove(board,b.numberToString(boardX)+" "+(boardY+1),sgScore,bbScore,cmScore,true);
						
						if (showDialog) JOptionPane.showMessageDialog(null, "Game Over");
					}
				}
				mouseMovementListener.setPosition(board);
				mouseMovementListener.computePossibleScore(e.getX(), e.getY());
			}
		}
		
		public void mouseExited(MouseEvent e)
		{
			mouseMovementListener.reset();
		}
	}
	
	private class BoardMouseMotionListener implements MouseMotionListener
	{
		private  ScorePanel scorePanel;
		private byte[] position;
		
		private int lastX=-1;
		private int lastY=-1;
		
		public BoardMouseMotionListener(ScorePanel s, byte[] p)
		{
			scorePanel=s;
			position=new byte[p.length];
			for (int i=0;i<p.length;i++) position[i]=p[i];
		}
		
		public void setPosition(byte[] p)
		{
			position=new byte[p.length];
			for (int i=0;i<p.length;i++) position[i]=p[i];
			lastX=-1;
			lastY=-1;
		}
		
		private boolean enabled=true;
		
		public void setEnabled(boolean value)
		{
			enabled=value;
		}
		
		public void reset()
		{
			lastX=-1;
			lastY=-1;
			scorePanel.setPossibleScore(0);
		}
		
		public void mouseMoved(MouseEvent e) 
		{
			computePossibleScore(e.getX(), e.getY());
	    }
		
		public void computePossibleScore(int x, int y)
		{
			if (enabled)
			{
				/*int newX = x/fieldWidth;
				int newY = y/fieldHeight;
				
				if (((newX!=lastX)||(newY!=lastY))&&(newX<boardXSize)&&(newY<boardYSize))
				{	
					lastX=newX;
					lastY=newY;
					byte[] positioncopy = new byte[position.length];
					for (int i=0;i<positioncopy.length;i++) positioncopy[i]=position[i];
					if (SameGameBoard.legalMove(position, boardXSize, boardYSize, newX, newY))
					{
						int bloks = SameGameBoard.makeMove(positioncopy, boardXSize, boardYSize, newX, newY, positioncopy[newX+(newY*boardXSize)],(byte)(-1));
						SameGameBoard.changedRows=new int[SameGameBoard.changedRows.length];
						if ((bloks>1)&&(currentMode==SAMEGAME)) scorePanel.setPossibleScore((bloks-2)*(bloks-2));
						else if ((bloks>1)&&(currentMode==BUBBLEBREAKER)) scorePanel.setPossibleScore((bloks)*(bloks-1));
						else if ((bloks>1)&&(currentMode==CLICKOMANIA)) scorePanel.setPossibleScore(bloks);
						else scorePanel.setPossibleScore(0);
					}
					else
					{
						scorePanel.setPossibleScore(0);
					}
				}*/
			}
		}

	    public void mouseDragged(MouseEvent e) 
	    {

	    }

	}
	
	public void setRectangles(boolean value)
	{
		rectangles=value;
	}
	
	public void removeMouseListener()
	{
		mouseMovementListener.setEnabled(false);
	}
	
	public void addMouseListener()
	{
		mouseMovementListener.setEnabled(true);
	}
}
