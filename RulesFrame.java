import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class RulesFrame extends JFrame 
{
	public RulesFrame()
	{
		this.setResizable(false);
		JPanel p = new JPanel();
		JTextArea t = new JTextArea();
		t.setText("Do 2 or more bricks of the same color touch each other directly \n" +
				"(i.e. horizontically or vertically), thay can be taken out. \n" +
				"The bricks above will fall down afterwards. \n \n"+
				"If one column is completely cleared the columns right of it move to the left. \n \n" +
				"The more bricks you clear at the same time, the more points you get. \n" +
				"The score is calculated by the formula Points = (amount of bricks - 2)^2. \n" +
				"If the game is over the remaining bricks are counted and points calculated by the \n" +
				"same formular are substracted from the total score. If however the container is \n" +
				"completely empty you get a bonus of 1000 points. \n \n" +
				"The object of the game is to score as many points as possible.");
		t.setEditable(false);
		p.add(t);
		this.setTitle("Rules");
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		this.setPreferredSize(new Dimension(680,275));
		this.setBounds(((int)(d.getWidth()/2))-150, ((int)(d.getHeight()/2))-150, 300, 300);
		
		this.add(p);
	}
	
}
