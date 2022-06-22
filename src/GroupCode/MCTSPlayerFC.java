package GroupCode;
import OldCode.*;

public class MCTSPlayerFC extends MCTSPlayer {

    FeatureCollector fc_tree = new FeatureCollector();

    int nodeID = 0;
    int counter = 0;

    public void playGame(byte[] position, int xDim, int yDim, int scoreMode, int time, FeatureCollector featureCollector) {
        fc_tree = featureCollector;
        counter = 0;
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
            int move = getMove(position, xDim, yDim, scoreMode, time);

            int colorPlayed = position[move];
            int bloks = SameGameBoard.makeMove(position, move, move % 15, move / 15, colorPlayed);
            SameGameBoard.dropDownStones(position, 15, 15);
            gamePosColors[colorPlayed] -= bloks;

            gameScore += (bloks - 2) * (bloks - 2);

            saveTree(root, tempBoard);

            moveCounter++;
            System.out.println("Game" + featureCollector.gameID + ", Move: "+moveCounter);
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
        int maxDepth = saveTree(n, 0 , pos, null, -1 , -1);
        fc_tree.exportJSON(maxDepth, counter);
        fc_tree.resetForNewGame();
    }

    public int saveTree(UCTNode n, int depth, byte[] pos, byte[] prevPos, int parentID, int move)
    {
        if (n==null || n.simulations < 10) return 0;

        nodeID++;
        int nID = nodeID;

        if (parentID != -1)
        {
            fc_tree.findGameFeatures(pos, prevPos, xDim, yDim, depth, move, n.average, nID);
        }
        else
        {
            fc_tree.findGameFeatures(pos, null, xDim, yDim, depth, -1, n.average, nID);
        }
        fc_tree.addEdge(nID, parentID);

        int max_depth = depth;
        if (parentID == -1){
            for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
            {
                int colorPlayed = pos[loop.move];
                byte[] position = new byte[255];
                System.arraycopy(pos, 0, position, 0, xDim*yDim);
                int bloks = SameGameBoard.makeMove(position, loop.move, loop.move % 15, loop.move / 15, colorPlayed);
                SameGameBoard.dropDownStones(position, 15, 15);
                //gamePosColors[colorPlayed] -= bloks;

                int child_depth = saveTree(loop.child, depth+1, position, pos, nID, loop.move);
                if (child_depth > max_depth){
                    max_depth = child_depth;
                }
            }
        }else{
            if (!(n.child == null)){
                UCTEdge bestChild = n.child;
                for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling){
                    if (loop.average > bestChild.average){
                        bestChild = loop;
                    }
                }
                int colorPlayed = pos[bestChild.move];
                byte[] position = new byte[255];
                System.arraycopy(pos, 0, position, 0, xDim*yDim);
                int bloks = SameGameBoard.makeMove(position, bestChild.move, bestChild.move % 15, bestChild.move / 15, colorPlayed);
                SameGameBoard.dropDownStones(position, 15, 15);
                //gamePosColors[colorPlayed] -= bloks;

                int child_depth = saveTree(bestChild.child, depth+1, position, pos, nID, bestChild.move);
                if (child_depth > max_depth){
                    max_depth = child_depth;
                }
            }
        }

        return max_depth;
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
