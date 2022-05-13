import java.util.ArrayList;

/**
 * 
 */

public class Columns extends BoardFeatures {

    ArrayList<ArrayList<Column>> history = new ArrayList<>();
    ArrayList<Column> shortestHistory = new ArrayList<>();
    ArrayList<Integer> shortestColumnHeightHistory = new ArrayList<>();

	public Columns() {}

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
	    ArrayList<Column> columns = new ArrayList<>();
	    
		for (int i = 0; i < xDim; i++) {
			byte[] columnSpace = new byte[yDim];
			for (int j = 0; j < yDim; j++) {
				columnSpace[yDim - 1 - j] = searchSpace[j*xDim + i];
			}
			columns.add(new Column(columnSpace, gameStep));
		}
		int shortestColumnHeight = 0;
		Column shortest = columns.get(0);
		shortestColumnHeight = shortest.height;
		if(shortestColumnHeight == 0) {shortestColumnHeight = 1000000000;}
		for (int i = 1; i < columns.size(); i++) {
			if(columns.get(i).height < shortestColumnHeight && columns.get(i).height != 0) {
				shortest = columns.get(i);
				shortestColumnHeight = shortest.height;
			}
		}
		// if the board is completely empty, the shortest column is size 0
		if(shortestColumnHeight == 1000000000) {shortestColumnHeight = 0;}
		this.shortestHistory.add(shortest);
		this.shortestColumnHeightHistory.add(shortestColumnHeight);
		this.history.add(columns);
		return columns;
	}

	public ArrayList<ArrayList<Column>> getFeatures() {
		return this.history;
	}
	
	public void printColumns(int gameStep) {
		ArrayList<Column> columns = getColumns(gameStep);
		for (int i = 0; i < columns.size(); i++) {
			System.out.print("Column "+(i+1)+": ");
			columns.get(i).printColumn();
		}
	}
	
	public Column getShortest(int gameStep) {
		int ind = getColumnsIndex(gameStep);
		if(ind != -1) {return this.shortestHistory.get(ind);}
		return null;
	}
	
	public int getShortestColumnHeight(int gameStep) {
		int ind = getColumnsIndex(gameStep);
		if(ind != -1) {return this.shortestColumnHeightHistory.get(ind);}
	return -1;
	}
	
	public ArrayList<Column> getColumns(int gameStep){
        for (ArrayList<Column> column : this.history){
            if(column.get(0).gameStep == gameStep){
                return column;
            }
        }
        return null;
    }	
	
	public int getColumnsIndex(int gameStep){
		int cn = 0;
        for (ArrayList<Column> column : this.history){
            if(column.get(0).gameStep == gameStep){
                return cn;
            }
            cn++;
        }
        return -1;
    }	
    
    public int numberOfEmptyColumns(int gameStep) {
		int counter = 0;
		for (int i = 0; i < history.get(gameStep).size(); i++) {
			if (history.get(gameStep).get(i).getHeight() == 0) {counter++;}
		}
		return counter;
	}

}
