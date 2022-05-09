import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.Math.min;

public class PlayableArea extends BoardFeatures {

    ArrayList<Area> playablearea = new ArrayList<>();
    ArrayList<Area> emptyarea = new ArrayList<>();

    public PlayableArea(byte[] searchSpace, int xDim, int yDim, int moveNum){
        findFeatures(searchSpace, xDim, yDim, moveNum);
        findEmpty(searchSpace, xDim, yDim, moveNum);
    }

    @Override
    public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int moveNum) {
//        byte[] tempBoard = Arrays.copyOf(searchSpace, searchSpace.length);
        int empty = 0;
        ArrayList<Integer> color = new ArrayList<>();
        ArrayList<Integer> playableboard = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            color.add(0);
        }
        for (int i = 0; i < searchSpace.length; i++) {
//        	System.out.println("current color " + tempBoard[i]);
            if (searchSpace[i] > empty) {
//                ArrayList<Integer> clusterShape = new ArrayList<>();
                playableboard.add(i);
                
                color.set(searchSpace[i], color.get(searchSpace[i]) + 1); 
//                System.out.println("current colors " + color);
            }
        }
        int mostWidth = -1;
        int leastWidth = xDim;
        int mostHeight = -1;
        int leastHeight = yDim;
        for(int cell : playableboard){
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
        playablearea.add(new Area(color, playableboard, playableboard.size(), height, width, xDim, yDim));
        return playablearea;
    }
    
    public ArrayList findEmpty(byte[] searchSpace, int xDim, int yDim, int moveNum) {
//        byte[] tempBoard = Arrays.copyOf(searchSpace, searchSpace.length);
        int empty = 0;
        ArrayList<Integer> emptyboard = new ArrayList<>();
        ArrayList<Integer> numberempty = new ArrayList<>();
        numberempty.add(0);
        for (int i = 0; i < searchSpace.length; i++) {
//        	System.out.println("current color " + tempBoard[i]);
            if (searchSpace[i] == empty) {
//                ArrayList<Integer> clusterShape = new ArrayList<>();
                emptyboard.add(i);
                numberempty.set(0, numberempty.get(0) + 1);
//                System.out.println("current colors " + color);
            }
        }
        int mostWidth = -1;
        int leastWidth = xDim;
        int mostHeight = -1;
        int leastHeight = yDim;
        for(int cell : emptyboard){
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
        emptyarea.add(new Area(numberempty, emptyboard, emptyboard.size(), height, width, xDim, yDim));
        return emptyarea;
    }

    public ArrayList<Area> getFeatures(){ return this.playablearea;}
    public ArrayList<Area> getEmpty(){ return this.emptyarea;}

    public Area getPlayArea(){
        Area playarea = playablearea.get(0);
        return playarea;
    }
   
    public Area getEmptyArea(){
        Area emarea = emptyarea.get(0);
        return emarea;
    }
    
    
//    public static void printBoard(byte[] board, int xDim, int yDim){
//        for (int j=0;j<yDim;j++)
//        {
//            for (int i=0;i<xDim;i++)
//                System.out.print(board[i+j*xDim]+",");
//            System.out.println();
//        }
//        System.out.println();
//    }

   
}
