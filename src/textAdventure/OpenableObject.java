package textAdventure;

public class OpenableObject {
  private Boolean isLocked;
  private String keyId;

  public OpenableObject() {
    this.isLocked = false;
    this.keyId = null;
  }

  public OpenableObject(boolean isLocked, String keyId) {
    this.isLocked = isLocked;
    this.keyId = keyId;
  }

  public boolean isLocked() {
    return isLocked;
  }

  public void setLocked(boolean isLocked) {
    this.isLocked = isLocked;
  }

  public String getKeyId() {
    return keyId;
  }
}
