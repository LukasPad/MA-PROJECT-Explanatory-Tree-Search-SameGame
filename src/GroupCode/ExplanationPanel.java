package GroupCode;

import OldCode.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExplanationPanel extends JTextArea {
    private int xDim, yDim, nodeID;
    private BoardPanel boardPanel;
    private MCTSPlayer bot;
    private ArrayList<Cluster> moves;
    private HashMap<Integer, UCTNode> moveNodePairs = new HashMap<>();
    private int bestMove;

    private FeatureCollector featureCollector;
    private HashMap<Integer, HashMap<String, Float>> nodeFeatureScores;
    private HashMap<String, Float> featureImportanceLookupTable;

    private String explanation;
    HashMap<String, HashMap<String, String>> explanations = new HashMap<>();

    HashMap<Integer, String> moveYmapping = new HashMap<>(){{
       put(0, "A");
       put(1, "B");
       put(2, "C");
       put(3, "D");
       put(4, "E");
       put(5, "F");
       put(6, "G");
       put(7, "H");
       put(8, "I");
       put(9, "J");
       put(10, "K");
       put(11, "L");
       put(12, "M");
       put(13, "N");
       put(14, "O");
    }};


    public ExplanationPanel(int xSizePanel, int ySizePanel){
        setPreferredSize(new Dimension(xSizePanel, ySizePanel));
        explanation = "XAI not enabled!";
        setEditable(false);
        setFocusable(false);
        setBackground(new Color(236,233,216));
        featureCollector = new FeatureCollector();
        nodeID=0;
        importFeatureImportance();
        generateExplanationMap();
    }

    private void importFeatureImportance() {
        featureImportanceLookupTable = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject featureValues = (JSONObject) parser.parse(new FileReader("src/data/corr_coeffs4.json"));
            featureImportanceLookupTable = new HashMap<>() {{
                put("gameScore", (float) (double) featureValues.get("score"));
                put("moveNumber", (float) (double) featureValues.get("Move number"));
                put("nodeID", (float) (double) featureValues.get("nodeID"));
                put("numRemovedCells", (float) (double) featureValues.get("numRemovedCells"));
                put("numRemovedColumns", (float) (double) featureValues.get("numRemovedColumns"));
                put("color", (float) (double) featureValues.get("color"));
                put("connectionsDestroyed", (float) (double) featureValues.get("connectionsDestroyed"));
                put("connectionsCreated", (float) (double) featureValues.get("connectionsCreated"));
                put("moveColumn", (float) (double) featureValues.get("move column"));
                put("moveHeight", (float) (double) featureValues.get("move height"));
                put("avgClusterSize", (float) (double) featureValues.get("average cluster size"));
                put("largestClusterSize", (float) (double) featureValues.get("largest cluster"));
                put("numCluster2", (float) (double) featureValues.get("size 2 clusters"));
                put("numCluster3", (float) (double) featureValues.get("size 3 clusters"));
                put("numCluster4", (float) (double) featureValues.get("size 4 clusters"));
                put("numCluster5", (float) (double) featureValues.get("size 5 clusters"));
                put("numCluster6", (float) (double) featureValues.get("size 6 clusters"));
                put("numCluster7plus", (float) (double) featureValues.get("size 7+ clusters"));
                put("avgColHeight", (float) (double) featureValues.get("average column height"));
                put("highestColumn", (float) (double) featureValues.get("highest column"));
                put("avgNumColorsPerColumn", (float) (double) featureValues.get("average colors per column"));
            }};
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateExplanationMap() {
        HashMap<String, String> innerMap = new HashMap<>();

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s removes less columns");
        innerMap.put("negative", "%s removes more columns");
        explanations.put("numRemovedColumns", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s removes a bigger cluster");
        innerMap.put("negative", "%s removes a smaller cluster");
        explanations.put("numRemovedCells", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s destroys less of the connections on the board");
        innerMap.put("negative", "%s destroys more the connections on the board");
        explanations.put("connectionsDestroyed", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s creates more new connections");
        innerMap.put("negative", "%s creates less new connections");
        explanations.put("connectionsCreated", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s is further to the left, which is good");
        innerMap.put("negative", "%s is further to the right, which is bad");
        explanations.put("moveColumn", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s is higher up, which is beneficial");
        innerMap.put("negative", "%s is lower down, which isn't good");
        explanations.put("moveHeight", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with bigger clusters on average");
        innerMap.put("negative", "%s will result in a board with smaller clusters on average");
        explanations.put("avgClusterSize", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will ensure a bigger biggest cluster");
        innerMap.put("negative", "%s will only ensure a smaller biggest cluster");
        explanations.put("largestClusterSize", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with more clusters of 2 tiles");
        innerMap.put("negative", "%s will result in a board with less clusters of 2 tiles");
        explanations.put("numCluster2", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with more clusters of 3 tiles");
        innerMap.put("negative", "%s will result in a board with less clusters of 3 tiles");
        explanations.put("numCluster3", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with more clusters of 4 tiles");
        innerMap.put("negative", "%s will result in a board with less clusters of 4 tiles");
        explanations.put("numCluster4", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with more clusters of 5 tiles");
        innerMap.put("negative", "%s will result in a board with less clusters of 5 tiles");
        explanations.put("numCluster5", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with more clusters of 6 tiles");
        innerMap.put("negative", "%s will result in a board with less clusters of 6 tiles");
        explanations.put("numCluster6", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with more clusters of 7+ tiles");
        innerMap.put("negative", "%s will result in a board with less clusters of 7+ tiles");
        explanations.put("numCluster7plus", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board that has higher columns on average");
        innerMap.put("negative", "%s will result in a board that has lower columns on average");
        explanations.put("avgColHeight", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s will result in a board with the tallest column being taller");
        innerMap.put("negative", "%s will result in a board with the tallest column being shorter");
        explanations.put("highestColumn", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("positive", "%s keeps the number of colors per column higher");
        innerMap.put("negative", "%s causes the number of colors per column to be lower");
        explanations.put("avgNumColorsPerColumn", innerMap);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setText(explanation);
    }

    public void updateExplanation(int boardX, int boardY) {
        if (moves != null){
            // (relevance ranking => score) or (feature rules) => scoring/bad/tactical move
            explanation = getExplanation(boardX, boardY);
            this.repaint();
        }
    }

    private String getExplanation(int boardX, int boardY) {
        String ex = "";
        //ex += "MCTS simulations: " + bot.totalSimulations + "\n";

        int bestX = 0;
        int bestY = 0;
        for (UCTEdge loop=bot.root.child;loop!=null;loop=loop.sibling) {
            if (moveNodePairs.get(bestMove) == loop.child){
                bestX = loop.move%15;
                bestY = (int) Math.floor(loop.move/15);
            }
        }

        ex += "MCTS Move Location: " + moveYmapping.get(bestX) + (bestY+1) + "\n";
        ex += "Current Location: " + moveYmapping.get(boardX) + (boardY+1) + "\n\n";

        boolean debug = false;
        if (debug){
            ex += "Available Moves: \n";
            for (int possibleMove : SameGameBoard.generateMoves(boardPanel.getPosition())){
                ex += "Move: x=" + possibleMove%15 +", y="+ Math.floor(possibleMove/15) +  "\n\n";
            }
        }

        int move = 15*boardY + boardX;
        int moveID = -1;
        for(Cluster cluster: moves){
            for (int tile:cluster.shape){
                if (tile == move){
                    moveID = cluster.ID;
                    break;
                }
            }
        }

        if (SameGameBoard.legalMove(boardPanel.getPosition(), boardPanel.getXDim(), boardPanel.getYDim(), boardX, boardY)) {
            ex += "The highest score MCTS was able to achieve from this move on is: " + moveNodePairs.get(moveID).topScore + "\n";
            ex += "---------------------------------------------\n";

            // Move explanations
            Map<String, Float> sortedMap = MapUtil.sortByValue(nodeFeatureScores.get(moveNodePairs.get(moveID).nodeID));

            ArrayList<String> sortedMoveExplanationsPositive = new ArrayList<>();
            ArrayList<String> sortedMoveExplanationsNegative = new ArrayList<>();
            HashMap<Integer, Float> keysPos = new HashMap<>();
            HashMap<Integer, Float> keysNeg = new HashMap<>();
            sortedMap.forEach((K, V) -> {
                if(V > 0.015){
                    keysPos.put(sortedMoveExplanationsPositive.size(), V);
                    sortedMoveExplanationsPositive.add(explanations.get(K).get("positive"));
                }else if(V < -0.015){
                    keysNeg.put(sortedMoveExplanationsNegative.size(), V);
                    sortedMoveExplanationsNegative.add(explanations.get(K).get("negative"));
                }
            });

            ex += "What is good about this move compared to other moves?\n";
            for (int i = 0; i < Math.min(5, sortedMoveExplanationsPositive.size()); i++) {
                ex += "- " + String.format(sortedMoveExplanationsPositive.get(i), "This move") + " (" + Math.round(keysPos.get(i) * 1000) / 1000.0 + ")" + "\n";
            }

            ex += "\n";
            ex += "What is bad about this move compared to other moves?\n";
            for (int i = 0; i < Math.min(5, sortedMoveExplanationsNegative.size()); i++) {
                ex += "- " + String.format(sortedMoveExplanationsNegative.get(sortedMoveExplanationsNegative.size() - (i+1)), "This move") + " (" + Math.round(keysNeg.get(sortedMoveExplanationsNegative.size() - (i+1)) * 1000) / 1000.0 + ")" + "\n";
            }
            ex += "\n";


            // Move vs best move explanations
            if (bestMove != moveID){
                ex += "\n";
                HashMap<String, Float> nodeFeatureScoresMCTSDiff = new HashMap<>();
                nodeFeatureScores.get(moveNodePairs.get(moveID).nodeID).forEach((feature, value) -> {
                    nodeFeatureScoresMCTSDiff.put(feature, nodeFeatureScores.get(moveNodePairs.get(bestMove).nodeID).get(feature) - value);
                });

                Map<String, Float> sortedMapMCTS = MapUtil.sortByValue(nodeFeatureScoresMCTSDiff);

                ArrayList<String> sortedMCTSExplanationsPositive = new ArrayList<>();
                ArrayList<String> sortedMCTSExplanationsNegative = new ArrayList<>();
                HashMap<Integer, Float> keysMCTSPos = new HashMap<>();
                HashMap<Integer, Float> keysMCTSNeg = new HashMap<>();
                sortedMapMCTS.forEach((K, V) -> {
                    if(V > 0.015){
                        keysMCTSPos.put(sortedMCTSExplanationsPositive.size(), V);
                        sortedMCTSExplanationsPositive.add(explanations.get(K).get("positive"));
                    }else if(V < -0.015){
                        keysMCTSNeg.put(sortedMCTSExplanationsNegative.size(), V);
                        sortedMCTSExplanationsNegative.add(explanations.get(K).get("positive"));
                    }
                });

                ex += "What is good about MCTS's move compared to this move?\n";
                for (int i = 0; i < Math.min(5, sortedMCTSExplanationsPositive.size()); i++) {
                    ex += "- " + String.format(sortedMCTSExplanationsPositive.get(i), "MCTS's move") + " (" + Math.round(keysMCTSPos.get(i) * 1000) / 1000.0 + ")" + "\n";
                }
                ex += "\n";

                ex += "What is good about this move compared to MCTS's move?\n";
                for (int i = 0; i < Math.min(5, sortedMCTSExplanationsNegative.size()); i++) {
                    ex += "- " + String.format(sortedMCTSExplanationsNegative.get(sortedMCTSExplanationsNegative.size() - (i+1)), "This move") + " (" + Math.round(keysMCTSNeg.get(sortedMCTSExplanationsNegative.size() - (i+1)) * 1000) / -1000.0 + ")" + "\n";
                }
                ex += "\n";
            }

//            ex += "Biggest diff w/ MCTS: " + bestFeatureExplanation + "\n";
//            ex += "Difference in Score: " + bestScore + "\n";

            if (debug){
                if (nodeFeatureScores.get(moveNodePairs.get(moveID).nodeID) != null){
                    for (String key : nodeFeatureScores.get(moveNodePairs.get(moveID).nodeID).keySet()){
                        ex += key + ": " + nodeFeatureScores.get(moveNodePairs.get(moveID).nodeID).get(key) + "\n";

                    }
                }
            }

        } else {
            ex += "Not a legal move!";
        }
        return ex;
    }

    public void congregateMoves(){
        Clusters movesGenerator = new Clusters(boardPanel.getPosition(), 15, 15,0, -1, -1);
        movesGenerator.generateIDs();
        moves = movesGenerator.getClusters(0);
        int bestScore = 0;
        if (!moves.isEmpty()){
            for (UCTEdge loop=bot.root.child;loop!=null;loop=loop.sibling){
                for(Cluster cluster : moves){
                    for (int tile:cluster.shape){
                        if (tile == loop.move){
                            moveNodePairs.put(cluster.ID, loop.child);
                            if (loop.topScore > bestScore){
                                bestMove = cluster.ID;
                                bestScore = loop.topScore;
                            }
                            break;
                        }
                    }
                }
            }
        }

        return;
    }

    public void updateFeatures(){
        xDim = boardPanel.getXDim();
        yDim = boardPanel.getYDim();
        getTreeFeautures(bot.root, boardPanel.getPosition());
    }

    public void getTreeFeautures(UCTNode n, byte[] pos){
        featureCollector.resetForNewGame();
        saveTree(n, 0 , pos, null, -1 , -1);
        updateFeatureScores();
    }

    private void updateFeatureScores() {
        nodeFeatureScores = new HashMap<>();
        GameStates gameStates = (GameStates) featureCollector.gameFeatures.get("GameStates");
        Clusters clusters = (Clusters) featureCollector.gameFeatures.get("Clusters");
        Columns columns = (Columns) featureCollector.gameFeatures.get("Columns");
        Moves moves = (Moves) featureCollector.gameFeatures.get("Moves");

        // get all the features and their averages
        HashMap<String, Float> avgValues = new HashMap<>();

        //ArrayList<Integer> nodeIDs = gameStates.getNodeIDs(1);
        ArrayList<Integer> nodeIDs = new ArrayList<>();
        for (UCTEdge loop=bot.root.child;loop!=null;loop=loop.sibling){
            nodeIDs.add(loop.child.nodeID);
        }

        for (Integer nodeID : nodeIDs) {
            HashMap<String, Float> nodeScores = new HashMap<>();

            // GameState features
            // float gameScore = gameStates.getState(nodeID).getScore();
            // nodeScores.put("gameScore", gameScore);
            // avgValues.put("gameScore", avgValues.getOrDefault("gameScore", 0.0f) + gameScore / (float) nodeIDs.size());

            // Move features
            int numRemovedCells = moves.getMove(nodeID).getNumRemovedCells();
            nodeScores.put("numRemovedCells", (float) numRemovedCells);
            avgValues.put("numRemovedCells", avgValues.getOrDefault("numRemovedCells", 0.0f) + numRemovedCells / (float) nodeIDs.size());

            int numRemovedColumns = moves.getMove(nodeID).getNumRemovedColumns();
            nodeScores.put("numRemovedColumns", (float) numRemovedColumns);
            avgValues.put("numRemovedColumns", avgValues.getOrDefault("numRemovedColumns", 0.0f) + numRemovedColumns / (float) nodeIDs.size());

            int connectionsDestroyed = moves.getMove(nodeID).getConnectionsDestroyed();
            nodeScores.put("connectionsDestroyed", (float) connectionsDestroyed);
            avgValues.put("connectionsDestroyed", avgValues.getOrDefault("connectionsDestroyed", 0.0f) + connectionsDestroyed / (float) nodeIDs.size());

            int connectionsCreated = moves.getMove(nodeID).getConnectionsCreated();
            nodeScores.put("connectionsCreated", (float) connectionsCreated);
            avgValues.put("connectionsCreated", avgValues.getOrDefault("connectionsCreated", 0.0f) + connectionsCreated / (float) nodeIDs.size());

            int moveColumn = moves.getMove(nodeID).getMoveColumn();
            nodeScores.put("moveColumn", (float) moveColumn);
            avgValues.put("moveColumn", avgValues.getOrDefault("moveColumn", 0.0f) + moveColumn / (float) nodeIDs.size());

            int moveHeight = moves.getMove(nodeID).getMoveHeight();
            nodeScores.put("moveHeight", (float) moveHeight);
            avgValues.put("moveHeight", avgValues.getOrDefault("moveHeight", 0.0f) + moveHeight / (float) nodeIDs.size());

            // Column features
            float avgColHeight = columns.getAvgColumnHeight(nodeID);
            nodeScores.put("avgColHeight", (float) avgColHeight);
            avgValues.put("avgColHeight", avgValues.getOrDefault("avgColHeight", 0.0f) + avgColHeight / (float) nodeIDs.size());

            int highestColumn = columns.getHighestColumnHeight(nodeID);
            nodeScores.put("highestColumn", (float) highestColumn);
            avgValues.put("highestColumn", avgValues.getOrDefault("highestColumn", 0.0f) + highestColumn / (float) nodeIDs.size());

            float avgNumColorsPerColumn = columns.getAvgNumColorsPerColumn(nodeID);
            nodeScores.put("avgNumColorsPerColumn", (float) avgNumColorsPerColumn);
            avgValues.put("avgNumColorsPerColumn", avgValues.getOrDefault("avgNumColorsPerColumn", 0.0f) + avgNumColorsPerColumn / (float) nodeIDs.size());

            // Cluster features
            float avgClusterSize = clusters.getAvgClusterSize(nodeID);
            nodeScores.put("avgClusterSize", avgClusterSize);
            avgValues.put("avgClusterSize", avgValues.getOrDefault("avgClusterSize", 0.0f) + avgClusterSize / (float) nodeIDs.size());

            int largestClusterSize = clusters.getBiggestCluster(nodeID).numCells;
            nodeScores.put("largestClusterSize", (float) largestClusterSize);
            avgValues.put("largestClusterSize", avgValues.getOrDefault("largestClusterSize", 0.0f) + largestClusterSize / (float) nodeIDs.size());

            int numCluster2 = clusters.getClusterCount(nodeID, 2 ,false);
            nodeScores.put("numCluster2", (float) numCluster2);
            avgValues.put("numCluster2", avgValues.getOrDefault("numCluster2", 0.0f) + numCluster2 / (float) nodeIDs.size());

            int numCluster3 = clusters.getClusterCount(nodeID, 3 ,false);
            nodeScores.put("numCluster3", (float) numCluster3);
            avgValues.put("numCluster3", avgValues.getOrDefault("numCluster3", 0.0f) + numCluster3 / (float) nodeIDs.size());

            int numCluster4 = clusters.getClusterCount(nodeID, 4 ,false);
            nodeScores.put("numCluster4", (float) numCluster4);
            avgValues.put("numCluster4", avgValues.getOrDefault("numCluster4", 0.0f) + numCluster4 / (float) nodeIDs.size());

            int numCluster5 = clusters.getClusterCount(nodeID, 5 ,false);
            nodeScores.put("numCluster5", (float) numCluster5);
            avgValues.put("numCluster5", avgValues.getOrDefault("numCluster5", 0.0f) + numCluster5 / (float) nodeIDs.size());

            int numCluster6 = clusters.getClusterCount(nodeID, 6 ,false);
            nodeScores.put("numCluster6", (float) numCluster6);
            avgValues.put("numCluster6", avgValues.getOrDefault("numCluster6", 0.0f) + numCluster6 / (float) nodeIDs.size());

            int numCluster7plus = clusters.getClusterCount(nodeID, 7 ,true);
            nodeScores.put("numCluster7plus", (float) numCluster7plus);
            avgValues.put("numCluster7plus", avgValues.getOrDefault("numCluster7plus", 0.0f) + numCluster7plus / (float) nodeIDs.size());

            nodeFeatureScores.put(nodeID, nodeScores);
        }

        nodeFeatureScores.forEach((i, nodeScores) -> {
            nodeScores.forEach((feature, value) -> {
                nodeScores.put(feature, ((value - avgValues.get(feature)) / (avgValues.get(feature) + 1)) * featureImportanceLookupTable.get(feature));
            });
        });
    }

    public int saveTree(UCTNode n, int depth, byte[] pos, byte[] prevPos, int parentID, int move)
    {
        if (n==null /*|| n.simulations < 10*/) return 0;

        nodeID++;
        int nID = nodeID;
        n.nodeID = nID;
        if (parentID != -1) {
            featureCollector.findGameFeatures(pos, prevPos, xDim, yDim, depth, move, n.average, nID, n.topScore);
        } else {
            featureCollector.findGameFeatures(pos, null, xDim, yDim, depth, -1, n.average, nID, n.topScore);
        }
        featureCollector.addEdge(nID, parentID);

        int max_depth = depth;
        if (parentID == -1){
            for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
            {
                int colorPlayed = pos[loop.move];
                byte[] position = new byte[255];
                System.arraycopy(pos, 0, position, 0, xDim*yDim);
                SameGameBoard.makeMove(position, loop.move, loop.move % 15, loop.move / 15, colorPlayed);
                SameGameBoard.dropDownStones(position, 15, 15);

                int child_depth = saveTree(loop.child, depth+1, position, pos, nID, loop.move);
                if (child_depth > max_depth){
                    max_depth = child_depth;
                }
            }
        } else {
            if (n.child != null){
                UCTEdge bestChild = n.child;
                for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling){
                    if (loop.average > bestChild.average){
                        bestChild = loop;
                    }
                }

                int colorPlayed = pos[bestChild.move];
                byte[] position = new byte[255];
                System.arraycopy(pos, 0, position, 0, xDim*yDim);
                SameGameBoard.makeMove(position, bestChild.move, bestChild.move % 15, bestChild.move / 15, colorPlayed);
                SameGameBoard.dropDownStones(position, 15, 15);

                int child_depth = saveTree(bestChild.child, depth+1, position, pos, nID, bestChild.move);
                if (child_depth > max_depth){
                    max_depth = child_depth;
                }
            }
        }

        return max_depth;
    }


    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void setBot(MCTSPlayer bot) {
        this.bot = bot;
    }
}
