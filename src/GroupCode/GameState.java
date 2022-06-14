package GroupCode;

import org.json.JSONException;
import org.json.JSONObject;

public class GameState extends Feature{
    byte[] board;
    int score, gameStep, nodeID;

    public GameState(byte[] board, int score, int gameStep, int nodeID) {
        this.board = board;
        this.score = score;
        this.gameStep = gameStep;
        this.nodeID = nodeID;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("board", board);
            json.put("score", score);
            json.put("nodeID", nodeID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
