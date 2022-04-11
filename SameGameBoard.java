
public class SameGameBoard 
{
	public static byte[] dropDownStones(byte[] position,int xDimension, int yDimension)
	{		
		dropTheStones2(position, xDimension, yDimension);
		
		shiftToTheLeft(position, xDimension, yDimension);
		
		return position;
	}

	public static void shiftToTheLeft(byte[] position, int xDimension, int yDimension) {
		int freeRowAt=0;
		boolean foundFreeRow=false;
		int lowestPos = ((yDimension-1)*xDimension);
		for (int i=0;i<xDimension;i++)
		{
			if ((!foundFreeRow)&&(position[lowestPos]==-1))
			{
				foundFreeRow=true;
				freeRowAt=i;
				changedRows[i]=yDimension-1;
			}
			
			if ((foundFreeRow)&&(position[lowestPos]!=-1))
			{
				changedRows[i]=yDimension-1;
				int j=yDimension-1;
				int arrayPos=i+(j*xDimension);
				int difference = i- freeRowAt;
				while ((j>=0)&&(position[arrayPos]!=-1))
				{
					//position[freeRowAt+(j*xDimension)]=	position[arrayPos];
					position[arrayPos-difference]= position[arrayPos];
					position[arrayPos]=-1;
					j--;
					arrayPos-=yDimension;
				}
				freeRowAt++;
			}
			lowestPos++;
		}
	}

	private static void dropTheStones(int[] position, int xDimension, int yDimension) {
		int downshift;
		boolean foundHole;
		int downShiftTimesXDimension;
		for (int i=0;i<xDimension;i++)
		{
			downshift=0;
			downShiftTimesXDimension=0;
			foundHole=false;

			for (int yPos=yDimension-1;yPos>-1;yPos--)
			{
				int arrayPos=i+yPos*xDimension;
				if (position[arrayPos]==-1)
				{
					downShiftTimesXDimension=++downshift*xDimension;
					foundHole=true;
				}
				else if (foundHole)
				{
						position[arrayPos+downShiftTimesXDimension]=position[arrayPos];
						position[arrayPos]=-1;
				}
			}
		}
	}
	
	public static void dropTheStones2(byte[] position, int xDimension, int yDimension) {
		int downshift;
		int downShiftTimesXDimension;
		
		for (int i=0;i<xDimension;i++)
		{
			//println(position, xDimension, yDimension);
			//System.out.println();
			
			//System.out.println(changedRows[i]);
			//System.out.println();
			int change = changedRows[i];
			
			if (change>0)
			{
				downshift=1;
				downShiftTimesXDimension=xDimension;
				
				int arrayPos=i+(change-1)*xDimension;
				
				for (int yPos=change-1;yPos>-1;yPos--)
				{
					//int arrayPos=i+yPos*xDimension;
					
					if (position[arrayPos]==-1) downShiftTimesXDimension=++downshift*xDimension;
					else
					{
						position[arrayPos+downShiftTimesXDimension]=position[arrayPos];
						position[arrayPos]=-1;
					}
					arrayPos-=xDimension;
				}
				//changedRows[i]=0;
			}
		}
	}
	
	public static void setMakeMoveVariables(byte[] position, int xDim, int yDim, byte colorAfter)
	{
		makeMoveXDim=xDim;
		makeMoveYDim=yDim;
		if (changedRows.length!=makeMoveXDim) changedRows=new int[xDim];
		makeMovePosition=position;
		makeMovecolorAfter=colorAfter;
		makeMoveXDimMinus1=makeMoveXDim-1;
		makeMoveYDimMinus1=makeMoveYDim-1;
		makeMoveXDimTimesYDim=xDim*yDim;
		makeMoveLastRow=makeMoveYDimMinus1*makeMoveXDim;
		
		/*System.out.println("X Dim: "+xDim);
		System.out.println("Y Dim: "+yDim);
		System.out.println("Color After: "+makeMovecolorAfter);
		System.out.println("X Dim Minus 1: "+makeMoveXDimMinus1);
		System.out.println("Y Dim Minus 1: "+makeMoveYDimMinus1);
		System.out.println("X Dim times Y Dim: "+makeMoveXDimTimesYDim);
		System.out.println("Last Row: "+makeMoveLastRow);*/
	}
	
