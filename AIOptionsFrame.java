import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AIOptionsFrame extends JFrame 
{
	private Choice aiChoice;
	private Choice evalChoice;
	//private JTextField maxGameTime;
	private JTextField maxMoveTime;
	private ComputerPlayThread threat;
	private Settings settings;
	
	public AIOptionsFrame(ComputerPlayThread playThreat, Settings s)
	{
		threat=playThreat;
		settings=s;
		this.setResizable(false);
		JPanel p = new JPanel();
		p.setLayout(null);
		
		this.setTitle("AI Options");
		
		JLabel label = new JLabel("Type:");
		label.setBounds(10,10,50,20);
		p.add(label);
		
		aiChoice = new Choice();
		aiChoice.add("RandomBot");
		aiChoice.add("AStarBot");
		aiChoice.add("IDAStarBot - OverEstimate");
		aiChoice.add("IDAStarBot - UnderEstimate");
		aiChoice.add("MaximaxBot");
		aiChoice.add("UCTBot");
		aiChoice.setBounds(50, 10, 200, 100);
		aiChoice.select(s.getBotType());
		p.add(aiChoice);
		
		label = new JLabel("Evaluation Function:");
		label.setBounds(10,40,150,20);
		p.add(label);
		
		evalChoice = new Choice();
		evalChoice.add("Overestimator 1.0");
		evalChoice.setBounds(115, 40, 125, 100);
		evalChoice.select(s.getEstimator());
		p.add(evalChoice);
		
		/*label = new JLabel("Maximum Thinking Time(sec):");
		label.setBounds(10,70,150,20);
		p.add(label);
		
		maxGameTime = new JTextField("0");
		maxGameTime.setBounds(160, 70, 50, 20);
		p.add(maxGameTime);
		
		label = new JLabel("(0=infinite)");
		label.setBounds(220,70,150,20);
		p.add(label);*/
		
		label = new JLabel("Time per Move(ms):");
		label.setBounds(10,70,150,20);
		p.add(label);
		
		int time = s.getTimePerMove();
		if (time == Integer.MAX_VALUE) time=0; 
		maxMoveTime = new JTextField(""+time);
		maxMoveTime.setBounds(115, 70, 50, 20);
		p.add(maxMoveTime);
		
		label = new JLabel("(0=infinite)");
		label.setBounds(175,70,150,20);
		p.add(label);
		
		JButton okButton = new JButton("Ok");
		okButton.setBounds(20, 230, 100, 20);
		okButton.addActionListener(new OkButtonActionListener(this));
		p.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setBounds(170, 230, 100, 20);
		cancelButton.addActionListener(new CancelButtonActionListener(this));
		p.add(cancelButton);
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		this.setPreferredSize(new Dimension(300,300));
		this.setBounds(((int)(d.getWidth()/2))-150, ((int)(d.getHeight()/2))-150, 300, 300);
		
		this.add(p);
	}
	
	private class OkButtonActionListener implements ActionListener
	{
		private JFrame f;
		public OkButtonActionListener(JFrame frame)
		{
			f=frame;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try {
				if (aiChoice.getSelectedItem().equals("RandomBot")) threat.setBot(new RandomBot());
				if (aiChoice.getSelectedItem().equals("AStarBot")) threat.setBot(new AStarBot());
				if (aiChoice.getSelectedItem().equals("IDAStarBot - OverEstimate")) threat.setBot(new IDAStarBotOverEstimate());
				if (aiChoice.getSelectedItem().equals("IDAStarBot - UnderEstimate")) threat.setBot(new IDAStarBotUnderEstimate());
				//if (aiChoice.getSelectedItem().equals("MaximaxBot")) threat.setBot(new Maximax());
				if (aiChoice.getSelectedItem().equals("UCTBot")) threat.setBot(new UCTPlayer());
			
				settings.setBotType(aiChoice.getSelectedIndex());
				settings.setEstimator(evalChoice.getSelectedIndex());
				if (!maxMoveTime.getText().equals("0")) settings.setTimePerMove(Integer.parseInt(maxMoveTime.getText()));
				else settings.setTimePerMove(Integer.MAX_VALUE);
			} 
			catch (NumberFormatException e1) 
			{
				JOptionPane.showMessageDialog(new JFrame(),
					    "Input is falsive. Please check and correct your input.",
					    "Input Error",
					    JOptionPane.ERROR_MESSAGE);
			}
			f.dispose();
		}
	}
	
	private class CancelButtonActionListener implements ActionListener
	{
		private JFrame f;
		public CancelButtonActionListener(JFrame frame)
		{
			f=frame;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			f.dispose();
		}
	}
}
