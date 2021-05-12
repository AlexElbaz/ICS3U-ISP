package textAdventure;

public class Item extends OpenableObject {
  private int weight;
  private String name;
  private boolean isOpenable;
  private Inventory inventory;

  public Item(int weight, String name) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = false;
    inventory = null;
  }

  public Item(int weight, String name, int maxWeight) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = true;
    this.inventory = new Inventory(maxWeight);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public void open() {
    if (!isOpenable)
      System.out.println("The " + name + " cannot be opened.");
    else {
      inventory.viewInventory();
    }
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isOpenable() {
    return isOpenable;
  }

  public void setOpenable(boolean isOpenable) {
    this.isOpenable = isOpenable;
  }

}
