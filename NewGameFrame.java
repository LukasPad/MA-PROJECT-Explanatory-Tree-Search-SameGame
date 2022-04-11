import java.awt.Choice;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class NewGameFrame extends JFrame
{
	private JRadioButton presettingRadioButton,randomRadioButton, customPositionRadioButton;
	private Choice presetGameChoice;
	private JLabel dimensionLabel,xSize,ySize, colors, numberOfColors;
	private JTextField xSizeTextField, ySizeTextField, numberOfColorsTextField;
	private JButton okButton, cancelbutton;
	private JTextArea customTextArea;
	
	private BoardPanel boardPanel;
	
	public NewGameFrame(BoardPanel bPanel, Settings settings)
	{
		boardPanel=bPanel;
		this.setTitle("New Game");
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		this.setPreferredSize(new Dimension(300,350));
		this.setBounds(((int)(d.getWidth()/2))-150, ((int)(d.getHeight()/2))-150, 300, 300);
		
		this.setResizable(false);
		JPanel p = new JPanel();
		p.setLayout(null);
		ButtonGroup bg1 = new ButtonGroup();
		presettingRadioButton= new JRadioButton("Presetting");
		presettingRadioButton.setBounds(20, 250, 100, 15);
		bg1.add(presettingRadioButton);
		presettingRadioButton.addActionListener(new PresettingActionListener());
		p.add(presettingRadioButton);
		randomRadioButton= new JRadioButton("Random");
		randomRadioButton.setSelected(true);
		randomRadioButton.setBounds(20,20,100,15);
		bg1.add(randomRadioButton);
		randomRadioButton.addActionListener(new RandomActionListener());
		p.add(randomRadioButton);
		
		presetGameChoice = new Choice();
		presetGameChoice.add("Game 1");
		presetGameChoice.add("Game 2");
		presetGameChoice.add("Game 3");
		presetGameChoice.add("Game 4");
		presetGameChoice.add("Game 5");
		presetGameChoice.add("Game 6");
		presetGameChoice.add("Game 7");
		presetGameChoice.add("Game 8");
		presetGameChoice.add("Game 9");
		presetGameChoice.add("Game 10");
		presetGameChoice.add("Game 11");
		presetGameChoice.add("Game 12");
		presetGameChoice.add("Game 13");
		presetGameChoice.add("Game 14");
		presetGameChoice.add("Game 15");
		presetGameChoice.add("Game 16");
		presetGameChoice.add("Game 17");
		presetGameChoice.add("Game 18");
		presetGameChoice.add("Game 19");
		presetGameChoice.add("Game 20");
		presetGameChoice.setBounds(175, 250, 100, 15);
		presetGameChoice.setEnabled(false);
		p.add(presetGameChoice);
		
		dimensionLabel = new JLabel("Dimension");
		dimensionLabel.setBounds(30, 40, 100, 15);
		dimensionLabel.setEnabled(true);
		p.add(dimensionLabel);
		
		xSize = new JLabel("X-Size:");
		xSize.setBounds(30, 60, 50, 15);
		xSize.setEnabled(true);
		p.add(xSize);
		
		ySize = new JLabel("Y-Size:");
		ySize.setBounds(120, 60, 50, 15);
		ySize.setEnabled(true);
		p.add(ySize);
		
		colors = new JLabel("Colors");
		colors.setBounds(30, 90, 100, 15);
		colors.setEnabled(true);
		p.add(colors);
		
		numberOfColors = new JLabel("Number of Colors(1-10):");
		numberOfColors.setBounds(30, 110, 140, 15);
		numberOfColors.setEnabled(true);
		p.add(numberOfColors);
		
		xSizeTextField = new JTextField("15");
		xSizeTextField.setBounds(70, 57, 30, 20);
		xSizeTextField.setEnabled(true);
		p.add(xSizeTextField);
		
		ySizeTextField = new JTextField("15");
		ySizeTextField.setBounds(160, 57, 30, 20);
		ySizeTextField.setEnabled(true);
		p.add(ySizeTextField);
		
		numberOfColorsTextField = new JTextField("5");
		numberOfColorsTextField.setBounds(155, 107, 30, 20);
		numberOfColorsTextField.setEnabled(true);
		p.add(numberOfColorsTextField);
		
		customPositionRadioButton = new JRadioButton("Custom");
		customPositionRadioButton.setBounds(20, 140, 70, 15);
		bg1.add(customPositionRadioButton);
		customPositionRadioButton.addActionListener(new CustomPositionActionListener());
		customPositionRadioButton.setToolTipText("You can insert a position here. \n" +
				"Use 0-x with x the number of colors for your colors. \n" +
				"0 is an empty space. Insert numbers without spaces or other characters. \n" +
				"Start with the top row from left to right.");
		p.add(customPositionRadioButton);
		
		
		customTextArea = new JTextArea();
		customTextArea.setToolTipText("You can insert a position here. \n" +
				"Use 0-x with x the number of colors for your colors. \n" +
				"0 is an empty space. Insert numbers without spaces or other characters. \n" +
				"Start with the top row from left to right.");
		customTextArea.setEnabled(false);
		JScrollPane scroll = new JScrollPane(customTextArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(100,137,175,100);
		p.add(scroll);
		
		okButton = new JButton("Ok");
		okButton.setBounds(25, 280, 100, 20);
		okButton.addActionListener(new OKButtonActionListener(this,boardPanel.getMainFrame()));
		p.add(okButton);
		
		cancelbutton = new JButton("Cancel");
		cancelbutton.setBounds(175, 280, 100, 20);
		cancelbutton.addActionListener(new CancelButtonActionListener(this));
		p.add(cancelbutton);
			
		if (settings.isCustomSelected()) 
		{
			randomRadioButton.setSelected(false);
			presettingRadioButton.setSelected(false);
			presetGameChoice.setEnabled(false);
			dimensionLabel.setEnabled(false);
			xSize.setEnabled(false);
			ySize.setEnabled(false);
			colors.setEnabled(false);
			numberOfColors.setEnabled(false);
			xSizeTextField.setEnabled(false);
			ySizeTextField.setEnabled(false);
			numberOfColorsTextField.setEnabled(false);
			customTextArea.setEnabled(true);
			customPositionRadioButton.setSelected(true);
		}
		
		if (settings.isRandomSelected()) 
		{
			presettingRadioButton.setSelected(false);
			presetGameChoice.setEnabled(false);
			dimensionLabel.setEnabled(false);
			xSize.setEnabled(true);
			ySize.setEnabled(true);
			colors.setEnabled(true);
			numberOfColors.setEnabled(true);
			xSizeTextField.setEnabled(true);
			ySizeTextField.setEnabled(true);
			numberOfColorsTextField.setEnabled(true);
			customTextArea.setEnabled(false);
			customPositionRadioButton.setSelected(false);
			randomRadioButton.setSelected(true);
		}
		
		if (!(settings.isCustomSelected())&&!(settings.isRandomSelected())) 
		{
			randomRadioButton.setSelected(false);
			presettingRadioButton.setSelected(false);
			presetGameChoice.setEnabled(false);
			dimensionLabel.setEnabled(false);
			xSize.setEnabled(false);
			ySize.setEnabled(false);
			colors.setEnabled(false);
			numberOfColors.setEnabled(false);
			xSizeTextField.setEnabled(false);
			ySizeTextField.setEnabled(false);
			numberOfColorsTextField.setEnabled(false);
			customTextArea.setEnabled(false);
			customPositionRadioButton.setSelected(false);
			presettingRadioButton.setSelected(true);
			presetGameChoice.setEnabled(true);
		}
		
		xSizeTextField.setText(settings.getXSize()+"");
		ySizeTextField.setText(settings.getYSize()+"");
		numberOfColorsTextField.setText(settings.getNumberOfColors()+"");
		customTextArea.setText(settings.getCustomGame());
		
		this.add(p);
	}
	
	private class PresettingActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			presetGameChoice.setEnabled(true);
			dimensionLabel.setEnabled(false);
			xSize.setEnabled(false);
			ySize.setEnabled(false);
			colors.setEnabled(false);
			numberOfColors.setEnabled(false);
			xSizeTextField.setEnabled(false);
			ySizeTextField.setEnabled(false);
			numberOfColorsTextField.setEnabled(false);
			customTextArea.setEnabled(false);
		}
	}
	
	private class RandomActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			presetGameChoice.setEnabled(false);
			dimensionLabel.setEnabled(true);
			customPositionRadioButton.setEnabled(true);
			xSize.setEnabled(true);
			ySize.setEnabled(true);
			colors.setEnabled(true);
			numberOfColors.setEnabled(true);
			xSizeTextField.setEnabled(true);
			ySizeTextField.setEnabled(true);
			numberOfColorsTextField.setEnabled(true);
			customTextArea.setEnabled(false);
		}
	}
	
	private class CustomPositionActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			presetGameChoice.setEnabled(false);
			dimensionLabel.setEnabled(false);
			xSize.setEnabled(false);
			ySize.setEnabled(false);
			colors.setEnabled(false);
			numberOfColors.setEnabled(false);
			xSizeTextField.setEnabled(false);
			ySizeTextField.setEnabled(false);
			numberOfColorsTextField.setEnabled(false);
			customTextArea.setEnabled(true);
		}
	}

	private class OKButtonActionListener implements ActionListener
	{
		private JFrame f;
		private MainFrame m;
		public OKButtonActionListener(JFrame frame,MainFrame main)
		{
			f=frame;
			m=main;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				m.setHumanPlay();
				if (randomRadioButton.isSelected())
				{
					Random r = new Random();
					int xDim = Integer.parseInt(xSizeTextField.getText());
					int yDim = Integer.parseInt(ySizeTextField.getText());
					int colors =  Integer.parseInt(numberOfColorsTextField.getText());
					if ((colors<1)||(colors>10)||(xDim<=0)||(yDim<=0)) throw new Exception();
					byte[] position = new byte[xDim*yDim];
					for (int i=0;i<position.length;i++)
						position[i]=(byte)r.nextInt(colors);
					boardPanel.setPosition(position,xDim,yDim,colors);
				}
				if (customPositionRadioButton.isSelected())
				{
					String input = customTextArea.getText().trim();
					StringTokenizer t = new StringTokenizer(input,";");
					int yDim = t.countTokens();
					
					String line = t.nextToken();
					StringTokenizer t2 = new StringTokenizer(line, ",");
					int xDim = t2.countTokens();
					
					t = new StringTokenizer(input,";");
					
					byte[] position = new byte[xDim*yDim];
					
					int maxColor=0;
					int y=0;
					
					while (t.hasMoreTokens())
					{
						int x=0;
						line = t.nextToken();
						if (line.substring(0, 1).equals("\n")) line=line.substring(1);
						t2 = new StringTokenizer(line, ",");
						if (t2.countTokens()!=xDim) throw new Exception();
						while (t2.hasMoreTokens())
						{
							String test = t2.nextToken();
							position[x+(y*xDim)]=(byte)(Byte.parseByte(test)-1);
							if (position[x+(y*xDim)]+1>maxColor) maxColor=position[x+(y*xDim)]+1;
							x++;
						}
						y++;
					}
					boardPanel.setPosition(position,xDim,yDim,maxColor); 
				}
				if (presettingRadioButton.isSelected())
				{
					if (presetGameChoice.getSelectedIndex()==0)
					{
					    byte[] position = {3,1,1,4,1,0,4,0,4,4,1,1,0,2,3,
					    				  3,3,2,0,4,4,1,3,1,2,0,0,4,0,4,
									      0,2,3,4,3,0,3,0,0,3,4,4,1,1,1,
									      2,3,4,0,2,3,0,2,4,4,4,3,0,2,3,
									      1,2,1,3,1,2,0,1,2,1,0,3,4,0,1,
									      0,4,4,3,0,3,4,2,2,2,0,2,3,4,0,
									      2,4,3,4,2,3,1,1,1,3,4,1,0,3,1,
									      1,0,0,4,0,3,1,2,1,0,4,1,3,3,1,
									      1,3,3,2,0,4,3,1,3,0,4,1,0,0,3,
									      0,3,3,4,2,3,0,0,2,1,2,3,4,0,1,
									      0,4,1,2,0,1,3,4,3,3,4,1,4,0,4,
									      2,2,3,1,0,4,0,1,2,4,1,3,3,0,1,
									      3,3,0,2,3,2,1,4,3,1,3,0,2,1,3,
									      1,0,3,2,1,4,4,4,4,0,4,2,1,3,4,
									      1,0,1,0,1,1,2,2,1,0,0,1,4,3,2};
					    
					    boardPanel.setPosition(position,15,15,5);
					}
						
					if (presetGameChoice.getSelectedIndex()==1)
					{
						byte[] position = {3,3,0,1,0,2,1,2,3,2,3,1,1,1,0,
										  4,1,3,4,0,3,3,2,2,4,0,2,4,0,0,
										  2,3,2,2,0,3,1,0,4,4,0,2,4,0,4,
										  0,3,4,4,2,2,1,3,3,1,3,0,3,3,4,
										  0,0,2,1,2,1,3,4,3,2,1,2,3,1,4,
										  1,2,4,2,0,0,0,1,1,1,0,0,2,4,4,
										  1,0,3,3,3,2,1,0,4,2,4,1,4,3,0,
										  4,4,3,3,0,2,3,3,4,3,0,3,0,0,4,
										  3,3,3,1,4,3,3,3,0,4,2,0,3,2,0,
										  2,4,1,1,1,1,4,0,0,3,0,4,0,4,3,
										  3,3,0,1,4,1,2,1,1,0,3,4,2,1,0,
										  2,2,3,3,2,0,4,3,3,4,0,4,3,3,1,
										  0,1,3,2,1,2,1,1,0,2,4,1,4,0,3,
										  4,1,4,0,2,1,3,1,3,1,4,0,1,0,3,
										  1,3,2,3,2,2,4,2,2,4,3,0,3,1,1};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==2)
					{
						byte[] position = {4,2,4,3,1,0,3,3,2,2,4,3,1,4,2,
										  3,0,3,4,0,3,3,3,2,4,4,3,1,3,3,
										  2,0,4,4,0,1,2,2,2,3,4,0,4,4,0,
										  0,4,3,0,0,2,4,2,1,2,0,3,2,4,2,
										  0,2,0,2,0,1,1,3,2,1,1,2,3,4,0,
										  1,0,1,0,4,3,3,3,4,2,2,2,3,4,1,
										  2,3,4,3,4,2,2,4,2,4,3,4,4,0,1,
										  4,2,3,2,2,0,1,2,4,3,3,0,0,2,1,
										  3,4,4,3,0,4,3,4,1,0,0,2,1,4,3,
										  4,0,1,3,1,0,2,3,0,2,0,2,3,0,1,
										  4,2,0,0,0,2,2,1,0,2,3,1,1,3,1,
										  0,3,1,1,3,3,2,1,2,0,0,4,2,4,1,
										  2,1,4,4,4,0,3,3,4,2,0,0,2,0,0,
										  1,0,4,4,0,1,3,2,4,0,4,2,0,0,1,
										  2,2,2,2,3,3,0,4,3,3,4,0,4,1,2};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==3)
					{
						byte[] position = {4,2,2,4,1,3,3,2,4,0,4,2,3,4,2,
										  2,0,2,1,2,1,0,1,2,1,1,3,0,4,2,
										  0,2,3,2,0,0,4,1,0,4,3,0,0,3,2,
										  2,2,3,1,1,0,0,1,0,1,1,4,3,0,0,
										  4,2,0,4,2,2,0,3,0,0,2,2,1,4,2,
										  1,4,3,3,2,3,0,4,4,0,0,2,2,3,0,
										  2,1,1,4,1,0,1,0,4,4,1,0,4,1,3,
										  3,3,0,2,1,3,1,1,4,0,2,3,3,3,3,
										  2,3,3,1,3,1,0,4,1,0,1,2,3,0,4,
										  3,2,1,1,3,4,0,2,4,2,4,2,0,2,0,
										  0,3,0,1,4,0,0,0,4,2,1,0,2,4,0,
										  2,0,1,4,2,3,1,4,2,0,1,0,3,4,2,
										  0,4,2,0,3,4,4,3,1,1,3,4,2,1,4,
										  4,2,4,0,4,3,0,2,2,4,1,4,3,4,1,
										  4,3,2,2,2,1,1,2,3,3,1,2,0,3,2};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==4)
					{
						byte[] position = {3,4,4,3,2,3,2,1,3,4,1,2,3,3,2,
										  2,0,2,0,3,1,0,3,1,1,2,1,4,3,4,
										  1,3,1,0,3,1,3,2,3,4,0,0,1,4,1,
										  0,2,1,0,2,2,2,4,1,0,4,4,3,3,2,
										  2,3,1,3,0,4,0,2,3,0,1,4,4,2,3,
										  3,1,3,3,2,3,0,1,0,4,3,4,0,1,4,
										  4,4,4,2,2,3,0,0,0,1,0,1,2,1,3,
										  2,1,3,4,4,0,4,1,0,4,0,1,2,1,3,
										  3,4,3,1,2,0,1,3,3,0,1,4,2,0,0,
										  2,3,0,1,2,4,3,3,0,1,1,2,2,3,3,
										  4,4,1,0,3,3,4,4,2,2,4,2,0,3,0,
										  3,1,0,4,3,2,0,2,3,1,4,3,1,2,2,
										  2,2,3,0,2,4,1,3,0,3,2,1,3,4,2,
										  2,4,3,1,3,0,3,2,0,4,3,2,2,3,4,
										  0,4,2,2,2,3,2,0,1,1,4,0,1,3,3};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==5)
					{
						byte[] position = {2,4,2,0,4,2,2,3,1,0,1,3,4,2,0,
										  2,3,3,2,3,1,3,3,0,1,4,1,0,0,1,
										  0,4,3,0,3,1,3,3,3,1,0,2,4,2,1,
										  3,0,1,0,1,2,3,0,0,2,1,1,1,4,4,
										  0,1,1,1,2,0,2,1,3,4,2,0,3,1,0,
										  1,1,1,4,1,1,0,0,1,1,4,1,1,2,1,
										  3,3,0,1,1,3,2,0,0,0,0,1,2,0,1,
										  0,3,0,3,4,0,1,1,2,1,4,2,1,0,2,
										  1,2,2,2,2,3,4,1,3,1,4,2,4,1,1,
										  2,2,0,3,3,0,2,2,3,3,2,2,1,0,3,
										  2,4,0,0,4,0,4,3,4,4,3,4,1,4,4,
										  2,1,2,3,1,1,2,2,1,0,3,1,4,4,0,
										  2,3,2,2,1,1,4,0,1,4,4,0,4,3,3,
										  1,1,3,0,3,1,4,3,4,1,0,4,1,1,4,
										  0,4,4,4,2,2,4,3,1,1,3,2,4,4,1};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==6)
					{
						byte[] position = {3,4,0,3,1,2,0,1,3,1,2,4,1,1,3,
									      3,1,4,3,0,0,1,3,0,2,0,4,4,4,4,
									      0,4,3,2,1,1,0,2,2,1,3,4,0,2,3,
									      2,4,0,1,3,3,3,2,2,2,2,0,2,2,0,
									      0,4,0,0,2,1,0,1,4,3,3,3,1,0,2,
									      1,0,4,1,2,4,4,2,2,0,0,0,3,4,4,
									      4,2,1,3,1,2,0,1,3,4,2,2,1,3,2,
									      1,1,1,0,3,0,3,1,3,3,1,1,2,3,0,
									      1,2,4,3,1,4,1,1,1,0,2,3,0,3,3,
									      0,4,1,3,4,0,4,1,4,0,4,2,3,0,1,
									      0,4,3,4,2,4,1,3,1,3,0,4,3,0,0,
									      3,1,1,1,0,4,2,0,3,0,4,4,2,4,4,
									      4,0,4,3,1,4,1,3,2,3,0,1,0,1,1,
									      3,3,4,2,4,4,2,0,3,4,3,0,1,0,3,
									      0,2,3,4,4,2,4,1,0,0,0,4,2,4,0};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==7)
					{
						byte[] position = {3,1,3,1,4,4,2,2,0,4,0,2,2,3,1,
									      1,1,2,3,3,1,0,2,2,2,0,2,4,1,1,
									      4,4,1,2,4,2,1,4,1,2,3,3,2,1,4,
									      1,0,2,2,3,4,1,3,2,2,1,3,4,3,2,
									      3,1,1,0,0,1,2,0,3,2,4,3,4,3,1,
									      1,1,3,0,4,2,1,3,0,1,2,4,4,0,3,
									      0,1,1,1,0,1,2,3,3,1,0,1,0,0,3,
									      2,3,2,3,1,1,1,2,4,0,2,1,2,3,3,
									      0,1,3,0,4,3,1,1,4,0,1,3,0,3,0,
									      1,3,3,0,3,0,0,0,3,4,1,3,0,0,0,
									      4,4,2,1,3,1,0,1,1,3,1,3,2,4,3,
									      0,3,0,2,3,1,1,1,3,3,1,2,3,2,2,
									      3,2,2,0,3,0,3,1,0,0,3,3,2,4,2,
									      0,1,2,2,0,2,4,4,1,3,4,3,1,1,4,
									      4,4,3,0,4,3,3,3,4,1,3,4,4,3,1};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==8)
					{
						byte[] position = {1,3,4,0,2,1,4,3,0,0,1,2,3,1,1,
										  0,0,3,0,3,2,3,0,1,4,0,3,3,3,2,
										  2,4,1,2,0,1,2,1,0,0,3,1,0,2,2,
										  0,2,1,2,1,1,0,0,0,3,3,0,1,1,3,
										  1,4,2,3,1,3,3,0,4,2,3,1,0,4,4,
										  2,1,1,4,1,1,4,0,4,4,2,0,0,4,0,
										  3,4,4,3,0,0,2,0,4,1,2,4,0,3,3,
										  1,4,0,4,0,0,3,3,4,4,0,2,2,4,4,
										  0,1,0,4,2,3,3,0,0,2,0,4,3,4,1,
										  3,1,1,4,2,4,0,0,2,0,3,1,2,4,3,
										  0,0,4,2,4,1,2,0,0,0,3,0,3,3,3,
										  0,0,1,0,1,2,2,0,3,4,3,2,4,3,4,
										  1,1,0,2,0,4,3,3,1,1,4,3,2,4,1,
										  0,1,2,2,3,4,0,3,1,4,0,0,3,1,1,
										  0,3,0,0,1,0,1,1,1,3,1,2,0,0,0};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==9)
					{
						byte[] position = {0,1,3,3,4,3,4,3,2,4,4,0,3,2,1,
										  4,0,1,1,0,0,0,1,2,0,3,0,0,2,1,
										  1,2,4,3,0,2,0,2,3,4,3,1,2,2,3,
										  3,4,3,0,1,3,3,2,3,1,1,0,3,4,2,
										  2,0,0,3,2,0,2,3,3,3,0,1,1,1,1,
										  2,4,2,2,1,4,3,2,1,4,0,1,4,4,1,
										  0,0,0,2,2,3,4,3,2,3,0,3,4,3,4,
										  1,2,0,4,1,2,2,4,0,2,4,2,4,0,3,
										  3,4,3,3,1,1,0,4,4,2,1,0,0,1,3,
										  1,2,2,2,4,3,2,0,2,1,0,1,0,1,3,
										  2,3,4,2,1,0,1,2,3,2,4,0,2,4,3,
										  1,3,2,4,3,0,4,4,1,1,4,1,2,4,0,
										  3,0,2,2,1,4,3,4,1,2,2,1,1,3,1,
										  2,0,2,1,0,4,1,4,0,3,2,3,0,2,4,
										  0,3,1,1,0,1,4,1,4,1,1,1,0,4,2};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==10)
					{
						byte[] position = {4,1,2,0,2,3,4,1,4,4,1,4,3,1,3,
										  1,3,1,3,4,0,3,4,2,3,3,2,3,4,1,
										  1,3,2,2,3,4,2,3,4,0,3,4,1,2,3,
										  1,3,2,4,0,2,0,0,1,2,1,3,4,4,2,
										  4,0,2,2,0,1,1,0,0,1,0,2,3,2,4,
										  2,2,0,3,4,1,0,4,3,4,4,2,3,3,4,
										  4,4,0,2,0,3,4,1,1,4,4,2,0,1,1,
										  3,1,0,4,1,1,1,3,2,4,1,3,2,0,2,
										  0,2,0,0,1,1,2,0,4,1,1,0,2,2,4,
										  3,1,0,4,3,4,3,1,1,0,0,3,2,3,4,
										  4,4,1,2,4,0,4,2,0,3,2,3,4,0,0,
										  2,4,3,0,1,3,1,3,1,0,1,0,0,1,4,
										  1,2,1,2,0,0,3,0,1,1,0,2,3,1,2,
										  3,2,0,1,3,0,2,4,3,4,4,4,0,3,0,
										  2,3,3,0,2,2,4,3,0,2,1,2,3,2,0};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==11)
					{
						byte[] position = {1,2,2,4,2,3,4,2,4,1,2,2,3,3,4,
										  3,1,1,4,1,1,1,1,1,2,1,1,4,1,0,
										  1,4,1,4,4,2,1,4,0,3,4,0,2,3,3,
										  3,3,1,2,0,3,3,3,2,4,0,1,2,3,0,
										  4,3,4,1,3,0,4,4,3,4,0,4,0,0,2,
										  2,0,3,1,2,4,4,4,0,0,2,3,0,0,3,
										  0,4,0,3,4,2,1,1,0,3,3,3,2,2,1,
										  0,2,0,3,1,4,0,0,1,2,0,3,4,1,2,
										  3,2,2,2,1,1,1,4,3,2,0,2,4,2,2,
										  4,3,3,0,3,0,0,4,0,0,2,2,3,3,1,
										  4,2,3,4,1,2,3,1,3,0,4,4,4,0,2,
										  0,1,3,1,2,3,2,4,3,3,1,2,4,0,1,
										  4,1,3,3,1,0,3,2,0,1,4,0,2,0,2,
										  4,0,2,4,1,0,0,4,2,0,0,4,4,3,0,
										  1,1,1,3,4,2,3,2,1,2,0,1,4,1,0};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==12)
					{
						byte[] position = {4,0,1,4,3,3,1,4,1,2,4,1,0,0,2,
										  0,1,4,0,3,0,0,2,4,2,2,3,3,2,4,
										  0,2,1,0,3,3,3,0,0,4,4,3,1,1,4,
										  4,4,2,1,0,2,4,3,3,2,2,4,2,4,0,
										  3,0,0,4,4,2,2,1,3,4,3,2,4,2,0,
										  0,4,1,4,4,4,4,4,1,2,3,4,2,3,3,
										  0,1,2,0,0,2,2,1,3,4,2,0,0,4,1,
										  4,3,3,2,0,0,1,0,1,4,3,2,3,1,1,
										  3,4,2,2,0,2,3,3,3,0,0,1,2,1,3,
										  1,3,2,1,2,2,4,1,1,1,2,3,1,3,1,
										  0,0,2,1,2,1,1,4,1,1,0,2,1,2,0,
										  4,1,2,1,0,3,1,0,3,4,0,4,3,3,2,
										  4,3,0,0,3,4,3,3,3,3,1,1,3,2,1,
										  0,1,1,3,0,1,1,0,4,0,4,0,2,0,4,
										  2,2,1,4,4,2,2,0,3,4,3,0,2,4,3};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==13)
					{
						byte[] position = {2,2,4,0,2,4,0,0,1,4,0,3,4,3,3,
										  0,4,3,1,0,3,2,0,1,2,2,1,4,4,0,
										  2,1,2,3,3,2,1,2,3,3,0,4,2,1,0,
										  4,4,3,3,2,4,1,0,1,4,4,0,4,2,1,
										  3,3,0,1,2,2,3,1,3,0,1,3,2,3,3,
										  1,2,0,3,4,0,4,2,2,2,1,3,3,3,1,
										  4,0,0,1,1,1,1,4,3,3,2,1,3,2,0,
										  4,1,4,4,1,0,0,2,0,3,2,2,0,2,3,
										  2,3,3,1,4,3,0,1,0,4,4,0,0,2,1,
										  0,1,2,2,4,3,1,1,4,4,2,4,4,2,4,
										  2,4,1,1,0,3,3,3,0,4,4,0,0,2,0,
										  3,2,1,3,0,4,4,2,3,0,2,1,1,3,1,
										  0,4,3,3,1,2,0,2,2,1,2,3,0,0,1,
										  4,3,4,2,1,1,3,0,4,1,4,1,4,2,0,
										  2,1,3,2,0,1,4,0,1,4,0,4,0,4,3};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==14)
					{
						byte[] position = {0,1,2,1,3,4,3,2,1,2,1,2,2,3,4,
										  4,0,0,1,3,0,4,2,0,4,4,4,2,1,1,
										  3,0,0,1,2,1,1,3,0,0,3,2,4,0,0,
										  4,2,1,4,4,1,4,0,0,3,2,0,2,2,0,
										  3,3,4,2,1,2,4,1,3,4,0,4,2,3,0,
										  0,4,4,1,2,2,1,4,4,2,3,3,4,4,1,
										  3,1,1,3,2,2,0,3,2,3,4,4,3,2,0,
										  2,4,1,3,2,0,2,4,4,4,1,4,4,0,0,
										  1,4,2,1,2,0,3,3,0,1,3,3,2,4,3,
										  2,2,3,2,1,1,0,0,1,1,3,1,2,4,3,
										  2,1,0,2,2,0,3,2,2,1,4,1,1,4,0,
										  0,1,3,2,1,0,4,0,0,3,3,0,3,0,4,
										  1,2,1,3,4,3,1,1,3,0,0,4,3,1,4,
										  0,3,3,3,1,1,4,0,0,4,2,4,1,0,3,
										  3,0,3,2,1,4,0,3,3,1,2,2,0,4,2};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==15)
					{
						byte[] position = {2,0,1,4,4,3,1,4,2,0,4,0,4,0,1,
										  3,3,0,2,1,1,1,4,2,4,3,4,2,1,0,
										  4,1,4,4,1,2,1,1,1,2,3,1,0,3,3,
										  4,4,2,3,3,0,2,0,3,2,1,4,4,1,4,
										  1,4,1,3,3,3,1,0,2,2,2,2,2,3,0,
										  0,4,2,3,0,3,1,0,1,1,3,1,3,2,1,
										  2,0,2,4,1,1,2,1,3,1,1,1,2,2,2,
										  1,3,3,3,1,1,0,0,3,3,0,2,1,1,1,
										  0,0,0,4,4,1,3,2,4,1,0,0,3,3,0,
										  4,3,2,3,1,3,3,3,4,3,1,2,2,1,1,
										  1,1,1,1,2,0,2,1,4,1,3,1,1,1,2,
										  0,1,2,1,0,4,0,2,3,1,0,0,0,1,0,
										  0,1,1,4,3,3,4,4,0,0,1,0,1,2,4,
										  3,2,2,1,1,4,0,2,1,0,1,0,4,4,4,
										  4,0,1,1,2,2,4,3,4,1,2,4,4,3,4};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==16)
					{
						byte[] position = {0,2,2,2,4,1,2,0,4,0,2,3,0,2,2,
										  4,4,4,2,2,2,1,2,3,2,3,0,0,2,1,
										  3,2,0,1,2,3,2,4,3,1,0,4,2,0,2,
										  2,1,2,0,0,2,2,3,4,3,2,2,2,1,3,
										  0,2,0,3,2,0,2,1,2,2,2,3,3,0,2,
										  3,1,0,4,3,0,1,1,0,3,0,0,2,3,4,
										  0,3,4,1,3,4,3,1,1,3,3,1,2,1,3,
										  4,2,3,1,1,0,3,3,4,4,1,1,4,4,3,
										  3,0,4,1,1,1,3,3,1,4,1,1,4,4,2,
										  2,1,3,0,2,2,4,2,4,2,1,1,2,2,0,
										  0,1,3,2,4,4,0,0,0,4,2,2,4,2,2,
										  3,0,1,2,4,1,0,3,3,1,0,4,0,2,2,
										  4,2,1,4,2,2,2,2,0,2,1,0,4,3,0,
										  0,4,3,2,0,2,3,2,4,2,1,1,1,3,4,
										  4,2,4,4,0,2,0,1,3,4,2,4,2,3,1
								
						};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==17)
					{
						byte[] position = {0,2,2,4,4,3,3,3,3,0,4,3,0,2,3,
										  4,1,4,4,4,1,3,1,4,1,0,3,0,2,1,
										  0,4,1,3,0,3,1,3,3,2,4,0,4,3,2,
										  1,2,3,3,4,2,1,0,2,3,3,3,2,3,4,
										  0,4,1,4,1,1,2,3,0,2,1,3,1,0,2,
										  3,1,3,4,1,3,3,1,4,3,3,2,4,4,0,
										  0,2,4,4,1,0,0,3,2,3,2,3,3,3,2,
										  0,1,4,3,3,1,2,1,3,2,3,1,2,0,2,
										  0,2,0,2,3,1,3,4,1,1,0,2,1,4,1,
										  0,3,4,0,0,2,3,2,4,3,3,0,0,0,3,
										  2,4,0,2,2,0,3,1,0,2,3,2,3,2,3,
										  4,3,1,1,4,3,1,1,3,1,3,0,4,1,3,
										  4,2,1,1,3,3,0,3,0,4,0,3,4,3,0,
										  1,2,4,4,2,4,3,4,1,4,3,0,0,0,2,
										  4,3,3,2,3,3,0,0,1,1,2,3,3,4,2};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==18)
					{
						byte[] position = {4,4,1,4,1,1,2,4,0,3,3,0,1,2,0,
										  4,1,1,4,3,1,1,2,2,3,0,2,4,0,3,
										  3,2,3,4,0,1,4,0,2,4,4,1,2,3,0,
										  0,4,3,4,0,2,4,0,4,4,4,1,4,3,4,
										  4,0,1,4,2,0,1,2,4,3,0,1,4,4,3,
										  3,1,4,2,2,1,2,1,2,2,4,2,1,2,2,
										  2,4,2,0,4,0,3,4,3,0,2,3,3,3,0,
										  3,4,3,4,0,0,0,1,1,4,1,2,1,3,3,
										  4,4,4,1,2,4,2,4,0,1,4,1,4,4,3,
										  0,4,2,2,4,0,3,1,3,2,4,1,4,4,4,
										  0,0,2,1,4,0,2,4,3,4,0,0,4,3,0,
										  4,2,1,0,2,2,4,2,2,2,3,3,1,0,0,
										  4,1,3,3,4,3,1,3,2,1,1,3,1,4,2,
										  1,1,4,0,4,3,3,2,0,2,4,3,1,4,0,
										  3,3,1,4,1,4,0,4,0,4,3,0,4,4,0};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
					if (presetGameChoice.getSelectedIndex()==19)
					{
						byte[] position = {3,0,1,3,3,0,0,1,0,0,2,4,0,0,1,
										  1,2,2,3,2,2,0,4,0,2,3,2,2,2,1,
										  3,1,0,0,0,0,4,4,1,3,1,3,2,0,4,
										  0,1,0,2,0,3,4,3,2,3,0,2,0,3,4,
										  2,3,2,2,0,3,3,0,0,3,0,3,4,1,1,
										  0,3,3,2,0,4,1,2,4,1,2,4,4,1,0,
										  3,2,4,0,4,1,4,3,2,1,1,4,0,0,2,
										  1,4,1,3,0,4,0,3,2,3,2,0,0,0,1,
										  0,0,0,1,4,2,1,0,4,4,4,3,1,0,4,
										  3,3,3,1,0,3,1,2,0,2,4,3,4,1,1,
										  1,1,1,3,0,2,2,3,0,4,3,4,4,1,1,
										  0,2,0,0,2,0,0,1,3,0,2,3,0,2,4,
										  4,3,3,2,4,0,0,0,4,3,1,0,4,1,2,
										  2,2,3,2,0,4,2,0,0,4,1,4,4,0,1,
										  3,4,1,4,4,0,0,0,0,1,0,2,1,0,0};
						
					    boardPanel.setPosition(position,15,15,5);
					}
					
				
				}
				boardPanel.setScore(0,0,0);
				boardPanel.getScorePanel().setScore(0);
				boardPanel.setHumanPlay(true);
				//m.setBotThread(new RandomBot(), boardPanel, boardPanel.getScorePanel());
				m.setBotThread(boardPanel, boardPanel.getScorePanel());
				
				boardPanel.getHistoryPanel().newGame(boardPanel.getPosition());
				
				Settings newSettings = m.getSettings();
				newSettings.setCustomGame(customTextArea.getText());
				newSettings.setCustomSelected(customPositionRadioButton.isSelected());
				newSettings.setRandomSelected(randomRadioButton.isSelected());
				newSettings.setNumberOfColors(Integer.parseInt(numberOfColorsTextField.getText()));
				newSettings.setXSize(Integer.parseInt(xSizeTextField.getText()));
				newSettings.setYSize(Integer.parseInt(ySizeTextField.getText()));
				//m.setSettings(newSettings);
				
				f.dispose();
			}
			catch (Exception exception)
			{
				//exception.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(),
					    "Input is falsive. Please check and correct your input.",
					    "Input Error",
					    JOptionPane.ERROR_MESSAGE);
			}
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
