package textAdventure;

public class Key extends Item {
  private String keyId;

  public Key(String keyId, String keyName, long weight) {
    super(weight, keyName);
    this.keyId = keyId;
  }

  public String getKeyId() {
    return keyId;
  }
}
