package GroupCode;

import OldCode.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FeatureCollector {
    int gameID;
    JSONObject gameJSON;
    HashMap<String, BoardFeatures> gameFeatures;
    ArrayList<int[]> nodeEdges = new ArrayList<>();

    public static void main(String[] args) {
        FeatureCollector featureCollector = new FeatureCollector();

        System.out.println("Starting Feature Collection");

        MCTSPlayerFC bot = new MCTSPlayerFC();
        bot.output = false;
        bot.maxNumberOfNodes = 5000;
        int millisecondsPerMove = 1000;

        System.out.println("Settings:");
        System.out.println("UCT Constant: " + bot.UCTConstant);
        System.out.println("Deviation Constant: " + bot.DeviationConstant);
        System.out.println("Number of visited nodes before expanding: " + MCTSPlayer.numberOfVisitsBeforeExpanding);
        System.out.println("Chance of playing chosen Color: " + Parameters.chanceOfPlayingChosenColor);
        System.out.println("Top Score weight: " + MCTSPlayer.topScoreWeight);
        System.out.println("Number of nodes: " + UCTNode.totalNodes);

        long startTime = System.currentTimeMillis();
        int max_pos = 100000;
        int num_pos = 0;
        for (byte[] position : positions) {
            if (num_pos == max_pos){break;}
            bot.playGame(position, 15, 15, BoardPanel.SAMEGAME, millisecondsPerMove, featureCollector);
            Runtime.getRuntime().gc();
            num_pos++;
            featureCollector.gameIDAdd();
        }
        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("Total time: " + (int) ((endTime - startTime) / 1000.0) + " seconds");
        System.exit(0);
    }

    public FeatureCollector() {
        this.resetForNewGame();
    }

    public void gameIDAdd(){
        gameID++;
    }

    public void resetForNewGame() {
        // Empty all feature collectors
        gameJSON = new JSONObject();
        gameFeatures = new HashMap<>() {{
            put("Clusters", new Clusters());
            put("Moves", new Moves());
            put("Columns", new Columns());
            put("PlayableArea", new PlayableArea());
            put("GameStates", new GameStates());
        }};

        nodeEdges.clear();
    }

    public void findGameFeatures(byte[] searchSpace, byte[] prevSearchSpace, int xDim, int yDim, int gameStep, int move, float mctsScore, int nodeID, float gameScore) {
        for (BoardFeatures gameFeature : gameFeatures.values()) {
            if (gameFeature instanceof Moves){
                gameFeature.findFeatures(prevSearchSpace, xDim, yDim, gameStep, move, mctsScore, nodeID);
            } else if (gameFeature instanceof GameStates) {
                gameFeature.findFeatures(searchSpace, xDim, yDim, gameStep, move, gameScore, nodeID);
            } else {
                gameFeature.findFeatures(searchSpace, xDim, yDim, gameStep, move, mctsScore, nodeID);
            }
        }
    }

    public void addEdge(int child, int parent){
        int[] edge = {child, parent};
        nodeEdges.add(edge);
    }

    public void exportJSON(int maxDepth, int num) {
        Clusters clusters = (Clusters) gameFeatures.get("Clusters");
        Moves moves = (Moves) gameFeatures.get("Moves");
        Columns columns = (Columns) gameFeatures.get("Columns");
        PlayableArea playableArea = (PlayableArea) gameFeatures.get("PlayableArea");
        GameStates gameStates = (GameStates) gameFeatures.get("GameStates");
        try {
            for (int gameStep = 0; gameStep < maxDepth; gameStep++) {
                //game state json
                JSONObject jsonGameStep = new JSONObject();

                // get all nodes at this gameStep from the gameState feature
                ArrayList<Integer> nodeIDs = gameStates.getNodeIDs(gameStep);
                for (Integer nodeID : nodeIDs) {
                    JSONObject jsonNode = new JSONObject();
                    jsonNode.put("GameState", gameStates.getState(nodeID).toJSON());

                    //cluster json
                    JSONArray jsonCluster = new JSONArray();
                    for (Cluster cluster : clusters.getClusters(nodeID)) {
                        jsonCluster.put(cluster.toJSON());
                    }
                    jsonNode.put("Clusters", jsonCluster);

                    //move json
                    Move move = moves.getMove(nodeID);
                    if (move != null){
                        jsonNode.put("Move", move.toJSON());
                    } else {
                        jsonNode.put("Move", "root node");
                    }

                    //columns json
                    JSONArray jsonColumns = new JSONArray();
                    for (Column column : columns.getColumns(nodeID)) {
                        jsonColumns.put(column.toJSON());
                    }
                    jsonNode.put("Columns", jsonColumns);

                    //play area json
                    Area playArea = playableArea.getPlayArea(nodeID);
                    if (playArea != null) {
                        jsonNode.put("PlayArea", playArea.toJSON());
                    } else {
                        jsonNode.put("PlayArea", "no tiles left");
                    }

                    Area emptyArea = playableArea.getEmptyArea(nodeID);
                    if (emptyArea != null) {
                        jsonNode.put("PlayArea", emptyArea.toJSON());
                    } else {
                        jsonNode.put("PlayArea", "board is full");
                    }

                    jsonGameStep.put(Integer.toString(nodeID), jsonNode);
                }

                gameJSON.put(Integer.toString(gameStep), jsonGameStep);
            }
            gameJSON.put("Edges", nodeEdges);
            //fullJSON.put(Integer.toString(gameID), gameJSON);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter("data/g"+gameID+"m"+num+".json")) {
            fileWriter.write(gameJSON.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    private static final byte[][] positions = {
            {3, 1, 1, 4, 1, 0, 4, 0, 4, 4, 1, 1, 0, 2, 3,
                    3, 3, 2, 0, 4, 4, 1, 3, 1, 2, 0, 0, 4, 0, 4,
                    0, 2, 3, 4, 3, 0, 3, 0, 0, 3, 4, 4, 1, 1, 1,
                    2, 3, 4, 0, 2, 3, 0, 2, 4, 4, 4, 3, 0, 2, 3,
                    1, 2, 1, 3, 1, 2, 0, 1, 2, 1, 0, 3, 4, 0, 1,
                    0, 4, 4, 3, 0, 3, 4, 2, 2, 2, 0, 2, 3, 4, 0,
                    2, 4, 3, 4, 2, 3, 1, 1, 1, 3, 4, 1, 0, 3, 1,
                    1, 0, 0, 4, 0, 3, 1, 2, 1, 0, 4, 1, 3, 3, 1,
                    1, 3, 3, 2, 0, 4, 3, 1, 3, 0, 4, 1, 0, 0, 3,
                    0, 3, 3, 4, 2, 3, 0, 0, 2, 1, 2, 3, 4, 0, 1,
                    0, 4, 1, 2, 0, 1, 3, 4, 3, 3, 4, 1, 4, 0, 4,
                    2, 2, 3, 1, 0, 4, 0, 1, 2, 4, 1, 3, 3, 0, 1,
                    3, 3, 0, 2, 3, 2, 1, 4, 3, 1, 3, 0, 2, 1, 3,
                    1, 0, 3, 2, 1, 4, 4, 4, 4, 0, 4, 2, 1, 3, 4,
                    1, 0, 1, 0, 1, 1, 2, 2, 1, 0, 0, 1, 4, 3, 2},

            {3, 3, 0, 1, 0, 2, 1, 2, 3, 2, 3, 1, 1, 1, 0,
                    4, 1, 3, 4, 0, 3, 3, 2, 2, 4, 0, 2, 4, 0, 0,
                    2, 3, 2, 2, 0, 3, 1, 0, 4, 4, 0, 2, 4, 0, 4,
                    0, 3, 4, 4, 2, 2, 1, 3, 3, 1, 3, 0, 3, 3, 4,
                    0, 0, 2, 1, 2, 1, 3, 4, 3, 2, 1, 2, 3, 1, 4,
                    1, 2, 4, 2, 0, 0, 0, 1, 1, 1, 0, 0, 2, 4, 4,
                    1, 0, 3, 3, 3, 2, 1, 0, 4, 2, 4, 1, 4, 3, 0,
                    4, 4, 3, 3, 0, 2, 3, 3, 4, 3, 0, 3, 0, 0, 4,
                    3, 3, 3, 1, 4, 3, 3, 3, 0, 4, 2, 0, 3, 2, 0,
                    2, 4, 1, 1, 1, 1, 4, 0, 0, 3, 0, 4, 0, 4, 3,
                    3, 3, 0, 1, 4, 1, 2, 1, 1, 0, 3, 4, 2, 1, 0,
                    2, 2, 3, 3, 2, 0, 4, 3, 3, 4, 0, 4, 3, 3, 1,
                    0, 1, 3, 2, 1, 2, 1, 1, 0, 2, 4, 1, 4, 0, 3,
                    4, 1, 4, 0, 2, 1, 3, 1, 3, 1, 4, 0, 1, 0, 3,
                    1, 3, 2, 3, 2, 2, 4, 2, 2, 4, 3, 0, 3, 1, 1},

            {4, 2, 4, 3, 1, 0, 3, 3, 2, 2, 4, 3, 1, 4, 2,
                    3, 0, 3, 4, 0, 3, 3, 3, 2, 4, 4, 3, 1, 3, 3,
                    2, 0, 4, 4, 0, 1, 2, 2, 2, 3, 4, 0, 4, 4, 0,
                    0, 4, 3, 0, 0, 2, 4, 2, 1, 2, 0, 3, 2, 4, 2,
                    0, 2, 0, 2, 0, 1, 1, 3, 2, 1, 1, 2, 3, 4, 0,
                    1, 0, 1, 0, 4, 3, 3, 3, 4, 2, 2, 2, 3, 4, 1,
                    2, 3, 4, 3, 4, 2, 2, 4, 2, 4, 3, 4, 4, 0, 1,
                    4, 2, 3, 2, 2, 0, 1, 2, 4, 3, 3, 0, 0, 2, 1,
                    3, 4, 4, 3, 0, 4, 3, 4, 1, 0, 0, 2, 1, 4, 3,
                    4, 0, 1, 3, 1, 0, 2, 3, 0, 2, 0, 2, 3, 0, 1,
                    4, 2, 0, 0, 0, 2, 2, 1, 0, 2, 3, 1, 1, 3, 1,
                    0, 3, 1, 1, 3, 3, 2, 1, 2, 0, 0, 4, 2, 4, 1,
                    2, 1, 4, 4, 4, 0, 3, 3, 4, 2, 0, 0, 2, 0, 0,
                    1, 0, 4, 4, 0, 1, 3, 2, 4, 0, 4, 2, 0, 0, 1,
                    2, 2, 2, 2, 3, 3, 0, 4, 3, 3, 4, 0, 4, 1, 2},

            {4, 2, 2, 4, 1, 3, 3, 2, 4, 0, 4, 2, 3, 4, 2,
                    2, 0, 2, 1, 2, 1, 0, 1, 2, 1, 1, 3, 0, 4, 2,
                    0, 2, 3, 2, 0, 0, 4, 1, 0, 4, 3, 0, 0, 3, 2,
                    2, 2, 3, 1, 1, 0, 0, 1, 0, 1, 1, 4, 3, 0, 0,
                    4, 2, 0, 4, 2, 2, 0, 3, 0, 0, 2, 2, 1, 4, 2,
                    1, 4, 3, 3, 2, 3, 0, 4, 4, 0, 0, 2, 2, 3, 0,
                    2, 1, 1, 4, 1, 0, 1, 0, 4, 4, 1, 0, 4, 1, 3,
                    3, 3, 0, 2, 1, 3, 1, 1, 4, 0, 2, 3, 3, 3, 3,
                    2, 3, 3, 1, 3, 1, 0, 4, 1, 0, 1, 2, 3, 0, 4,
                    3, 2, 1, 1, 3, 4, 0, 2, 4, 2, 4, 2, 0, 2, 0,
                    0, 3, 0, 1, 4, 0, 0, 0, 4, 2, 1, 0, 2, 4, 0,
                    2, 0, 1, 4, 2, 3, 1, 4, 2, 0, 1, 0, 3, 4, 2,
                    0, 4, 2, 0, 3, 4, 4, 3, 1, 1, 3, 4, 2, 1, 4,
                    4, 2, 4, 0, 4, 3, 0, 2, 2, 4, 1, 4, 3, 4, 1,
                    4, 3, 2, 2, 2, 1, 1, 2, 3, 3, 1, 2, 0, 3, 2},

            {3, 4, 4, 3, 2, 3, 2, 1, 3, 4, 1, 2, 3, 3, 2,
                    2, 0, 2, 0, 3, 1, 0, 3, 1, 1, 2, 1, 4, 3, 4,
                    1, 3, 1, 0, 3, 1, 3, 2, 3, 4, 0, 0, 1, 4, 1,
                    0, 2, 1, 0, 2, 2, 2, 4, 1, 0, 4, 4, 3, 3, 2,
                    2, 3, 1, 3, 0, 4, 0, 2, 3, 0, 1, 4, 4, 2, 3,
                    3, 1, 3, 3, 2, 3, 0, 1, 0, 4, 3, 4, 0, 1, 4,
                    4, 4, 4, 2, 2, 3, 0, 0, 0, 1, 0, 1, 2, 1, 3,
                    2, 1, 3, 4, 4, 0, 4, 1, 0, 4, 0, 1, 2, 1, 3,
                    3, 4, 3, 1, 2, 0, 1, 3, 3, 0, 1, 4, 2, 0, 0,
                    2, 3, 0, 1, 2, 4, 3, 3, 0, 1, 1, 2, 2, 3, 3,
                    4, 4, 1, 0, 3, 3, 4, 4, 2, 2, 4, 2, 0, 3, 0,
                    3, 1, 0, 4, 3, 2, 0, 2, 3, 1, 4, 3, 1, 2, 2,
                    2, 2, 3, 0, 2, 4, 1, 3, 0, 3, 2, 1, 3, 4, 2,
                    2, 4, 3, 1, 3, 0, 3, 2, 0, 4, 3, 2, 2, 3, 4,
                    0, 4, 2, 2, 2, 3, 2, 0, 1, 1, 4, 0, 1, 3, 3},

            {2, 4, 2, 0, 4, 2, 2, 3, 1, 0, 1, 3, 4, 2, 0,
                    2, 3, 3, 2, 3, 1, 3, 3, 0, 1, 4, 1, 0, 0, 1,
                    0, 4, 3, 0, 3, 1, 3, 3, 3, 1, 0, 2, 4, 2, 1,
                    3, 0, 1, 0, 1, 2, 3, 0, 0, 2, 1, 1, 1, 4, 4,
                    0, 1, 1, 1, 2, 0, 2, 1, 3, 4, 2, 0, 3, 1, 0,
                    1, 1, 1, 4, 1, 1, 0, 0, 1, 1, 4, 1, 1, 2, 1,
                    3, 3, 0, 1, 1, 3, 2, 0, 0, 0, 0, 1, 2, 0, 1,
                    0, 3, 0, 3, 4, 0, 1, 1, 2, 1, 4, 2, 1, 0, 2,
                    1, 2, 2, 2, 2, 3, 4, 1, 3, 1, 4, 2, 4, 1, 1,
                    2, 2, 0, 3, 3, 0, 2, 2, 3, 3, 2, 2, 1, 0, 3,
                    2, 4, 0, 0, 4, 0, 4, 3, 4, 4, 3, 4, 1, 4, 4,
                    2, 1, 2, 3, 1, 1, 2, 2, 1, 0, 3, 1, 4, 4, 0,
                    2, 3, 2, 2, 1, 1, 4, 0, 1, 4, 4, 0, 4, 3, 3,
                    1, 1, 3, 0, 3, 1, 4, 3, 4, 1, 0, 4, 1, 1, 4,
                    0, 4, 4, 4, 2, 2, 4, 3, 1, 1, 3, 2, 4, 4, 1},

            {3, 4, 0, 3, 1, 2, 0, 1, 3, 1, 2, 4, 1, 1, 3,
                    3, 1, 4, 3, 0, 0, 1, 3, 0, 2, 0, 4, 4, 4, 4,
                    0, 4, 3, 2, 1, 1, 0, 2, 2, 1, 3, 4, 0, 2, 3,
                    2, 4, 0, 1, 3, 3, 3, 2, 2, 2, 2, 0, 2, 2, 0,
                    0, 4, 0, 0, 2, 1, 0, 1, 4, 3, 3, 3, 1, 0, 2,
                    1, 0, 4, 1, 2, 4, 4, 2, 2, 0, 0, 0, 3, 4, 4,
                    4, 2, 1, 3, 1, 2, 0, 1, 3, 4, 2, 2, 1, 3, 2,
                    1, 1, 1, 0, 3, 0, 3, 1, 3, 3, 1, 1, 2, 3, 0,
                    1, 2, 4, 3, 1, 4, 1, 1, 1, 0, 2, 3, 0, 3, 3,
                    0, 4, 1, 3, 4, 0, 4, 1, 4, 0, 4, 2, 3, 0, 1,
                    0, 4, 3, 4, 2, 4, 1, 3, 1, 3, 0, 4, 3, 0, 0,
                    3, 1, 1, 1, 0, 4, 2, 0, 3, 0, 4, 4, 2, 4, 4,
                    4, 0, 4, 3, 1, 4, 1, 3, 2, 3, 0, 1, 0, 1, 1,
                    3, 3, 4, 2, 4, 4, 2, 0, 3, 4, 3, 0, 1, 0, 3,
                    0, 2, 3, 4, 4, 2, 4, 1, 0, 0, 0, 4, 2, 4, 0},

            {3, 1, 3, 1, 4, 4, 2, 2, 0, 4, 0, 2, 2, 3, 1,
                    1, 1, 2, 3, 3, 1, 0, 2, 2, 2, 0, 2, 4, 1, 1,
                    4, 4, 1, 2, 4, 2, 1, 4, 1, 2, 3, 3, 2, 1, 4,
                    1, 0, 2, 2, 3, 4, 1, 3, 2, 2, 1, 3, 4, 3, 2,
                    3, 1, 1, 0, 0, 1, 2, 0, 3, 2, 4, 3, 4, 3, 1,
                    1, 1, 3, 0, 4, 2, 1, 3, 0, 1, 2, 4, 4, 0, 3,
                    0, 1, 1, 1, 0, 1, 2, 3, 3, 1, 0, 1, 0, 0, 3,
                    2, 3, 2, 3, 1, 1, 1, 2, 4, 0, 2, 1, 2, 3, 3,
                    0, 1, 3, 0, 4, 3, 1, 1, 4, 0, 1, 3, 0, 3, 0,
                    1, 3, 3, 0, 3, 0, 0, 0, 3, 4, 1, 3, 0, 0, 0,
                    4, 4, 2, 1, 3, 1, 0, 1, 1, 3, 1, 3, 2, 4, 3,
                    0, 3, 0, 2, 3, 1, 1, 1, 3, 3, 1, 2, 3, 2, 2,
                    3, 2, 2, 0, 3, 0, 3, 1, 0, 0, 3, 3, 2, 4, 2,
                    0, 1, 2, 2, 0, 2, 4, 4, 1, 3, 4, 3, 1, 1, 4,
                    4, 4, 3, 0, 4, 3, 3, 3, 4, 1, 3, 4, 4, 3, 1},

            {1, 3, 4, 0, 2, 1, 4, 3, 0, 0, 1, 2, 3, 1, 1,
                    0, 0, 3, 0, 3, 2, 3, 0, 1, 4, 0, 3, 3, 3, 2,
                    2, 4, 1, 2, 0, 1, 2, 1, 0, 0, 3, 1, 0, 2, 2,
                    0, 2, 1, 2, 1, 1, 0, 0, 0, 3, 3, 0, 1, 1, 3,
                    1, 4, 2, 3, 1, 3, 3, 0, 4, 2, 3, 1, 0, 4, 4,
                    2, 1, 1, 4, 1, 1, 4, 0, 4, 4, 2, 0, 0, 4, 0,
                    3, 4, 4, 3, 0, 0, 2, 0, 4, 1, 2, 4, 0, 3, 3,
                    1, 4, 0, 4, 0, 0, 3, 3, 4, 4, 0, 2, 2, 4, 4,
                    0, 1, 0, 4, 2, 3, 3, 0, 0, 2, 0, 4, 3, 4, 1,
                    3, 1, 1, 4, 2, 4, 0, 0, 2, 0, 3, 1, 2, 4, 3,
                    0, 0, 4, 2, 4, 1, 2, 0, 0, 0, 3, 0, 3, 3, 3,
                    0, 0, 1, 0, 1, 2, 2, 0, 3, 4, 3, 2, 4, 3, 4,
                    1, 1, 0, 2, 0, 4, 3, 3, 1, 1, 4, 3, 2, 4, 1,
                    0, 1, 2, 2, 3, 4, 0, 3, 1, 4, 0, 0, 3, 1, 1,
                    0, 3, 0, 0, 1, 0, 1, 1, 1, 3, 1, 2, 0, 0, 0},

            {0, 1, 3, 3, 4, 3, 4, 3, 2, 4, 4, 0, 3, 2, 1,
                    4, 0, 1, 1, 0, 0, 0, 1, 2, 0, 3, 0, 0, 2, 1,
                    1, 2, 4, 3, 0, 2, 0, 2, 3, 4, 3, 1, 2, 2, 3,
                    3, 4, 3, 0, 1, 3, 3, 2, 3, 1, 1, 0, 3, 4, 2,
                    2, 0, 0, 3, 2, 0, 2, 3, 3, 3, 0, 1, 1, 1, 1,
                    2, 4, 2, 2, 1, 4, 3, 2, 1, 4, 0, 1, 4, 4, 1,
                    0, 0, 0, 2, 2, 3, 4, 3, 2, 3, 0, 3, 4, 3, 4,
                    1, 2, 0, 4, 1, 2, 2, 4, 0, 2, 4, 2, 4, 0, 3,
                    3, 4, 3, 3, 1, 1, 0, 4, 4, 2, 1, 0, 0, 1, 3,
                    1, 2, 2, 2, 4, 3, 2, 0, 2, 1, 0, 1, 0, 1, 3,
                    2, 3, 4, 2, 1, 0, 1, 2, 3, 2, 4, 0, 2, 4, 3,
                    1, 3, 2, 4, 3, 0, 4, 4, 1, 1, 4, 1, 2, 4, 0,
                    3, 0, 2, 2, 1, 4, 3, 4, 1, 2, 2, 1, 1, 3, 1,
                    2, 0, 2, 1, 0, 4, 1, 4, 0, 3, 2, 3, 0, 2, 4,
                    0, 3, 1, 1, 0, 1, 4, 1, 4, 1, 1, 1, 0, 4, 2},

            {4, 1, 2, 0, 2, 3, 4, 1, 4, 4, 1, 4, 3, 1, 3,
                    1, 3, 1, 3, 4, 0, 3, 4, 2, 3, 3, 2, 3, 4, 1,
                    1, 3, 2, 2, 3, 4, 2, 3, 4, 0, 3, 4, 1, 2, 3,
                    1, 3, 2, 4, 0, 2, 0, 0, 1, 2, 1, 3, 4, 4, 2,
                    4, 0, 2, 2, 0, 1, 1, 0, 0, 1, 0, 2, 3, 2, 4,
                    2, 2, 0, 3, 4, 1, 0, 4, 3, 4, 4, 2, 3, 3, 4,
                    4, 4, 0, 2, 0, 3, 4, 1, 1, 4, 4, 2, 0, 1, 1,
                    3, 1, 0, 4, 1, 1, 1, 3, 2, 4, 1, 3, 2, 0, 2,
                    0, 2, 0, 0, 1, 1, 2, 0, 4, 1, 1, 0, 2, 2, 4,
                    3, 1, 0, 4, 3, 4, 3, 1, 1, 0, 0, 3, 2, 3, 4,
                    4, 4, 1, 2, 4, 0, 4, 2, 0, 3, 2, 3, 4, 0, 0,
                    2, 4, 3, 0, 1, 3, 1, 3, 1, 0, 1, 0, 0, 1, 4,
                    1, 2, 1, 2, 0, 0, 3, 0, 1, 1, 0, 2, 3, 1, 2,
                    3, 2, 0, 1, 3, 0, 2, 4, 3, 4, 4, 4, 0, 3, 0,
                    2, 3, 3, 0, 2, 2, 4, 3, 0, 2, 1, 2, 3, 2, 0},

            {1, 2, 2, 4, 2, 3, 4, 2, 4, 1, 2, 2, 3, 3, 4,
                    3, 1, 1, 4, 1, 1, 1, 1, 1, 2, 1, 1, 4, 1, 0,
                    1, 4, 1, 4, 4, 2, 1, 4, 0, 3, 4, 0, 2, 3, 3,
                    3, 3, 1, 2, 0, 3, 3, 3, 2, 4, 0, 1, 2, 3, 0,
                    4, 3, 4, 1, 3, 0, 4, 4, 3, 4, 0, 4, 0, 0, 2,
                    2, 0, 3, 1, 2, 4, 4, 4, 0, 0, 2, 3, 0, 0, 3,
                    0, 4, 0, 3, 4, 2, 1, 1, 0, 3, 3, 3, 2, 2, 1,
                    0, 2, 0, 3, 1, 4, 0, 0, 1, 2, 0, 3, 4, 1, 2,
                    3, 2, 2, 2, 1, 1, 1, 4, 3, 2, 0, 2, 4, 2, 2,
                    4, 3, 3, 0, 3, 0, 0, 4, 0, 0, 2, 2, 3, 3, 1,
                    4, 2, 3, 4, 1, 2, 3, 1, 3, 0, 4, 4, 4, 0, 2,
                    0, 1, 3, 1, 2, 3, 2, 4, 3, 3, 1, 2, 4, 0, 1,
                    4, 1, 3, 3, 1, 0, 3, 2, 0, 1, 4, 0, 2, 0, 2,
                    4, 0, 2, 4, 1, 0, 0, 4, 2, 0, 0, 4, 4, 3, 0,
                    1, 1, 1, 3, 4, 2, 3, 2, 1, 2, 0, 1, 4, 1, 0},

            {4, 0, 1, 4, 3, 3, 1, 4, 1, 2, 4, 1, 0, 0, 2,
                    0, 1, 4, 0, 3, 0, 0, 2, 4, 2, 2, 3, 3, 2, 4,
                    0, 2, 1, 0, 3, 3, 3, 0, 0, 4, 4, 3, 1, 1, 4,
                    4, 4, 2, 1, 0, 2, 4, 3, 3, 2, 2, 4, 2, 4, 0,
                    3, 0, 0, 4, 4, 2, 2, 1, 3, 4, 3, 2, 4, 2, 0,
                    0, 4, 1, 4, 4, 4, 4, 4, 1, 2, 3, 4, 2, 3, 3,
                    0, 1, 2, 0, 0, 2, 2, 1, 3, 4, 2, 0, 0, 4, 1,
                    4, 3, 3, 2, 0, 0, 1, 0, 1, 4, 3, 2, 3, 1, 1,
                    3, 4, 2, 2, 0, 2, 3, 3, 3, 0, 0, 1, 2, 1, 3,
                    1, 3, 2, 1, 2, 2, 4, 1, 1, 1, 2, 3, 1, 3, 1,
                    0, 0, 2, 1, 2, 1, 1, 4, 1, 1, 0, 2, 1, 2, 0,
                    4, 1, 2, 1, 0, 3, 1, 0, 3, 4, 0, 4, 3, 3, 2,
                    4, 3, 0, 0, 3, 4, 3, 3, 3, 3, 1, 1, 3, 2, 1,
                    0, 1, 1, 3, 0, 1, 1, 0, 4, 0, 4, 0, 2, 0, 4,
                    2, 2, 1, 4, 4, 2, 2, 0, 3, 4, 3, 0, 2, 4, 3},

            {2, 2, 4, 0, 2, 4, 0, 0, 1, 4, 0, 3, 4, 3, 3,
                    0, 4, 3, 1, 0, 3, 2, 0, 1, 2, 2, 1, 4, 4, 0,
                    2, 1, 2, 3, 3, 2, 1, 2, 3, 3, 0, 4, 2, 1, 0,
                    4, 4, 3, 3, 2, 4, 1, 0, 1, 4, 4, 0, 4, 2, 1,
                    3, 3, 0, 1, 2, 2, 3, 1, 3, 0, 1, 3, 2, 3, 3,
                    1, 2, 0, 3, 4, 0, 4, 2, 2, 2, 1, 3, 3, 3, 1,
                    4, 0, 0, 1, 1, 1, 1, 4, 3, 3, 2, 1, 3, 2, 0,
                    4, 1, 4, 4, 1, 0, 0, 2, 0, 3, 2, 2, 0, 2, 3,
                    2, 3, 3, 1, 4, 3, 0, 1, 0, 4, 4, 0, 0, 2, 1,
                    0, 1, 2, 2, 4, 3, 1, 1, 4, 4, 2, 4, 4, 2, 4,
                    2, 4, 1, 1, 0, 3, 3, 3, 0, 4, 4, 0, 0, 2, 0,
                    3, 2, 1, 3, 0, 4, 4, 2, 3, 0, 2, 1, 1, 3, 1,
                    0, 4, 3, 3, 1, 2, 0, 2, 2, 1, 2, 3, 0, 0, 1,
                    4, 3, 4, 2, 1, 1, 3, 0, 4, 1, 4, 1, 4, 2, 0,
                    2, 1, 3, 2, 0, 1, 4, 0, 1, 4, 0, 4, 0, 4, 3},

            {0, 1, 2, 1, 3, 4, 3, 2, 1, 2, 1, 2, 2, 3, 4,
                    4, 0, 0, 1, 3, 0, 4, 2, 0, 4, 4, 4, 2, 1, 1,
                    3, 0, 0, 1, 2, 1, 1, 3, 0, 0, 3, 2, 4, 0, 0,
                    4, 2, 1, 4, 4, 1, 4, 0, 0, 3, 2, 0, 2, 2, 0,
                    3, 3, 4, 2, 1, 2, 4, 1, 3, 4, 0, 4, 2, 3, 0,
                    0, 4, 4, 1, 2, 2, 1, 4, 4, 2, 3, 3, 4, 4, 1,
                    3, 1, 1, 3, 2, 2, 0, 3, 2, 3, 4, 4, 3, 2, 0,
                    2, 4, 1, 3, 2, 0, 2, 4, 4, 4, 1, 4, 4, 0, 0,
                    1, 4, 2, 1, 2, 0, 3, 3, 0, 1, 3, 3, 2, 4, 3,
                    2, 2, 3, 2, 1, 1, 0, 0, 1, 1, 3, 1, 2, 4, 3,
                    2, 1, 0, 2, 2, 0, 3, 2, 2, 1, 4, 1, 1, 4, 0,
                    0, 1, 3, 2, 1, 0, 4, 0, 0, 3, 3, 0, 3, 0, 4,
                    1, 2, 1, 3, 4, 3, 1, 1, 3, 0, 0, 4, 3, 1, 4,
                    0, 3, 3, 3, 1, 1, 4, 0, 0, 4, 2, 4, 1, 0, 3,
                    3, 0, 3, 2, 1, 4, 0, 3, 3, 1, 2, 2, 0, 4, 2},

            {2, 0, 1, 4, 4, 3, 1, 4, 2, 0, 4, 0, 4, 0, 1,
                    3, 3, 0, 2, 1, 1, 1, 4, 2, 4, 3, 4, 2, 1, 0,
                    4, 1, 4, 4, 1, 2, 1, 1, 1, 2, 3, 1, 0, 3, 3,
                    4, 4, 2, 3, 3, 0, 2, 0, 3, 2, 1, 4, 4, 1, 4,
                    1, 4, 1, 3, 3, 3, 1, 0, 2, 2, 2, 2, 2, 3, 0,
                    0, 4, 2, 3, 0, 3, 1, 0, 1, 1, 3, 1, 3, 2, 1,
                    2, 0, 2, 4, 1, 1, 2, 1, 3, 1, 1, 1, 2, 2, 2,
                    1, 3, 3, 3, 1, 1, 0, 0, 3, 3, 0, 2, 1, 1, 1,
                    0, 0, 0, 4, 4, 1, 3, 2, 4, 1, 0, 0, 3, 3, 0,
                    4, 3, 2, 3, 1, 3, 3, 3, 4, 3, 1, 2, 2, 1, 1,
                    1, 1, 1, 1, 2, 0, 2, 1, 4, 1, 3, 1, 1, 1, 2,
                    0, 1, 2, 1, 0, 4, 0, 2, 3, 1, 0, 0, 0, 1, 0,
                    0, 1, 1, 4, 3, 3, 4, 4, 0, 0, 1, 0, 1, 2, 4,
                    3, 2, 2, 1, 1, 4, 0, 2, 1, 0, 1, 0, 4, 4, 4,
                    4, 0, 1, 1, 2, 2, 4, 3, 4, 1, 2, 4, 4, 3, 4},

            {0, 2, 2, 2, 4, 1, 2, 0, 4, 0, 2, 3, 0, 2, 2,
                    4, 4, 4, 2, 2, 2, 1, 2, 3, 2, 3, 0, 0, 2, 1,
                    3, 2, 0, 1, 2, 3, 2, 4, 3, 1, 0, 4, 2, 0, 2,
                    2, 1, 2, 0, 0, 2, 2, 3, 4, 3, 2, 2, 2, 1, 3,
                    0, 2, 0, 3, 2, 0, 2, 1, 2, 2, 2, 3, 3, 0, 2,
                    3, 1, 0, 4, 3, 0, 1, 1, 0, 3, 0, 0, 2, 3, 4,
                    0, 3, 4, 1, 3, 4, 3, 1, 1, 3, 3, 1, 2, 1, 3,
                    4, 2, 3, 1, 1, 0, 3, 3, 4, 4, 1, 1, 4, 4, 3,
                    3, 0, 4, 1, 1, 1, 3, 3, 1, 4, 1, 1, 4, 4, 2,
                    2, 1, 3, 0, 2, 2, 4, 2, 4, 2, 1, 1, 2, 2, 0,
                    0, 1, 3, 2, 4, 4, 0, 0, 0, 4, 2, 2, 4, 2, 2,
                    3, 0, 1, 2, 4, 1, 0, 3, 3, 1, 0, 4, 0, 2, 2,
                    4, 2, 1, 4, 2, 2, 2, 2, 0, 2, 1, 0, 4, 3, 0,
                    0, 4, 3, 2, 0, 2, 3, 2, 4, 2, 1, 1, 1, 3, 4,
                    4, 2, 4, 4, 0, 2, 0, 1, 3, 4, 2, 4, 2, 3, 1},

            {0, 2, 2, 4, 4, 3, 3, 3, 3, 0, 4, 3, 0, 2, 3,
                    4, 1, 4, 4, 4, 1, 3, 1, 4, 1, 0, 3, 0, 2, 1,
                    0, 4, 1, 3, 0, 3, 1, 3, 3, 2, 4, 0, 4, 3, 2,
                    1, 2, 3, 3, 4, 2, 1, 0, 2, 3, 3, 3, 2, 3, 4,
                    0, 4, 1, 4, 1, 1, 2, 3, 0, 2, 1, 3, 1, 0, 2,
                    3, 1, 3, 4, 1, 3, 3, 1, 4, 3, 3, 2, 4, 4, 0,
                    0, 2, 4, 4, 1, 0, 0, 3, 2, 3, 2, 3, 3, 3, 2,
                    0, 1, 4, 3, 3, 1, 2, 1, 3, 2, 3, 1, 2, 0, 2,
                    0, 2, 0, 2, 3, 1, 3, 4, 1, 1, 0, 2, 1, 4, 1,
                    0, 3, 4, 0, 0, 2, 3, 2, 4, 3, 3, 0, 0, 0, 3,
                    2, 4, 0, 2, 2, 0, 3, 1, 0, 2, 3, 2, 3, 2, 3,
                    4, 3, 1, 1, 4, 3, 1, 1, 3, 1, 3, 0, 4, 1, 3,
                    4, 2, 1, 1, 3, 3, 0, 3, 0, 4, 0, 3, 4, 3, 0,
                    1, 2, 4, 4, 2, 4, 3, 4, 1, 4, 3, 0, 0, 0, 2,
                    4, 3, 3, 2, 3, 3, 0, 0, 1, 1, 2, 3, 3, 4, 2},

            {4, 4, 1, 4, 1, 1, 2, 4, 0, 3, 3, 0, 1, 2, 0,
                    4, 1, 1, 4, 3, 1, 1, 2, 2, 3, 0, 2, 4, 0, 3,
                    3, 2, 3, 4, 0, 1, 4, 0, 2, 4, 4, 1, 2, 3, 0,
                    0, 4, 3, 4, 0, 2, 4, 0, 4, 4, 4, 1, 4, 3, 4,
                    4, 0, 1, 4, 2, 0, 1, 2, 4, 3, 0, 1, 4, 4, 3,
                    3, 1, 4, 2, 2, 1, 2, 1, 2, 2, 4, 2, 1, 2, 2,
                    2, 4, 2, 0, 4, 0, 3, 4, 3, 0, 2, 3, 3, 3, 0,
                    3, 4, 3, 4, 0, 0, 0, 1, 1, 4, 1, 2, 1, 3, 3,
                    4, 4, 4, 1, 2, 4, 2, 4, 0, 1, 4, 1, 4, 4, 3,
                    0, 4, 2, 2, 4, 0, 3, 1, 3, 2, 4, 1, 4, 4, 4,
                    0, 0, 2, 1, 4, 0, 2, 4, 3, 4, 0, 0, 4, 3, 0,
                    4, 2, 1, 0, 2, 2, 4, 2, 2, 2, 3, 3, 1, 0, 0,
                    4, 1, 3, 3, 4, 3, 1, 3, 2, 1, 1, 3, 1, 4, 2,
                    1, 1, 4, 0, 4, 3, 3, 2, 0, 2, 4, 3, 1, 4, 0,
                    3, 3, 1, 4, 1, 4, 0, 4, 0, 4, 3, 0, 4, 4, 0},

            {3, 0, 1, 3, 3, 0, 0, 1, 0, 0, 2, 4, 0, 0, 1,
                    1, 2, 2, 3, 2, 2, 0, 4, 0, 2, 3, 2, 2, 2, 1,
                    3, 1, 0, 0, 0, 0, 4, 4, 1, 3, 1, 3, 2, 0, 4,
                    0, 1, 0, 2, 0, 3, 4, 3, 2, 3, 0, 2, 0, 3, 4,
                    2, 3, 2, 2, 0, 3, 3, 0, 0, 3, 0, 3, 4, 1, 1,
                    0, 3, 3, 2, 0, 4, 1, 2, 4, 1, 2, 4, 4, 1, 0,
                    3, 2, 4, 0, 4, 1, 4, 3, 2, 1, 1, 4, 0, 0, 2,
                    1, 4, 1, 3, 0, 4, 0, 3, 2, 3, 2, 0, 0, 0, 1,
                    0, 0, 0, 1, 4, 2, 1, 0, 4, 4, 4, 3, 1, 0, 4,
                    3, 3, 3, 1, 0, 3, 1, 2, 0, 2, 4, 3, 4, 1, 1,
                    1, 1, 1, 3, 0, 2, 2, 3, 0, 4, 3, 4, 4, 1, 1,
                    0, 2, 0, 0, 2, 0, 0, 1, 3, 0, 2, 3, 0, 2, 4,
                    4, 3, 3, 2, 4, 0, 0, 0, 4, 3, 1, 0, 4, 1, 2,
                    2, 2, 3, 2, 0, 4, 2, 0, 0, 4, 1, 4, 4, 0, 1,
                    3, 4, 1, 4, 4, 0, 0, 0, 0, 1, 0, 2, 1, 0, 0}};
}
