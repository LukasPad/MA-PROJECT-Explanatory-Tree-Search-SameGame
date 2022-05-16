package GroupCode;
import java.util.ArrayList;

public abstract class BoardFeatures {
    protected int gameID;

    public abstract ArrayList<?> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move);

    public abstract ArrayList<?> getFeatures();

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String toJSON() {
        return null;
    }
}
