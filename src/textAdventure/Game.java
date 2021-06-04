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
  private long carryingCapacity = 5000;
  private long countWorkout = 0;
  private boolean hasPrinted = false;

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    try {
      player = new Character(new Inventory(carryingCapacity));
      initRooms("src\\textAdventure\\data\\rooms.json");
      initItems("src\\textAdventure\\data\\items.json");
      currentRoom = roomMap.get("MusicRoom");
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
      String itemName = (String) ((JSONObject) roomObj).get("name");
      String roomId = (String) ((JSONObject) roomObj).get("room");
      long weight = (long) ((JSONObject) roomObj).get("weight");
      Boolean isOpenable = (Boolean) ((JSONObject) roomObj).get("isOpenable");
      if (((JSONObject) roomObj).get("keyId") != null) {
        String keyId = (String) ((JSONObject) roomObj).get("keyId");
        Item key = new Key(keyId, itemName, weight);
        roomMap.get(roomId).getInventory().addItem(key);
      } else {
        Item item = new Item();
        item.setName(itemName);
        item.setWeight(weight);
        item.setOpenable(isOpenable);
        if (((JSONObject) roomObj).get("maxWeight") != null) {
          long maxWeight = (long) ((JSONObject) roomObj).get("maxWeight");
          item.setInventory(new Inventory(maxWeight));
        }
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
        processWin();
        processDeath();      
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
    System.out.println("Welcome player to the wonderous Wizarding World!");
    System.out.println("This is a place for adventure, heroism, and spirit!");
    System.out.println("If you ever get stuck or need a hand, type 'help'.");
    System.out.println();
    System.out.println(currentRoom.longDescription());
  }

  /**
   * Given a command, process (that is: execute) the command. If this command ends
   * the game, true is returned, otherwise false is returned.
   */
  private boolean processCommand(ArrayList<String> command) {
    if (command.get(0).equals("inventory"))
      System.out.println(player.getInventory().viewInventory());
    else if (command.get(0).equals("help"))
      printHelp(command);
    else if (command.get(0).equals("go"))
      goRoom(command);
    else if (command.get(0).equals("quit") || command.get(0).equals("exit")) {
      if (command.size() > 1)
        System.out.println("Quit what?");
      else
        return true; // signal that we want to quit
    } else if (command.get(0).equals("eat")) 
        eatItem(command);    
    else if (command.get(0).equals("board"))
      boardTrain(command);
    else if (command.get(0).equals("take")) {
      takeCommand(command);    
    } else if (command.get(0).equals("drop")) {
      if (command.size() < 2)
        System.out.println("Drop what?");
      else
        dropItem(command.get(1));
    } else if (command.get(0).equals("open")) {
      if (command.size() < 2)
        System.out.println("Open what?");
      else
        openContainer(command.get(1));
    } else if (command.get(0).equals("run"))
        runWall(command);
    else if(command.get(0).equals("workout"))
      workout();
    else if(command.get(0).equalsIgnoreCase("KJ9E3L"))
      enterCode();
    else if(command.get(0).equals("play")){
      if(command.get(1).equals("flute")){
        if (player.getInventory().viewInventory().indexOf("flute") > -1) {
          playFlute(command);
        } else{
          System.out.println("You don't have the flute, so you can't use it!");
        }
      }
    }else if(command.get(0).equals("equip")){
      if(command.get(1).equals("cloak")){
        if (player.getInventory().viewInventory().indexOf("cloak") > -1) {
          equipCloak(command);
        } else{
        System.out.println("You don't have the cloak, so you can't play it!");
        }
      }
    }else if(command.get(0).equals("use")){
      if(command.get(1).equals("charm")){
        if (player.getInventory().viewInventory().indexOf("charm") > -1) {
          useCharm(command);
        } else{
        System.out.println("You don't have the charm, so you can't use it!");
        }
      }
    } 
    else if(command.get(0).equals("cast")) {
      spellsCast(command);
    } else if (command.get(0).equals("put") || command.get(0).equals("place"))
      putItemInContainer(command.get(1), command.get(3));
    else if(command.get(0).equals("read")) {
    if (command.size() < 2)
      System.out.println("Read what?");
    else
      readSpellbook(command.get(1));
    } else
      System.out.println("You can't do that.");
    return false;
  }
  private void processWin(){
    if(currentRoom.getRoomName().equals("Final Room") && !hasPrinted) {
      System.out.println("                                                                                     ,---,    ,---,    ,---,  ");
      System.out.println("                                                                                  ,`--.' | ,`--.' | ,`--.' |  ");
      System.out.println("                                                        .---.                     |   :  : |   :  : |   :  :  ");
      System.out.println("        ,---,                                          /. ./|  ,--,               '   '  ; '   '  ; '   '  ; ");
      System.out.println("       /_ ./|   ,---.           ,--,               .--'.  ' ;,--.'|         ,---, |   |  | |   |  | |   |  |  ");
      System.out.println(" ,---, |  ' :  '   ,'\\        ,'_ /|              /__./ \\ : ||  |,      ,-+-. /  |'   :  ; '   :  ; '   :  ;  ");
      System.out.println("/___/ \\.  : | /   /   |  .--. |  | :          .--'.  '   \\' .`--'_     ,--.'|'   ||   |  ' |   |  ' |   |  '  ");
      System.out.println(" .  \\  \\ ,' '.   ; ,. :,'_ /| :  . |         /___/ \\ |    ' ',' ,'|   |   |  ,\"' |'   :  | '   :  | '   :  |  ");
      System.out.println("  \\  ;  `  ,''   | |: :|  ' | |  . .         ;   \\  \\;      :'  | |   |   | /  | |;   |  ; ;   |  ; ;   |  ;  ");
      System.out.println("   \\  \\    ' '   | .; :|  | ' |  | |          \\   ;  `      ||  | :   |   | |  | |`---'. | `---'. | `---'. |  ");
      System.out.println("    '  \\   | |   :    |:  | : ;  ; |           .   \\    .\\  ;'  : |__ |   | |  |/  `--..`;  `--..`;  `--..`;  ");
      System.out.println("     \\  ;  ;  \\   \\  / '  :  `--'   \\           \\   \\   ' \\ ||  | '.'||   | |--'  .--,_    .--,_    .--,_     ");
      System.out.println("      :  \\  \\  `----'  :  ,      .-./            :   '  |--\" ;  :    ;|   |/      |    |`. |    |`. |    |`.  ");
      System.out.println("       \\  ' ;           `--`----'                 \\   \\ ;    |  ,   / '---'       `-- -`, ;`-- -`, ;`-- -`, ; ");
      System.out.println("        `--`                                       '---\"      ---`-'                '---`\"   '---`\"   '---`\"  ");
      System.out.println("                                                                                                              ");
      hasPrinted = true;
    }
  }
  private void processDeath(){
    if(currentRoom.getRoomName().equals("Funny Death Room") ) {
      killPlayer();
    } else if(currentRoom.getRoomName().equals("A Cold Room") ) { 
        if (player.getInventory().viewInventory().indexOf("flute") < 0) {
          System.out.println("As you tried to go into the cold room without calming the dog down, the dog got angry and bit your head off. You died. \n\n\n\n\n ");
          killPlayer();
        }
    } else if(currentRoom.getRoomName().equals("Overgrown Plant House") ) { 
      if (player.getInventory().viewInventory().indexOf("spellbook") < 0) {
        System.out.println("As you tried to go into the overgrown plant house, you weren't able to control the plant and it ate you like how a venus fly trap eats a fly. You died. \n\n\n\n\n ");
        killPlayer();
      }
    } else if(currentRoom.getRoomName().equals("Quidditch Field") ) { 
      if (player.getInventory().viewInventory().indexOf("cloak") < 0) {
        System.out.println("As you tried to go into the quidditch field, Balthazar forced you to play quidditch for three days straight. You died from exhaustion. \n\n\n\n\n ");
        killPlayer();
      }
    } else if(currentRoom.getRoomName().equals("Tiny Room") ) { 
      if (player.getInventory().viewInventory().indexOf("gillyweed") < 0) {
        System.out.println("As you tried to go into the tiny room, you weren't able to stop the drip of water. You drowned. \n\n\n\n\n ");
        killPlayer();
      }
    } else if(currentRoom.getRoomName().equals("Long Room") ) { 
      if (player.getInventory().viewInventory().indexOf("charm") < 0) {
        System.out.println("As you tried to go into the long room, the spirit turns YOU into a spirit. You died. \n\n\n\n\n ");
        killPlayer();
      }
    } 
  }
  private void killPlayer(){
    currentRoom = roomMap.get("UndergroundCellar");
    System.out.println(currentRoom.longDescription());
  }
  // implementations of user commands:

  private void takeCommand(ArrayList<String> command) {
    if (command.size() < 3) {
      if (command.size() == 1) // no second word
        System.out.println("Take what?");
      else
        takeItem(command.get(1));
    } else {
      boolean hasFound = false;
      int countForItem = 1; // command.get(0) MUST be "take" for this line to occur.
      while (!hasFound) {   // This will run an infinite loop if the item in the inputLine is not in the inventory
        if (checkForItem(command.get(countForItem))[0] >= 0)
          hasFound = true;
        else 
          countForItem++;
      }
      hasFound = false;
      int countForContainer = countForItem + 1; // Container cannot appear before or at the same index as item in the InputLine.
      while (!hasFound) {
        if (checkForItem(command.get(countForContainer))[0] >= 0)
          hasFound = true;
        else 
          countForContainer++;
      }
      takeItemFromContainer(command.get(countForItem), command.get(countForContainer));
    }
  }

  private void enterCode() {
    if (currentRoom.getRoomName().equals("Your Room")) {
      System.out.println("The small door creaks open and you crawl through, the door shutting behind you. ");
      System.out.println();
      currentRoom = roomMap.get("Tunnel");
      System.out.println(currentRoom.longDescription());
    }
  }

  private void eatItem(ArrayList<String> command) {
    if(command.get(1).equals("gillyweed")){
      if (player.getInventory().viewInventory().indexOf("gillyweed") > -1) {
        eatGillyweed(command);
      } else
      System.out.println("You ate gillyweed, but nothing happened. ");
    }
    else
      System.out.println("Do you really think you should be eating at a time like this?");
  }

  /**
   * Handles everything regarding using the charm item.
   * @param command user input
   */
  private void useCharm(ArrayList<String> command) {
    if (currentRoom.getRoomName().equals("Long Room")) { //checks the name of the room, since you can only use the charm in one of the challenge rooms
      System.out.println("As you use the charm, you notice the boggart starting to transform into spongebob, telling you about his day. He lets you into the next room and congratulates you on beating the game. ");
      System.out.println();
      currentRoom = roomMap.get("MirrorRoom"); //once you use the charm in the correct room, the game teleports you into the next challenge room
      System.out.println(currentRoom.longDescription());
    }
  }

  /**
   * Handles everything regarding using the gillyweed item.
   * @param command user input
   */
  private void eatGillyweed(ArrayList<String> command) {
    if (currentRoom.getRoomName().equals("Tiny Room")) {
      System.out.println("You eat the gillyweed and you watch in amazement as you start to grow gills. Your feet become webbed and you easily swim in teh water to the door above. ");
      System.out.println();
      currentRoom = roomMap.get("BoggartRoom");
      System.out.println(currentRoom.longDescription());
    }
  }

  /**
   * Handles everything regarding using the cloak item.
   * @param command user input
   */
  private void equipCloak(ArrayList<String> command) {
    if (currentRoom.getRoomName().equals("Quidditch Field")) {
      System.out.println("You wait till Balthazar turns his head and pull the cloak over yourself. He tries to find you but you are long gone in the next room. ");
      System.out.println();
      currentRoom = roomMap.get("GillyWeedRoom");
      System.out.println(currentRoom.longDescription());
  } else 
    System.out.println("You use the cloak, but nothing happens. ");
  }
  
  /**
   * Handles everything regarding using the flute item. 
   * @param command user input
   */
  private void playFlute(ArrayList<String> command) { 
    if (currentRoom.getRoomName().equals("A Cold Room")) {
      System.out.println("You use the flute and now the 3 Headed Dog has fallen asleep. Success! This flute has magical powers afterall since you got teleported to the Death Snare Plant Room. ");
      System.out.println();
      currentRoom = roomMap.get("DeathSnarePlant");
      System.out.println(currentRoom.longDescription());
    } else 
      System.out.println("You play the flute, but nothing happens. ");
  }
  
  /**
   * Allows the user to cast spells. Has each specific spell and the funny message that comes with it. Additionally, one of the final challenges requires
   * a spell so we cover that.
   * @param command user input
   */
  private void spellsCast(ArrayList<String> command) {
    if (player.getInventory().viewInventory().indexOf("book") > -1) {
      if (command.get(1).equals("rictusempra"))
        System.out.println("Haha you're tickling yourself!");
      else if (command.get(1).equals("furnunculu"))
        System.out.println("That just backfired, you covered yourself in boils!");
      else if (command.get(1).equals("densaugeo"))
        System.out.println("Ahhh now you have bunny like teeth! ");
      else if (command.get(1).equals("incendio")) {
        if (currentRoom.getRoomName().equals("Death Snare Plant")) {
          System.out.println("You set the death snare on fire, shrinking its size down considerably. You burned a hole through the wall and you walked through it. ");
          currentRoom = roomMap.get("FlyingWingsGame");
          System.out.println(currentRoom.longDescription());
        } else 
          System.out.println("You consider lighting the room on fire, but you've decided not to. ");
      }
    } else
      System.out.println("You don't have the book of spells so you can't cast any spells. ");
  }

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

  /**
   * specific command help for the user, just in case they get stuck or are wondering what a command does
   * @param command the input from the user, at index 0 is help, and at index 1 is the command they want to learn about
   */
  private void commandHelp(ArrayList<String> command) {
    if (command.get(1).equals("go")){
      System.out.println("Allows you to move in the following directions: [North, South, East, West, Up, Down]");
    } else if (command.get(1).equals("board")){
      System.out.println("Helps you get onto a train.");
    } else if (command.get(1).equals("take")){
      System.out.println("Allows you to pick up items that you can use later.");
    }  else if (command.get(1).equals("drop")) {
      System.out.println("Allows you to release items that you do not wish to hold onto anymore.");
    } else if (command.get(1).equals("cast")){
      System.out.println("Helps you make magical spells with your wand.");
    } else if (command.get(1).equals("hit")){
      System.out.println("Allows you to whack things around you.");
    } else if (command.get(1).equals("open")){
      System.out.println("Allows you to see what is inside of an object");
    } else if (command.get(1).equals("quit") || command.get(1).equals("exit")){
      System.out.println("Ends the game. That's one way to go out!");
    } else if (command.get(1).equals("help")) {
      System.out.println("Prints the help message.");
    } else if (command.get(1).equals("eat")){
      System.out.println("Allows you to fuel up before a very cool adventure in Hogwarts!");
    } else if (command.get(1).equals("run")){
      System.out.println("Makes you sprint as fast as you can in the direction you choose. You do however risk losing your dignity if you trip and fall.");
    } else if (command.get(1).equals("workout")){
      System.out.println("Allows you to carry more weight by working out and becoming buff. Self improvement is key.");
    } else if (command.get(1).equals("inventory")){
      System.out.println("Tells you what you are currently carrying in your inventory.");
    } else if (command.get(1).equals("read")){
      System.out.println("Allows you to read the contents of a book, and maybe gain some knowledge to help you in the game!");
    } else if (command.get(1).equals("equip")){
      System.out.println("Allows you to wear a piece of clothing.");
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
        if ("west-east-north-south-up-down".indexOf(direction) >= 0)  
          System.out.println("You can't go that way.");
      } else {
        for (int e = 0; e < currentRoom.getExits().size(); e++) {
          if (currentRoom.getExits().get(e).getDirection().equalsIgnoreCase(direction)) {
            if (currentRoom.getExits().get(e).isLocked()) {
              Exit tempExit = currentRoom.getExits().get(e);
              for (Item item : player.getItems()) {
                if (item.getKeyId().equals(tempExit.getKeyId())) {
                  currentRoom.getExits().get(e).setLocked(false);
                  currentRoom = nextRoom;
                  System.out.println(currentRoom.longDescription());
                }
              }
              if (tempExit.isLocked()) // not working, if player has key, then e is still
                System.out.println("This door is locked. You need the right key to enter. ");
            } else {
              currentRoom = nextRoom;
              System.out.println(currentRoom.longDescription());
            }
          }
        }
      }
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
      if ("west-east-north-south-up-down".indexOf(command.get(1)) >= 0)
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

  private int[] checkForItem(String item) {
    int[] result = {-1, -1};
    for (int i = 0; i < player.getItems().size(); i++) {
      Item tempItem = player.getItems().get(i);
      if (tempItem.getName().equals(item)) {
        result[0] = i;
        return result;
      } else if (tempItem.isOpenable()) {
        for (int j = 0; j < tempItem.getItems().size(); j++) {
          if (tempItem.getItems().get(j).getName().equals(item)) {
            result[0] = j;
            result[1] = i;
            return result;
          }
        }
      }
    }
    return result;
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
        Item tempItem = currentRoom.getItems().get(i);
        if (currentRoom.getItems().get(i).getName().equals(item)) {
          if (player.getInventory().addItem(tempItem)) {
            currentRoom.getInventory().removeItem(tempItem);
            currentRoom.setDescription(currentRoom.getShortDescription() + setRoomDescription());
            System.out.println("Taken.");
          }
          itemExists = true;
        }
      }
      if (!itemExists)
        System.out.println("You can't see " + item + " anywhere.");
          // Maybe make it so that if the item exists in the game then it says the above, otherwise say something else.
  }

  private void takeItemFromContainer(String item, String container) {
    int containerIndex = checkForItem(container)[0]; // the index of the container that is specified by the player in the player's inventory 
    int itemInContainerIndex = checkForItem(item)[0]; // the index of the item that is specified by the player in its container 
    int containerOfItemIndex = checkForItem(item)[1]; // the index of the container that the specified item is actually in
    if (containerIndex >= 0) {
      Item tempContainer = player.getItems().get(containerIndex); // the container that is specified by the player
      if (tempContainer.isOpenable()) {
        if (itemInContainerIndex >= 0 && containerOfItemIndex == containerIndex) {
          Item tempItemInContainer = tempContainer.getItems().get(itemInContainerIndex); // the item that is specified by the player that they want to remove from the specified container
          if (player.getInventory().addItem(tempItemInContainer)) { // maybe need to automatically drop the item if there is no room
            tempContainer.getInventory().removeItem(tempItemInContainer); // changed the start of the line
            System.out.println("You took the " + item + " out of the " + container + ".");
          } else { 
            System.out.println("You are carrying too much to pick up the " + item + ".");
            
            //System.out.println("The " + item + " was too heavy for you to hold.");
            //dropItem(item);
              // can't drop the item because the item will never make it to the player's inventory
              // might need to make separate method to drop item from container directly into room
          }
        } else
          System.out.println("The " + item + " is not in the " + container + ".");
      } else
        System.out.println("You can't open the " + container + ".");
    } else
      System.out.println("You don't have the " + container + ".");
  }

  /**
   * The player drops an item into the room.
   * This checks if the item is actually in the player's inventory or not.
   * The item leaves the player's inventory and goes into the room's inventory.
   * If the item is not in the player's inventory, there is an error message and nothing happens.
   * @param item the name of the item the player wants to drop
   */
  private void dropItem(String item) {  // This needs to be modified to account for items in containers
    int i = checkForItem(item)[0];
    if (i >= 0) {
      Item tempItem = player.getItems().get(i);
      currentRoom.getInventory().addItem(tempItem);
      player.getInventory().removeItem(tempItem);
      currentRoom.setDescription(currentRoom.getShortDescription() + setRoomDescription());
      System.out.println("You dropped your " + item + " in the " + currentRoom.getRoomName());
    } else
      System.out.println("You don't have a " + item + ".");
        // Maybe make it so that if the item exists in the game then it says the above, otherwise say something else.
  }

  /**
   * The player puts an item from their inventory into a container (ex. backpack) in their inventory
   * If the item or container do not exist or are not in the player's inventory, then there is an error message
   * @param item the item they want to put in the container
   * @param container the place to store that item
   */
  private void putItemInContainer(String item, String container) {
    int itemIndex = checkForItem(item)[0];
    int containerOfItemIndex = checkForItem(item)[1];
    int containerIndex = checkForItem(container)[0];
    if (itemIndex >= 0) {
      Item tempItem = player.getItems().get(itemIndex);
      if (containerOfItemIndex < 0) {
        if (containerIndex >= 0) {
          Item tempContainer = player.getItems().get(containerIndex);
          if (tempContainer.isOpenable()) {
            if (tempContainer.getInventory().addItem(tempItem)) {
              player.getInventory().removeItem(tempItem);
              System.out.println("You put your " + item + " in the " + container + ".");
            }
          } else
            System.out.println("You can't open " + container + ".");
        } else
          System.out.println("You don't have " + container + ".");
      } else
        System.out.println("The " + item + " is already in the " + player.getItems().get(containerOfItemIndex).getName() +".");
    } else 
      System.out.println("You don't have " + item + ".");
  }
  
  private void openContainer(String container) {
    int containerIndex = checkForItem(container)[0];
    int containerOfContainerIndex = checkForItem(container)[1];
    if (containerOfContainerIndex < 0) {
      if (containerIndex >= 0) {
        Item tempContainer = player.getItems().get(containerIndex); // the container that is specified by the player
        if (tempContainer.isOpenable())
          tempContainer.open();
        else 
          System.out.println("You can't open " + container + ".");
      } else 
        System.out.println("You don't have " + container + ".");
    } else 
      System.out.println("The " + container + " is already in the " + player.getItems().get(containerOfContainerIndex).getName() +".");
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

  private void workout() {
    if (currentRoom.getRoomName().equals("Gym")) {
      if (countWorkout != 0)
        System.out.println("As you make your way over to the weights yet again and look at the " + countWorkout + " empty protein shake bottle(s), the body builders applaud you.");
      System.out.println("You lift with all your might and realize you're getting stronger. You down a protein shake. You earned that extra 10 pounds you can hold.");
      carryingCapacity += 10;
      player.getInventory().updateMaxWeight(carryingCapacity);
      countWorkout++;
    } else
      System.out.println("You can't workout here. Make your way to the gym to get jacked!");
  }

  private void readSpellbook(String spellbook) {
    int spellbookIndex = checkForItem(spellbook)[0];
    if (spellbookIndex >= 0) {
      Item tempSpellbook = player.getItems().get(spellbookIndex);
      if (tempSpellbook.getSpells() != null) {
        for (String spell : tempSpellbook.getSpells()) {
          System.out.println(spell);
        }
      } else
        System.out.println("There is nothing to read. ");
    } else
      System.out.println("You don't have " + spellbook + ". ");
  }
}
