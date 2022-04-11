import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


public class HistoryPanel extends JPanel
{
	private ArrayList positionHistory = new ArrayList();
	private int[] sgScoreHistory = new int[0];
	private int[] bbScoreHistory = new int[0];
	private int[] cmScoreHistory = new int[0];
	private String[] moveHistory = new String[0];
	private int selected=0;
	private ArrayList moveTextFields = new ArrayList();
	private ArrayList scoreTextFields = new ArrayList();
	private BoardPanel boardPanel;
	private JButton continueButton;
	private MainFrame main;
	
	public HistoryPanel(MainFrame m)
	{
		main=m;
	}
	
	public void setBoardPanel(BoardPanel b)
	{
		boardPanel=b;
	}
	
	public void addMove(byte[] position, String newMove, int sgScore, int bbScore, int cmScore, boolean update)
	{
		byte[] p = new byte[position.length];
		for (int i=0;i<position.length;i++) p[i]=position[i];
		positionHistory.add(p);
		
		int[] newScoreHistory = new int[sgScoreHistory.length+1];
		for (int i=0;i<sgScoreHistory.length;i++)
			newScoreHistory[i]=sgScoreHistory[i];
		newScoreHistory[newScoreHistory.length-1]=sgScore;
		sgScoreHistory=newScoreHistory;
		
		newScoreHistory = new int[bbScoreHistory.length+1];
		for (int i=0;i<bbScoreHistory.length;i++)
			newScoreHistory[i]=bbScoreHistory[i];
		newScoreHistory[newScoreHistory.length-1]=bbScore;
		bbScoreHistory=newScoreHistory;
		
		newScoreHistory = new int[cmScoreHistory.length+1];
		for (int i=0;i<cmScoreHistory.length;i++)
			newScoreHistory[i]=cmScoreHistory[i];
		newScoreHistory[newScoreHistory.length-1]=cmScore;
		cmScoreHistory=newScoreHistory;
		
		String[] newMoveHistory = new String[moveHistory.length+1];
		for (int i=0;i<moveHistory.length;i++)
			newMoveHistory[i]=moveHistory[i];
		newMoveHistory[newMoveHistory.length-1]=newMove;
		moveHistory=newMoveHistory;
		selected=positionHistory.size()-1;
		if (update) update();
	}
	
	public void update()
	{
		this.removeAll();
		moveTextFields = new ArrayList();
		scoreTextFields = new ArrayList();
		
		this.setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(80,40));
		p.setLayout(null);
		JLabel text = new JLabel("History");
		text.setBounds(20, 0, 40, 20);
		p.add(text);
		text = new JLabel("Move");
		text.setBounds(6, 21, 50, 20);
		p.add(text);
		text = new JLabel("Score");
		text.setBounds(46, 21, 50, 20);
		p.add(text);
		this.add(p,BorderLayout.NORTH);
		
		p=new JPanel();
		p.setLayout(null);
		for (int i=0;i<positionHistory.size();i++)
		{
			JTextField t = new JTextField(moveHistory[i]);
			t.setBounds(0, (i*20), 40, 20);
			t.setEditable(false);
			if (i==selected) t.setBackground(Color.CYAN);
			moveTextFields.add(t);
			t.addMouseListener(new HistoryActionListener(i,this));
			p.add(t);
			if (boardPanel.getScoreMode()==boardPanel.SAMEGAME) t = new JTextField(""+sgScoreHistory[i]);
			if (boardPanel.getScoreMode()==boardPanel.BUBBLEBREAKER) t = new JTextField(""+bbScoreHistory[i]);
			if (boardPanel.getScoreMode()==boardPanel.CLICKOMANIA) t = new JTextField(""+cmScoreHistory[i]);
			t.setBounds(40, (i*20), 40, 20);
			t.setEditable(false);
			if (i==selected) t.setBackground(Color.CYAN);
			scoreTextFields.add(t);
			t.addMouseListener(new HistoryActionListener(i,this));
			p.add(t);
		}
		p.setPreferredSize(new Dimension(80,(positionHistory.size())*20));
		JScrollPane scroll = new JScrollPane(p);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(scroll,BorderLayout.CENTER);
		
		p=new JPanel();
		p.setLayout(new GridLayout(1,3));
		continueButton = new JButton("Continue");
		continueButton.setEnabled(false);
		continueButton.addActionListener(new ContinueButtonActionListener(this));
		p.add(continueButton);
		this.add(p,BorderLayout.SOUTH);
		JScrollBar bar = scroll.getVerticalScrollBar();
		bar.setMaximum(Integer.MAX_VALUE);
		bar.setValue(bar.getMaximum());
		
