package OldCode;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SideBoardPanel extends JPanel
{
	private boolean horizontal;
	private Font f;
	private JLabel[] index;
	private int boardSize;
	private BoardPanel boardPanel;
	
	public SideBoardPanel(boolean h, int b, BoardPanel board) 
	{
		this.setLayout(null);
		index = new JLabel[b];
		boardSize=b;
		boardPanel=board;
		f = new Font("Arial",Font.BOLD,14);
		horizontal=h;
		
		if (horizontal)
		{
			this.setPreferredSize(new Dimension(30,30));
			for (int i=0;i<boardSize;i++)
			{
				index[i] = new JLabel(""+(boardPanel.numberToString(i)));
				index[i].setFont(f);
				this.add(index[i]);
			}
		}
		else
		{
			this.setPreferredSize(new Dimension(30,30));
			for (int i=0;i<boardSize;i++)
			{
				index[i] = new JLabel(""+(i+1));
				index[i].setFont(f);
				this.add(index[i]);
			}
		}
	}
	
	public void setNumber(int number)
	{
		this.removeAll();
		boardSize=number;
		
		index = new JLabel[boardSize];
		
		if (horizontal)
		{
			for (int i=0;i<boardSize;i++)
			{
				index[i] = new JLabel(""+(boardPanel.numberToString(i)));
				index[i].setFont(f);
				this.add(index[i]);
			}
		}
		else
		{
			for (int i=0;i<boardSize;i++)
			{
				index[i] = new JLabel(""+(i+1));
				index[i].setFont(f);
				this.add(index[i]);
			}
		}
	}
	
	public void paintComponent(Graphics g) 
	{
		int width = this.getWidth();
		int height = this.getHeight();
		
		if (horizontal)
		{
			if (boardSize!=boardPanel.getXDim()) setNumber(boardPanel.getXDim());
			boardSize=boardPanel.getXDim();
			int pixelPerNumber = (width-28)/boardSize;
			for (int i=0;i<boardSize;i++)
			{
				index[i].setBounds(35+(pixelPerNumber/2-10)+(pixelPerNumber*i), 5, 20, 20);
			}
		}
		else
		{
			if (boardSize!=boardPanel.getYDim()) setNumber(boardPanel.getYDim());
			boardSize=boardPanel.getYDim();
			int pixelPerNumber = height/boardSize;
			for (int i=0;i<boardSize;i++)
			{
				if (i<9) index[i].setBounds(13, (pixelPerNumber/2-10)+(pixelPerNumber*i), 20, 20);
				else index[i].setBounds(5, (pixelPerNumber/2-10)+(pixelPerNumber*i), 20, 20);
			}
		}
	}

}
