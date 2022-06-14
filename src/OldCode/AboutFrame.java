package OldCode;//import java.awt.Graphics;
//import java.awt.Graphics2D;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AboutFrame extends JFrame 
{
	//private Graphics2D g2;
	
	public AboutFrame()
	{
		this.setResizable(false);
		JPanel p = new JPanel();
		JTextArea t = new JTextArea();
		t.setText("PROGRAMNAME \n" +
				"\n" +
				"This program has been developed for the project TACTICS. \n" +
				"\n" +
				"Programmer: Maarten Schadd \n" +
				"\n" +
				"Maastricht 2007");
		t.setEditable(false);
		p.add(t);
		this.setTitle("About");
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		this.setPreferredSize(new Dimension(475,175));
		this.setBounds(((int)(d.getWidth()/2))-150, ((int)(d.getHeight()/2))-150, 300, 300);
		
		this.add(p);
	}
	
	/*public void paintComponent(Graphics g) 
	{
		g2 = (Graphics2D) g;
		
	}*/
	
}
