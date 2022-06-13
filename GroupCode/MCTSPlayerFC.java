package GroupCode;
import OldCode.*;

public class MCTSPlayerFC extends MCTSPlayer {
    public void playGame(byte[] position, int xDim, int yDim, int scoreMode, int time, FeatureCollector featureCollector) {
        int[] gamePosColors = new int[10];
        for (int i = 0; i < 225; i++) if (position[i] >= 0) gamePosColors[position[i]]++;

        int moveCounter = 0;
        gameScore = 0;

        while (SameGameBoard.canMove(position, xDim, yDim)) {
            primaryVariant = new int[225];
            Evaluation.gameRecordPosition = 0;
            topScore = Integer.MIN_VALUE;
            int move = getMove(position, xDim, yDim, scoreMode, time / 35);

            // Feature collection before the move is played
            featureCollector.findGameFeatures(position, xDim, yDim, moveCounter, move, featureCollector.getGameID(), topScore);

            int colorPlayed = position[move];
            int bloks = SameGameBoard.makeMove(position, move, move % 15, move / 15, colorPlayed);
            SameGameBoard.dropDownStones(position, 15, 15);
            gamePosColors[colorPlayed] -= bloks;

            gameScore += (bloks - 2) * (bloks - 2);

            featureCollector.saveCurrentGameScore(gameScore, position, featureCollector.getGameID(), moveCounter);

            moveCounter++;
        }

        if (SameGameBoard.isEmpty(position, 15, 15)) gameScore += 1000;
        else {
            for (int i = 4; i >= 0; i--) {
                int amount = gamePosColors[i] - 2;
                if (amount > 0) gameScore -= amount * amount;
            }
        }
    }
}
