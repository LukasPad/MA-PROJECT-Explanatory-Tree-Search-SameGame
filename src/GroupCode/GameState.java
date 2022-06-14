package GroupCode;

import org.json.JSONException;
import org.json.JSONObject;

public class GameState extends Feature{
    byte[] board;
    int score, gameStep;

    public GameState(byte[] board, int score, int gameStep) {
        this.board = board;
        this.score = score;
        this.gameStep = gameStep;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("board", board);
            json.put("score", score);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
