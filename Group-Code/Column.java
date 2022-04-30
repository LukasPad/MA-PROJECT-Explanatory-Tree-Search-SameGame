import java.util.ArrayList;

/**
 * Class representing a single column, ordered from bottom to top
 */

public class Column extends Feature {

	byte[] column;
	int height;
	ArrayList<Integer> colors = new ArrayList<>();
	int colorCount = 0;
	
	public Column(byte[] columnSpace) {
		this.column = columnSpace;
		attributes();
	}
		
	public void attributes() {
		boolean empty = false;
		if((int) column[0] == 0) {
			return;
		}
		for (int i = 0; i < column.length; i++) {
			if(!empty) {
				if(i > 0 && (int) column[i] == 0) {
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
}
