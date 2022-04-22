import java.util.ArrayList;
import java.util.Arrays;

public class Clusters extends BoardFeatures {

    ArrayList<Cluster> clusters = new ArrayList<>();

    public Clusters(byte[] searchSpace, int xDim, int yDim, int moveNum){
        findFeatures(searchSpace, xDim, yDim, moveNum);
    }

    @Override
    public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int moveNum) {
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
                clusters.add(new Cluster(color, clusterShape, clusterShape.size(), height, width, xDim, yDim));
            }
        }
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

    public ArrayList<Cluster> getFeatures(){ return this.clusters;}
    public ArrayList<Cluster> getClusters(){ return this.clusters;}

    public ArrayList<Cluster> getClustersExSingletons(){
        ArrayList<Cluster> found = new ArrayList<>();
        for(Cluster cluster : clusters){
            if (cluster.numCells != 1){
                found.add(cluster);
            }
        }
        return found;
    }

    public ArrayList<Cluster> getXtons(int i){
        ArrayList<Cluster> singletons = new ArrayList<>();
        for(Cluster cluster : clusters){
            if (cluster.numCells == i){
                singletons.add(cluster);
            }
        }
        return singletons;
    }

    public Cluster getBiggestCluster(){
        Cluster biggest = clusters.get(0);
        for(Cluster cluster : clusters){
            if (cluster.numCells > biggest.numCells){
                biggest = cluster;
            }
        }
        return biggest;
    }
}
