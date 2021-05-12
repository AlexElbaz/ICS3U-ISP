package textAdventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Parser {
  private ArrayList<String> validCommands = new ArrayList<String>(Arrays.asList("go", "quit", "help", "eat", "run", "board", "take", "drop", "cast", "hit", "open"));
  private Scanner in;

  public Parser() {
    in = new Scanner(System.in);
  }

  public ArrayList<String> getCommand() throws java.io.IOException {
    String inputLine = "";
    ArrayList<String> words = new ArrayList<String>();
    boolean moreWords = false;

    System.out.print("> "); // print prompt

    inputLine = in.nextLine();

    while (!moreWords) {
      if (inputLine.indexOf(" ") >= 0)
        words.add(inputLine.substring(0, inputLine.indexOf(" ")));

      if (inputLine.indexOf(" ") == -1) {
        words.add(inputLine);
        moreWords = true;
      }

      inputLine = inputLine.substring(inputLine.indexOf(" ") + 1);
    }

    return words;
  }

  /**
   * Print out a list of valid command words.
  */
  public void showCommands() {
    System.out.println(validCommands);
  }
}