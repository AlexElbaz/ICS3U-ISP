package textAdventure;

import java.util.ArrayList;

public class Room {

  private String roomName;
  private String description;
  private String shortDescription;
  private ArrayList<Exit> exits;
  private Inventory inventory;
  private ArrayList<String> hints;

  /**
   * No arguement Room constructor. Sets up Room objects' attributes and leaves them
   *  as defaults so that they can be changed later in initRooms().
   */
  public Room() {
    roomName = "DEFAULT ROOM";
    description = "DEFAULT DESCRIPTION";
    exits = new ArrayList<Exit>();
    setInventory(new Inventory(Integer.MAX_VALUE));
  }

  /**
   * @return an ArrayList of all the items this room is holding.
   */
  public ArrayList<Item> getItems() {
    return inventory.getItems();
  }

  /**
   * @return the short description (shortDescription) of this room.
   * (The short description of a room is just the description without mention
   *   of any items that may have started in that room)
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Sets the short description (shortDescription) of this room.
   * @param shortDescription the short description we are setting this room's shortDescription to be.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  /**
   * @return the Inventory of this room.
   * Method used in startup to add items to rooms' inventories as well as throughout the code
   *  to access and edit the inventory of a room.
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Sets the inventory of this room.
   * @param inventory the inventory we are setting this room's inventory to be.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  /**
   * @return the ArrayList of exists from this room.
   */
  public ArrayList<Exit> getExits() {
    return exits;
  }

  /**
   * Sets the exits to this room.
   * @param exits the ArrayList of exits we are setting this room's exits to be.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setExits(ArrayList<Exit> exits) {
    this.exits = exits;
  }

  /**
   * Adds an exit to this room.
   * @param exit the Exit we are adding to this room.
   */
  public void addExit(Exit exit) throws Exception {
    exits.add(exit);
  }

  /**
   * @return the full description String (including exits) of this room, of the form:
   *  "You are in the [InsertRoomNameHere]."
   *  "Exits: north  west"
   * If this room has an empty String as the description attribute, then this method will set the description attribute's
   *  part of the full description String to a random hint String. This is because the only room with an empty description
   *  attribute is the Room of Wisdom which gives random hints to the player.
   */
  public String longDescription() {
    String temp = description;
    if (description.equals(""))
      temp = hints.get((int)(Math.random() * hints.size()));
    return "Room: " + roomName + "\n\n" + temp + "\n" + exitString();
  }

  /**
   * @return A String displaying the direction of all this room's exits.
   * For example: "Exits: north  west".
   */
  private String exitString() {
    String returnString = "Exits: ";
    for (Exit exit : exits) {
      returnString += exit.getDirection() + "  ";
    }

    return returnString;
  }

  /**
   * @param direction the direction that the player wants to go in.
   * @return the Room reached if you go from the this room in the direction
   * "direction". If there is no room in that direction, check if the direction input was valid,
   * tell the player if it was not, and return null. 
   */
  public Room nextRoom(String direction) {
    try {
      for (Exit exit : exits) {
        if (exit.getDirection().equalsIgnoreCase(direction)) {
            String adjacentRoom = exit.getAdjacentRoom();
            return Game.roomMap.get(adjacentRoom);
        }
      }
    } catch (IllegalArgumentException ex) {
      if ("west east north south up down".indexOf(direction) == -1) {
        // checks if the direction is one of the game's valid directions
        //  (by checking if it appears in the String "west east north south up down").
        System.out.println(direction + " is not a valid direction.");
      }
      return null;
    }
    if ("west east north south up down".indexOf(direction) == -1)
      System.out.println(direction + " is not a valid direction.");
    return null;
  }

  /**
   * @return the name (roomName) of this room.
   */
  public String getRoomName() {
    return roomName;
  }

  /**
   * Sets the name (roomName) of this room.
   * @param roomName the name we are setting this room's name to be.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  /**
   * @return the description of this room.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of this room.
   * @param description the description we are setting this room's description to be.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the hints for this room.
   * @param hints the ArrayList of hints we are setting this room's hints to be.
   * Used in initRooms() when initializing the Room of Wisdom upon startup.
   */
  public void setHints(ArrayList<String> hints) {
    this.hints = hints;
  }

  
}
