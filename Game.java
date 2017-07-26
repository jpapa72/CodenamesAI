import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game {
	
	static Word[] words;
	
	public static void main (String[] args) throws IOException{
		getWords();
		String response = "";
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.print("(L)earn or (P)lay? (L/P): ");
			response = in.nextLine().toUpperCase().trim();
			if(response.length() == 0) continue;
			if(response.charAt(0) == 'L'){
				learnMode();
				continue;
			}
			if(response.equals("/q")){
				break;
			}
		}
		in.close();
	}
	
	static void print() {
		System.out.print("|\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t| |\t\t\t|\n");
		for(int i = 0; i < 5; i++) {
			System.out.print("|\t");
			for(int j = 0; j < 5; j++){
				System.out.print(words[j*5 + i] + "\t");
				if(words[j*5 + i].getWord().length() < 8)
					System.out.print("\t");
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
