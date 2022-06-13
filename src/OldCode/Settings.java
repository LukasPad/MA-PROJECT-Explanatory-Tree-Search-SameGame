package OldCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Settings 
{
	private boolean customSelected, randomSelected;
	private int xSize,ySize, numberOfColors;
	private String customGame;
	private File defaultDirectory = new File("");
	private int botType;
	private int estimator;
	private int timePerMove;
	
	public Settings()
	{
		customSelected=true;
		randomSelected=true;
		xSize=15;
		ySize=15;
		numberOfColors=5;
		customGame="";
	}
	
	public void safe()
	{
		try
		{
			File f = new File("OldCode/settings.cfg");
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			if (f.exists()) f.delete();
			f.createNewFile();
			if (customSelected) out.write("1 ");
			else out.write("0 ");
			if (randomSelected) out.write("1 ");
			else out.write("0 ");
			out.write(xSize+" ");
			out.write(ySize+" ");
			out.write(numberOfColors+" ");
			out.write(botType+" ");
			out.write(estimator+" ");
			out.write(timePerMove+"\n");
			out.write(customGame+"\n");
			out.write(defaultDirectory.getAbsolutePath());
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		File f = new File("OldCode/settings.cfg");
		if (f.exists())
		{
			try
			{
				FileReader fr = new FileReader(f);  
				BufferedReader reader = new BufferedReader(fr);
				String line = reader.readLine();
				String[] t = line.split(" ");
				if (Integer.parseInt(t[0])==1) customSelected=true;
				else customSelected=false;
				if (Integer.parseInt(t[1])==1) randomSelected=true;
				else randomSelected=false;
				xSize=Integer.parseInt(t[2]);
				ySize=Integer.parseInt(t[3]);
				numberOfColors=Integer.parseInt(t[4]);
				botType=Integer.parseInt(t[5]);
				estimator=Integer.parseInt(t[6]);
				timePerMove=Integer.parseInt(t[7]);
				
				line=reader.readLine();
				customGame="";
				while (line.split(",").length>1)
				{
					customGame=customGame+line+"\n";
					line=reader.readLine();
				}
				if (line.equals("")) line=reader.readLine();
				defaultDirectory=new File(line);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public String getCustomGame() {
		return customGame;
	}

	public void setCustomGame(String customGame) {
		this.customGame = customGame;
	}

	public boolean isCustomSelected() {
		return customSelected;
	}

	public void setCustomSelected(boolean customSelected) {
		this.customSelected = customSelected;
	}

	public int getNumberOfColors() {
		return numberOfColors;
	}

	public void setNumberOfColors(int numberOfColors) {
		this.numberOfColors = numberOfColors;
	}

	public boolean isRandomSelected() {
		return randomSelected;
	}

	public void setRandomSelected(boolean randomSelected) {
		this.randomSelected = randomSelected;
	}

	public int getXSize() {
		return xSize;
	}

	public void setXSize(int size) {
		xSize = size;
	}

	public int getYSize() {
		return ySize;
	}

	public void setYSize(int size) {
		ySize = size;
	}

	public File getDefaultDirectory() {
		return defaultDirectory;
	}

	public void setDefaultDirectory(File defaultDirectory) {
		this.defaultDirectory = defaultDirectory;
	}

	public int getBotType() {
		return botType;
	}

	public void setBotType(int botType) {
		this.botType = botType;
	}

	public int getEstimator() {
		return estimator;
	}

	public void setEstimator(int estimator) {
		this.estimator = estimator;
	}

	public int getTimePerMove() {
		return timePerMove;
	}

	public void setTimePerMove(int timePerMove) {
		this.timePerMove = timePerMove;
	}
	
}
