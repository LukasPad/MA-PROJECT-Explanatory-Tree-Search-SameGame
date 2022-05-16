package GroupCode;

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
    public ArrayList<Move> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move) {
        Move cur_move = new Move(searchSpace, xDim, yDim, gameStep, move);
        cur_move.setGameID(gameID);
        history.add(cur_move);
        return new ArrayList<>(Collections.singleton(cur_move));
    }

    @Override
    public ArrayList<Move> getFeatures() {
        return history;
    }

    public Move getMove(int gameStep){
        for (Move move : this.history){
            if(move.gameStep == gameStep){
                return move;
            }
        }
        return null;
    }

    @Override
    public String toJSON(){
        ArrayList<String> move_list = new ArrayList<>();
        for (Move move : history) {
            move_list.add(move.toJSON());
        }

        JSONObject json = new JSONObject();
        try {
            json.put("Moves", move_list);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json.toString();
    }
}
