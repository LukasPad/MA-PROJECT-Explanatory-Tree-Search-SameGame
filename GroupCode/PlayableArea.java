package GroupCode;

import java.util.ArrayList;
import java.util.Collections;

public class PlayableArea extends BoardFeatures {

    ArrayList<Area> historyplay = new ArrayList<>();
    ArrayList<Area> historyempt = new ArrayList<>();

    public PlayableArea() {
        historyempt = new ArrayList<>();
        historyplay = new ArrayList<>();
    }

    public PlayableArea(byte[] searchSpace, int xDim, int yDim, int gameStep, int moveNum){
        findFeatures(searchSpace, xDim, yDim, gameStep, moveNum);
        findEmpty(searchSpace, xDim, yDim, gameStep, moveNum);
    }

    @Override
    public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int moveNum) {
        findEmpty(searchSpace, xDim, yDim, gameStep, moveNum);
        return findPlayableArea(searchSpace, xDim, yDim, gameStep, moveNum);
    }

    public ArrayList findPlayableArea(byte[] searchSpace, int xDim, int yDim, int gameStep, int moveNum) {
//        byte[] tempBoard = Arrays.copyOf(searchSpace, searchSpace.length);
        int empty = -1;
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
        Area playablearea = new Area(color, playableboard, playableboard.size(), height, width, xDim, yDim, gameStep);

        historyplay.add(playablearea);
        return new ArrayList<>(Collections.singleton(playablearea));
    }

    public ArrayList findEmpty(byte[] searchSpace, int xDim, int yDim, int gameStep, int moveNum) {
//        byte[] tempBoard = Arrays.copyOf(searchSpace, searchSpace.length);
        int empty = -1;
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
        Area emptyarea = new Area(numberempty, emptyboard, emptyboard.size(), height, width, xDim, yDim, gameStep);
        historyempt.add(emptyarea);
        return new ArrayList<>(Collections.singleton(emptyarea));
    }

    public ArrayList<Area> getFeatures(){ return this.historyplay;}
    public ArrayList<Area> getEmpty(){ return this.historyempt;}

    @Override
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
    public Area getCurPlay(int time){
        for (Area area : this.historyplay){
            if(area.time == time){
                return area;
            }
        }
        return null;
    }

    public Area getCurEmpty(int time){
        for (Area area : this.historyempt){
            if(area.time == time){
                return area;
            }
        }
        return null;
    }
    public Area getPlayArea(int time){
        return this.getCurPlay(time);
    }

    public Area getEmptyArea(int time){
        return this.getCurEmpty(time);
    }

    public String toJSON(){
        StringBuilder fullJSON = new StringBuilder();
        for (Area playarea : historyplay) {
            fullJSON.append(playarea.toJSON());
        }
        for(Area emptyarea:historyempt){
            fullJSON.append(emptyarea.toJSON());
        }
        return fullJSON.toString();
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
