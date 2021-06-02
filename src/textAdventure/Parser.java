package textAdventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Parser {
  private ArrayList<String> invalidWords = new ArrayList<String>(Arrays.asList("the", "a", "as", "from", "of"));  // Add "in" and other stuff once while loop functionality stuff from takeItemFromContainer() is added everywhere
  private ArrayList<String> validCommands = new ArrayList<String>(Arrays.asList("go", "quit", "help", "eat", "run", "board", "take", "drop", "cast", "open", "put", "place", "workout", "inventory", "read"));
  private Scanner in;

  public Parser() {
    in = new Scanner(System.in);
  }

  public ArrayList<String> getCommand() throws java.io.IOException {
    String inputLine = "";
    boolean moreWords = false;

    System.out.print("> "); // print prompt

    inputLine = in.nextLine().toLowerCase();

    ArrayList<String> words = new ArrayList<String>(Arrays.asList(inputLine.split(" ")));
    words.removeAll(invalidWords);
    
    return words;
  }

  /**
   * Print out a list of valid command words.
  */
  public void showCommands() {
   System.out.println(validCommands);
  }
}