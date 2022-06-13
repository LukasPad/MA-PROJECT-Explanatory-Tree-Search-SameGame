package GroupCode;

import java.util.ArrayList;
import java.util.Collections;

public class GameStates extends BoardFeatures{
    private ArrayList<GameState> history;

    public GameStates() {
        history = new ArrayList<>();
    }
    @Override
    public ArrayList<GameState> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move, int mctsScore) {
        GameState gameState = new GameState(searchSpace, mctsScore, gameStep);
        history.add(gameState);
        return new ArrayList<>(Collections.singleton(gameState));
    }

    @Override
    public ArrayList<GameState> getFeatures() {
        return history;
    }

    public GameState getState(int gameStep) {
        for (GameState gameState : this.history){
            if(gameState.gameStep == gameStep){
                return gameState;
            }
        }
        return null;
    }
}
