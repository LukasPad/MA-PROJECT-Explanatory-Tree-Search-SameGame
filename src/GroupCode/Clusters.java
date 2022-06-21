package GroupCode;

import java.util.ArrayList;
import java.util.Arrays;

public class Clusters extends BoardFeatures {

    ArrayList<ArrayList<Cluster>> history = new ArrayList();
    int gameID = 0;
    byte[] tempBoard;
    int minClusterSize = 2;

    public Clusters(){}

    public Clusters(byte[] searchSpace, int xDim, int yDim, int time, int move, int mctsScore){
        findFeatures(searchSpace, xDim, yDim, time, move, mctsScore, 0);
    }

    @Override
    public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move, int mctsScore, int nodeID) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        tempBoard = Arrays.copyOf(searchSpace, searchSpace.length);
        for (int i = 0; i < searchSpace.length; i++) {
            if (tempBoard[i] != -1) {
                ArrayList<Integer> clusterShape = new ArrayList<>();
                clusterShape.addAll(findCluster(xDim, yDim, i, tempBoard[i]));
                int mostWidth = -1;
                int leastWidth = xDim;
                int mostHeight = -1;
                int leastHeight = yDim;
                for(int cell : clusterShape){
                    if (cell%xDim > mostWidth){
                        mostWidth = cell%xDim;
                    }
                    if (cell%xDim < leastWidth){
                        leastWidth = cell%xDim;
                    }
                    if (Math.floor(cell/xDim) > mostHeight){
                        mostHeight = (int) Math.floor(cell/xDim);
                    }
                    if (Math.floor(cell/xDim) < leastHeight){
                        leastHeight = (int) Math.floor(cell/xDim);
                    }
                }
                int width = mostWidth - leastWidth + 1;
                int height = mostHeight - leastHeight + 1;
                byte color = searchSpace[i];
                if(clusterShape.size() >= minClusterSize){
                    clusters.add(new Cluster(color, clusterShape, clusterShape.size(), height, width, xDim, yDim, gameStep, gameID, nodeID));
                }
            }
        }
        history.add(clusters);
        return clusters;
    }

    private ArrayList<Integer> findCluster(int xDim, int yDim, int location, byte value) {
        ArrayList<Integer> shape = new ArrayList<>();
        if (tempBoard[location] == value) {
            shape.add(location);
            tempBoard[location] = (byte) -1;
            if ((location - xDim) >= 0) { // Check for tile above
                ArrayList<Integer> subShape = findCluster(xDim, yDim, location - xDim, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }
            if (((location - 1) % xDim != xDim - 1) && ((location - 1) >= 0)) { // Check for tile on the left
                ArrayList<Integer> subShape = findCluster(xDim, yDim, location - 1, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }
            if (((location + 1) % xDim != 0) && ((location + 1) < (xDim * yDim))) { // Check for tile on the right
                ArrayList<Integer> subShape = findCluster(xDim, yDim, location + 1, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }
            if ((location + xDim) < (xDim * yDim)) { // Check for tile below
                ArrayList<Integer> subShape = findCluster(xDim, yDim, location + xDim, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }

            return shape;
        } else {
            return null;
        }
    }

    public ArrayList<ArrayList<Cluster>> getFeatures(){ return this.history;}

    @Override
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public ArrayList<Cluster> getClusters(int nodeID){
        for (ArrayList<Cluster> cluster : this.history){
            if(cluster.size()>1){
                if(cluster.get(0).nodeID == nodeID){
                    return cluster;
                }
            }
        }
        return new ArrayList<Cluster>();
    }

    public ArrayList<Cluster> getClustersExSingletons(int time){
        ArrayList<Cluster> clusterList = this.getClusters(time);
        if(clusterList != null){
            ArrayList<Cluster> found = new ArrayList<>();
            for(Cluster cluster : clusterList){
                if (cluster.numCells != 1){
                    found.add(cluster);
                }
            }
            return found;
        }
        return null;
    }

    public ArrayList<Cluster> getXtons(int i, int time){
        ArrayList<Cluster> clusterList = this.getClusters(time);
        ArrayList<Cluster> singletons = new ArrayList<>();
        for(Cluster cluster : clusterList){
            if (cluster.numCells == i){
                singletons.add(cluster);
            }
        }
        return singletons;
    }

    public Cluster getBiggestCluster(int nodeID){
        ArrayList<Cluster> clusterList = this.getClusters(nodeID);

        Cluster biggest = clusterList.get(0);
        for(Cluster cluster : clusterList){
            if (cluster.numCells > biggest.numCells){
                biggest = cluster;
            }
        }
        return biggest;
    }

    public int getClusterCount(int nodeID, int clusterSize, boolean includeGreaterThan){
        int res = 0;
        for (Cluster cluster : this.getClusters(nodeID)) {
            if (includeGreaterThan) {
                if (cluster.numCells >= clusterSize) res++;
            } else {
                if (cluster.numCells == clusterSize) res++;
            }
        }

        return res;
    }

    public float getAvgClusterSize(int nodeID){
        float res = 0;
        float numClusters = 0;
        for (Cluster cluster : getClusters(nodeID)) {
            numClusters++;
            res += cluster.numCells;
        }

        return res / numClusters;
    }

    public void generateIDs(){
        int counter = 0;
        for(Cluster cluster:history.get(0)){
            cluster.setID(counter);
            counter++;
        }
    }

    public String toJSON(){
        StringBuilder fullJSON = new StringBuilder();
        for(ArrayList<Cluster> clusters:this.history){
            for(Cluster cluster:clusters){
                fullJSON.append(cluster.toJSON());
            }
        }
        return fullJSON.toString();
    }

}
