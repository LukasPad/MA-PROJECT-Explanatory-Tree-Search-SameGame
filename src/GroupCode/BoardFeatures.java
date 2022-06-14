package GroupCode;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class BoardFeatures {
    protected int gameID;
    protected int nodeID;

    public abstract ArrayList<?> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move, int mctsScore, int nodeID);

    public abstract ArrayList<?> getFeatures();

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    //public void setNodeID(int nodeID) { this.nodeID = nodeID; }


    public String toJSON() {
        return null;
    }
}
