package textAdventure;

public class OpenableObject {
  private Boolean isLocked;
  private String keyId;

  /**
   * OpenableObject no arguement constructor. Initializes OpenableObjects with the isLocked attribute as false
   *  and the keyId attribute as null (as by default OpenableObjects will be unlocked ans as such not requite a key).
   */
  public OpenableObject() {
    this.isLocked = false;
    this.keyId = null;
  }

  /**
   * OpenableObject two arguement constructor. Initializes OpenableObjects with a boolean isLocked
   *  (whether they are locked or not) and a keyId (if they are locked, otherwise the keyId will be null).
   * @param isLocked
   * @param keyId
   */
  public OpenableObject(boolean isLocked, String keyId) {
    this.isLocked = isLocked;
    this.keyId = keyId;
  }

  /**
   * @return whether or not this OpenableObject is locked.
   */
  public boolean isLocked() {
    return isLocked;
  }

  /**
   * Sets this OpenableObject to be either locked (true) or unlocked (false).
   * @param isLocked the boolean determining whether or not this OpenableObject is locked.
   */
  public void setLocked(boolean isLocked) {
    this.isLocked = isLocked;
  }

  /**
   * @return the keyId required to unlock this OpenableObject.
   */
  public String getKeyId() {
    return keyId;
  }
}
