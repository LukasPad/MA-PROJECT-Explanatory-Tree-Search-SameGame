package GroupCode;

import java.util.ArrayList;
import java.util.Random;

public class TestingArea {
    public static void main(String[] args) {
    	int colors = 5;
        int xDim = 15;
        int yDim = 15;
        byte[] board = generateBoard(xDim, yDim, colors);

        Clusters clusters = new Clusters(board, xDim, yDim,0);
        ArrayList<Cluster> foundClusters = clusters.getFeatures();
        Cluster biggestCluster = clusters.getBiggestCluster();

        PlayableArea playablearea = new PlayableArea(board, xDim, yDim, 0);
        Area playarea = playablearea.getPlayArea();
        Area emptyarea = playablearea.getEmptyArea();

        Move move = new Move(board, xDim, yDim, 0);

        printBoard(board, xDim, yDim);
        playarea.print();
        System.out.println();
        emptyarea.print();
        System.out.println();
//        System.out.println(foundArea);
//        biggestCluster.print();
    }

    public static byte[] generateBoard(int xDim, int yDim, int colors){
        byte[] board = new byte[xDim*yDim];
        Random r = new Random();
        for(int i=0; i<xDim*yDim; i++){
            board[i] = (byte) r.nextInt(colors);
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
