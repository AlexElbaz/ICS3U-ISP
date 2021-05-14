package textAdventure;

import java.util.ArrayList;

public class Item extends OpenableObject {
  private long weight;
  private String name;
  private boolean isOpenable;
  private Inventory inventory;
  private String itemRoomDescription;

  public Item(int weight, String name) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = false;
    inventory = null;
  }

  public String getItemRoomDescription() {
    return itemRoomDescription;
  }

  public void setItemRoomDescription(String roomDescription) {
    this.itemRoomDescription = roomDescription;
  }

  public Item(int weight, String name, int maxWeight) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = true;
    this.inventory = new Inventory(maxWeight);
  }

  public ArrayList<Item> getItems() {
    if (!isOpenable) {
      System.out.println("The");
      return null;
    }
    else
      return inventory.getItems();
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public Item() {
  }

  public void open() {
    if (!isOpenable)
      System.out.println("The " + name + " cannot be opened.");
    else {
      inventory.viewInventory();
    }
  }

  public long getWeight() {
    return weight;
  }

  public void setWeight(long weight) {
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
