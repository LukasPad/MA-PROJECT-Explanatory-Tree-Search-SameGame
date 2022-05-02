import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Move extends BoardFeatures{
    private int location, numRemovedCells, numRemovedColumns;
    int gameStep;
    private byte color;

    public Move(byte[] searchSpace, int xDim, int yDim, int gameStep, int move){
        // findFeatures(searchSpace, xDim, yDim, gameStep, move);
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

        // get already removed columns
        numRemovedColumns = 0;
        for (int row = 0; row < yDim; row++) {
            boolean emptyColumn = true;
            for (int col = 0; col < xDim; col++) {
                if (searchSpace[col*xDim + row] != (byte) -1){
                    emptyColumn = false;
                    break;
                }
            }

            if (emptyColumn) numRemovedColumns--;
        }

        // get #removed cells
        numRemovedCells = 1;
        searchSpace[pos] = (byte)-1;
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) checkMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) checkMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);

        // get #removed columns
        for (int row = 0; row < yDim; row++) {
            boolean emptyColumn = true;
            for (int col = 0; col < xDim; col++) {
                if (searchSpace[col*xDim + row] != (byte) -1){
                    emptyColumn = false;
                    break;
                }
            }

            if (emptyColumn) numRemovedColumns++;
        }
    }

    private void checkMoveLeft(byte[] searchSpace, int xDim, int yDim, int pos, int xChoice, int yChoice) {
        numRemovedCells++;
        searchSpace[pos] = (byte)-1;
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) checkMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        // if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) makeMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);
    }

    private void checkMoveRight(byte[] searchSpace, int xDim, int yDim, int pos, int xChoice, int yChoice) {
        numRemovedCells++;
        searchSpace[pos] = (byte)-1;
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
        if ((xChoice>0)&&(searchSpace[pos-1]==color)) checkMoveLeft(searchSpace, xDim, yDim, pos-1,xChoice-1,yChoice);
        // if ((yChoice>0)&&(searchSpace[pos-xDim]==color)) makeMoveUp(searchSpace, xDim, yDim, pos-xDim,xChoice,yChoice-1);
        if ((xChoice<xDim - 1)&&(searchSpace[pos+1]==color)) checkMoveRight(searchSpace, xDim, yDim, pos+1,xChoice+1,yChoice);
        if ((yChoice<yDim - 1)&&(searchSpace[pos+xDim]==color)) checkMoveDown(searchSpace, xDim, yDim, pos+xDim,xChoice,yChoice+1);
    }

    public void print(){
        System.out.println("Type: Move, Location: " + location + ", Color: " + color + ", removed cells: " + numRemovedCells + ", removed columns: " + numRemovedColumns);
    }

    @Override
    public ArrayList<Move> getFeatures() {
        return new ArrayList<>(Collections.singleton(this));
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
}
