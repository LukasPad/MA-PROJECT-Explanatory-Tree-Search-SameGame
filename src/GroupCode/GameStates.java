package GroupCode;

import java.util.ArrayList;
import java.util.Collections;

public class GameStates extends BoardFeatures{
    private ArrayList<GameState> history;

    public GameStates() {
        history = new ArrayList<>();
    }
    @Override
    public ArrayList<GameState> findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move, int mctsScore, int nodeID) {
        GameState gameState = new GameState(searchSpace, mctsScore, gameStep, nodeID);
        history.add(gameState);
        return new ArrayList<>(Collections.singleton(gameState));
    }

    @Override
    public ArrayList<GameState> getFeatures() {
        return history;
    }

    public ArrayList<Integer> getNodeIDs(int gameStep) {
        ArrayList<Integer> nodeIDs = new ArrayList<>();
        for (GameState gameState : this.history) {
            if (gameState.gameStep == gameStep) nodeIDs.add(gameState.nodeID);
        }

        return nodeIDs;
    }

    public GameState getState(int nodeID) {
        for (GameState gameState : this.history) {
            if(gameState.nodeID == nodeID){
                return gameState;
            }
        }
        return null;
    }
}
