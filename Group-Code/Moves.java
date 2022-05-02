import java.util.ArrayList;

public class Moves extends BoardFeatures {
    private ArrayList<Move> history;

    public Moves(){
        history = new ArrayList<>();
    }

    @Override
    public ArrayList<Move> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move) {
        Move cur_move = new Move(searchSpace, xDim, yDim, gameStep, move);
        history.add(cur_move);
        return cur_move.findFeatures(searchSpace, xDim, yDim, gameStep, move);
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
}
