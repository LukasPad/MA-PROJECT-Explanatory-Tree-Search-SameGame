package GroupCode;

import OldCode.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FeatureCollector {
    ArrayList<BoardFeatures> gameFeatures = new ArrayList<>();
    ArrayList<int[]> nodeEdges = new ArrayList<>();
    ArrayList<String> gameScores = new ArrayList<>();
    int gameID;
    int id;


    static HashMap<String, BoardFeatures> possibleGameFeatures = new HashMap<>() {{
        put("Clusters", new Clusters());
        put("Moves", new Moves());
        put("Columns", new Columns());
        put("PlayableArea", new PlayableArea());
    }};

    public static void main(String[] args) {
        FeatureCollector featureCollector = new FeatureCollector();

        System.out.println("Starting Feature Collection");

        MCTSPlayerFC bot = new MCTSPlayerFC();
        bot.output = false;
        bot.maxNumberOfNodes = 500000;
        int millisecondsPerMove = 1000;

        System.out.println("Settings:");
        System.out.println("UCT Constant: " + bot.UCTConstant);
        System.out.println("Deviation Constant: " + bot.DeviationConstant);
        System.out.println("Number of visited nodes before expanding: " + MCTSPlayer.numberOfVisitsBeforeExpanding);
        System.out.println("Chance of playing chosen Color: " + Parameters.chanceOfPlayingChosenColor);
        System.out.println("Top Score weight: " + MCTSPlayer.topScoreWeight);
        System.out.println("Number of nodes: " + UCTNode.totalNodes);

        long startTime = System.currentTimeMillis();
        featureCollector.setGameID(0);
        int max_pos = 1;
        int counter = 0;
        for (byte[] position : positions) {
            if (counter == max_pos){break;}
            bot.playGame(position, 15, 15, BoardPanel.SAMEGAME, millisecondsPerMove, featureCollector);
            Runtime.getRuntime().gc();

            featureCollector.setGameID(featureCollector.getGameID() + 1);
            counter++;
        }
        featureCollector.exportJSON();
        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("Total time: " + (int) ((endTime - startTime) / 1000.0) + " seconds");
        System.exit(0);
    }

    public FeatureCollector() {
        this(possibleGameFeatures.keySet().toArray(new String[0]));
    }

    public FeatureCollector(int id) {
        this(possibleGameFeatures.keySet().toArray(new String[0]));
        this.id = id;
    }


    public FeatureCollector(String[] featureNames) {
        for (String featureName : featureNames) {
            if (possibleGameFeatures.containsKey(featureName)) {
                gameFeatures.add(possibleGameFeatures.get(featureName));
            } else {
                System.out.println("I: There is not feature class: " + featureName);
            }
        }
    }

    public ArrayList<BoardFeatures> findGameFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move, int gameID, int mctsScore, int nodeID) {
        for (BoardFeatures gameFeature : gameFeatures) {
            gameFeature.setGameID(gameID);
            gameFeature.findFeatures(searchSpace, xDim, yDim, gameStep, move, mctsScore, nodeID);
        }

        return gameFeatures;
    }

    public ArrayList<BoardFeatures> findGameFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int gameID, int mctsScore, int nodeID) {
        for (BoardFeatures gameFeature : gameFeatures) {
            if(!(gameFeature instanceof Moves))
            {
                gameFeature.setGameID(gameID);
                gameFeature.findFeatures(searchSpace, xDim, yDim, gameStep, 0, mctsScore, nodeID);
            }
        }

        return gameFeatures;
    }

    public void addEdge(int child, int parent){
        int[] edge = {child, parent};
        nodeEdges.add(edge);
    }

    public void exportJSON() {
        String json = "";
        for (BoardFeatures gameFeature : gameFeatures) {
            json = gameFeature.toJSON();

            String className = gameFeature.getClass().getName().split("\\.")[1];


            try (FileWriter fileWriter = new FileWriter("data/" + className + ".json")) {
                fileWriter.write(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JSONObject jsonScores = new JSONObject();
        try {
            jsonScores.put("Scores", gameScores);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter("data/gameScores.json")) {
            fileWriter.write(jsonScores.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportTreeJSON(int num) {
        String json = "";
        for (BoardFeatures gameFeature : gameFeatures) {
            json = gameFeature.toJSON();

            String className = gameFeature.getClass().getName().split("\\.")[1];


            try (FileWriter fileWriter = new FileWriter("data/tree" + className + "" + num + ".json")) {
                fileWriter.write(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        JSONObject edges = new JSONObject();
        try {
            edges.put("edges", nodeEdges);
            edges.put("gameID", gameID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        json = edges.toString();
        try (FileWriter fileWriter = new FileWriter("data/edges"+num+".json")) {
            fileWriter.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonScores = new JSONObject();
        try {
            jsonScores.put("Scores", gameScores);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter("data/gameScores.json")) {
            fileWriter.write(jsonScores.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveCurrentGameScore(int gameScore, byte[] position, int gameID, int gameStep) {
        JSONObject json = new JSONObject();
        try {
            json.put("gameScore", gameScore);
            json.put("position", position);
            json.put("gameID", gameID);
            json.put("gameStep", gameStep);
            json.put("gameID", gameID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        gameScores.add(json.toString());
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void clearHistory(){
        gameFeatures.clear();
        for (String featureName : possibleGameFeatures.keySet().toArray(new String[0])) {
            if (possibleGameFeatures.containsKey(featureName)) {
                gameFeatures.add(possibleGameFeatures.get(featureName));
            } else {
                System.out.println("I: There is not feature class: " + featureName);
            }
        }
        nodeEdges.clear();
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