	public static short makeMove(byte[] position, int xDim, int yDim, int xChoice,int yChoice, int color, byte colorAfter)
	{
		setMakeMoveVariables(position, xDim, yDim, colorAfter);
		return makeMove(xChoice+(yChoice*xDim),xChoice, yChoice, color);
	}
	
	public static short makeMove(byte[] position,int move, int xChoice,int yChoice, int color)
	{
		makeMovePosition=position;
		return makeMove(move, xChoice, yChoice, color);
	}
	
	public static short makeMove(int move, int xChoice,int yChoice, int color /* int colorAfter*/)
	{
		makeMoveColor=color;
		if (makeMoveColor==makeMovecolorAfter)
		{
			Exception e = new Exception("Color is the same as ColorAfter("+makeMoveColor+") Exception");
			e.printStackTrace();
			
			SameGameBoard.println(makeMovePosition, makeMoveXDim, makeMoveYDim);
			System.out.println("Played move: "+"["+xChoice+","+yChoice+"]");
			System.exit(0);
		}
		
		if ((xChoice<0)||(yChoice<0)||(xChoice>=makeMoveXDim)||(yChoice>=makeMoveYDim)) return 0;
		for (int i=makeMoveXDim;--i>=0;) changedRows[i]=0;
		
		return makeMoveLocal(move,xChoice,yChoice);
	}
	
	private static byte[] makeMovePosition;
	private static int makeMoveXDim;
	private static int makeMoveYDim;
	private static int makeMoveColor;
	private static byte makeMovecolorAfter;
	private static int makeMoveLastRow;
	private static int makeMoveXDimMinus1;
	private static int makeMoveYDimMinus1;
	private static int makeMoveXDimTimesYDim;
	public static int[] changedRows = new int[0];
	
