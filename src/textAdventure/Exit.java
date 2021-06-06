package textAdventure;

/**
 * Exit
 */
public class Exit extends OpenableObject {
  private String direction;
  private String adjacentRoom;

  /**
   * initializes the new exit's direction, adjacent room, lock status, and key ID (used when the exit is going to be initialized as locked)
   * @param direction the cardinal direction of the exit from the room that it is initialized in
   * @param adjacentRoom the room adjacent in the direction of the exit
   * @param isLocked whether the exit is locked or not
   * @param keyId the ID that matches up to a key if the exit is locked
   */
  public Exit(String direction, String adjacentRoom, boolean isLocked, String keyId) {
    super(isLocked, keyId);
    this.direction = direction;
    this.adjacentRoom = adjacentRoom;
  }

  /**
   * initializes the new exit's direction and adjacent room (used when the exit is permanently unlocked)
   * @param direction the cardinal direction of the exit from the room that it is initialized in
   * @param adjacentRoom the room adjacent in the direction of the exit
   */
  public Exit(String direction, String adjacentRoom) {
    this.direction = direction;
    this.adjacentRoom = adjacentRoom;
  }

  /**
   * gets the cardinal direction of the exit from the room that it is initialized in
   * @return the cardinal direction of the exit from the room that it is initialized in
   */
  public String getDirection() {
    return direction;
  }

  /**
   * sets the cardinal direction of the exit with the specified direction
   * @param direction the specified cardinal direction
   */
  public void setDirection(String direction) {
    this.direction = direction;
  }

  /**
   * gets the room adjacent in the direction of the exit
   * @return the room adjacent in the direction of the exit
   */
  public String getAdjacentRoom() {
    return adjacentRoom;
  }

  /**
   * sets the room adjacent in the direction of the exit with the specified adjacent room
   * @param adjacentRoom the room adjacent in the direction of the exit
   */
  public void setAdjacentRoom(String adjacentRoom) {
    this.adjacentRoom = adjacentRoom;
  }

}