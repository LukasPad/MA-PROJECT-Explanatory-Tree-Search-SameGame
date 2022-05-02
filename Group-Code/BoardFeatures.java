import java.util.ArrayList;

public abstract class BoardFeatures {

    public abstract ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move);

    public abstract ArrayList getFeatures();
}
