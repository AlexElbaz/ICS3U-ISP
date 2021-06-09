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
  private int countSandwich = 0;
  private boolean hasPrinted = false;

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    try {
      player = new Character(new Inventory(carryingCapacity));
      initRooms("src\\textAdventure\\data\\rooms.json");
      initItems("src\\textAdventure\\data\\items.json");
      currentRoom = roomMap.get("Room");
    } catch (Exception e) {
      e.printStackTrace();
    }
    parser = new Parser();
  }

  /**
   * Initializes the rooms in the room map using the rooms provided in rooms.json
   * @param fileName the address of rooms.json
   * @throws Exception
   */
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
      String roomDescription = (String) ((JSONObject) roomObj).get("description"); // the initial description of the room that includes the items that will be initialized in this room
      String roomShortDescription = (String) ((JSONObject) roomObj).get("shortDescription"); // the description of the room without any item descriptions
      room.setDescription(roomDescription);
      room.setShortDescription(roomShortDescription);
      room.setRoomName(roomName);

      JSONArray jsonExits = (JSONArray) ((JSONObject) roomObj).get("exits");
      ArrayList<Exit> exits = new ArrayList<Exit>();
      for (Object exitObj : jsonExits) { // initializes the exits for each room
        String direction = (String) ((JSONObject) exitObj).get("direction");
        String adjacentRoom = (String) ((JSONObject) exitObj).get("adjacentRoom");
        String keyId = (String) ((JSONObject) exitObj).get("keyId");
        Boolean isLocked = (Boolean) ((JSONObject) exitObj).get("isLocked");
        Exit exit = new Exit(direction, adjacentRoom, isLocked, keyId);
        exits.add(exit);
      }
      room.setExits(exits);

      // adds hints to the room if roomObj has a non-null hints attribute in rooms.json
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
  
  /**
   * initializes the items in their respective rooms using the items provided in items.json
   * @param fileName the address of items.json
   * @throws Exception
   */
  private void initItems(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    JSONArray jsonItems = (JSONArray) json.get("items");

    for (Object itemObj : jsonItems) {
      String itemName = (String) ((JSONObject) itemObj).get("name");
      String roomId = (String) ((JSONObject) itemObj).get("room");
      long weight = (long) ((JSONObject) itemObj).get("weight");
      Boolean isOpenable = (Boolean) ((JSONObject) itemObj).get("isOpenable");
      if (((JSONObject) itemObj).get("keyId") != null) { // if itemObj has a non-null keyId attribute in items.json, create an item with that keyId attribute
        String keyId = (String) ((JSONObject) itemObj).get("keyId");
        Item key = new Key(keyId, itemName, weight);
        roomMap.get(roomId).getInventory().addItem(key);
      } else { // create an item with null keyId (an item that is not a key)
        Item item = new Item();
        item.setName(itemName);
        item.setWeight(weight);
        item.setOpenable(isOpenable);
        if (((JSONObject) itemObj).get("maxWeight") != null) { // if itemObj has a non-null maxWeight attribute in items.json, add an inventory with maxWeight to the item
          long maxWeight = (long) ((JSONObject) itemObj).get("maxWeight");
          item.setInventory(new Inventory(maxWeight));
        }
        roomMap.get(roomId).getInventory().addItem(item);

        if (((JSONObject) itemObj).get("spells") != null) { // if itemObj has a non-null spells attribute in items.json, add that spells attribute to the item
          JSONArray jsonSpells = (JSONArray) ((JSONObject) itemObj).get("spells");
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
        System.out.println();
        ArrayList<String> command = parser.getCommand();
        System.out.println();
        finished = processCommand(command);
        processWin();
        processDeath();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    System.out.println("Thank you for playing. Goodbye.");
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
    System.out.println(currentRoom.getDescription());
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
    else if (command.get(0).equals("go")) {
      if (!(currentRoom.getRoomName().equals("Train Station") || currentRoom.getRoomName().equals("Platform 9 3/4") || currentRoom.getRoomName().equals("Train Room")))
        goRoom(command);
      else
        System.out.println("You can't do that.");
    } else if (command.get(0).equals("quit") || command.get(0).equals("exit")) {
      if (command.size() > 1)
        System.out.println("Quit what?");
      else
        return true; // signal that we want to quit
    } else if (command.get(0).equals("eat")) 
        eatItem(command);    
    else if (command.get(0).equals("board"))
      boardTrain(command);
    else if (command.get(0).equals("wait") && command.size() < 2)
      wait(command);
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
        if (player.getInventory().viewInventory().indexOf("flute") > -1) { // if the player has a flute
          playFlute(command);
        } else {
          System.out.println("You don't have the flute, so you can't use it!");
        }
      }
    } else if (command.get(0).equals("equip")) {
        equipItem(command);
    } else if (command.get(0).equals("use")) {
        useItem(command);
    } else if (command.get(0).equals("cast")) {
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
  
  /**
   * prints a fancy "YOU WIN!!!" message when the player is in the final room
   */
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
      hasPrinted = true; // prevents the message from showing multiple times
    }
  }

  /**
   * kills the player every time they are in a situation that kills them
   */
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

  /**
   * sends the player to the UndergroundCellar room as a way of "killing" the player
   */
  private void killPlayer(){
    currentRoom = roomMap.get("UndergroundCellar");
    System.out.println(currentRoom.longDescription());
  }
  // implementations of user commands:

  /**
   * processes multiple situations where "take" is the first word of the command to account for taking an item from the room and taking an item from a container.
   * @param command user input
   */
  private void takeCommand(ArrayList<String> command) {
    if (command.size() > 2) { // if the command is four words or longer
      boolean hasFound = false;
      int countForItem = 1; // command.get(0) MUST be "take" for this line to occur.
      while (!hasFound && countForItem < command.size() - 1) { // iterates through the command
        if (checkForItem(command.get(countForItem))[0] >= 0) // checks if the player has the specified item
          hasFound = true;
        else 
          countForItem++;
      }
      if (hasFound) { // if the player has the item, check for the container
        hasFound = false;
        int countForContainer = countForItem + 1; // the container must be after the item
        while (!hasFound && countForContainer < command.size()) { // iterates through the part of the command after the item
          if (checkForItem(command.get(countForContainer))[0] >= 0) // checks if the player has the specified container
            hasFound = true;
          else 
            countForContainer++;
        }
        if (hasFound) // if the player has both the item and container
          takeItemFromContainer(command.get(countForItem), command.get(countForContainer)); 
        else
          takeItemFromContainer(command.get(countForItem), command.get(countForContainer - 1)); // this is to prevent an IndexOutOfBoundsException and will still call the error message provided by takeItemFromContainer()
      } else
        System.out.println("You don't have " + command.get(1));
    } else {
      if (command.size() == 1) // no second word
        System.out.println("Take what?");
      else
        takeItem(command.get(1));
    }
  }

  /**
   * moves the player past the painting and into the tunnel when they input the correct code in the right room
   */
  private void enterCode() {
    if (currentRoom.getRoomName().equals("Your Room")) {
      System.out.println("The small door creaks open and you crawl through, the door shutting behind you. ");
      System.out.println();
      currentRoom = roomMap.get("Tunnel");
      System.out.println(currentRoom.longDescription());
    }
  }

  /**
   * the player eats an item.
   * currently, the only edible items is gillyweed.
   * the sandwiches are just text for amusement purposes.
   * @param command
   */
  private void eatItem(ArrayList<String> command) {
    if (command.size() >= 2) {
      if (command.get(1).equals("gillyweed")){
        if (player.getInventory().viewInventory().indexOf("gillyweed") >= 0) { // if the player has gillyweed in their inventory
          if (currentRoom.getRoomName().equals("Tiny Room")) { // checks if the player is in "Tiny Room" because the gillyweed is only used in this room
            System.out.println("You eat the gillyweed and you watch in amazement as you start to grow gills. Your feet become webbed and you easily swim in teh water to the door above. ");
            System.out.println();
            currentRoom = roomMap.get("BoggartRoom");
            System.out.println(currentRoom.longDescription());
          } else
            System.out.println("You really shouldn't be eating that here, you might reverse drown...");
        } else 
          System.out.println("You don't have gillyweed.");
      } else
        System.out.println("You can't eat that.");
    } else {
      if (countSandwich == 0)
        System.out.println("You pull a sandwich out of your back pocket and eat it. You feel energized.");
      else
        System.out.println("You pull another sandwich out of your back pocket and eat it. This is now sandwich " + (countSandwich + 1) + ", how odd...");
      countSandwich++;
    }
  }

  /**
   * Handles everything regarding using the charm item.
   * @param command user input
   */
  private void useItem(ArrayList<String> command) {
    if (command.get(1).equals("charm")) {
      if (player.getInventory().viewInventory().indexOf("charm") > -1) { // if the player has a charm
        if (currentRoom.getRoomName().equals("Long Room")) { //checks the name of the room, since you can only use the charm in one of the challenge rooms
          System.out.println("As you use the charm, you notice the boggart starting to transform into spongebob, telling you about his day. He lets you into the next room and congratulates you on beating the game. ");
          System.out.println();
          currentRoom = roomMap.get("MirrorRoom"); //once you use the charm in the correct room, the game teleports you into the next challenge room
          System.out.println(currentRoom.longDescription());
        } else
          System.out.println("You feel that it would be unwise to use that here.");
      } else
        System.out.println("You don't have a charm on you at the moment.");
    } else
      System.out.println("You can't use that.");
  }

  /**
   * Handles everything regarding using the cloak item.
   * @param command user input
   */
  private void equipItem(ArrayList<String> command) {
    if(command.get(1).equals("cloak")) {
      if (player.getInventory().viewInventory().indexOf("cloak") > -1) { // if the player has a cloak
        if (currentRoom.getRoomName().equals("Quidditch Field")) { // checks if the player is in "Quidditch Field" because it is the only place where the invisibility cloak is needed
          System.out.println("You wait till Balthazar turns his head and pull the cloak over yourself. He tries to find you but you are long gone in the next room. ");
          System.out.println();
          currentRoom = roomMap.get("GillyWeedRoom");
          System.out.println(currentRoom.longDescription());
        } else
          System.out.println("It doesn't seem worthwhile to use that here.");
      } else 
        System.out.println("You don't have any cloak on you at the moment.");
    } else 
      System.out.println("You can't equip that.");
  }
  
  /**
   * Handles everything regarding using the flute item. 
   * @param command user input
   */
  private void playFlute(ArrayList<String> command) { 
    if (currentRoom.getRoomName().equals("A Cold Room")) { // checks if the player is in "A Cold Room" because it is the only room where the flute is needed
      System.out.println("You use the flute and now the 3 Headed Dog has fallen asleep. Success! This flute has magical powers afterall since you got teleported to the Death Snare Plant Room. ");
      System.out.println();
      currentRoom = roomMap.get("DeathSnarePlant");
      System.out.println(currentRoom.longDescription());
    } else 
      System.out.println("You play the flute, but nothing happens. ");
  }
  
  /**
   * Allows the user to cast spells. Has each specific spell and the funny message that comes with it. Additionally, one of the final challenges requires
   * a spell so this covers that.
   * @param command user input
   */
  private void spellsCast(ArrayList<String> command) {
    if (player.getInventory().viewInventory().indexOf("book") > -1) { // if the player has the book in their inventory
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
    } else {
      commandHelp(command.get(1));
    }
  }

  /**
   * specific command help for the user, just in case they get stuck or are wondering what a command does.
   * @param command the input from the user, at index 0 is help, and at index 1 is the command they want to learn about
   */
  private void commandHelp(String command) {
    if (command.equals("go")){
      System.out.println("Allows you to move in the following directions: [North, South, East, West, Up, Down]");
    } else if (command.equals("board")){
      System.out.println("Helps you get onto a train.");
    } else if (command.equals("take")){
      System.out.println("Allows you to either pick up items that you can use later (take [item]), or take items out of containers (take [item] from [container]).");
    }  else if (command.equals("drop")) {
      System.out.println("Allows you to release items that you do not wish to hold onto anymore.");
    } else if (command.equals("put") || command.equals("place")) {
      System.out.println("Allows you to put items into a container (put [item] in [container]).");
    } else if (command.equals("cast")){
      System.out.println("Helps you make magical spells with your wand.");
    } else if (command.equals("hit")){
      System.out.println("Allows you to whack things around you.");
    } else if (command.equals("open")){
      System.out.println("Allows you to see what is inside of an object");
    } else if (command.equals("quit") || command.equals("exit")){
      System.out.println("Ends the game. That's one way to go out!");
    } else if (command.equals("help")) {
      System.out.println("Prints the help message.");
    } else if (command.equals("eat")){
      System.out.println("Allows you to fuel up before a very cool adventure in Hogwarts!");
    } else if (command.equals("run")){
      System.out.println("Makes you sprint as fast as you can. You may have to do so to get past a certain obstacle... However, if you wish to navigate through rooms, please use the \"go\" command.");
    } else if (command.equals("workout")){
      System.out.println("Allows you to carry more weight by working out and becoming buff. Self improvement is key.");
    } else if (command.equals("inventory")){
      System.out.println("Tells you what you are currently carrying in your inventory.");
    } else if (command.equals("read")){
      System.out.println("Allows you to read the contents of a book, and maybe gain some knowledge to help you in the game!");
    } else if (command.equals("equip")){
      System.out.println("Allows you to wear a piece of clothing.");
    } else if (command.equals("wait")) {
      System.out.println("Allows you to sit and wait.");
    } else if (command.equals("use")) {
      System.out.println("Allows you to use an item. Note, there are only certain items that you can use.");
    } else {
      System.out.println("That is not a command.");
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
        for (int e = 0; e < currentRoom.getExits().size(); e++) { // iterates through the exits of the current room
          if (currentRoom.getExits().get(e).getDirection().equalsIgnoreCase(direction)) { // checks if any of the exits' direction match with the specified direction
            if (currentRoom.getExits().get(e).isLocked()) { // checks if the exit is locked
              Exit tempExit = currentRoom.getExits().get(e); // place holder for the desired exit to make code cleaner
              for (Item item : player.getItems()) {
                if (item.getKeyId() != null && item.getKeyId().equals(tempExit.getKeyId())) { // checks if the item has a keyId and equals the keyId of tempExit
                  currentRoom.getExits().get(e).setLocked(false); // permanently unlocks the door
                  currentRoom = nextRoom;
                  System.out.println(currentRoom.longDescription());
                  e = 4; // advances the index of exits so it does not iterate through the exits of the new room (at this point in the code, currentRoom is nextRoom)
                }
              }
              if (tempExit.isLocked()) // if the player does not have the right key
                System.out.println("This door is locked. You need the right key to enter. ");
            } else {
              currentRoom = nextRoom;
              System.out.println(currentRoom.longDescription());
              e = 4; // advances the index of exits so it does not iterate through the exits of the new room (at this point in the code, currentRoom is nextRoom)
            }
          }
        }
      }
    } else
      System.out.println("You can only go one way at a time.");
  }

  /**
   * currently, the only function of the run command is for the player to run "through" the wall in the train station.
   * @param command user input
   */
  private void runWall(ArrayList<String> command) {
    if (command.size() < 2) {
      // if there is no second word, we don't know where to go...
      System.out.println("Run where?");
      return;
    }
    if (command.contains("wall") && currentRoom.getRoomName().equals("Train Station") && !hasRunAtWall) {
      hasRunAtWall = true; // the player should only be able to run through the wall once
      Room nextRoom = currentRoom.nextRoom("east");
        // direction of room exit from player
      currentRoom = nextRoom;
      System.out.println(currentRoom.getDescription());
    } else {
      if ("west east north south up down".indexOf(command.get(1)) >= 0) // if the player tries to use a direction with run (ex. if the player says "run east"), prompt them to use go instead
        System.out.println("Try using the go command.");
      else if (command.contains("wall") && !currentRoom.getRoomName().equals("Train Station")) // if the player says "run wall" in a room other than "Train Station" because the only wall the player can run through is at "Train Station"
        System.out.println("You feel the walls around you. They're all solid and it's probably a bad idea to run headfirst into them. It seems like the only wall you can run through is at the train station. ");
      else
        System.out.println("You can't do that.");
    }
  }

  /**
   * currently, the only function of the board command is for the player to board the train in Platform 9 3/4.
   * @param command user input
   */
  private void boardTrain(ArrayList<String> command) {
    if (command.size() < 2) {
      // if there is no second word, we don't know where to go...
      System.out.println("Board what?");
      return;
    }
    if (command.contains("train") && currentRoom.getRoomName().equals("Platform 9 3/4") && !hasBoardedTrain) {
      hasBoardedTrain = true; // the player should only be able to board the train once
      Room nextRoom = currentRoom.nextRoom("east");
        // direction of train exit from player
      currentRoom = nextRoom;
      System.out.println(currentRoom.getDescription());
    } else {
      if (command.contains("train"))
        System.out.println("There is no train here.");
      else
        System.out.println("You can't board that.");
    }
  }

  /**
   * used when the player says to wait.
   * if the player is in "Train Room", a message is displayed and the player is transported to Hogwarts.
   * waiting is currently the only way to get out of the train.
   * @param command user input
   */
  private void wait(ArrayList<String> command) {
    if (currentRoom.getRoomName().equals("Train Room")) { // checks if the player is in "Train Room"
      System.out.println("You wait, looking at the beautiful country scenery outside the window. After some time you notice the train starts to come to a halt...");
      System.out.println("In a whirlwind you are out of the train, bags in hand, and before you know it, in Hogwarts!");
      System.out.println();

      Room nextRoom = currentRoom.nextRoom("east");
        // direction of train exit from player
      currentRoom = nextRoom;
      System.out.println(currentRoom.longDescription());
    } else // if the player calls wait when they are not in "Train Room"
      System.out.println("You sit and wait. Nothing seems to happen.");
  }

  /**
   * checks if the player has the specified item in their inventory or any container(s) they have.
   * @param item the name of the item that the player wants to take
   * @return an int array with 2 numbers: the index of the specified item in the inventory or container (-1 if the player does not have the specified item anywhere) 
   *         and the index of the container that holds the specified item (-1 if the specified item is not in a container)
   */
  private int[] checkForItem(String item) {
    int[] result = {-1, -1};
    for (int i = 0; i < player.getItems().size(); i++) {
      Item tempItem = player.getItems().get(i); // a place holder for the item to make code cleaner
      if (tempItem.getName().equals(item)) { // checks if any item in the player's inventory matches the specified item
        result[0] = i; // sets result[0] to the index of the specified item in the player's inventory
        return result;
      } else if (tempItem.isOpenable()) { // checks the player's containers
        for (int j = 0; j < tempItem.getItems().size(); j++) {
          if (tempItem.getItems().get(j).getName().equals(item)) { // checks if any item in the player's containers matches the specified item
            result[0] = j; // sets result[0] to the index of the specified item in its container
            result[1] = i; // sets result[1] to the index of the container of the specified item
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
   * @param item the name of the item that the player wants to take
   */
  private void takeItem(String item) {
    boolean itemExists = false;
      for (int i = 0; i < currentRoom.getItems().size(); i++) {
        Item tempItem = currentRoom.getItems().get(i); // a place holder for the item to make code cleaner
        if (tempItem.getName().equals(item)) {
          if (player.getInventory().addItem(tempItem)) { // adds the specified item to the player's inventory if they have the space; if they don't, then additem() returns false and an error message
            currentRoom.getInventory().removeItem(tempItem); // removes the specified item from the room
            currentRoom.setDescription(currentRoom.getShortDescription() + setItemRoomDescription()); // sets the description of the room to the description without items (getShortDescription()) plus the items in the room (setItemRoomDescription()) so that the room's items update and don't overlap in the description 
            System.out.println("Taken.");
          }
          itemExists = true;
        }
      }
      if (!itemExists) // checks if the specified item exists in the player's inventory
        System.out.println("You can't see " + item + " anywhere.");
  }

  /**
   * The player takes the specified item from the specified container..
   * If the player does not correctly input a valid item and its container, then there is an error message.
   * If inputted correctly, the specified item goes into the player's inventory and leaves its container.
   * @param item the name of the item that the player wants to take
   * @param container the name of the container that the player wants to take the item from
   */
  private void takeItemFromContainer(String item, String container) {
    int containerIndex = checkForItem(container)[0]; // the index of the container that is specified by the player in the player's inventory 
    int itemInContainerIndex = checkForItem(item)[0]; // the index of the item that is specified by the player in its container 
    int containerOfItemIndex = checkForItem(item)[1]; // the index of the container that the specified item is actually in
    if (containerIndex >= 0) {
      Item tempContainer = player.getItems().get(containerIndex); // a place holder for the specified container in the player's inventory to make code cleaner
      if (tempContainer.isOpenable()) { // checks if the specified container is actually a container
        if (itemInContainerIndex >= 0 && containerOfItemIndex == containerIndex) { // checks if the specified item exists in one of the player's containers and if the specified container matches with the container of the specified item (ex. the player could say "take key from backpack", but the key is in the pot)
          Item tempItemInContainer = tempContainer.getItems().get(itemInContainerIndex); // a place holder for the specified item that the player wants to remove from the specified container to make code cleaner
          if (player.getInventory().addItem(tempItemInContainer)) { // adds the specified item to the player's inventory if they have the space; if they don't, then additem() returns false and an error message
            tempContainer.getInventory().removeItem(tempItemInContainer);
            System.out.println("You took the " + item + " out of the " + container + ".");
          } else { 
            System.out.println("You are carrying too much to take the " + item + ".");
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
  private void dropItem(String item) { 
    int i = checkForItem(item)[0]; // the index of the specified item in the player's inventory
    if (i >= 0) {
      Item tempItem = player.getItems().get(i);
      currentRoom.getInventory().addItem(tempItem); // an if statement is not needed here because rooms do not have a max weight for their inventory (they can hold an unlimited amount of items)
      player.getInventory().removeItem(tempItem);
      currentRoom.setDescription(currentRoom.getShortDescription() + setItemRoomDescription()); // sets the description of the room to the description without items (getShortDescription()) plus the items in the room (setItemRoomDescription()) so that the room's items update and don't overlap in the description
      System.out.println("You dropped your " + item + " in the " + currentRoom.getRoomName());
    } else
      System.out.println("You don't have a " + item + ".");
  }

  /**
   * The player puts an item from their inventory into a container (ex. backpack) in their inventory.
   * If the item or container do not exist or are not in the player's inventory, then there is an error message.
   * @param item the item they want to put in the container
   * @param container the place to store that item
   */
  private void putItemInContainer(String item, String container) {
    int itemIndex = checkForItem(item)[0]; // the index of the item in the player's inventory (or container, but that would result in an error message)
    int containerOfItemIndex = checkForItem(item)[1]; // the index of the container that the item is in (-1 if the item is not already in a container, so this should be -1 if the player is using this correctly)
    int containerIndex = checkForItem(container)[0]; // the index of the container that the player wants to put the specified item in
    if (itemIndex >= 0) {
      Item tempItem = player.getItems().get(itemIndex); // a place holder for the specified item to make code cleaner
      if (containerOfItemIndex < 0) { // checks if the item is not already in a container
        if (containerIndex >= 0) {
          Item tempContainer = player.getItems().get(containerIndex); // a place holder for the specified container to make code cleaner
          if (tempContainer.isOpenable()) { // checks if the specified container is actually a container
            if (tempContainer.getInventory().addItem(tempItem)) { // adds the specified item to the specified container if it has the space; if it doesn't, then additem() returns false and an error message
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
  
  /**
   * if the player inputs a valid container that they have, then this prints the container's inventory
   * @param container the container that the player wants to view the contents of
   */
  private void openContainer(String container) {
    int containerIndex = checkForItem(container)[0]; // the index of the container in the player's inventory
    int containerOfContainerIndex = checkForItem(container)[1]; // the index of the container that the specified container is in (-1 if the specified container is not already in a container, so this should be -1 if the player is using this correctly)
    if (containerOfContainerIndex < 0) { // checks if the specified container is not in another container
      if (containerIndex >= 0) {
        Item tempContainer = player.getItems().get(containerIndex); // a place holder for the specified container to make code cleaner
        if (tempContainer.isOpenable()) // checks if the specified container is actually a container
          tempContainer.open(); // prints all the items in the container
        else 
          System.out.println("You can't open " + container + ".");
      } else 
        System.out.println("You don't have " + container + ".");
    } else 
      System.out.println("The " + container + " is already in the " + player.getItems().get(containerOfContainerIndex).getName() +".");
  }

  /**
   * @return a string of all the items in the current room that is based on the number of items in the room (for better punctuation)
   */
  private String setItemRoomDescription() {
    String items = "";
    int tempNumberOfItems = currentRoom.getItems().size(); // a place holder for the number of items in the current room
    for (int i = 0; i < tempNumberOfItems; i++) {
      String tempItem = currentRoom.getItems().get(i).getName(); // a place holder for the name of the current item to make code cleaner
      if (tempNumberOfItems > 2) { // if there are more than 2 items in the room, use commas
        if (i == 0) // the first item
          items += "You can see a " + tempItem + ", ";
        else if (i < tempNumberOfItems - 1) // the items that are not the first or last items
          items += "a " + tempItem + ", ";
        else // the last item
          items += "and a " + tempItem + " in the room. ";
      } else if (tempNumberOfItems == 2) { // if there are 2 items in the room, use "and" to connect the items
        if (i == 0) // the first word
          items += "You can see a " + tempItem + " ";
        else // the last word
          items += "and a " + tempItem + " in the room. ";
      } else if (tempNumberOfItems == 1) // if there is 1 item in the room, just state that item
        items += "You can see a " + tempItem + " in the room. ";
      
    }
    return items;
  }

  /**
   * increases the player's carrying capacity by 10 pounds.
   * keeps track of how many workouts the player has done
   */
  private void workout() {
    if (currentRoom.getRoomName().equals("Gym")) {
      if (countWorkout != 0)
        System.out.println("As you make your way over to the weights yet again and look at the " + countWorkout + " empty protein shake bottle(s), the body builders applaud you.");
      System.out.println("You lift with all your might and realize you're getting stronger. You down a protein shake. You earned that extra 10 pounds you can hold.");
      carryingCapacity += 10;
      player.getInventory().setMaxWeight(carryingCapacity);
      countWorkout++;
    } else
      System.out.println("You can't workout here. Make your way to the gym to get jacked!");
  }

  /**
   * if the player has a spellbook, this prints the spells in that spellbook
   * @param spellbook the name of the spellbook that the player wants to read from
   */
  private void readSpellbook(String spellbook) {
    int spellbookIndex = checkForItem(spellbook)[0]; // the index of the spellbook in the player's inventory
    if (spellbookIndex >= 0) {
      Item tempSpellbook = player.getItems().get(spellbookIndex); // a place holder for the spellbook to make code cleaner
      if (tempSpellbook.getSpells() != null) { // checks if tempSpellbook has a spells attribute (because the player could try to read something that is not a spellbook, but that would result in an error message)
        for (String spell : tempSpellbook.getSpells()) {
          System.out.println(spell);
        }
      } else
        System.out.println("There is nothing to read. ");
    } else
      System.out.println("You don't have " + spellbook + ". ");
  }
}
