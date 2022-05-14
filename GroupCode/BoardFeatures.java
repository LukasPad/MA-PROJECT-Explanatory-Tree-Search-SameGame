package GroupCode;
import java.util.ArrayList;

public abstract class BoardFeatures {
    protected int gameID;

    public abstract ArrayList<?> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move);

    public abstract ArrayList<?> getFeatures();

    public abstract void setGameID(int gameID);
}
