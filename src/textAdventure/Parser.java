package textAdventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Parser {
  private ArrayList<String> invalidWords = new ArrayList<String>(Arrays.asList("PUTADJECTIVESHERE")); // Implement!!!!!
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
    // words = Arrays.asList(inputLine.split(" ")); // this line doesn't work

    // while (!moreWords) {
    //   if (inputLine.indexOf(" ") >= 0)
    //     words.add(inputLine.substring(0, inputLine.indexOf(" ")));

    //   if (inputLine.indexOf(" ") == -1) {
    //     words.add(inputLine);
    //     moreWords = true;
    //   }

    //   inputLine = inputLine.substring(inputLine.indexOf(" ") + 1);
    // }

    /*
    for (int i = 0; i < words.size(); i++) {
      if (!validWords.contains(words.get(i))) {
        words.remove(i);
        i--;
      }
    }

    System.out.println(words);
    */

    return words;
  }

  /**
   * Print out a list of valid command words.
  */
  public void showCommands() {
    /*
    for (int i = 0; i < NUM_COMMANDS; i++) {
      System.out.println(validWords.get(i));
    }
    */
   System.out.println(validCommands);
  }
}