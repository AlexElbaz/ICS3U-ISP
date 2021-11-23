package textAdventure;

public class Exit extends OpenableObject {
  private String direction;
  private String adjacentRoom;

  /**
   * Initializes a new Exit with a direction, adjacent room, lock status,
   *   and keyId (used when the exit is going to be initialized as locked).
   * @param direction the cardinal direction of this exit (from the room that it is initialized in).
   * @param adjacentRoom the room this exit connects to.
   *  (AKA the adjacent room in the direction of this exit from the room this exit is initialized in).
   * @param isLocked whether this exit is locked or not.
   * @param keyId the Id that matches up to a key if this exit is locked.
   */
  public Exit(String direction, String adjacentRoom, boolean isLocked, String keyId) {
    super(isLocked, keyId);
    this.direction = direction;
    this.adjacentRoom = adjacentRoom;
  }

  /**
   * Initializes a new Exit with a direction and adjacent room
   *  (used when the exit is permanently unlocked).
   * @param direction the cardinal direction of this exit.
   * @param adjacentRoom the room this exit connects to.
   *  (AKA the adjacent room in the direction of this exit from the room this exit is initialized in).
   */
  public Exit(String direction, String adjacentRoom) {
    this.direction = direction;
    this.adjacentRoom = adjacentRoom;
  }

  /**
   * @return the cardinal direction of this exit (from the room that it is initialized in).
   */
  public String getDirection() {
    return direction;
  }

  /**
   * Sets the cardinal direction of this exit to the specified direction.
   * @param direction the specified cardinal direction.
   */
  public void setDirection(String direction) {
    this.direction = direction;
  }

  /**
   * @return the room this exit connects to.
   *  (AKA the adjacent room in the direction of this exit from the room this exit is initialized in).
   */
  public String getAdjacentRoom() {
    return adjacentRoom;
  }

  /**
   * Sets the adjacent room in the direction of this exit (from the room this exit is
   *  initialized in) to the specified room.
   * @param adjacentRoom The room adjacent the room this exit is intitialized in, in the direction of this exit.
   *  (AKA the specified room mentioned above).
   */
  public void setAdjacentRoom(String adjacentRoom) {
    this.adjacentRoom = adjacentRoom;
  }

}