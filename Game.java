import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game {
	
	static Word[] words;
	static ArrayList<Identifier> ranking;
	static boolean devmode;
	
	public static void main (String[] args) throws IOException{
		devmode = false;
		getWords();
		String response = "";
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.print("(L)earn or (P)lay? (L/P): ");
			response = in.nextLine().toUpperCase().trim();
			if(response.length() == 0) continue;
			if(response.charAt(0) == 'D') {
				System.out.println("dev mode activated.");
				devmode = true;
				continue;
			}
			if(response.charAt(0) == 'L'){
				learnMode();
				continue;
			}
			if(response.charAt(0) == 'P'){
				playMode();
				continue;
			}
			if(response.equals("q")){
				break;
			}
		}
		in.close();
	}
	
	static void playMode() {
		Scanner in = new Scanner(System.in);
		Random rand = new Random();
		
		// Fisher-Yates Shuffle
	    for (int i = 0; i < words.length; i++) {
	        int random = i + (int) (Math.random() * (words.length - i));
	        Word randomElement = words[random];
	        words[random] = words[i];
	        words[i] = randomElement;
	    }
	    
	    // Assign colors
	    int goesFirst = rand.nextInt(2);
	    
	    // Place assassin
	    words[rand.nextInt(words.length)].color = Word.BLACK;
	    
	    // Place extra color on team that goes first;
	    int a = rand.nextInt(words.length);
    	while(words[a].color != Word.WHITE){
    		a = (a + 1) % words.length;
    	}
	    words[a].color = goesFirst;
		
	    // Place 8 reds and 8 blues
		for(int color = 0; color < 2; color++){
		    for(int i = 0; i < 8; i++){
		    	int j = rand.nextInt(words.length);
		    	while(words[j].color != Word.WHITE){
		    		j = (j + 1) % words.length;
		    	}
		    	words[j].color = color;
		    }
		}
		
		setUpIdentifiers();
		Identifier clue;
		int turn = goesFirst;
		
		String response = "";
		
		while(true) {
			int guessCount = 0;
			calculateScores();
			print(devmode);
			clue = bestClue(turn);
			
			// Check to see if game is over
			int redWordsLeft = 0;
			int blueWordsLeft = 0;
			for(int i = 0; i < words.length; i++){
				if(words[i].chosen) continue;
				if(words[i].color == Word.RED)
					redWordsLeft++;
				else if(words[i].color == Word.BLUE)
					blueWordsLeft++;
			}
			
			if(redWordsLeft == 0) {
				System.out.println("Red Team Wins!");
				System.exit(0);
			} else if(blueWordsLeft == 0) {
				System.out.println("Blue Team Wins!");
				System.exit(0);
			}
			
			while(true) {
				if(turn == 0) {
					System.out.print("Red's Clue: " + clue + " for " + clue.forRed + ": ");
				}
				else {
					System.out.print("Blue's Clue: " + clue + " for " + clue.forBlue + ": ");
				}
				response = in.nextLine().toLowerCase().trim();
				if(response.length() == 0) {
					System.out.println("Pass.");
					break;
				}
				if(response.contains(" ")){
					System.out.println("One word at a time.");
					continue;
				}
				int i;
				for(i = 0; i < words.length; i++) {
					if(response.equals(words[i].word.toLowerCase())) {
						break;
					}
				}
				if(i == 25) {
					System.out.println("Word not found.");
					continue;
				}
				if(!response.equals(words[i].word.toLowerCase())) {
					System.out.println("Word not found.");
					continue;
				}
				if(words[i].chosen) {
					System.out.println("Word already chosen.");
					continue;
				}
				// Word Accepted
				guessCount++;
				words[i].choose();
				if(words[i].color == Word.BLACK) {
					// Assassin
					System.out.println(words[i].word + " is the assassin. Game over");
					System.exit(0);
				} else if(words[i].color == turn) {
					if((turn == 0 && guessCount > clue.forRed) || (turn == 1 && guessCount > clue.forBlue)) {
						System.out.println(words[i].word + " is correct. Out of turns!");
						break;
					} else {
						print(devmode);
						System.out.println(words[i].word + " is correct. Keep Guessing or Pass!");
						continue;
					}
				} else {
					System.out.println(words[i].word + " is incorrect. Turn is over.");
					break;
				}
				
			}
			turn = (turn + 1) % 2;
		}
		
	    
	}
	
	
	static void setUpIdentifiers(){
		ranking = new ArrayList<Identifier>();
		ArrayList<String> inRanking = new ArrayList<String>();
		
		// Every Identifier of every Word
		for(int i = 0; i < words.length; i++) {
			for(int j = 0; j < 4; j++){
				for(int k = 0; k < words[i].identifiers[j].length; k++) {
					// Update ranking or create new Identifier in Ranking
					String nameOfIdentifier = words[i].identifiers[j][k];
					int index = inRanking.indexOf(nameOfIdentifier);
					if(index >= 0) {
						// Already exists, update ranking
						if(!ranking.get(index).name.equals(nameOfIdentifier)) {
							System.out.println("Index_Error");
							System.exit(1);
						} else {
							ranking.get(index).words.add(words[i]);
						}
					} else {
						// Add identifier to list
						Identifier identifierToAdd = new Identifier(nameOfIdentifier);
						identifierToAdd.words.add(words[i]);
						ranking.add(identifierToAdd);
						inRanking.add(nameOfIdentifier);
					}
				}
			}
		}
	}
	
	static void calculateScores(){
		for(int i = 0; i < ranking.size(); i++) {
			ranking.get(i).calculateScore();
		}
	}
	
	static Identifier bestClue(int color){
		if(color != 0 && color != 1 || ranking.size() == 0) {
			System.out.println("error.");
			System.exit(1);
		}
		Identifier highest = ranking.get(0);
		int numWords;
		double score;
		if(color == 0) {
			score = highest.redScore;
			numWords = highest.forRed;
		} else {
			score = highest.blueScore;
			numWords = highest.forBlue;
		}
		for(int i = 1; i < ranking.size(); i++){
			if(color == 0){
				if(ranking.get(i).forRed > numWords || (ranking.get(i).forRed == numWords && ranking.get(i).redScore > score)){
					highest = ranking.get(i);
					score = highest.redScore;
					numWords = highest.forRed;
				}
			} else {
				if(ranking.get(i).forBlue > numWords || (ranking.get(i).forBlue == numWords && ranking.get(i).blueScore > score)){
					highest = ranking.get(i);
					score = highest.blueScore;
					numWords = highest.forBlue;
				}
			}
		}
		return highest;
	}
	
	static void printRanking(){
		for(int i = 0; i < ranking.size(); i++){
			Identifier A = ranking.get(i);
			System.out.print(A.name + ": ");
			for(int j = 0; j < A.words.size(); j++) {
				System.out.print(A.words.get(j).word + " ");
			}
			System.out.println();
		}
	}
	
	
	static void print(boolean print_color) {
		System.out.print("|\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t|\n");
		for(int i = 0; i < 5; i++) {
			System.out.print("|\t");
			for(int j = 0; j < 5; j++){
				if(print_color) {
					System.out.print(words[j*5 + i] + " [" + words[j*5 + i].printColorChar() +"]" + "\t");
					if(words[j*5 + i].getWord().length() + 4 < 8)
						System.out.print("\t");
				} else {
					System.out.print(words[j*5 + i] + "\t");
					if(words[j*5 + i].getWord().length() < 8 || words[j*5 + i].chosen)
						System.out.print("\t");
				}
				if(j < 4)
					System.out.print("| |\t");
			}
			System.out.print("|\n");
			System.out.print("|\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t|\n");
		}
	}
	
	// Don't print words that are already associated with identifier
	static void print(String identifier) {
		System.out.print("|\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t|\n");
		for(int i = 0; i < 5; i++) {
			System.out.print("|\t");
			for(int j = 0; j < 5; j++){
				if(words[j*5 + i].search(identifier) < 0) {
					System.out.print(words[j*5 + i] + "\t");
					if(words[j*5 + i].getWord().length() < 8)
						System.out.print("\t");
				} else {
					System.out.print("\t\t");
				}
				if(j < 4)
					System.out.print("| |\t");
			}
			System.out.print("|\n");
			System.out.print("|\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t|\n");
		}
	}
	
	static void learnMode() throws IOException {
		System.out.println("Welcome to Learn Mode. Make sure your clues follow the rules.");
		String response = "";
		Scanner in = new Scanner(System.in);
		Random random = new Random();
		
		do {
			int numWords = random.nextInt(6) + 1;
			
			// get numWords non-repeating random ints
			int[] rand = {-1, -1, -1, -1, -1, -1};
			for(int i = 0; i < numWords; i++){
				int a;
				while(true){
					a = random.nextInt(words.length);
					if(a != rand[0] && a != rand[1] && a != rand[2] && a != rand[3] && a!= rand[4])
						break;
				}
				rand[i] = a;
			}
			
			System.out.println("What one-word clue describes the following word(s)?\n");
			for(int i = 0; i < numWords; i++){
				System.out.print(words[rand[i]].getWord() + " ");
			}
			System.out.println();
			
			String identifier;
			
			while(true) {
				response = in.nextLine().toLowerCase();
				if(response.contains(" ")) {
					System.out.println("Clues can only be one word. Please choose another.");
					continue;
				}
				// TODO: more checking to make sure rules are followed
				// Word accepted.
				identifier = response;
				break;
			}
			
			
			if(identifier.length() == 0){
				System.out.println("Skipped.");
			} else {
				System.out.println("\nRank each word on a scale of"
					+ " 1 (the clue only identifies this word in the context of the other word(s)) to 4 (the clue strongly identifies this word)\n"
					+ "Rank a word 0 if the clue does not identify the word at all.\n");

				for(int i = 0; i < numWords; i++){
					System.out.print(words[rand[i]].getWord() + " (0-4): ");
					response = in.nextLine().toLowerCase();
					int rank;
					try{
						rank = Integer.parseInt(response);
					} catch (NumberFormatException e) {
						rank = -1;
					}
					while(rank < 0 || rank > 4) {
						System.out.print("Only accepts integers 0-4: ");
						response = in.nextLine().toLowerCase().trim();
						try{
							rank = Integer.parseInt(response);
						} catch (NumberFormatException e) {
							rank = -1;
						}
					}
					int prevRank = words[rand[i]].search(identifier);
					if(prevRank >= 0) {
						System.out.println("This clue was previously ranked " + (prevRank + 1) + ".");
					} else if(rank > 0){
						words[rand[i]].addIdentifier(identifier, rank - 1);
					}
				}
				print(identifier);
				System.out.println("Type each word (seperated by spaces) that is also identified by '" + identifier + "' or press enter:\n");
				response = in.nextLine().toLowerCase().trim();
				if(response.length() != 0){
					response += " ";
					String[] otherWords = response.split(" ");
					for(int i = 0; i < otherWords.length; i++) {
						for(int j = 0; j < words.length; j++) {
							if((otherWords[i].toLowerCase()).equals(words[j].getWord().toLowerCase())) {
								System.out.print("Rank " + words[j] + " (0-4): ");
								response = in.nextLine();
								int rank;
								try{
									rank = Integer.parseInt(response);
								} catch (NumberFormatException e) {
									rank = -1;
								}
								while(rank < 0 || rank > 4) {
									System.out.print("Only accepts integers 0-4: ");
									response = in.nextLine().toLowerCase().trim();
									try{
										rank = Integer.parseInt(response);
									} catch (NumberFormatException e) {
										rank = -1;
									}
								}
								int prevRank = words[j].search(identifier);
								if(prevRank >= 0) {
									System.out.println("This clue was previously ranked " + (prevRank + 1) + ".");
								} else if(rank > 0){
									words[j].addIdentifier(identifier, rank - 1);
								}
								
							}
						}
					}
				}
			}
			System.out.println("type 'q' to quit, or press enter to continue");
			response = in.nextLine();
		} while (!response.equals("q"));
		
		in.close();
		saveWords();
		System.out.println("Saved.");
		System.exit(0);
	}
	
	static void getWords() throws IOException{
		
		// Set up input file
		File f = new File("codenameWords.txt");
	    FileReader r = new FileReader(f);
	    BufferedReader b = new BufferedReader(r);
	    
	    // Get number of words and initialize word array
	    String line = b.readLine();
	    int numWords = Integer.parseInt(line);
	    words = new Word[numWords];
	    
	    
	    int i = 0;
	    // For each word, get word and identifiers
	    while( (line=b.readLine()) != null) {
	    	// Skip empty spaces
	    	if(line.trim().length() == 0){
	        	continue;
	    	}
	    	
	    	// Get name. First line.
	        String word = line.trim();
	        String[][] identifiers = new String[4][];
	        
	        // Get identifiers. 4 lines.
	        for(int j = 0; j < 4; j++){
	        	line=b.readLine().trim();
	        	if(line.trim().length() == 0)
	        		identifiers[j] = new String[0];
	        	else
	        		identifiers[j] = line.split(" ");
	        	Arrays.sort(identifiers[j]);
	        }
	        
	        words[i] = new Word(word, identifiers);
	    	
	    	i++;
	    	
	    }
	    b.close();
	
	}
	
	static void saveWords() throws IOException{
	    File file = new File("codenameWords.txt");
	    FileWriter f= new FileWriter(file, false);
	    BufferedWriter b = new BufferedWriter(f);
	    b.write(words.length + "\n\n");
	    
	    // New Word
	    for(int i = 0; i < words.length; i++) {
	    	b.write(words[i].getWord() +"\n");
	    	// Get word identifiers
	    	String[][] identifiers = words[i].getIdentifiers();
	    	for(int j = 0; j < 4; j++) {
	    		for(int k = 0; k < identifiers[j].length; k++)
	    			b.write(identifiers[j][k] + " ");
	    		b.write("\n");
	    	}
    		b.write("\n");
	    }
	    
	    b.close();
	}
}