	private static short makeMoveLocal(int pos, int xChoice, int yChoice)
	{
		makeMovePosition[pos]=makeMovecolorAfter;
		if (changedRows[xChoice]<yChoice) 
			changedRows[xChoice]=yChoice;
		short numberOfFields=1;
		if ((xChoice>0)&&(makeMovePosition[pos-1]==makeMoveColor)) numberOfFields+=makeMoveLeft(pos-1,xChoice-1,yChoice);
		if ((yChoice>0)&&(makeMovePosition[pos-makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveUp(pos-makeMoveXDim,xChoice,yChoice-1);
		if ((xChoice<makeMoveXDimMinus1)&&(makeMovePosition[pos+1]==makeMoveColor)) numberOfFields+=makeMoveRight(pos+1,xChoice+1,yChoice);
		if ((yChoice<makeMoveYDimMinus1)&&(makeMovePosition[pos+makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveDown(pos+makeMoveXDim,xChoice,yChoice+1);
		return numberOfFields;
	}
	
	private static int makeMoveLeft(int pos, int xChoice, int yChoice)
	{
		//SameGameBoard.println(makeMovePosition, makeMoveXDim, makeMoveYDim);
		makeMovePosition[pos]=makeMovecolorAfter;
		if (changedRows[xChoice]<yChoice) 
			changedRows[xChoice]=yChoice;
		int numberOfFields=1;
		if ((xChoice>0)&&(makeMovePosition[pos-1]==makeMoveColor)) numberOfFields+=makeMoveLeft(pos-1,xChoice-1,yChoice);
		if ((yChoice>0)&&(makeMovePosition[pos-makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveUp(pos-makeMoveXDim,xChoice,yChoice-1);
		//if ((xChoice<makeMoveXDimMinus1)&&(makeMovePosition[pos+1]==makeMoveColor)) numberOfFields+=makeMoveRight(pos+1,xChoice+1,yChoice);
		if ((yChoice<makeMoveYDimMinus1)&&(makeMovePosition[pos+makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveDown(pos+makeMoveXDim,xChoice,yChoice+1);
		return numberOfFields;
	}
	
	private static int makeMoveRight(int pos, int xChoice, int yChoice)
	{
		//SameGameBoard.println(makeMovePosition, makeMoveXDim, makeMoveYDim);
		makeMovePosition[pos]=makeMovecolorAfter;
		if (changedRows[xChoice]<yChoice) 
			changedRows[xChoice]=yChoice;
		int numberOfFields=1;
		//if ((xChoice>0)&&(makeMovePosition[pos-1]==makeMoveColor)) numberOfFields+=makeMoveLeft(pos-1,xChoice-1,yChoice);
		if ((yChoice>0)&&(makeMovePosition[pos-makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveUp(pos-makeMoveXDim,xChoice,yChoice-1);
		if ((xChoice<makeMoveXDimMinus1)&&(makeMovePosition[pos+1]==makeMoveColor)) numberOfFields+=makeMoveRight(pos+1,xChoice+1,yChoice);
		if ((yChoice<makeMoveYDimMinus1)&&(makeMovePosition[pos+makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveDown(pos+makeMoveXDim,xChoice,yChoice+1);
		return numberOfFields;
	}
	
	private static int makeMoveUp(int pos, int xChoice, int yChoice)
	{
		//SameGameBoard.println(makeMovePosition, makeMoveXDim, makeMoveYDim);
		makeMovePosition[pos]=makeMovecolorAfter;
		/*if (changedRows[xChoice]<yChoice) 
			changedRows[xChoice]=yChoice;*/
		int numberOfFields=1;
		if ((xChoice>0)&&(makeMovePosition[pos-1]==makeMoveColor)) numberOfFields+=makeMoveLeft(pos-1,xChoice-1,yChoice);
		if ((yChoice>0)&&(makeMovePosition[pos-makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveUp(pos-makeMoveXDim,xChoice,yChoice-1);
		if ((xChoice<makeMoveXDimMinus1)&&(makeMovePosition[pos+1]==makeMoveColor)) numberOfFields+=makeMoveRight(pos+1,xChoice+1,yChoice);
		//if ((yChoice<makeMoveYDimMinus1)&&(makeMovePosition[pos+makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveDown(pos+makeMoveXDim,xChoice,yChoice+1);
		return numberOfFields;
	}
	
	private static int makeMoveDown(int pos, int xChoice, int yChoice)
	{
		//SameGameBoard.println(makeMovePosition, makeMoveXDim, makeMoveYDim);
		makeMovePosition[pos]=makeMovecolorAfter;
		if (changedRows[xChoice]<yChoice) 
			changedRows[xChoice]=yChoice;
		int numberOfFields=1;
		if ((xChoice>0)&&(makeMovePosition[pos-1]==makeMoveColor)) numberOfFields+=makeMoveLeft(pos-1,xChoice-1,yChoice);
		//if ((yChoice>0)&&(makeMovePosition[pos-makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveUp(pos-makeMoveXDim,xChoice,yChoice-1);
		if ((xChoice<makeMoveXDimMinus1)&&(makeMovePosition[pos+1]==makeMoveColor)) numberOfFields+=makeMoveRight(pos+1,xChoice+1,yChoice);
		if ((yChoice<makeMoveYDimMinus1)&&(makeMovePosition[pos+makeMoveXDim]==makeMoveColor)) numberOfFields+=makeMoveDown(pos+makeMoveXDim,xChoice,yChoice+1);
		return numberOfFields;
	}
	
	
	private static int[] queue = new int[0]; 
	public static int makeMove2(byte[] position, int xDim, int yDim, int xChoice,int yChoice, byte colorAfter)
	{
		int score=0;
		if (2*xDim*yDim!=queue.length) queue = new int[2*xDim*yDim];
		queue[0]=xChoice+(yChoice*xDim);
		int queueStart=0;
		int queueEnd=1;
		
		while (queueStart!=queueEnd)
		{
			if ((position[queue[queueStart]]!=-1)&&(position[queue[queueStart]]!=colorAfter))
			{
				if ((queue[queueStart]%xDim!=0)&&(position[queue[queueStart]]==position[queue[queueStart]-1])) 
				{
					queue[queueEnd]=queue[queueStart]-1;
					queueEnd++;
				}
				
				if ((queue[queueStart]%xDim!=xDim-1)&&(position[queue[queueStart]]==position[queue[queueStart]+1])) 
				{
					queue[queueEnd]=queue[queueStart]+1;
					queueEnd++;
				}
				
				if ((queue[queueStart]>=xDim)&&(position[queue[queueStart]]==position[queue[queueStart]-xDim])) 
				{
					queue[queueEnd]=queue[queueStart]-xDim;
					queueEnd++;
				}
				
				if ((queue[queueStart]<xDim*(yDim-1))&&(position[queue[queueStart]]==position[queue[queueStart]+xDim])) 
				{
					queue[queueEnd]=queue[queueStart]+xDim;
					queueEnd++;
				}
				score++;
				position[queue[queueStart]]=colorAfter;
			}
			queueStart++;
		}
		return score;
	}
	
	public static boolean legalMove(byte[] position, int xDim, int yDim, int xChoice,int yChoice)
	{
		int c = xChoice+(yChoice*xDim);
		if (position[c]==-1) return false;
		if (xChoice>=1) if (position[c]==position[c-1]) return true;
		if (xChoice<xDim-1) if (position[c]==position[c+1]) return true;
		if (yChoice>=1) if (position[c]==position[c-xDim]) return true;
		if (yChoice<yDim-1) if (position[c]==position[c+xDim]) return true;
		return false;
	}
	
	private static int canMovexDimMinusOne;
	private static int canMoveyDimMinusOne;
	private static int canMovexDim;
	private static int canMoveyDim;
	private static int lastRowStart;
	
	public static boolean canMove(byte[] position, int xDim, int yDim)
	{
		if ((xDim!=canMovexDim)||(yDim!=canMoveyDim))
		{
			canMovexDim=xDim;
			canMoveyDim=yDim;
			canMovexDimMinusOne=xDim-1;
			canMoveyDimMinusOne=yDim-1;
			lastRowStart=canMoveyDimMinusOne*xDim;
		}
			
		int x=0;
		while ((x<xDim)&&(position[x+lastRowStart]>=0))
		{
			int c = x+lastRowStart;
			while ((position[c]>=0)&&(c>=yDim))
			{
				if ((x<canMovexDimMinusOne)&&(position[c]==position[c+1])) return true;
				if ((c<lastRowStart)&&(position[c]==position[c+xDim])) return true;
				
				c-=yDim;
			}
			x++;
		}		
		return false;
	}
	
	/*public static int[] generateMoves2(int[] position, int xDim, int yDim)
	{
		int[] p = new int[position.length];
		int[] moves = new int[0];
		//setMakeMoveVariables(xDim, yDim);
		for (int i=0;i<position.length;i++) p[i]=position[i];
		
		for (int i=0;i<xDim;i++)
		{
			for (int j=i%2;j<yDim;j+=2)
			{
				if (legalMove(p, xDim, yDim, i, j))
				{
					int[] newMoves = new int[moves.length+1];
					for (int k=0;k<moves.length;k++) newMoves[k]=moves[k];
					newMoves[moves.length]=i+(j*xDim);
					moves=newMoves;
					makeMove(p, xDim, yDim, i, j, p[i+(j*xDim)],-1);
				}
			}
		}
		return moves;
	}*/
	
	private static byte[] localP = new byte[0];
	
	public static int[] generateMoves1(byte[] position, int xDim, int yDim)
	{
		int xDimTimesYDim=xDim*yDim;
		int[] moves = new int[(int)((xDimTimesYDim/2.0)+0.5)];
		if (localP.length!=position.length ) localP = new byte[position.length];
		int nr=0;
		
		System.arraycopy(position, 0, localP, 0, xDimTimesYDim);
		
		int x=0;
		while ((x<xDim)&&(localP[x+((yDim-1)*xDim)]!=-1))
		{
			int y=yDim-1;
			while ((y>=0)&&(localP[x+(y*xDim)]!=-1))
			{
				//System.out.print(y+" ");
				int c = x+(y*xDim);
				if ((localP[c]>=0)&&(((x<xDim-1)&&(localP[c]==localP[c+1]))||((y<yDim-1)&&(localP[c]==localP[c+xDim])))) 
				{
					moves[nr]=x+(y*xDim);
					nr++;
					
					makeMove(localP, xDim, yDim, x, y, localP[c],(byte)-2);
				}
				y--;
			}
			//System.out.println();
			x++;
		}	
		moves[nr]=-1;
		numberOfMoves=nr;
		makeMovecolorAfter=-1;
		return moves;
	}
	
	public static int[] moveSize = new int[225];
	
	public static int[] generateMoves(byte[] position)
	{
		int[] moves = new int[(int)((makeMoveXDimTimesYDim/2.0)+0.5)];
		if (localP.length!=position.length ) localP = new byte[position.length];
		System.arraycopy(position, 0, localP, 0, makeMoveXDimTimesYDim);
		makeMovePosition=localP;
		makeMovecolorAfter=-2;
		int nr=0;
		int x=0;
		while ((x<makeMoveXDim)&&(localP[x+makeMoveLastRow]!=-1))
		{
			int y=makeMoveYDimMinus1;
			while ((y>=0)&&(localP[x+(y*makeMoveXDim)]!=-1))
			{
				int c = x+(y*makeMoveXDim);

				if ((localP[c]>=0)&&(((x<makeMoveXDimMinus1)&&(localP[c]==localP[c+1]))||((y<makeMoveYDimMinus1)&&(localP[c]==localP[c+makeMoveXDim])))) 
				{
					moves[nr]=c;
					nr++;
					moveSize[nr-1]=makeMove(c, x, y, localP[c]);
					//makeMove(localP, makeMoveXDim, makeMoveYDim, x, y, localP[c],(byte)-1);
				}
				y--;
			}
			x++;
		}	
		moves[nr]=-1;
		numberOfMoves=nr;
		makeMovecolorAfter=-1;
		return moves;
	}
	
	public static int[] generateMoves3(byte[] position, int xDim, int yDim)
	{
		int[] moves = new int[0];
		byte[] p = new byte[position.length];
		if (moves.length!=(int)(((xDim*yDim)/2.0)+0.5))	moves = new int[(int)(((xDim*yDim)/2.0)+0.5)];
		int nr=0;
		
		for (int i=0;i<position.length;i++) p[i]=position[i];
		
		for (int i=0;i<xDim;i++)
		{
			for (int j=i%2;j<yDim;j+=2)
			{
				if (legalMove(p, xDim, yDim, i, j))
				{
					moves[nr]=i+(j*xDim);
					nr++;
					makeMove2(p, xDim, yDim, i, j,(byte)-1);
				}
			}
		}
		moves[nr]=-1;
		return moves;
	}
	
	public static int[] getChangedWindow()
	{
		int[] window = new int[3];
		window[0]=-1;
		
		for (int i=0;i<changedRows.length;i++)
		{
			if (changedRows[i]>0) 
			{
				window[1]=i;
				if (window[0]==-1) window[0]=i;
			}
			
			if (window[2]<changedRows[i]) window[2]=changedRows[i];
		}
		
		return window;
	}
	
	private static byte[] p = new byte[0];
	public static int numberOfMoves=0;
	private final static byte BYTEZERO=(byte)0;
	private final static byte BYTEMINUSONE=(byte)-1;
	private final static byte BYTEMINUSTWO=(byte)-2;
	public static int[] generateMovesIteratively(byte[] position, int[] earlierMoves, int[] window, int xDim, int yDim)
	{
		//Preparing new move array
		firstEmpty=0;
		int mostLeftColumn=-1, mostRightColumn=-1, deepestChange=0;
		if (p.length!=position.length) p = new byte[position.length];
		for (int i=0;i<p.length;i++) p[i]=position[i]; 
		int[] newMoves = new int[earlierMoves.length];
		
		mostLeftColumn=window[0];
		mostRightColumn=window[1];
		deepestChange=window[2];
		
		
		//If a move at tht top is played, we do not have to do anything.
		if (mostLeftColumn==-1)
		{
			int freePos=0;
			int i=0;
			while (earlierMoves[i]!=-1)
			{
				if (p[earlierMoves[i]]!=-1)
				{
					newMoves[freePos]=earlierMoves[i];
					freePos++;
				}
				i++;
			}
			newMoves[freePos]=-1;
			numberOfMoves=freePos;
			return newMoves;
		}
		
		//System.out.println("MostLeftColumn: "+ mostLeftColumn+" MostRightColum: "+mostRightColumn+" DeepestChange: "+deepestChange);
		
		//Determine all new moves inside the changed window.
		int mostRight=mostRightColumn+1;
		if (mostRight==xDim) mostRight--;
		int mostLeft=mostLeftColumn-1;
		if (mostLeft==-1) mostLeft=0;
		
		for (int x=mostLeft;x<=mostRight;x++)
		{
			for (int y=deepestChange;y>=0;y--)
			{
				int possibleMove=x+(y*xDim);
				int color=p[possibleMove];
				if (color>=0)
				{
					if (legalMove(p, xDim, yDim, x, y))
					{
						newMoves[firstEmpty]=possibleMove;
						firstEmpty++;
						makeMove(p, xDim, yDim, x, y, color, BYTEMINUSTWO);
					}
				}
				if (color==-1) break;
			}
		}
		
		//Checking old moves
		int i=0;
		while (earlierMoves[i]!=-1) 
		{
			int column = earlierMoves[i]%xDim;
			int row = earlierMoves[i]/xDim;
			
			if ((column<mostLeftColumn-1)||(column>mostRightColumn+1)||(row>deepestChange))
			{
				int color = p[earlierMoves[i]];
				if (color>=0)
				{
					newMoves[firstEmpty]=earlierMoves[i];
					firstEmpty++;
				}
			}
			
			if (((column==mostLeftColumn-1)||(column==mostRightColumn+1))&&(row<=deepestChange))
			{
				int color = p[earlierMoves[i]];
				if (color>=0)
				{
					if (legalMove(p, xDim, yDim, column, row))
					{
						newMoves[firstEmpty]=earlierMoves[i];
						firstEmpty++;
						makeMove(p, xDim, yDim, column, row, color, BYTEMINUSONE);
					}
				}
			}	
			i++;
		}
		
		newMoves[firstEmpty]=-1;
		numberOfMoves=firstEmpty;
		return newMoves;
	}
	
	/*private static boolean isConnectedToLeftColumn(int[] p, int pos, int color, int xDim)
	{
		if (p[pos-1]==color) return true;
		if (pos-xDim>=0) if (p[pos-xDim]==color) return isConnectedToLeftColumn(p, pos-xDim, color, xDim);
		return false;
	}*/
	
	private static int firstEmpty=Integer.MIN_VALUE;
	/*private static void addToMoveArray(int[] moves, int move)
	{
		//for (int i=0;i<moves.length;i++) System.out.print(moves[i]+" ");
		if (moves[firstEmpty]==-1) moves[firstEmpty+1]=-1;
		moves[firstEmpty]=move;
		firstEmpty++;
		while (moves[firstEmpty]>=0) firstEmpty++;
		//for (int i=0;i<moves.length;i++) System.out.print(moves[i]+" ");
	}*/
	
	public static boolean isEqual(int[] p1, int[] p2)
	{
		if (p1.length!=p2.length) return false;
		
		for (int i=0;i<p1.length;i++)
			if (p1[i]!=p2[2]) return false;
		
		return true;
	}
	
	public static void println(byte[] pos, int xDim, int yDim)
	{
		for (int j=0;j<yDim;j++)
		{
			for (int i=0;i<xDim;i++)
				System.out.print(pos[i+j*xDim]+1+",");
			System.out.println();
		}
	}
	
	public static void println(int[] pos, int xDim, int yDim)
	{
		for (int j=0;j<yDim;j++)
		{
			for (int i=0;i<xDim;i++)
				System.out.print(pos[i+j*xDim]+1+",");
			System.out.println();
		}
	}
	
	public static boolean isEmpty(byte[] pos, int xDim, int yDim)
	{
		if (pos[(yDim-1)*xDim]==-1) return true;
		return false;
	}
	
	public static boolean isSame(byte[] pos1, byte[] pos2)
	{
		if (pos1.length!=pos2.length) return false;
		for (int i=0;i<pos1.length;i++) if (pos1[i]!=pos2[i]) return false;
		
		return true;
	}
}
