package GroupCode;
import OldCode.*;

public class MCTSPlayerFC extends MCTSPlayer {

    FeatureCollector fc_tree = new FeatureCollector();

    int nodeID = 0;
    int counter = 0;

    public void playGame(byte[] position, int xDim, int yDim, int scoreMode, int time, FeatureCollector featureCollector) {
        int[] gamePosColors = new int[10];
        for (int i = 0; i < 225; i++) if (position[i] >= 0) gamePosColors[position[i]]++;

        int moveCounter = 0;
        gameScore = 0;

        while (SameGameBoard.canMove(position, xDim, yDim)) {
            primaryVariant = new int[225];
            Evaluation.gameRecordPosition = 0;
            topScore = Integer.MIN_VALUE;
            byte[] tempBoard = new byte[225];
            System.arraycopy(position, 0, tempBoard, 0, xDim*yDim);
            int move = getMove(position, xDim, yDim, scoreMode, time / 35);

            // Feature collection before the move is played
            featureCollector.findGameFeatures(position, xDim, yDim, moveCounter, move, topScore);

            int colorPlayed = position[move];
            int bloks = SameGameBoard.makeMove(position, move, move % 15, move / 15, colorPlayed);
            SameGameBoard.dropDownStones(position, 15, 15);
            gamePosColors[colorPlayed] -= bloks;

            gameScore += (bloks - 2) * (bloks - 2);

            featureCollector.saveCurrentGameScore(gameScore, position, featureCollector.getGameID(), moveCounter);
            saveTree(root, tempBoard);

            moveCounter++;
            System.out.println("Move: "+moveCounter);
            if (moveCounter % 10 == 0){
                SameGameBoard.println(position, 15, 15);
            }
        }

        if (SameGameBoard.isEmpty(position, 15, 15)) gameScore += 1000;
        else {
            for (int i = 4; i >= 0; i--) {
                int amount = gamePosColors[i] - 2;
                if (amount > 0) gameScore -= amount * amount;
            }
        }
    }

    public void saveTree(UCTNode n, byte[] pos){
        counter++;
        saveTree(n, 0 , pos, -1 , -1);
        fc_tree.exportTreeJSON(counter);
        fc_tree.clearHistory();
    }

    public void saveTree(UCTNode n, int depth, byte[] pos, int parentID, int move)
    {
        if (n==null && n.simulations < 10) return;

        nodeID++;
        int nID = nodeID;

        if (parentID != -1)
        {
            fc_tree.findGameFeatures(pos, xDim, yDim, depth/*, move*/, 0, n.topScore, nID);
        }
        else
        {
            fc_tree.findGameFeatures(pos, xDim, yDim, depth, 0, n.topScore, nID);
        }
        fc_tree.addEdge(nID, parentID);

        for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
        {
            int colorPlayed = pos[loop.move];
            byte[] position = new byte[255];
            System.arraycopy(pos, 0, position, 0, xDim*yDim);
            int bloks = SameGameBoard.makeMove(position, loop.move, loop.move % 15, loop.move / 15, position[loop.move]);
            SameGameBoard.dropDownStones(position, 15, 15);
            //gamePosColors[colorPlayed] -= bloks;

            saveTree(loop.child, depth+1, position, nID, loop.move);
        }
        return;
    }

//    public void saveTree(UCTNode n, int depth, int parentID)
//    {
//        if (n==null || n.simulations<10) return;
//
//        nodeID++;
//        int nID = nodeID;
//
//        if (parentID != -1) {
//            fc.findGameFeatures(n.state.position, xDim, yDim, depth/*, move*/, 0, n.topScore, nID);
//        }else{
//            fc.findGameFeatures(n.state.position, xDim, yDim, depth, 0, n.topScore, nID);
//        }
//        fc.addEdge(nID, parentID);
//
//        for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling) {
//            saveTree(loop.child, depth+1, nID);
//        }
//
//        return;
//    }


}
