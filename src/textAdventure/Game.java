package textAdventure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Game {

  public static HashMap<String, Room> roomMap = new HashMap<String, Room>();

  private Parser parser;
  private Room currentRoom;
  private Character player;
  private boolean hasRunAtWall = false;
  private boolean hasBoardedTrain = false;

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    try {
      player = new Character(new Inventory(100));
      initRooms("src\\textAdventure\\data\\rooms.json");
      currentRoom = roomMap.get("TrainStation");
    } catch (Exception e) {
      e.printStackTrace();
    }
    parser = new Parser();
  }

  private void initRooms(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    JSONArray jsonRooms = (JSONArray) json.get("rooms");

    for (Object roomObj : jsonRooms) {
      Room room = new Room();
      String roomName = (String) ((JSONObject) roomObj).get("name");
      String roomId = (String) ((JSONObject) roomObj).get("id");
      String roomDescription = (String) ((JSONObject) roomObj).get("description");
      room.setDescription(roomDescription);
      room.setRoomName(roomName);

      JSONArray jsonExits = (JSONArray) ((JSONObject) roomObj).get("exits");
      ArrayList<Exit> exits = new ArrayList<Exit>();
      for (Object exitObj : jsonExits) {
        String direction = (String) ((JSONObject) exitObj).get("direction");
        String adjacentRoom = (String) ((JSONObject) exitObj).get("adjacentRoom");
        String keyId = (String) ((JSONObject) exitObj).get("keyId");
        Boolean isLocked = (Boolean) ((JSONObject) exitObj).get("isLocked");
        Boolean isOpen = (Boolean) ((JSONObject) exitObj).get("isOpen");
        Exit exit = new Exit(direction, adjacentRoom, isLocked, keyId, isOpen);
        exits.add(exit);
      }
      room.setExits(exits);
      roomMap.put(roomId, room);
    }
  }

  /**
   * Main play routine. Loops until end of play.
   */
  public void play() {
    printWelcome();

    boolean finished = false;
    while (!finished) {
      try {
        ArrayList<String> command = parser.getCommand();
        finished = processCommand(command);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    System.out.println("Thank you for playing.  Good bye.");
  }

  /**
   * Print out the opening message for the player.
   */
  private void printWelcome() {
    System.out.println();
    System.out.println("Welcome to Zork!");
    System.out.println("Zork is a new, incredibly boring adventure game.");
    System.out.println("Type 'help' if you need help.");
    System.out.println();
    System.out.println(currentRoom.longDescription());
  }

  /**
   * Given a command, process (that is: execute) the command. If this command ends
   * the game, true is returned, otherwise false is returned.
   */
  private boolean processCommand(ArrayList<String> command) {
    if (command.size() < 1)
      System.out.println("I don't know what you mean...");
    else {
    //if (command.size() <= 2) { // command is 2 words or less (but above 0 words)
      if (command.get(0).equals("help"))
        printHelp();
      else if (command.get(0).equals("go"))
        goRoom(command);
      else if (command.get(0).equals("quit")) {
        if (command.size() > 1)
          System.out.println("Quit what?");
        else
          return true; // signal that we want to quit
      } else if (command.get(0).equals("eat")) 
        System.out.println("Do you really think you should be eating at a time like this?");
      else if (command.get(0).equals("board"))
        boardTrain(command);
      else if (command.get(0).equals("take"))
        takeItem(command.get(1));
      else if (command.get(0).equals("drop"))
        dropItem(command.get(1));
      else if (command.get(0).equals("run"))
        runWall(command);
      else {
        System.out.println("You can't do that.");
      }
    //}
    //if (command.size() <= 4) { // command is under 5 words (but above 0 words)

    //} else { // command is over 4 words
      //System.out.println("You can't do that.");
    //}
    }
    return false;
  }

  // implementations of user commands:

  /**
   * Print out some help information. Here we print some stupid, cryptic message
   * and a list of the command words.
   */
  private void printHelp() {
    System.out.println("You are lost. You are alone. You wander");
    System.out.println("around at Monash Uni, Peninsula Campus.");
    System.out.println();
    System.out.println("Your command words are:");
    parser.showCommands();
  }

  /**
   * Try to go to one direction. If there is an exit, enter the new room,
   * otherwise print an error message.
   */
  private void goRoom(ArrayList<String> command) {
    if (command.size() < 2) {
      // if there is no second word, we don't know where to go...
      System.out.println("Go where?");
      return;
    } else if (command.size() < 3) {  // if the command is 2 words only.
      String direction = command.get(1);

      // Try to leave current room.
      Room nextRoom = currentRoom.nextRoom(direction);

      if (nextRoom == null) {
        if ("west east north south up down".indexOf(direction) >= 0)  
          System.out.println("You can't go that way.");
      } else {
        currentRoom = nextRoom;
        System.out.println(currentRoom.longDescription());
      }
    } else {
      System.out.println("You can only go one way at a time.");
    }
  }

  private void runWall(ArrayList<String> command) {
    if (command.size() < 2) {
      // if there is no second word, we don't know where to go...
      System.out.println("Run where?");
      return;
    }
    if (command.contains("wall") && !hasRunAtWall) {
      hasRunAtWall = true;
      Room nextRoom = currentRoom.nextRoom("east");
        // direction of room exit from player
        currentRoom = nextRoom;
        System.out.println(currentRoom.longDescription());
    } else {
      if ("west east north south up down".indexOf(command.get(1)) >= 0)
        System.out.println("Try using the go command.");
      else
        System.out.println("You can't do that.");
    }
  }

  private void boardTrain(ArrayList<String> command) {
    if (command.size() < 2) {
      // if there is no second word, we don't know where to go...
      System.out.println("Board what?");
      return;
    }
    if (command.contains("train") && !hasBoardedTrain) {
      hasBoardedTrain = true;
      Room nextRoom = currentRoom.nextRoom("east");
        // direction of train exit from player
        currentRoom = nextRoom;
        System.out.println(currentRoom.longDescription());
    } else {
      if (command.contains("train"))
        System.out.println("There is no train here.");
      else
        System.out.println("You can't board that.");
    }
  }

  private void takeItem(String item) {
    boolean itemExists = false;
      for (int i = 0; i < currentRoom.getItems().size(); i++) {
        if (currentRoom.getItems().get(i).getName().equals(item)) {
          currentRoom.getInventory().removeItem(currentRoom.getItems().get(i));
          player.getInventory().addItem(currentRoom.getItems().get(i));
          System.out.println("Taken.");

          itemExists = true;
        }
      }
      if (!itemExists)
        System.out.println("You can't see " + item + " anywhere.");
          // Maybe make it so that if the item exists in the game then it says the above, otherwise say something else.
  }

  private void dropItem(String item) {
    boolean itemExists = false;
    for (int i = 0; i < currentRoom.getItems().size(); i++) {
      if (currentRoom.getItems().get(i).getName().equals(item)) {
        player.getInventory().removeItem(currentRoom.getItems().get(i));
        currentRoom.getInventory().addItem(currentRoom.getItems().get(i));
        System.out.println("You dropped your " + item + " in the " + currentRoom.getRoomName());
        itemExists = true;
      }
    }
    if (!itemExists)
      System.out.println("You can't see " + item + " anywhere.");
        // Maybe make it so that if the item exists in the game then it says the above, otherwise say something else.
  }
}