		main.setPreferredSize(main.getSize());
		main.pack();
		//main.repaint();
		//if (getTopLevelAncestor()!=null) getTopLevelAncestor().repaint();
	}
	
	public void select(int number)
	{
		JTextField f = (JTextField)moveTextFields.get(selected);
		f.setBackground(new Color(236,233,216));
		f = (JTextField)scoreTextFields.get(selected);
		f.setBackground(new Color(236,233,216));
		selected=number;
		f = (JTextField)moveTextFields.get(selected);
		f.setBackground(Color.CYAN);
		f = (JTextField)scoreTextFields.get(selected);
		f.setBackground(Color.CYAN);
		
		byte[] p = (byte[])positionHistory.get(selected);
		byte[] newPos = new byte[p.length];
		for (int i=0;i<p.length;i++) newPos[i]=p[i];
		
		boardPanel.setPosition(newPos, boardPanel.getXDim(), boardPanel.getYDim(), boardPanel.getNumberofColors());
		
		if (number==positionHistory.size()-1)
		{
			boardPanel.setHumanPlay(true);
			continueButton.setEnabled(false);
		}
		else 
		{
			boardPanel.setHumanPlay(false);
			continueButton.setEnabled(true);
		}
	}
	
	public void continueFromHere()
	{
		boardPanel.setHumanPlay(true);
		continueButton.setEnabled(false);
		
		String[] newmoveHistory = new String[selected+1];
		
		while (positionHistory.size()>selected+1) positionHistory.remove(selected+1);
		
		int[] newsgscoreHistory = new int[selected+1];
		int[] newbbscoreHistory = new int[selected+1];
		int[] newcmscoreHistory = new int[selected+1];
		for (int i=0;i<=selected;i++)
		{
			newsgscoreHistory[i]=sgScoreHistory[i];
			newbbscoreHistory[i]=bbScoreHistory[i];
			newcmscoreHistory[i]=cmScoreHistory[i];
			newmoveHistory[i]=moveHistory[i];
		}
		sgScoreHistory=newsgscoreHistory;
		bbScoreHistory=newbbscoreHistory;
		cmScoreHistory=newcmscoreHistory;
		moveHistory=newmoveHistory;
		
		if (boardPanel.getScoreMode()==boardPanel.SAMEGAME) boardPanel.getScorePanel().setScore(sgScoreHistory[selected]);
		if (boardPanel.getScoreMode()==boardPanel.BUBBLEBREAKER) boardPanel.getScorePanel().setScore(bbScoreHistory[selected]);
		if (boardPanel.getScoreMode()==boardPanel.CLICKOMANIA) boardPanel.getScorePanel().setScore(cmScoreHistory[selected]);
		boardPanel.setScore(sgScoreHistory[selected],bbScoreHistory[selected],cmScoreHistory[selected]);
		
		ComputerPlayThread t = boardPanel.getMainFrame().getComputerPlayThreat();
		t.setPosition(boardPanel.getPosition());
		t.setScore(sgScoreHistory[selected],boardPanel.SAMEGAME);
		t.setScore(bbScoreHistory[selected],boardPanel.BUBBLEBREAKER);
		t.setScore(cmScoreHistory[selected],boardPanel.CLICKOMANIA);
		t.setEnded(false);
		
		update();
	}
	
	public void newGame(byte[] position)
	{
		positionHistory=new ArrayList();
		byte[] newP = new byte[position.length];
		for (int i=0;i<position.length;i++) newP[i]=position[i];
		positionHistory.add(newP);
		sgScoreHistory=new int[1];
		bbScoreHistory=new int[1];
		cmScoreHistory=new int[1];
		moveHistory=new String[1];
		moveHistory[0]="Start";
		selected=0;
		
		update();
	}
	
	public int getHistorySize()
	{
		return positionHistory.size();
	}
	
	public int[] getPosition(int i)
	{
		return (int[])positionHistory.get(i);
	}
	
	public String getMove(int i)
	{
		return moveHistory[i];
	}
	
	public int getScore(int i, int mode)
	{
		if (mode == boardPanel.SAMEGAME) return sgScoreHistory[i];
		if (mode == boardPanel.BUBBLEBREAKER) return bbScoreHistory[i];
		return cmScoreHistory[i];
	}
	
	public byte[] getLastPosition()
	{
		return (byte[])(positionHistory.get(positionHistory.size()-1));
	}
	
	public int getLastScore(int mode)
	{
		return getScore(sgScoreHistory.length-1,mode);
	}
	
	private class ContinueButtonActionListener implements ActionListener
	{
		private HistoryPanel history;
		
		public ContinueButtonActionListener(HistoryPanel h)
		{
			history=h;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			history.continueFromHere();
		}
	}
	
	private class HistoryActionListener implements MouseListener
	{
		private int number;
		private HistoryPanel history;
		
		public HistoryActionListener(int n, HistoryPanel h)
		{
			number=n;
			history=h;
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
			boardPanel.getMainFrame().setHumanPlay();
			history.select(number);
			ComputerPlayThread t = boardPanel.getMainFrame().getComputerPlayThreat();
			t.stopIt();
		}
		
		public void mouseExited(MouseEvent e)
		{
			
		}
	}
}


