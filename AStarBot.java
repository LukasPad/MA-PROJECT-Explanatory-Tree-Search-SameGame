import java.util.Random;


public class AStarBot implements SameGameBot 
{
	private int xDim;
	private int yDim;
	
	private Random r = new Random();
	
	private int toDoSize=1;
	
	private ZobristHash zobrist;
	private int colorsLeft[];
	
	private int evalFunction=1;
	
	private boolean stop=false;
	
	public int getMove(byte[] position, int xDimension, int yDimension, int scoreMode, int time) 
	{	
		stop=false;
		int colors=0;
		for (int i=0;i<position.length;i++)
			if (position[i]>colors) colors=position[i];
		colors++;
		
		colorsLeft=new int[colors];
		
		for (int i=0;i<position.length;i++)
			if (position[i]>=0) colorsLeft[position[i]]++;
		
		zobrist= new ZobristHash(xDimension,yDimension,10);
		toDoSize=1;
		int closedSize=1;
		xDim=xDimension;
		yDim=yDimension;
		
		AStarNode toDoList = null;
		AStarNode closedList = null;
		
		toDoList = new AStarNode(position,0,Evaluation.estimate(position,scoreMode, colorsLeft,xDim, yDim, evalFunction),null,-1);
		
		boolean finished = false;
		
		System.out.println("------------------------");
		
		while ((!finished)&&(!stop))
		{
			AStarNode current = toDoList;
			
			//SameGameBoard.println(current.getPosition(), xDim, yDim);
			
			if (toDoList!=null) toDoList=toDoList.next;
			else toDoList=null;
			System.out.println("ToDo Size: "+toDoSize+ " Closed Size: "+closedSize+" Estimated score: "+(current.getScore()+current.getEstimate()));
			toDoSize--;
			
			int[] moves = SameGameBoard.generateMoves1(current.getPosition(), xDim, yDim);
			
			if (moves[0]==-1)
			{
				finished=true;
				return current.getMoveToPlay();
			}
			
			int i=0;
			while (moves[i]!=-1)
			{
				byte[] p = new byte[current.getPosition().length];
				for (int j=current.getPosition().length-1;j>=0;j--)
					p[j]=current.getPosition()[j];
				
				//SameGameBoard.setMakeMoveVariables(xDim, yDim);
				int score = SameGameBoard.makeMove(p, xDim, yDim, moves[i]%xDim, moves[i]/xDim, p[moves[i]],(byte)-1);
				SameGameBoard.dropDownStones(p, xDim, yDim);
				
				colorsLeft=new int[colors];
				for (int j=0;j<position.length;j++)
					if (position[j]!=-1) colorsLeft[position[j]]++;
				
				int g;
				if (scoreMode==BoardPanel.CLICKOMANIA) g = current.getScore()+score;
				else if (scoreMode==BoardPanel.SAMEGAME)
				{
					g=current.getScore()+((score-2)*(score-2));
					
					if (!SameGameBoard.canMove(p, xDim, yDim))
					{
						if (p[xDim*(yDim-1)]==-1) g+=1000;
						else
						{
							for (int j=0;j<colors;j++)
								if (colorsLeft[j]>1)g-=(colorsLeft[j]-2)*(colorsLeft[j]-2);
						}
					}
				}
				else //BubbleBreaker
					g=current.getScore()+(score*(score-1));
					
				int h = Evaluation.estimate(p,scoreMode, colorsLeft,xDim, yDim, evalFunction);
				AStarNode n;
				if (current.getMoveToPlay()==-1)n = new AStarNode(p,g,h,null,moves[i]);
				else n = new AStarNode(p,g,h,null,current.getMoveToPlay());
				
				
				boolean sorted=false;
				
				AStarNode[] nodeInList = inList(n, closedList);
				
				if (nodeInList!=null) 
				{
					sorted=true;
					toDoSize--;
				}
				else
				{
					nodeInList = inList(n, toDoList);
					if (nodeInList!=null)
					{
						if (nodeInList[1].getScore()>n.getScore()) sorted=true;
						else
						{
							if (nodeInList[0]==null)toDoList=toDoList.next;
							else nodeInList[0].setNext(nodeInList[1].getNext());
						}
						toDoSize--;
					}
				}
				
				AStarNode nodeBefore=null;
				AStarNode nodeAfter=toDoList;
				
				while (!sorted)
				{
					if (nodeAfter==null)
					{
						sorted=true;
						if (nodeBefore!=null) nodeBefore.setNext(n);
						else toDoList=n;
					}
					else
					if ((nodeAfter.getScore()+nodeAfter.getEstimate()<g+h)
							||((nodeAfter.getScore()+nodeAfter.getEstimate()==g+h)&&(nodeAfter.getScore()<g)))
					{
						sorted=true;
						if (nodeBefore!=null) nodeBefore.setNext(n);
						else toDoList=n;
						n.setNext(nodeAfter);
					}
					else
					{
						nodeBefore=nodeAfter;
						nodeAfter=nodeAfter.next;
					}
				}
				toDoSize++;
				i++;
			}
			
			if (closedList!=null) current.setNext(closedList);
			else closedList=current;
			
			closedSize++;
		}
		
		return -1;
	}
	
