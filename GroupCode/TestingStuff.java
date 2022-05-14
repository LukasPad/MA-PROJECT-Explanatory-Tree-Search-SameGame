package GroupCode;

import java.util.ArrayList;
import java.util.Random;

public class TestingStuff {
    public static void main(String[] args) {
        int xDim = 15;
        int yDim = 15;
        byte[] board = generateBoard(xDim, yDim);

        Clusters clusters = new Clusters(board, xDim, yDim, 0, 0);
//        ArrayList<ArrayList<Cluster>> foundClusters = clusters.getFeatures();
        Cluster biggestCluster = clusters.getBiggestCluster(0);
//        ArrayList<Cluster> singletons = clusters.getXtons(1, 0);
//        ArrayList<Cluster> doubletons = clusters.getXtons(2, 0);
//        ArrayList<Cluster> biggerClusters = clusters.getClustersExSingletons(0);

        Move move = new Move(board, xDim, yDim, 0, 0);

        printBoard(board, xDim, yDim);
        biggestCluster.print();
        System.out.println();
        move.print();
        System.out.println();
        System.out.println("Biggest Cluster JSON: "+biggestCluster.toJSON());
        System.out.println();
        
        Columns columns = new Columns(board, xDim, yDim,0, 0);
//        columns.printColumns(0);
        ArrayList<Column> columnList = columns.getColumns(0);
        System.out.println("Column JSON: "+columnList.get(0).toJSON());
//        System.out.println();
//        System.out.println("Shortest column:");
//        columns.getShortest(0).printColumn();
//        System.out.println("of size:"+columns.getShortestColumnHeight(0));
//        System.out.println();
//        System.out.println("number of colors in column 4");
//        System.out.println(columns.getColumns(0).get(3).getColorCount());
//        System.out.println(columns.getColumns(0).get(3).getColors());
//        System.out.println();
//        System.out.println("number of colors in column 9");
//        System.out.println(columns.getColumns(0).get(8).getColorCount());
//        System.out.println(columns.getColumns(0).get(8).getColors());

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
