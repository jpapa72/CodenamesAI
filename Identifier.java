import java.util.ArrayList;

public class Identifier {
	final static double SAME_COLOR = 2.0;
	final static double DIFF_COLOR = -3.0;
	final static double ASSASSIN = -8.0;
	final static double WHITE = -1.0;
	final static double RANKING_WEIGHT = 2.0;
	
	String name;
	double redScore;
	int forRed;
	double blueScore;
	int forBlue;
	ArrayList<Word> words;
	
	public Identifier(String name){
		this.name = name;
		redScore = 0;
		forRed = 0;
		blueScore = 0;
		forBlue = 0;
		words = new ArrayList<Word>();
	}
	
	
	public void calculateScore(){
		// Red
		redScore = 0;
		double[] red = {0, 0, 0, 0};
		forRed = 0;

		blueScore = 0;
		double[] blue = {0, 0, 0, 0};
		forBlue = 0;
		
		
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).chosen)
				continue;
			
			double factor = WHITE;
			int rating = words.get(i).search(name);
			double weightedRating = 0;
			if(words.get(i).color == Word.BLACK) {
				factor = ASSASSIN;
				red[rating] = -1;
			}
			else if(words.get(i).color == Word.WHITE) {
				red[rating] = -1;
			}
			else if(words.get(i).color == Word.RED) {
				factor = SAME_COLOR;
				if(red[rating] >= 0) red[rating]++;
			}
			else if(words.get(i).color == Word.BLUE) {
				factor = DIFF_COLOR;
				red[rating] = -1;
			}
			weightedRating = (rating + 1) * RANKING_WEIGHT;
			redScore += factor * weightedRating;
			
			factor = WHITE;
			if(words.get(i).color == Word.BLACK) {
				factor = ASSASSIN;
				blue[rating] = -1;
			}
			else if(words.get(i).color == Word.WHITE) {
				blue[rating] = -1;
			}
			else if(words.get(i).color == Word.BLUE) {
				factor = SAME_COLOR;
				if(blue[rating] >= 0) blue[rating]++;
			}
			else if(words.get(i).color == Word.RED) {
				factor = DIFF_COLOR;
				blue[rating] = -1;
			}
			blueScore += factor * weightedRating;
			
		}
		
		for(int i = 3; i >= 0; i--){
			if(red[i] > 0)
				forRed += red[i];
			else break;
		}
		for(int i = 3; i >= 0; i--){
			if(blue[i] > 0)
				forBlue += blue[i];
			else break;
		}
		
	}
	
	public String toString(){
		return name;
	}
}
