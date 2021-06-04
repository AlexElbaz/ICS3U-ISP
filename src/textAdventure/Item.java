package textAdventure;

import java.util.ArrayList;

public class Item extends OpenableObject {
  private long weight;
  private String name;
  private boolean isOpenable;
  private Inventory inventory;
  private String itemRoomDescription;
  private ArrayList<String> spells;

  /**
   * Item constructor. Initializes items with a passed in weight and name,
   *  and sets isOpenable and inventory to false (by default items aren't openable).
   * This constructor is only used for making keys.
   */
  public Item(int weight, String name) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = false;
    inventory = null;
  }

  /**
   * No arguement item constructor. This constructor is used for creating most items.
   * It has no arguements because when we create items we use mainly the JSON to handle item attributes.
   */
  public Item() {
  }

  /**
   * @return Returns an ArrayList of all the items a given item (container) is holding.
   */
  public ArrayList<Item> getItems() {
      return inventory.getItems();
  }

  /**
   * @return the inventory of a item (when applicable, i.e. when it is a container).
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Sets the inventory of an item (when applicable, i.e. when it is a container).
   * Used in initItems() when initializing all items upon startup.
   */
  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  /**
   * Checks if an item is openable. If so, displays the inventory. If not, informs the player.
   */
  public void open() {
    if (!isOpenable)
      System.out.println("The " + name + " cannot be opened.");
    else {
      System.out.println(inventory.viewInventory());
    }
  }

  /**
   * @return the weight of the item.
   */
  public long getWeight() {
    return weight;
  }

  /**
   * Sets the weight of an item.
   * Used in initItems() when initializing all items upon startup.
   */
  public void setWeight(long weight) {
    this.weight = weight;
  }

  /**
   * @return the name String of an item.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of an item.
   * Used in initItems() when initializing all items upon startup.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return whether an item is openable or not.
   */
  public boolean isOpenable() {
    return isOpenable;
  }

  /**
   * Sets whether an item is openable or not.
   * Used in initItems() when initializing all items upon startup.
   */
  public void setOpenable(boolean isOpenable) {
    this.isOpenable = isOpenable;
  }

  /**
   * @return the ArrayList of available spells.
   */
  public ArrayList<String> getSpells() {
    return spells;
  }

  /**
   * Sets the spells ArrayList to an ArrayList of all the spells that exist in the JSON.
   * Used in initItems() when initializing all the spells upon startup.
   */
  public void setSpells(ArrayList<String> spells) {
    this.spells = spells;
  }
}
