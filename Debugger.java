
public class Debugger 
{
	public static void main(String[] args) 
	{
		byte[] pos = new byte[]{0,0,3,2,4,4,4,1,1,2,1,2,4,4,3,4,2,4,1,0,0,3,3,3,4};
		
		SameGameBoard.println(pos, 5, 5);
		//System.out.println(pos[3+(1*5)]);
		SameGameBoard.makeMove(pos, 5, 5, 3, 1, 1,(byte) -1);
		
		System.out.println();
		SameGameBoard.println(pos, 5, 5);
		
		SameGameBoard.dropDownStones(pos, 5, 5);
		System.out.println();
		SameGameBoard.println(pos, 5, 5);
				
	}
}
