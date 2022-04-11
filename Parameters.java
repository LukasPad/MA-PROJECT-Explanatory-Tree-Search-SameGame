
public class Parameters 
{
	//Parameters for UCT tree part
	//public static double UCTConstant=4.31;
	public static double UCTConstant=0.1;
	//public static double DeviationConstant=96.67;
	public static double DeviationConstant=32;
	//public static int numberOfVisitsBeforeExpanding = 13;
	public static int numberOfVisitsBeforeExpanding = 10;
	public static double topScoreWeight = 0.02;
	public static int maxNumberOfNodes=Integer.MAX_VALUE;
	//public static int maxNumberOfNodes=Integer.MAX_VALUE;

	//Parameter for UCT playout style
	public static double chanceOnPlayingWithSameStrategy=1;
	
	//Parameters for Mixed Playout style
	public static double RWeight = 0;
	public static double TRWeight = 0; 
	public static double TCRWeight = 1;
	public static double RPWeight = 0;
	public static double FRPWeight = 0;
	public static double CMWeight = 0;
	
	//Parameters for Single Playout style
	public static double RWeight2 = 0;
	public static double TRWeight2 = 0; 
	public static double TCRWeight2 = 1;
	public static double RPWeight2 = 0;
	public static double FRPWeight2 = 0;
	public static double CMWeight2 = 0;
	
	//Parameter for TabuColorRandom
	public static double chanceOfPlayingChosenColor = 0.0007;
}
