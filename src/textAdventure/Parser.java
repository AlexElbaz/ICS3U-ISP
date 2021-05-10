package textAdventure;

import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
  private CommandWords commands; // holds all valid command words
  private Scanner in;

  public Parser() {
    //commands = new CommandWords();
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
    
    /*
    String inputLine = "";
    String[] words;

    System.out.print("> "); // print prompt

    inputLine = in.nextLine();

    words = inputLine.split(" ");

    String word1 = words[0];
    String word2 = null;
    if (words.length > 1)
      word2 = words[1];

    if (commands.isCommand(word1))
      return new Command(word1, word2);
    else
      return new Command(null, word2);

    */
  }

  /**
   * Print out a list of valid command words.
  */
  public void showCommands() {
    commands.showAll();
  }
}