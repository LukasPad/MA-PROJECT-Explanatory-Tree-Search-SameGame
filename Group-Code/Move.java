import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Move extends BoardFeatures{
    private int location, numRemovedCells, numRemovedColumns;
    int gameStep;
    private byte color;
    int[] columnRange = new int[3];
    int connectionsCreated;
    int connectionsDestroyed;

    public Move(byte[] searchSpace, int xDim, int yDim, int gameStep, int move){
        findFeatures(searchSpace, xDim, yDim, gameStep, move);
    }

    @Override
    public ArrayList<Move> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move) {
        location = move;
        color = searchSpace[move];
        this.gameStep = gameStep;

        byte[] boardCopy = Arrays.copyOf(searchSpace, searchSpace.length);
        getRemovedCellsColumns(boardCopy, xDim, yDim, move);

        ArrayList<Move> res = new ArrayList<>();
        res.add(this);
        return res;
    }

    private void getRemovedCellsColumns(byte[] searchSpace, int xDim, int yDim, int pos) {
        int xChoice = pos % xDim;
        int yChoice = pos / xDim;
        
        // setup for finding created/destroyed connections
        
        // column range are the limits of the examinable board for the move, 0=first column, 1=last column, 2=floor row
        this.columnRange[0] = pos%xDim;
        this.columnRange[1] = pos%xDim;
        this.columnRange[2] = (int) pos/xDim;
        ArrayList<Integer> dissapearingColumns = new ArrayList<>();
        
        // get already removed columns
        numRemovedColumns = 0;
        for (int col = 0; col < xDim; col++) {
            boolean emptyColumn = true;
            for (int row = 0; row < yDim; row++) {
                if (searchSpace[row*xDim + col] != (byte) -1){
                    emptyColumn = false;
                    break;
                }
            }
            
            if (emptyColumn) {
            	numRemovedColumns--;
            }
        }
        

        // get #removed cells
        numRemovedCells = 1;
        searchSpace[pos] = (byte)-1;
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) checkMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) checkMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);

        // get #removed columns
        for (int col = 0; col < xDim; col++) {
            boolean emptyColumn = true;
            for (int row = 0; row < yDim; row++) {
                if (searchSpace[row*xDim + col] != (byte) -1){
                    emptyColumn = false;
                    break;
                }
            }
            if (emptyColumn) {
                numRemovedColumns++;
                if(numRemovedColumns > 0) {
                	int relativeColumn = col-this.columnRange[0]+1;
                	if(this.columnRange[0] == 0) relativeColumn--;
                	dissapearingColumns.add(relativeColumn);
                }
            }
        }
        
        //extend perimeter by one if possible
        int xMin = this.columnRange[0];
        if(xMin > 0) xMin--;
        int xMax = this.columnRange[1]+1;
        if(xMax < xDim) xMax++;
        int yMax = this.columnRange[2]+1;
        if(yMax < yDim) yMax++;
        int width = xMax-xMin;
        
        // copy interested area
        byte[] subBoard = new byte[(yMax)*width];
        for (int i=0; i<yMax; i++) {
        	for (int j=0; j<width; j++) {
        		subBoard[i*width +j] = searchSpace[(i*xDim)+xMin+j];
        	}
        }
        checkConnections(subBoard, width, yMax, dissapearingColumns);
    }


	private void checkMoveLeft(byte[] searchSpace, int xDim, int yDim, int pos, int xChoice, int yChoice) {
        numRemovedCells++;
        searchSpace[pos] = (byte)-1;
        if(pos%xDim < this.columnRange[0]) {this.columnRange[0] = pos%xDim;}
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) checkMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        // if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) makeMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);
    }

    private void checkMoveRight(byte[] searchSpace, int xDim, int yDim, int pos, int xChoice, int yChoice) {
        numRemovedCells++;
        searchSpace[pos] = (byte)-1;
        if(pos%xDim > this.columnRange[1]) {this.columnRange[1] = pos%xDim;}
        // if ((xChoice>0)&&(searchSpace[pos-1]==color)) makeMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) checkMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) checkMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);
    }

    private void checkMoveUp(byte[] searchSpace, int xDim, int yDim, int pos, int xChoice, int yChoice) {
        numRemovedCells++;
        searchSpace[pos] = (byte)-1;
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) checkMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) checkMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        // if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) makeMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);
    }

    private void checkMoveDown(byte[] searchSpace, int xDim, int yDim, int pos, int xChoice, int yChoice) {
        numRemovedCells++;
        searchSpace[pos] = (byte)-1;
        if((int) pos/xDim > this.columnRange[2]) {this.columnRange[2] = (int) pos/xDim;}
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        // if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) makeMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) checkMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);
    }
        
    private void checkConnections(byte[] subSpace, int subxDim, int subyDim, ArrayList<Integer> disColumns) {
    	this.connectionsCreated = 0;
    	this.connectionsDestroyed = 0;
    	// count connections
    	int conn1 = 0;
    	for (int i=0; i<subxDim; i++) {
    		for(int j=0; j<subyDim; j++) {
				byte block = subSpace[j*subxDim + i];
				if(block != (byte) -1) {
					if(i < subxDim-1) {
						// compare right
						if(block == subSpace[j*subxDim + i + 1]) conn1++;
					}
					if(j < subyDim-1) {
						// compare down
						if(block == subSpace[(j+1)*subxDim + i]) conn1++;
					}
				}
    		}
    	}
    	
    	//find number of destroyed connections
    	byte[] destCopy = Arrays.copyOf(subSpace, subSpace.length);
    	for (int i=0; i<subxDim; i++) {
    		boolean falling = false;
    		for (int j=subyDim-1; j>=0; j--) {
				byte block = destCopy[j*subxDim + i];
    			if(falling) {
    				if(block != (byte) -1) {
    					// check left
    					if (i > 0) {
    						if(block == destCopy[j*subxDim + i - 1]) this.connectionsDestroyed++;
    					}
    					// check right
    					if (i < subxDim-1) {
    						if(block == destCopy[j*subxDim + i + 1]) this.connectionsDestroyed++;
    					}
    				}
    				destCopy[j*subxDim + i] = (byte) -1;
    			} else {
    				if(block == (byte) -1) {
    					falling = true;
    				}
    			}
    		}
    	}
    	
    	// simulate movement
    	
    	//shift empty columns
    	while(disColumns.isEmpty() == false) {
    		int col = disColumns.remove(0);
    		for (int i=col; i>0; i--) {
    			for(int j=0; j<subyDim; j++) {
    				subSpace[j*subxDim + i] = subSpace[j*subxDim + i -1];
    			}
    		}
			for(int j=0; j<subyDim; j++) {
				subSpace[j*subxDim] = (byte) -1;
			}
    	}
    	//simulate gravity
    	for (int i=0; i<subxDim; i++) {
    		int dropAmount = 0;
    		for (int j=subyDim-1; j>=0; j--) {
    			subSpace[(j+dropAmount)*subxDim + i] = subSpace[j*subxDim + i];
				if(subSpace[j*subxDim + i] == (byte) -1) {
					dropAmount++;
				}
    		}
    		for(int f=0; f<dropAmount; f++) {
    			subSpace[f*subxDim + i] = (byte) -1;
    		}
    	}
    	
    	// recount connections
    	int conn2 = 0;
    	for (int i=0; i<subxDim; i++) {
    		for(int j=0; j<subyDim; j++) {
				byte block = subSpace[j*subxDim + i];
				if(block != (byte) -1) {
					if(i < subxDim-1) {
						// compare right
						if(block == subSpace[j*subxDim + i + 1]) conn2++;
					}
					if(j < subyDim-1) {
						// compare down
						if(block == subSpace[(j+1)*subxDim + i]) conn2++;
					}
				}
    		}
    	}
    	this.connectionsCreated = (conn2-conn1) +this.connectionsDestroyed;		
	}

    public void print(){
        System.out.println("Type: Move, Location: " + location + ", Color: " + color + ", removed cells: " + numRemovedCells + ", removed columns: " + numRemovedColumns);
    }

    @Override
    public ArrayList<Move> getFeatures() {
        return new ArrayList<>(Collections.singleton(this));
    }

    @Override
    public String toJSON() {
        // TODO: build json string
        return null;
    }

    @Override
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getLocation() {
        return location;
    }

    public byte getColor() {
        return color;
    }

    public int getColorInt() {
        return color;
    }

    public int getNumRemovedCells() {
        return numRemovedCells;
    }

    public int getNumRemovedColumns() {
        return numRemovedColumns;
    }
    
    public int getConnectionsCreated() {
        return this.connectionsCreated;
    }
        
    public int getConnectionsDestroyed() {
        return this.connectionsDestroyed;
    }
    
    public int getConnectionsDelta() {
        return (this.connectionsCreated - this.connectionsDestroyed);
    }
    
}
