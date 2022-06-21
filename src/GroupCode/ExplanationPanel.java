package GroupCode;

import OldCode.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ExplanationPanel extends JTextArea {
    private int xSizePanel = 300;
    private int ySizePanel = 300;

    private int xDim, yDim, nodeID;
    private BoardPanel boardPanel;
    private MCTSPlayer bot;
    private ArrayList<Cluster> moves;
    private HashMap<Integer, UCTNode> moveNodePairs = new HashMap<>();

    private FeatureCollector featureCollector;
    private HashMap<Integer, HashMap<String, Float>> nodeFeatureScores;
    private HashMap<String, HashMap<String, Float>> featureImportanceLookupTable;

    private String explanation;

    public ExplanationPanel(){
        setPreferredSize(new Dimension(xSizePanel, ySizePanel));
        explanation = "XAI not enabled!";
        setEditable(false);
        setFocusable(false);
        setBackground(new Color(236,233,216));
        featureCollector = new FeatureCollector();
        nodeID=0;
        importFeatureImportance();
    }

    private void importFeatureImportance() {
        featureImportanceLookupTable = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject featureValues = (JSONObject) parser.parse(new FileReader("src/data/FeatureValues.json"));
            JSONObject numClusters = (JSONObject) featureValues.get("num_clusters");
            featureImportanceLookupTable.put("numClusters", new HashMap<>(){{
                put("3", (float) (double) numClusters.get("3"));
                put("4", (float) (double) numClusters.get("4"));
                put("5", (float) (double) numClusters.get("5"));
                put("6", (float) (double) numClusters.get("6"));
                put("6+", (float) (double) numClusters.get("6+"));
            }});

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        xSizePanel=this.getWidth();
        ySizePanel=this.getHeight();
        this.setText(explanation);
    }

    public void updateExplanation(int boardX, int boardY) {
        // TODO: update to new json file

        // (relevance ranking => score) or (feature rules) => scoring/bad/tactical move
        explanation = getExplanation(boardX, boardY);
        this.repaint();
    }

    private String getExplanation(int boardX, int boardY) {
        String ex = "";
        ex += "MCTS simulations: " + bot.totalSimulations + "\n";

        boolean debug = false;
        if (debug){
            ex += "Available Moves: \n";
            for (int possibleMove : SameGameBoard.generateMoves(boardPanel.getPosition())){
                ex += "Move: x=" + possibleMove%15 +", y="+ Math.floor(possibleMove/15) +  "\n";
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
            return ex + "Explanation for move: " + moveID + "\nSims for this move: " + moveNodePairs.get(moveID).simulations +"\nScore for this move: " + moveNodePairs.get(moveID).topScore;
        } else {
            return ex + "Not a legal move!";
        }
    }

    public void congregateMoves(){
        Clusters movesGenerator = new Clusters(boardPanel.getPosition(), 15, 15,0, -1, -1);
        movesGenerator.generateIDs();
        moves = movesGenerator.getClusters(0);
        for (UCTEdge loop=bot.root.child;loop!=null;loop=loop.sibling){
            for(Cluster cluster : moves){
                for (int tile:cluster.shape){
                    if (tile == loop.move){
                        moveNodePairs.put(cluster.ID, loop.child);
                        break;
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

        // get all the features and their averages
        float avgNumCluster3 = 0;
        float avgNumCluster4 = 0;
        float avgNumCluster5 = 0;
        float avgNumCluster6 = 0;
        float avgNumCluster6plus = 0;
        ArrayList<Integer> nodeIDs = gameStates.getNodeIDs(1);
        for (Integer nodeID : nodeIDs) {
            HashMap<String, Float> nodeScores = new HashMap<>();

            int numCluster3 = clusters.getClusterCount(nodeID, 3 ,false);
            nodeScores.put("numCluster3", (float) numCluster3);
            avgNumCluster3 += numCluster3 / (float) nodeIDs.size();

            int numCluster4 = clusters.getClusterCount(nodeID, 4 ,false);
            nodeScores.put("numCluster4", (float) numCluster4);
            avgNumCluster4 += numCluster4 / (float) nodeIDs.size();

            int numCluster5 = clusters.getClusterCount(nodeID, 5 ,false);
            nodeScores.put("numCluster5", (float) numCluster5);
            avgNumCluster5 += numCluster5 / (float) nodeIDs.size();

            int numCluster6 = clusters.getClusterCount(nodeID, 6 ,false);
            nodeScores.put("numCluster6", (float) numCluster6);
            avgNumCluster6 += numCluster6 / (float) nodeIDs.size();

            int numCluster6plus = clusters.getClusterCount(nodeID, 7 ,true);
            nodeScores.put("numCluster6plus", (float) numCluster6plus);
            avgNumCluster6plus += numCluster6plus / (float) nodeIDs.size();

            nodeFeatureScores.put(nodeID, nodeScores);
        }

        float finalAvgNumCluster3 = avgNumCluster3;
        float finalAvgNumCluster4 = avgNumCluster4;
        float finalAvgNumCluster5 = avgNumCluster5;
        float finalAvgNumCluster6 = avgNumCluster6;
        float finalAvgNumCluster6plus = avgNumCluster6plus;
        nodeFeatureScores.forEach((i, nodeScores) -> {
            nodeScores.forEach((feature, value) -> {
                if (Objects.equals(feature, "numCluster3")){
                    nodeScores.put(feature, (value - finalAvgNumCluster3) / finalAvgNumCluster3 * featureImportanceLookupTable.get("numClusters").get("3"));
                } else if (Objects.equals(feature, "numCluster4")) {
                    nodeScores.put(feature, (value - finalAvgNumCluster4) / finalAvgNumCluster4 * featureImportanceLookupTable.get("numClusters").get("4"));
                } else if (Objects.equals(feature, "numCluster5")) {
                    nodeScores.put(feature, (value - finalAvgNumCluster5) / finalAvgNumCluster5 * featureImportanceLookupTable.get("numClusters").get("5"));
                } else if (Objects.equals(feature, "numCluster6")) {
                    nodeScores.put(feature, (value - finalAvgNumCluster6) / finalAvgNumCluster6 * featureImportanceLookupTable.get("numClusters").get("6"));
                } else if (Objects.equals(feature, "numCluster6plus")) {
                    nodeScores.put(feature, (value - finalAvgNumCluster6plus) / finalAvgNumCluster6plus * featureImportanceLookupTable.get("numClusters").get("6+"));
                }
            });
        });
        System.out.println();
    }

    public int saveTree(UCTNode n, int depth, byte[] pos, byte[] prevPos, int parentID, int move)
    {
        if (n==null || n.simulations < 10) return 0;

        nodeID++;
        int nID = nodeID;

        if (parentID != -1) {
            featureCollector.findGameFeatures(pos, prevPos, xDim, yDim, depth, move, n.topScore, nID);
        } else {
            featureCollector.findGameFeatures(pos, null, xDim, yDim, depth, -1, n.topScore, nID);
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
                    if (loop.topScore > bestChild.topScore){
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
