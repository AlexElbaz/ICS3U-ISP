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
  private int carryingCapacity = 50;
  private int countWorkout = 0;

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    try {
      player = new Character(new Inventory(carryingCapacity));
      initRooms("src\\textAdventure\\data\\rooms.json");
      initItems("src\\textAdventure\\data\\items.json");
      currentRoom = roomMap.get("DarkMatterClass");
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
      String roomShortDescription = (String) ((JSONObject) roomObj).get("shortDescription");
      room.setDescription(roomDescription);
      room.setShortDescription(roomShortDescription);
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
      if (((JSONObject) roomObj).get("hints") != null) {
        JSONArray jsonHints = (JSONArray) ((JSONObject) roomObj).get("hints");
        ArrayList<String> hints = new ArrayList<String>();
        for (Object hint : jsonHints) {
          hints.add((String) hint);
        }
        room.setHints(hints);
      }
      roomMap.put(roomId, room);
    }
  }
  private void initItems(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    JSONArray jsonItems = (JSONArray) json.get("items");

    for (Object roomObj : jsonItems) {
      Item item = new Item();
      String itemName = (String) ((JSONObject) roomObj).get("name");
      item.setName(itemName);
      String roomId = (String) ((JSONObject) roomObj).get("room");
      long weight = (long) ((JSONObject) roomObj).get("weight");
      item.setWeight(weight);
      Boolean isOpenable = (Boolean) ((JSONObject) roomObj).get("isOpenable");
      item.setOpenable(isOpenable);
      String itemRoomDescription = (String) ((JSONObject) roomObj).get("itemRoomDescription");
      item.setItemRoomDescription(itemRoomDescription);
      System.out.println(roomId); // delete this
      roomMap.get(roomId).getInventory().addItem(item);

      if (((JSONObject) roomObj).get("spells") != null) {
        JSONArray jsonSpells = (JSONArray) ((JSONObject) roomObj).get("spells");
        ArrayList<String> spells = new ArrayList<String>();
        for (Object spell : jsonSpells) {
          spells.add((String) spell);
        }
        item.setSpells(spells);
      }
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
      if (command.get(0).equals("inventory"))
        System.out.println(player.getInventory().viewInventory());
      else if (command.get(0).equals("help"))
        printHelp(command);
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
      else if (command.size() <= 3 && command.get(0).equals("take")) {
        if (command.size() == 1) // no second word
          System.out.println("Take what?");
        else
          takeItem(command.get(1));
      } else if (command.get(0).equals("drop"))
      if (command.size() < 2) {
        System.out.println("Drop what?");
        return false;
      } else
        dropItem(command.get(1));
      else if (command.get(0).equals("run"))
        runWall(command);
      else if (command.get(0).equals("put") || command.get(0).equals("place"))
        putItemInContainer(command.get(1), command.get(3));
      else if (command.size() > 2 && command.get(0).equals("take")) {
        if (command.get(2).equals("from"))
          takeItemFromContainer(command.get(1), command.get(3));
        else 
          takeItemFromContainer(command.get(1), command.get(4));
      }else if(command.get(0).equals("workout")){
        if (currentRoom.getRoomName().equals("Gym")){
          if(countWorkout != 0)
            System.out.println("As you make your way over to the weights and look at the " + countWorkout + " empty protein shake bottle(s), the body builders applaud you.");
          carryingCapacity += 10;
          System.out.println("You lift with all your might as you realize youre getting stronger. You down a protein shake because you earned the extra 10 pounds you can hold in your inventory.");
          countWorkout++;
          player.getInventory().updateMaxWeight(carryingCapacity);
        } else{
          System.out.println("Make your way to the gym to get jacked!");
        }

      }else if(command.get(0).equals("Rictusempra")){
        if(player.getInventory().indexOf("book") > -1){
          System.out.println("Haha you're tickling yourself!");
        }
      }
       else {
        System.out.println("You can't do that.");
      }
    }
    return false;
  }

  // implementations of user commands:

  /**
   * Print out some help information. Here we print some stupid, cryptic message
   * and a list of the command words.
   */
  private void printHelp(ArrayList<String> command) {
    if (command.size() < 2) {
      System.out.println("You are lost. You are alone. You wander");
      System.out.println("around Hogwarts.");
      System.out.println();
      System.out.println("Your command words are:");
      parser.showCommands();
      System.out.println("If you want to learn more about each command, type 'help' [command word]");
    } else{
      commandHelp(command);

    }
  }


  private void commandHelp(ArrayList<String> command) {

    if (command.get(1).equals("go")){
      System.out.println("Allows you to move in the following directions: [North, South, East, West, Up, Down]");
    } else if (command.get(1).equals("board")){
      System.out.println("Helps you get onto a train.");
    } else if (command.get(1).equals("take")){
      System.out.println("Allows you to pick up items that you can use later.");
    }  else if (command.get(1).equals("drop")){
      System.out.println("Allows you to release items that you do not wish to hold onto anymore.");
    } else if (command.get(1).equals("cast")){
      System.out.println("Helps you make magical spells with your wand.");
    } else if (command.get(1).equals("hit")){
      System.out.println("Allows you to whack things around you.");
    } else if (command.get(1).equals("open")){
      System.out.println("Allows you to see what is inside of an object");
    } else if (command.get(1).equals("quit")){
      System.out.println("Ends the game. That's one way to go out!");
    } else if (command.get(1).equals("help")){
      System.out.println("Prints the help message.");
    } else if (command.get(1).equals("eat")){
      System.out.println("Allows you to fuel up before a very cool adventure in Hogwarts!");
    } else if (command.get(1).equals("run")){
      System.out.println("Makes you sprint as fast as you can in the direction you choose. You do however risk losing your dignity if you trip and fall.");
    } else if (command.get(1).equals("workout")){
      System.out.println("Allows you to carry more weight by working out and becoming buff. Self improvement is key.");
    } 
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
  /**
   * The player takes an item from the room.
   * This checks if the item is actually in the room or not.
   * The item leaves the room's inventory and goes into the player's inventory.
   * If the item is not in the room's inventory, there is an error message and nothing happens.
   * @param item the name of the item that player wants to take
   */
  private void takeItem(String item) {
    boolean itemExists = false;
      for (int i = 0; i < currentRoom.getItems().size(); i++) {
        if (currentRoom.getItems().get(i).getName().equals(item)) {
          player.getInventory().addItem(currentRoom.getItems().get(i));
          currentRoom.getInventory().removeItem(currentRoom.getItems().get(i));
          itemExists = true;
        }
      }
      currentRoom.setDescription(currentRoom.getShortDescription() + setRoomDescription());
      System.out.println("Taken.");
      if (!itemExists)
        System.out.println("You can't see " + item + " anywhere.");
          // Maybe make it so that if the item exists in the game then it says the above, otherwise say something else.
  }

  /**
   * The player drops an item into the room.
   * This checks if the item is actually in the player's inventory or not.
   * The item leaves the player's inventory and goes into the room's inventory.
   * If the item is not in the player's inventory, there is an error message and nothing happens.
   * @param item the name of the item the player wants to drop
   */
  private void dropItem(String item) {
    boolean itemExists = false;
    for (int i = 0; i < player.getItems().size(); i++) {
      if (player.getItems().get(i).getName().equals(item)) {
        currentRoom.getInventory().addItem(player.getItems().get(i));
        player.getInventory().removeItem(player.getItems().get(i));
        itemExists = true;
      }
    }
    currentRoom.setDescription(currentRoom.getShortDescription() + setRoomDescription());
    System.out.println("You dropped your " + item + " in the " + currentRoom.getRoomName());
    if (!itemExists)
      System.out.println("You don't have a " + item + ".");
        // Maybe make it so that if the item exists in the game then it says the above, otherwise say something else.
  }

  private String setRoomDescription() {
    String items = "";
    for (int i = 0; i < currentRoom.getItems().size(); i++) {
      if (currentRoom.getItems().size() > 2) {
        if (i == 0)
          items += "You can see a " + currentRoom.getItems().get(i).getName() + ", ";
        else if (i < currentRoom.getItems().size() - 1)
          items += "a " + currentRoom.getItems().get(i).getName() + ", ";
        else
          items += "and a " + currentRoom.getItems().get(i).getName() + " in the room. ";
      } else if (currentRoom.getItems().size() == 2) {
        if (i == 0)
          items += "You can see a " + currentRoom.getItems().get(i).getName() + " ";
        else
          items += "and a " + currentRoom.getItems().get(i).getName() + " in the room. ";
      } else if (currentRoom.getItems().size() == 1)
        items += "You can see a " + currentRoom.getItems().get(i).getName() + " in the room. ";
      
    }
    return items;
  }

  /**
   * The player puts an item from their inventory into a container (ex. backpack) in their inventory
   * If the item or container do not exist or are not in the player's inventory, then there is an error message
   * @param item the item they want to put in the container
   * @param container the place to store that item
   */
  private void putItemInContainer(String item, String container) { // FIX SO THAT YOU ADD ITEM TO CONTAINER INVENTORY BEFORE REMOVING IT FROM PLAYER INVENTORY
    boolean itemExists = false;
    boolean containerExists = false;
    boolean containerOpenable = false;
    for (int i = 0; i < player.getItems().size(); i++) {
      if (player.getItems().get(i).getName().equals(item)) {
        for (int j = 0; j < player.getItems().size(); j++) {
          if (player.getItems().get(j).getName().equals(container)) {
            if (player.getItems().get(j).isOpenable()) {
              player.getItems().get(j).getInventory().addItem(player.getItems().get(i));
              player.getInventory().removeItem(player.getItems().get(i));
              System.out.println("You put your " + item + " in the " + container + ".");
              containerOpenable = true;
            }
            containerExists = true;
          }
        }
        itemExists = true;
      }
    }
    if (!itemExists)
      System.out.println("You don't have " + item + ".");
    else if (!containerExists)
      System.out.println("You don't have " + container + ".");
    else if (!containerOpenable)
      System.out.println("You can't open " + container + ".");
  }

  private void takeItemFromContainer(String item, String container) { // FIX SO THAT YOU ADD ITEM TO CONTAINER INVENTORY BEFORE REMOVING IT FROM PLAYER INVENTORY
    boolean itemExists = false;
    boolean containerExists = false;
    boolean containerOpenable = false;
    for (int i = 0; i < player.getItems().size(); i++) {
      if (player.getItems().get(i).getName().equals(container)) {
        if (player.getItems().get(i).isOpenable()) {
          for (int j = 0; j < player.getItems().get(i).getItems().size(); j++) {
            if (player.getItems().get(i).getItems().get(j).getName().equals(item)) {
              boolean addItem = player.getInventory().addItem(player.getItems().get(j)); // maybe need to automatically drop the item if there is no room
              if (!addItem) {
                System.out.println("The " + item + " was too heavy for you to hold.");
                dropItem(item);
              } else
                System.out.println("You took the " + item + " out of the " + container + ".");
              player.getItems().get(i).getInventory().removeItem(player.getItems().get(j));
              itemExists = true;
            }
          }
          containerOpenable = true;
        }
        containerExists = true;
      }
    }
    if (!containerExists)
      System.out.println("You don't have " + container + ".");
    else if (!containerOpenable)
      System.out.println("You can't open " + container + ".");
    else if (!itemExists)
      System.out.println("You don't have " + item + " in the " + container + ".");
  }
}
