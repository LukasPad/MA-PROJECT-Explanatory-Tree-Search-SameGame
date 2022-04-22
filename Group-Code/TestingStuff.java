import java.util.ArrayList;
import java.util.Random;

public class TestingStuff {
    public static void main(String[] args) {
        int xDim = 15;
        int yDim = 15;
        byte[] board = generateBoard(xDim, yDim);

        Clusters clusters = new Clusters(board, xDim, yDim,0);
        ArrayList<Cluster> foundClusters = clusters.getFeatures();
        Cluster biggestCluster = clusters.getBiggestCluster();
        ArrayList<Cluster> singletons = clusters.getXtons(1);
        ArrayList<Cluster> doubletons = clusters.getXtons(2);
        ArrayList<Cluster> biggerClusters = clusters.getClustersExSingletons();

        printBoard(board, xDim, yDim);
        biggestCluster.print();
        System.out.println();
    }

    public static byte[] generateBoard(int xDim, int yDim){
        byte[] board = new byte[xDim*yDim];
        Random r = new Random();
        for(int i=0; i<xDim*yDim; i++){
            board[i] = (byte) r.nextInt(5);
        }
        return board;
    }

    public static void printBoard(byte[] board, int xDim, int yDim){
        for (int j=0;j<yDim;j++)
        {
            for (int i=0;i<xDim;i++)
                System.out.print(board[i+j*xDim]+",");
            System.out.println();
        }
        System.out.println();
    }
}
