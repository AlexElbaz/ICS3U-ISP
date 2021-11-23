package textAdventure;

public class Key extends Item {
  private String keyId;

  /**
   * Key constructor. Initializes keys with a name and weight (which are passed to the parent
   *  Item two arguement constructor) and a keyId (signifying which locked Exit this Key opens).
   * @param keyId the Id of this key which matches with a corresponding locked Exit.
   * @param keyName the name of this key.
   * @param weight the weight of this key.
   */
  public Key(String keyId, String keyName, long weight) {
    super(weight, keyName);
    this.keyId = keyId;
  }

  /**
   * @return the ID of this key.
   */
  public String getKeyId() {
    return keyId;
  }
}
