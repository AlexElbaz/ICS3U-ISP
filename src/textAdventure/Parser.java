package textAdventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Parser {
  private ArrayList<String> unnecessaryWords = new ArrayList<String>(Arrays.asList("the"));
  private ArrayList<String> validCommands = new ArrayList<String>(Arrays.asList("go", "quit", "help", "eat", "run", "board", "take", "drop", "cast", "open", "put", "place", "workout", "inventory", "read", "wait", "equip", "use"));
  private Scanner in;

  /**
   * No arguement Parser constructor. Initializes a Parser object and creates a
   *  Scanner for it so that it can be used to detect and stor user input.
   */
  public Parser() {
    in = new Scanner(System.in);
  }

  /**
   * Prompts the user to input a command, stores their input, and parses it into an ArrayList.
   * @return An ArrayList composed of all the individual words input by the user minus
   *  any unnecessary words (defined by the unnecessaryWords ArrayList).
   * @throws java.io.IOException
   */
  public ArrayList<String> getCommand() throws java.io.IOException {
    String inputLine = "";
    
    System.out.print("> ");
      // Print prompt to prompt the user to type a command.

    inputLine = in.nextLine().toLowerCase();
      // Convert input to lowercase (as we deal with commands and such
      //  in exclusively lowercase throughout our code (for simplicity)).

    ArrayList<String> words = new ArrayList<String>(Arrays.asList(inputLine.split(" ")));
      // Creates an ArrayList of individual words from the inputLine split by spaces. Have to do it
      //  like this because the [String].split("[String]") method only works for splitting Strings into Arrays.

    words.removeAll(unnecessaryWords);
      // ArrayList functionality allows us to remove all instances of specific words (or whatever type 
      //  the ArrayLists are) from one ArrayList in another specified ArrayList. So, because "the" (the only 
      //  element in unnecessaryWords) is not necessary, we removed all instances of it from the inputLine.
        
    return words;
  }

  /**
   * Print out a list of valid command words.
   */
  public void showCommands() {
   System.out.println(validCommands);
  }
}