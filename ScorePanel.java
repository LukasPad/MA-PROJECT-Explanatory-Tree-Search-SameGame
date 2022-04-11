import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ScorePanel extends JPanel
{
	private static int numberOfColors;
	private static JLabel[] colorsLeft;
	private static JLabel scoreLabel;
	private static JLabel possibleLabel;
	private Graphics2D g2;
	private int gameScore=0;
	private JFrame mainFrame;
	
	public ScorePanel(int n, JFrame m)
	{
		mainFrame=m;
		numberOfColors=n;
		colorsLeft=new JLabel[numberOfColors];
		GridLayout g = new GridLayout(numberOfColors+2,0);
		this.setLayout(g);
		for (int i=0;i<numberOfColors;i++)
		{
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(0,3));
			p.add(new JLabel(""));
			colorsLeft[i] = new JLabel("   0");
			colorsLeft[i].setBackground(MainFrame.colors[i]);
			colorsLeft[i].setOpaque(true);
			p.add(colorsLeft[i]);
			p.add(new JLabel(""));
			this.add(p);
		}
		JLabel score = new JLabel(" Score: 0 ");
		this.add(score);
		possibleLabel = new JLabel(" Possible: 0 ");
		this.add(possibleLabel);
		
		this.setPreferredSize(new Dimension(80,500));
	}
	
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		
	}
	
	public void setFieldsLeft(int[] leftOvers)
	{
		this.removeAll();
		numberOfColors=leftOvers.length;
		colorsLeft=new JLabel[numberOfColors];
		GridLayout g = new GridLayout(numberOfColors+2,0);
		this.setLayout(g);
		for (int i=0;i<numberOfColors;i++)
		{
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(0,3));
			p.add(new JLabel());
			if (leftOvers[i]<100) colorsLeft[i] = new JLabel("  "+leftOvers[i]+" ");
			else colorsLeft[i] = new JLabel(" "+leftOvers[i]+" ");
			colorsLeft[i].setBackground(MainFrame.colors[i]);
			colorsLeft[i].setOpaque(true);
			p.add(colorsLeft[i]);
			p.add(new JLabel());
			this.add(p);
		}
		scoreLabel = new JLabel(" Score: "+gameScore);
		this.add(scoreLabel);
		possibleLabel = new JLabel("Possible: 0  ");
		this.add(possibleLabel);
		Dimension s = mainFrame.getSize();
		mainFrame.setPreferredSize(s);
		mainFrame.pack();
		mainFrame.repaint();
	}
	
	public void setScore(int score)
	{
		gameScore=score;
		scoreLabel.setText(" Score: "+gameScore);
	}
	
	public void updateNumbers(int[] leftOvers, int score)
	{
		gameScore=score;
		for (int i=0;i<leftOvers.length;i++)
		{
			if (leftOvers[i]<100) colorsLeft[i].setText("  "+leftOvers[i]+" ");
			else colorsLeft[i].setText(" "+leftOvers[i]+" ");
			scoreLabel.setText(" Score: "+score);
			mainFrame.repaint();
		}
	}
	
	public void setPossibleScore(int number)
	{
		possibleLabel.setText(" Possible: "+number);
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setPreferredSize(new Dimension(300,300));
		
		JPanel p = new ScorePanel(3,null);
		frame.add(p);
		frame.pack();
		frame.show();
	}
}
