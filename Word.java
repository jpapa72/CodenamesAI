import java.util.Arrays;

public class Word {
	
	// Card colors
	final static int RED = 0;
	final static int BLUE = 1;
	final static int BLACK = 2;	// Assassin
	final static int WHITE = 3;
	
	String word;				// Actual word on card
	String[][] identifiers;		// Words related to word on card. Rated 0-3, 0 being least related, 3 being most related
	int numIdentifiers;
	int color;
	boolean chosen;
	
	public Word(String word, String[][] identifiers){
		this.word = word;
		this.identifiers = identifiers;
		numIdentifiers = identifiers[0].length + identifiers[1].length + identifiers[2].length + identifiers[3].length;
		color = WHITE;	// Can be changed
		chosen = false;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public void addIdentifier(String identifier, int rating){
		if(rating > 3 | rating < 0)
			return;
		String[] newIdentifiers = new String[identifiers[rating].length + 1];
		System.arraycopy(identifiers[rating], 0, newIdentifiers, 0, identifiers[rating].length);
		newIdentifiers[identifiers[rating].length] = identifier.toLowerCase().trim();
		Arrays.sort(newIdentifiers);
		identifiers[rating] = newIdentifiers;
		numIdentifiers++;
	}
	
	// Returns rating of identifier, or -1 if identifier is not associated with word
	public int search(String identifier){
		String a = identifier.toLowerCase().trim();
		for(int i = 0; i < 4; i++) {
			int index = Arrays.binarySearch(identifiers[i], a);
			if(index < 0) continue;
			if(identifiers[i][index].equals(a)) return i;
		}
		return -1;	// not found
	}
	
	public String getWord(){
		return word;
	}
	
	public String[][] getIdentifiers(){
		return identifiers;
	}
	
	public int getColor(){
		return color;
	}
	
	public void choose(){
		chosen = true;
	}
	
	public void unchoose(){
		chosen = false;
	}
	
	public String printColor(){
		if(color == RED)
			return "RED";
		if(color == BLUE)
			return "BLUE";
		if(color == BLACK)
			return "BLACK";
		return "WHITE";
	}
	
	public char printColorChar(){
		if(color == RED)
			return 'R';
		if(color == BLUE)
			return 'B';
		if(color == BLACK)
			return 'A';
		return 'W';
	}
	
	public String toString(){
		if(chosen)
			return "[" + printColor() + "]";
		return word;
	}
	
}
