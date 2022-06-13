package GroupCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class representing a single column, ordered from bottom to top
 */

public class Column extends Feature {

	int gameStep;
	byte[] column;
	int height;
	ArrayList<Integer> colors = new ArrayList<>();
	int colorCount = 0;
	int gameID;
	
	public Column(byte[] columnSpace, int gameStep, int gameID) {
		this.gameStep = gameStep;
		this.column = columnSpace;
		this.gameID = gameID;
		attributes();
	}
		
	public void attributes() {
		boolean empty = false;
		this.height = 0;
		if((int) column[0] == -1) {
			return;
		}
		for (int i = 0; i < column.length; i++) {
			if(!empty) {
				if(i > 0 && (int) column[i] == -1) {
					this.height = i;
					empty = true;
					break;
				}
				boolean matchingColor = false;
				for (int j = 0; j < colors.size(); j++) {
					if(colors.get(j) == (int) this.column[i]) {
						matchingColor = true;
						break;
					}
				}
				if (!matchingColor) {
					this.colorCount++;
					this.colors.add((int) this.column[i]);
				}
			}
		}
		if(this.height == 0) {this.height = column.length;}
		
	}

	public void printColumn() {
		System.out.print("{");
		for (int i = 0; i < column.length-1; i++) {
			System.out.print(( (int) this.column[i])+", ");
		}
		System.out.println((int) this.column[this.column.length - 1]+"}");
	}
	
	public int getHeight() {return this.height;}
	public int getColorCount() {return this.colorCount;}
	public ArrayList<Integer> getColors() {return this.colors;}

	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		try {
			json.put("shape", this.column);
			json.put("colors", this.colors);
			json.put("height", Integer.valueOf(this.height));
			json.put("numColors", Integer.valueOf(this.colorCount));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return json;
	}
}
