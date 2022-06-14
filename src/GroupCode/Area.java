package GroupCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Area extends Feature{

    int time;
    int xDim;
    int yDim;
    ArrayList<Integer> shape;
    int numCells;
    int height;
    int width;
    double[] middleLocation = new double[2];
    ArrayList<Integer> areacolors;
    int nodeID;
   
    public Area(ArrayList<Integer> areacolors, ArrayList<Integer> shape, int numCells, int height, int width, int xDim, int yDim, int time, int nodeID) {
        this.areacolors = areacolors;
        this.shape = shape;
        this.numCells = numCells;
        this.height = height;
        this.width = width;
        this.xDim = xDim;
        this.yDim = yDim;
        this.time = time;
        this.nodeID = nodeID;
        findMiddle();
    }

    private void findMiddle(){
        double leastWidth = xDim;
        double leastHeight = yDim;
        for (int cell : shape) {
            if (cell % xDim < leastWidth) {
                leastWidth = cell % xDim;
            }
            if (Math.floor(cell / xDim) < leastHeight) {
                leastHeight = Math.floor(cell / xDim);
            }
            double tempWidth = width;
            double tempHeight = height;
            this.middleLocation[0] = leastWidth + tempWidth / 2;
            this.middleLocation[1] = leastHeight + tempHeight / 2;
        }
    }

    public void print(){
        System.out.println("Type: Area, Color: "+areacolors+", Size: "+numCells+", Width: "+width+", Height: "+height+", Middle Point: ("+middleLocation[0]+","+middleLocation[1]+")");
        for (int j=0;j<yDim;j++){
            for (int i=0;i<xDim;i++){
                String space = " ";
                for (int cell : shape){
                    if (cell == i + j*xDim){
                        space = "X";
                        break;
                    }
                }
                System.out.print(space+",");

            }
            System.out.println();
        }
        System.out.println();
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("colors", this.areacolors);
            json.put("numCells", Integer.valueOf(this.numCells));
            json.put("shape", this.shape);
            json.put("middleLocation", this.middleLocation);
            json.put("height", Integer.valueOf(this.height));
            json.put("width", Integer.valueOf(this.width));
            json.put("nodeID", nodeID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

}
