package GroupCode;

import java.util.ArrayList;
import java.util.Arrays;

public class Clusters extends BoardFeatures {

    ArrayList<ArrayList<Cluster>> history = new ArrayList();
    int gameID = 0;

    public Clusters(){}

    public Clusters(byte[] searchSpace, int xDim, int yDim, int time, int move){
        findFeatures(searchSpace, xDim, yDim, time, move);
    }

    @Override
    public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int move) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        byte[] tempBoard = Arrays.copyOf(searchSpace, searchSpace.length);
        for (int i = 0; i < searchSpace.length; i++) {
            if (tempBoard[i] != -1) {
                ArrayList<Integer> clusterShape = new ArrayList<>();
                clusterShape.addAll(findCluster(tempBoard, xDim, yDim, i, tempBoard[i]));
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
                clusters.add(new Cluster(color, clusterShape, clusterShape.size(), height, width, xDim, yDim, gameStep, gameID));
            }
        }
        history.add(clusters);
        return clusters;
    }

    private ArrayList<Integer> findCluster(byte[] board, int xDim, int yDim, int location, byte value) {
        ArrayList<Integer> shape = new ArrayList<>();
        if (board[location] == value) {
            shape.add(location);
            board[location] = (byte) -1;
            if ((location - xDim) >= 0) { // Check for tile above
                ArrayList<Integer> subShape = findCluster(board, xDim, yDim, location - xDim, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }
            if (((location - 1) % xDim != xDim - 1) && ((location - 1) >= 0)) { // Check for tile on the left
                ArrayList<Integer> subShape = findCluster(board, xDim, yDim, location - 1, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }
            if (((location + 1) % xDim != 0) && ((location + 1) < (xDim * yDim))) { // Check for tile on the right
                ArrayList<Integer> subShape = findCluster(board, xDim, yDim, location + 1, value);
                if (subShape != null) {
                    shape.addAll(subShape);
                }
            }
            if ((location + xDim) < (xDim * yDim)) { // Check for tile below
                ArrayList<Integer> subShape = findCluster(board, xDim, yDim, location + xDim, value);
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

    public ArrayList<Cluster> getClusters(int time){
        for (ArrayList<Cluster> cluster : this.history){
            if(cluster.get(0).time == time){
                return cluster;
            }
        }
        return null;
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

    public Cluster getBiggestCluster(int time){
        ArrayList<Cluster> clusterList = this.getClusters(time);

        Cluster biggest = clusterList.get(0);
        for(Cluster cluster : clusterList){
            if (cluster.numCells > biggest.numCells){
                biggest = cluster;
            }
        }
        return biggest;
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
