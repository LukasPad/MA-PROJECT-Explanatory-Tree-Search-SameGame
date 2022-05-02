import java.util.ArrayList;

/**
 * 
 */

public class Columns extends BoardFeatures {

    ArrayList<Column> columns = new ArrayList<>();
    Column shortest;
    int shortestColumnHeight = 0;

	public Columns(byte[] searchSpace, int xDim, int yDim, int gameStep, int move) {
		findFeatures(searchSpace, xDim, yDim, gameStep, move);
	}
	
	/**
	 * Separate the search space into separate columns
	 * 
	 * @return all columns ordered left to right on the board
	 */
	@Override
	public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move) {
		for (int i = 0; i < xDim; i++) {
			byte[] columnSpace = new byte[yDim];
			for (int j = 0; j < yDim; j++) {
				columnSpace[yDim - 1 - j] = searchSpace[j*xDim + i];
			}
			this.columns.add(new Column(columnSpace));
		}
		this.shortest = columns.get(0);
		this.shortestColumnHeight = this.shortest.height;
		if(this.shortestColumnHeight == 0) {this.shortestColumnHeight = 1000000000;}
		for (int i = 1; i < this.columns.size(); i++) {
			if(this.columns.get(i).height < this.shortestColumnHeight && this.columns.get(i).height != 0) {
				this.shortest = columns.get(i);
				this.shortestColumnHeight = this.shortest.height;
			}
		}
		// if the board is completely empty, the shortest column is size 0
		if(this.shortestColumnHeight == 1000000000) {this.shortestColumnHeight = 0;}
		
		return columns;
	}

	@Override
	public ArrayList getFeatures() {
		return this.columns;
	}
	
	public void printColumns() {
		for (int i = 0; i < columns.size(); i++) {
			System.out.print("Column "+(i+1)+": ");
			columns.get(i).printColumn();
		}
	}
	
	public Column getShortest() {return this.shortest;}
	public int getShortestColumnHeight() {return this.shortestColumnHeight;}
	public ArrayList<Column> getColumns() {return this.columns;}
	public int numberOfEmptyColumns() {
		int counter = 0;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getHeight() == 0) {counter++;}
		}
		return counter;
	}

}