	private AStarNode[] inList(AStarNode element, AStarNode list)
	{
		AStarNode current = list;
		AStarNode before = null;
		
		while (current!=null)
		{
			//if (SameGameBoard.isEqual(element.getPosition(), current.getPosition())) return true;
			if (current.hash==element.hash)
			{
				/*if (current.getScore()<element.getScore())
				{
					current.setScore(element.getScore());
					current.setMoveToPlay(element.getMoveToPlay());
				}*/
				
				return new AStarNode[]{before, current};
			}
			before=current;
			current=current.getNext();
		}
		
		return null;
	}
	
	private class AStarNode
	{
		private byte[] position;
		private int score;
		private int estimate;
		private AStarNode next=null;
		private int moveToPlay;
		private int hash;
		
		public AStarNode(byte[] pos, int g, int h, AStarNode nextNode, int play)
		{
			position=pos;
			score=g;
			estimate=h;
			next=nextNode;
			moveToPlay=play;
			hash=zobrist.makeHash(position);
		}

		public int getEstimate() {
			return estimate;
		}

		public void setEstimate(int estimate) {
			this.estimate = estimate;
		}

		public byte[] getPosition() {
			return position;
		}

		public void setPosition(byte[] position) {
			this.position = position;
			hash=zobrist.makeHash(position);
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public AStarNode getNext() {
			return next;
		}

		public void setNext(AStarNode next) {
			this.next = next;
		}

		public int getMoveToPlay() {
			return moveToPlay;
		}

		public void setMoveToPlay(int moveToPlay) {
			this.moveToPlay = moveToPlay;
		}

		public int getHash() {
			return hash;
		}
	}
	
	private class ZobristHash
	{
		int[][] hash;
		private ZobristHash(int xDim, int yDim, int numberOfColors)
		{
			Random r = new Random();
			hash = new int[xDim*yDim][numberOfColors];
			for (int i=0;i<hash.length;i++)
				for (int j=0;j<numberOfColors;j++)
				hash[i][j] = r.nextInt();
		}
		
		private int makeHash(byte[] pos)
		{
			int code=0;
			for (int i=0;i<pos.length;i++)
				if (pos[i]!=-1) code^=hash[i][pos[i]];
			return code;
		}
	}
	
	public void setEvaluationFunction(int function)
	{
		evalFunction=function;
	}
	
	public void stopCalculation()
	{
		stop=true;
	}
	
	public void reUseEarlierThreshold(boolean value)
	{
		//reUseThreshold=value;
	}
	
	public void timeIsUp()
	{
		
	}

}
