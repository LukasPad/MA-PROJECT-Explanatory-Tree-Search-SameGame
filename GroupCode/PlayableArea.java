package GroupCode;

import java.util.ArrayList;

public class PlayableArea extends BoardFeatures {

    ArrayList<ArrayList<Area>> historyplay = new ArrayList();
    ArrayList<ArrayList<Area>> historyempt = new ArrayList();
    int gameID = 0;

    ArrayList<Area> playablearea = new ArrayList<>();
    ArrayList<Area> emptyarea = new ArrayList<>();

    public PlayableArea(byte[] searchSpace, int xDim, int yDim, int gameStep, int moveNum){
        findFeatures(searchSpace, xDim, yDim, gameStep, moveNum);
        findEmpty(searchSpace, xDim, yDim, gameStep, moveNum);
    }

    @Override
    public ArrayList findFeatures(byte[] searchSpace, int xDim, int yDim, int gameStep, int moveNum) {
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
        playablearea.add(new Area(color, playableboard, playableboard.size(), height, width, xDim, yDim, gameStep));

        historyplay.add(playablearea);
        return playablearea;
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
        emptyarea.add(new Area(numberempty, emptyboard, emptyboard.size(), height, width, xDim, yDim, gameStep));
        historyempt.add(emptyarea);
        return emptyarea;
    }

    public ArrayList<ArrayList<Area>> getFeatures(){ return this.historyplay;}
    public ArrayList<ArrayList<Area>> getEmpty(){ return this.historyempt;}

    @Override
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
    public ArrayList<Area> getCurPlay(int time){
        for (ArrayList<Area> area : this.historyplay){
            if(area.get(0).time == time){
                return area;
            }
        }
        return null;
    }

    public ArrayList<Area> getCurEmpty(int time){
        for (ArrayList<Area> area : this.historyempt){
            if(area.get(0).time == time){
                return area;
            }
        }
        return null;
    }
    public Area getPlayArea(int time){
        Area playarea = this.getCurPlay(time).get(0);

        return playarea;
    }

    public Area getEmptyArea(int time){
        Area emarea = this.getCurEmpty(time).get(0);
        return emarea;
    }

    public String toJSON(){
        String fullJSON = "";
        for(ArrayList<Area> plarea:this.historyplay) {
            for (Area playarea : plarea) {
                fullJSON += playarea.toJSON();
            }
        }
        for(ArrayList<Area> emarea:this.historyempt){
            for(Area emptyarea:emarea){
                fullJSON += emptyarea.toJSON();
            }
        }
        return fullJSON;
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
