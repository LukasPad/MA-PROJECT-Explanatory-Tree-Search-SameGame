package GroupCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Moves extends BoardFeatures {
    private ArrayList<Move> history;

    public Moves(){
        history = new ArrayList<>();
    }

    @Override
    public ArrayList<Move> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move, float mctsScore, int nodeID) {
        Move cur_move = null;
        if (move != -1 && searchSpace != null) {
            cur_move = new Move(searchSpace, xDim, yDim, gameStep, move, mctsScore, nodeID);
            history.add(cur_move);
        }
        return new ArrayList<>(Collections.singleton(cur_move));
    }

    @Override
    public ArrayList<Move> getFeatures() {
        return history;
    }

    public Move getMove(int nodeID){
        for (Move move : this.history){
            if(move.nodeID == nodeID){
                return move;
            }
        }
        return null;
    }

    public String toJSON(){
        JSONArray json = new JSONArray();
        for (Move move : history) {
            json.put(move.toJSON());
        }

        return json.toString();
    }
}
