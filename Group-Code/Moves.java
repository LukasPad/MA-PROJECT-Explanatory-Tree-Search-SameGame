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

    @Override
    public String toJSON() {
        StringBuilder json = new StringBuilder();
        for (Move move : history) {
            json.append(move.toJSON());
        }
        return json.toString();
    }

    @Override
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public Move getMove(int gameStep){
        for (Move move : this.history){
            if(move.gameStep == gameStep){
                return move;
            }
        }
        return null;
    }
}
