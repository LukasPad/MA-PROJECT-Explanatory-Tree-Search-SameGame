package OldCode;

import GroupCode.ExplanationPanel;
import GroupCode.MCTSPlayerFC;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class MainFrame extends JFrame
{
	
	public static Color[] colors = new Color[10];
	private BoardPanel boardPanel;
	private ExplanationPanel explanationPanel;
	JRadioButtonMenuItem humanPlay;
	JRadioButtonMenuItem computerPlay;
	JRadioButtonMenuItem xaiPlay;
	JRadioButtonMenuItem rectangles;
	JRadioButtonMenuItem ellipses;
	JCheckBoxMenuItem letters;
	JRadioButtonMenuItem sameGameScore, bubbleBreakerScore, clickomaniaScore;
	private ScorePanel scorePanel;
	private ComputerPlayThread t;
	private PlayerActionListener pal;
	private PlayerActionListener cal;
	private PlayerActionListener xal;
	private Settings settings = new Settings();
	
	public MainFrame()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("PROGRAMNAME");
		this.setLayout(new BorderLayout());
		this.addWindowListener(new MainFrameWindowListener());
		
		settings.load();
		
		colors[0]=Color.yellow;
		colors[1]=Color.red;
		colors[2]=Color.blue;
		colors[3]=Color.green;
		colors[4]=Color.pink;
		
		/*colors[0]=new Color(130,130,130);
		colors[1]=Color.white;
		colors[2]=Color.lightGray;
		colors[3]=new Color(100,100,100);
		colors[4]=new Color(160,160,160);*/
		
		colors[5]=Color.cyan;
		colors[6]=Color.magenta;
		colors[7]=Color.orange;
		colors[8]=Color.white;
		colors[9]=Color.gray;
		
		Random r = new Random();
		byte[] position = new byte[225];
		for (int i=0;i<position.length;i++)
			position[i]=(byte)r.nextInt(5);
		scorePanel = new ScorePanel(5,this);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(scorePanel,BorderLayout.WEST);
		HistoryPanel h = new HistoryPanel(this);

		// xai
		explanationPanel = new ExplanationPanel();
		explanationPanel.setSize(300, 300);
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		left.add(explanationPanel, BorderLayout.CENTER);
		this.add(left, BorderLayout.WEST);
		
		//this.add(scorePanel,BorderLayout.EAST);
		boardPanel = new BoardPanel(position,15,15,5,(ScorePanel)scorePanel,this, h, explanationPanel);
		boardPanel.setSize(300, 300);
		explanationPanel.setBoardPanel(boardPanel);
		
		h.setBoardPanel(boardPanel);
		
		h.addMove(position,"Start",0,0,0,true);
		p.add(h,BorderLayout.EAST);
		this.add(p,BorderLayout.EAST);

		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		
		center.add(boardPanel, BorderLayout.CENTER);
		center.add(new SideBoardPanel(false,15,boardPanel), BorderLayout.WEST);
		center.add(new SideBoardPanel(true,15,boardPanel),BorderLayout.SOUTH);
		
		this.add(center,BorderLayout.CENTER);
		h.setBoardPanel(boardPanel);
		boardPanel.setPosition(position, 15, 15, 5);
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		
		this.setPreferredSize(new Dimension(1000,600));
		this.setBounds(((int)(d.getWidth()/2))-350, ((int)(d.getHeight()/2))-300, 1000, 600);
		
		JMenuBar menuBar;
		JMenu game,player,view,score, about;
		JMenuItem menuItem;

		menuBar = new JMenuBar();
		
		game = new JMenu("Game");
		menuBar.add(game);
		menuItem = new JMenuItem("New Game");
		game.add(menuItem);
		menuItem.addActionListener(new NewGameActionListener());
		menuItem = new JMenuItem("Load Game");
		menuItem.addActionListener(new LoadGameActionListener());
		game.add(menuItem);
		menuItem = new JMenuItem("Save Game");
		menuItem.addActionListener(new SaveGameActionListener());
		game.add(menuItem);
		
		menuItem = new JMenuItem("Exit");
		game.add(menuItem);
		menuItem.addActionListener(new CloseActionListener());
		
		player = new JMenu("Player");
		menuBar.add(player);
		menuItem = new JMenuItem("AI Options");
		menuItem.addActionListener(new AIOptionsActionListener());
		player.add(menuItem);
		player.addSeparator();
		ButtonGroup group = new ButtonGroup();
		t = new ComputerPlayThread(new RandomBot(),boardPanel,scorePanel,0,0,0);
		t.start();
		humanPlay = new JRadioButtonMenuItem("Human Play");
		pal = new PlayerActionListener(t);
		humanPlay.addActionListener(pal);
		group.add(humanPlay);
		humanPlay.setSelected(true);
		player.add(humanPlay);
		computerPlay = new JRadioButtonMenuItem("Computer Play");
		cal = new PlayerActionListener(t);
		computerPlay.addActionListener(cal);
		group.add(computerPlay);
		player.add(computerPlay);
		xaiPlay = new JRadioButtonMenuItem("MCTS + Explanations)");
		xal = new PlayerActionListener(t);
		xaiPlay.addActionListener(xal);
		group.add(xaiPlay);
		player.add(xaiPlay);
		
		score = new JMenu("Score");
		menuBar.add(score);
		ButtonGroup scoregroup = new ButtonGroup();
		sameGameScore = new JRadioButtonMenuItem("SameGame");
		sameGameScore.setSelected(true);
		sameGameScore.addActionListener(new ChangeScoreActionListener());
		score.add(sameGameScore);
		scoregroup.add(sameGameScore);
		bubbleBreakerScore = new JRadioButtonMenuItem("BubbleBreaker");
		bubbleBreakerScore.addActionListener(new ChangeScoreActionListener());
		score.add(bubbleBreakerScore);
		scoregroup.add(bubbleBreakerScore);
		clickomaniaScore = new JRadioButtonMenuItem("Clickomania");
		clickomaniaScore.addActionListener(new ChangeScoreActionListener());
		score.add(clickomaniaScore);
		scoregroup.add(clickomaniaScore);
		
		view=new JMenu("View");
		menuBar.add(view);
		menuItem = new JMenuItem("Position String");
		view.add(menuItem);
		menuItem.addActionListener(new PositionStringActionListener());
		menuItem = new JMenuItem("Primary Variation");
		view.add(menuItem);
		menuItem.addActionListener(new PrimaryVariantActionListener());
		view.addSeparator();
		ButtonGroup g = new ButtonGroup();
		rectangles=new JRadioButtonMenuItem("Rectabgles");
		rectangles.setSelected(true);
		rectangles.addActionListener(new ViewActionListener());
		g.add(rectangles);
		view.add(rectangles);
		ellipses=new JRadioButtonMenuItem("Ellipses");
		ellipses.addActionListener(new ViewActionListener());
		g.add(ellipses);
		view.add(ellipses);
		view.addSeparator();
		letters=new JCheckBoxMenuItem("Letters");
		letters.addActionListener(new ViewActionListener());
		view.add(letters);

		
		about = new JMenu("About");
		menuBar.add(about);
		menuItem = new JMenuItem("Rules");
		about.add(menuItem);
		menuItem.addActionListener(new RulesActionListener());
		menuItem = new JMenuItem("About");
		about.add(menuItem);
		menuItem.addActionListener(new AboutActionListener());
		
		this.add(menuBar,BorderLayout.NORTH);

	}

	public static void main(String[] args) 
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e) {}

		
		JFrame frame = new MainFrame();
		frame.pack();
		frame.show();
	}
	
	public void setBotThread(BoardPanel boardPanel,ScorePanel scorePanel)
	{
		t.setPosition(boardPanel.getPosition());
		t.setEnded(false);
		t.setBoardPanel(boardPanel);
		t.stopIt();
		pal.setThread(t);
		cal.setThread(t);
		xal.setThread(t);
	}
	
	public void setEnded(boolean b)
	{
		t.setEnded(b);
	}
	
	public ComputerPlayThread getComputerPlayThreat()
	{
		return t;
	}
	
	private class AIOptionsActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			AIOptionsFrame f = new AIOptionsFrame(t,settings);
			f.pack();
			f.show();
		}
	}
	
	private class CloseActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			settings.safe();
			System.exit(0);
		}
	}
	
	private class AboutActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFrame about = new AboutFrame();
			about.pack();
			about.show();
		}
	}
	
	private class RulesActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFrame rules = new RulesFrame();
			rules.pack();
			rules.show();
		}
	}
	
	private class NewGameActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFrame newGame = new NewGameFrame(boardPanel,settings);
			newGame.pack();
			newGame.show();
		}
	}
	
	private class PlayerActionListener implements ActionListener
	{
		private ComputerPlayThread t;
		//private HistoryPanel history;
		
		public PlayerActionListener(ComputerPlayThread thread/*, HistoryPanel h*/)
		{
			t=thread;
			//history=h;
		}
		
		public void setThread(ComputerPlayThread thread)
		{
			t=thread;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if (humanPlay.isSelected())
			{
				
				((BoardPanel)boardPanel).setHumanPlay(true);
				((BoardPanel)boardPanel).setScore(t.getScore(boardPanel.SAMEGAME),t.getScore(boardPanel.BUBBLEBREAKER),t.getScore(boardPanel.CLICKOMANIA));
				t.stopIt();
			}
			if (computerPlay.isSelected() || xaiPlay.isSelected())
			{
				boardPanel.getHistoryPanel().select(boardPanel.getHistoryPanel().getHistorySize()-1);

				if (xaiPlay.isSelected()) {
					MCTSPlayerFC bot = new MCTSPlayerFC();
					t.setBot(bot);
					explanationPanel.setBot(bot);
					settings.setTimePerMove(100);
					boardPanel.setXaiPlay(true);
				} else {
					boardPanel.setXaiPlay(false);
				}

				((BoardPanel)boardPanel).setHumanPlay(false);
				t.setScore(((BoardPanel)boardPanel).getHistoryPanel().getLastScore(boardPanel.SAMEGAME),boardPanel.SAMEGAME);
				t.setScore(((BoardPanel)boardPanel).getHistoryPanel().getLastScore(boardPanel.BUBBLEBREAKER),boardPanel.BUBBLEBREAKER);
				t.setScore(((BoardPanel)boardPanel).getHistoryPanel().getLastScore(boardPanel.CLICKOMANIA),boardPanel.CLICKOMANIA);
				t.setPosition(((BoardPanel)boardPanel).getHistoryPanel().getLastPosition());
				boardPanel.setPosition(t.getPosition(), boardPanel.getXDim(), boardPanel.getYDim(), boardPanel.getNumberofColors());
				t.startIt();
			}
		}
	}
	
	private class ViewActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (rectangles.isSelected()) boardPanel.setRectangles(true);
			else if (ellipses.isSelected())boardPanel.setRectangles(false);
			boardPanel.setLetters(letters.isSelected());
			repaint();
		}
	}
	
	private class PositionStringActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			byte[] p = boardPanel.getPosition();
			String ps = "";
			for (int i=0;i<p.length-1;i++)
			{
				if ((i+1)%boardPanel.getXDim()!=0) ps+=(p[i]+1)+",";
				else ps+=(p[i]+1)+";\n";
			}
			ps+=(p[p.length-1]+1)+";";
			TextTransfer clipboard = new TextTransfer();
			clipboard.setClipboardContents(ps);

			JOptionPane.showMessageDialog(null, "The position string has been copied to your clipboard. \n \n"+ps);
		}
	}
	
	
	private class PrimaryVariantActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String input = JOptionPane.showInputDialog(null, "Please enter primary variation.");
			
			if (input!=null)
			{
				input = removeChar(input, '[');
				input = removeChar(input, ']');
				input = input.replace(',',' ');
				System.out.println(input);
				String[] splitted = input.split(" ");
				HistoryPanel history = boardPanel.getHistoryPanel();
				byte[] p = boardPanel.getPosition();
				int sgScore=0;
				int bbScore=0;
				int cmScore=0;
				try
				{
					for (int i=0;i<splitted.length;i+=2)
					{
						int moveX=Integer.parseInt(splitted[i]);
						int moveY=Integer.parseInt(splitted[i+1]);
						System.out.print(boardPanel.numberToString(moveX)+(15-moveY+1)+" ");
						
						int move=moveX+(15*moveY);
						System.out.println(moveX+" "+moveY);
						if (!SameGameBoard.legalMove(p, 15, 15, moveX, moveY)) throw new Exception();
						//System.out.println("Was valid.");
						int score = SameGameBoard.makeMove(p, 15, 15, moveX, moveY, p[move], (byte)-1);
						sgScore+=(score-2)*(score-2);
						bbScore+=score*(score-1);
						cmScore+=score;
						SameGameBoard.dropDownStones(p, 15, 15);
						if (i==splitted.length-2)
						{
							if (p[14*15]==-1) sgScore+=1000;
							else
							{
								int[] colors = new int[10];
								for (int j=0;j<p.length;j++)
									if (p[j]!=-1) colors[p[j]]++;
								for (int j=0;j<10;j++)
									if (colors[j]>=2)sgScore-=(colors[j]-2)*(colors[j]-2);
							}
						}
						//SameGameBoard.println(p, 15, 15);
						if (i!=splitted.length-2) history.addMove(p, BoardPanel.numberToString(moveX)+" "+moveY, sgScore, bbScore, cmScore, true);
						else history.addMove(p, BoardPanel.numberToString(moveX)+" "+moveY, sgScore, bbScore, cmScore, true);
					}
				}
				catch (Exception f)
				{
					f.printStackTrace();
					JOptionPane.showMessageDialog(null, "Invalid");
				}
				System.out.println();
			}
		}
	}
	
	public static String removeChar(String s, char c) 
	{
	    String r = "";
	    for (int i = 0; i < s.length(); i ++) {
	       if (s.charAt(i) != c) r += s.charAt(i);
	       }
	    return r;
	    }
	
	public void setHumanPlay()
	{
		humanPlay.setSelected(true);
		((BoardPanel)boardPanel).setHumanPlay(true);
	}
	
	private final class TextTransfer implements ClipboardOwner 
	{
		public void lostOwnership( Clipboard aClipboard, Transferable aContents) 
		{
		}

		  public void setClipboardContents( String aString )
		  {
		    StringSelection stringSelection = new StringSelection( aString );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, this );
		  }

		  public String getClipboardContents() {
		    String result = "";
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		 
		    Transferable contents = clipboard.getContents(null);
		    boolean hasTransferableText =
		      (contents != null) &&
		      contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		    if ( hasTransferableText ) {
		      try {
		        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
		      }
		      catch (UnsupportedFlavorException ex){
		        System.out.println(ex);
		        ex.printStackTrace();
		      }
		      catch (IOException ex) {
		        System.out.println(ex);
		        ex.printStackTrace();
		      }
		    }
		    return result;
		  }
		}
	
	

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	} 
	
	private class LoadGameActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser;
			if (settings.getDefaultDirectory().getName().equals("")) chooser = new JFileChooser();
			else chooser = new JFileChooser(settings.getDefaultDirectory());
			chooser.setFileFilter(new SameGameFileFilter());
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
	            File file = chooser.getSelectedFile();
	            settings.setDefaultDirectory(file);
	            if (!file.exists()) JOptionPane.showMessageDialog(new JFrame(),
					    "File does not exist.",
					    "File Error",
					    JOptionPane.ERROR_MESSAGE);
	            try
	            {
	            	FileReader fr = new FileReader(file);  
	            	BufferedReader reader = new BufferedReader(fr);
	            	String line = reader.readLine();
	            	String[] tokens = line.split(" ");
	            	int xDim = Integer.parseInt(tokens[0]);
	            	int yDim = Integer.parseInt(tokens[1]);
	            	int numberOfColors = Integer.parseInt(tokens[2]);
	            	
	            	line = reader.readLine();
	            	tokens = line.split(" ");
	            	byte[] position = new byte[tokens.length];
	            	for (int i=0;i<tokens.length;i++) position[i]=Byte.parseByte(tokens[i]);
	            	reader.readLine();
	            	reader.readLine();
	            	HistoryPanel history = boardPanel.getHistoryPanel();
	            	history.newGame(position);
	            	
	            	int sgScore=0;
	            	int bbScore=0;
	            	int cmScore=0;
	            	
	            	while (line!=null)
	            	{
	            		line = reader.readLine();
	            		if (line!=null)
	            		{
		            		tokens = line.split(" ");
			            	position = new byte[tokens.length];
			            	for (int i=0;i<tokens.length;i++) position[i]=Byte.parseByte(tokens[i]);
			            	String move = reader.readLine();
			            	String[] scores = (reader.readLine()).split(" ");
			            	sgScore=Integer.parseInt(scores[0]);
			            	bbScore=Integer.parseInt(scores[1]);
			            	cmScore=Integer.parseInt(scores[2]);
			            	history.addMove(position, move, sgScore,bbScore,cmScore, false);
	            		}
	            	}
	            	
	            	history.update();
	            	
	            	t.stopIt();
	            	boardPanel.setHumanPlay(true);
	            	setHumanPlay();
	            	t.setPosition(position);
	            	if (boardPanel.getScoreMode() == boardPanel.SAMEGAME) boardPanel.getScorePanel().setScore(sgScore);
	            	if (boardPanel.getScoreMode() == boardPanel.BUBBLEBREAKER) boardPanel.getScorePanel().setScore(bbScore);
	            	if (boardPanel.getScoreMode() == boardPanel.CLICKOMANIA) boardPanel.getScorePanel().setScore(cmScore);
	            	boardPanel.setScore(sgScore,bbScore,cmScore);
	            	
	            	boardPanel.setPosition(position, xDim, yDim, numberOfColors);
	            }
	            catch (Exception ex)
	            {
	            	JOptionPane.showMessageDialog(new JFrame(),
						    "File corrupt.",
						    "File Error",
						    JOptionPane.ERROR_MESSAGE);
	            }
			}
		}
	}
	
	private class ChangeScoreActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			setHumanPlay();
			t.stopIt();
			
			if (sameGameScore.isSelected())
			{
				boardPanel.setScoreMode(boardPanel.SAMEGAME);
			}
			if (bubbleBreakerScore.isSelected())
			{
				boardPanel.setScoreMode(boardPanel.BUBBLEBREAKER);
			}
			if (clickomaniaScore.isSelected())
			{
				boardPanel.setScoreMode(boardPanel.CLICKOMANIA);
			}
		}
	}
	
	private class SaveGameActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser chooser;
			if (settings.getDefaultDirectory().getName().equals("")) chooser = new JFileChooser();
			else chooser = new JFileChooser(settings.getDefaultDirectory());
			chooser.setFileFilter(new SameGameFileFilter());
			int returnVal = chooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				try
				{
	            	File file = chooser.getSelectedFile();
	            	String[] t = (file.getName()).split("\\.");
	            	if (t.length==1) file = new File(file.getAbsolutePath()+".sg");
	            	if (file.exists()) file.delete();
	            	file.createNewFile();
	            	settings.setDefaultDirectory(file);
	            	BufferedWriter out = new BufferedWriter(new FileWriter(file));
	            	HistoryPanel h = boardPanel.getHistoryPanel();
	            	out.write(boardPanel.getXDim()+" "+boardPanel.getYDim()+" "+boardPanel.getNumberofColors()+"\n");
	            	
	            	for (int i=0;i<h.getHistorySize();i++)
	            	{
	            		int[] p = h.getPosition(i);
	            		for (int j=0;j<p.length;j++) out.write(p[j]+" ");
	            		out.write("\n");
	            		out.write(h.getMove(i)+"\n");
	            		out.write(h.getScore(i,boardPanel.SAMEGAME)+" "+h.getScore(i,boardPanel.BUBBLEBREAKER)+" "+h.getScore(i,boardPanel.CLICKOMANIA)+"\n");
	            	}
	            	
	            	out.close();
				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	private class SameGameFileFilter extends FileFilter
	{
		public boolean accept(File f)
		{
			if (f.getName()==null) return true;
			if (f.getName()=="") return true;
			if (f.isDirectory()) return true;
			String[] t = (f.getName()).split("\\.");
			if (t.length<2) return false;
			if (t[1].equals("sg")) return true;
			return false;
		}
		
		public String getDescription()
		{
			return ".sg";
		}
	}
	
	private class MainFrameWindowListener implements WindowListener
	{
		public void windowClosing(WindowEvent e) {
			settings.safe();
	    }

	    public void windowClosed(WindowEvent e) 
	    {
	    }

	    public void windowOpened(WindowEvent e) {
	    }

	    public void windowIconified(WindowEvent e) {
	    }

	    public void windowDeiconified(WindowEvent e) {
	    }

	    public void windowActivated(WindowEvent e) {
	    }

	    public void windowDeactivated(WindowEvent e) {
	    }

	}
}
