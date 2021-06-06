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
   * @return an ArrayList of all the items this room is holding.
   */
  public ArrayList<Item> getItems() {
    return inventory.getItems();
  }

  /**
   * @return the short description String of this room.
   * (The short description of a room is just the description without mention
   *   of any items that may have started in that room)
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Sets the short description String of this room.
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
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setExits(ArrayList<Exit> exits) {
    this.exits = exits;
  }

  /**
   * No arguement Room constructor. Sets up Room objects' attributes and leaves them as defaults so that they
   *  can be changed later in initRooms().
   */
  public Room() {
    roomName = "DEFAULT ROOM";
    description = "DEFAULT DESCRIPTION";
    exits = new ArrayList<Exit>();
    setInventory(new Inventory(Integer.MAX_VALUE));
  }

  /**
   * Adds an exit to this room.
   */
  public void addExit(Exit exit) throws Exception {
    exits.add(exit);
  }

  /**
   * @return the full description String (including exits) of this room, of the form:
   *  "You are in the [InsertRoomNameHere]."
   *  "Exits: north  west"
   * If this room has no description, then it will return a random hint String. This is because 
   *  the only room without a description is the Room of Wisdom which gives random hints to the player.
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
   * "direction". If there is no room in that direction, return null. 
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
      if ("west-east-north-south-up-down".indexOf(direction) == -1)
        System.out.println(direction + " is not a valid direction.");
      return null;
    }

    if ("west-east-north-south-up-down".indexOf(direction) == -1)
      System.out.println(direction + " is not a valid direction.");
    return null;
  }

  /**
   * @return the name String of this room.
   */
  public String getRoomName() {
    return roomName;
  }

  /**
   * Sets the name String of this room.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  /**
   * @return the description String of this room.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description String of this room.
   * Used in initRooms() when initializing all rooms upon startup.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the hints for this room.
   * Used in initRooms() when initializing the Room of Wisdom upon startup.
   */
  public void setHints(ArrayList<String> hints) {
    this.hints = hints;
  }

  
}
