package textAdventure;

import java.util.ArrayList;

public class Item extends OpenableObject {
  private long weight;
  private String name;
  private boolean isOpenable;
  private Inventory inventory;
  private ArrayList<String> spells;

  /**
   * Item constructor. Initializes items with a passed in weight and name,
   *  and sets isOpenable and inventory to false (by default items aren't openable).
   * @param weight this item's weight.
   * @param name this item's name.
   * This constructor is only used for making keys.
   */
  public Item(long weight, String name) {
    this.weight = weight;
    this.name = name;
    this.isOpenable = false;
    inventory = null;
  }

  /**
   * No arguement Item constructor. This constructor is used for creating most items.
   * It has no arguements because when we create items we use mainly the JSON to handle item attributes.
   */
  public Item() {
  }

  /**
   * @return an ArrayList of all the items that this item (container) is holding.
   */
  public ArrayList<Item> getItems() {
      return inventory.getItems();
  }

  /**
   * @return the Inventory of this item (when applicable, i.e. when it is a container).
   */
  public Inventory getInventory() {
    return inventory;
  }

  /**
   * Sets the inventory of this item (when applicable, i.e. when it is a container).
   * @param inventory the Inventory we are setting this item's inventory to be.
   * Used in initItems() when initializing all items upon startup.
   */
  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  /**
   * Checks if this item is openable. If so, displays its inventory. If not, informs the player that this item is not openable.
   */
  public void open() {
    if (!isOpenable)
      System.out.println("The " + name + " cannot be opened.");
    else {
      System.out.println(inventory.viewInventory());
    }
  }

  /**
   * @return the weight of this item.
   */
  public long getWeight() {
    return weight;
  }

  /**
   * Sets the weight of this item.
   * @param weight the weight we are setting this item's weight to be.
   * Used in initItems() when initializing all items upon startup.
   */
  public void setWeight(long weight) {
    this.weight = weight;
  }

  /**
   * @return the name of this item.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this item.
   * @param name the name we are setting this item's name to be.
   * Used in initItems() when initializing all items upon startup.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return whether or not this item is openable.
   */
  public boolean isOpenable() {
    return isOpenable;
  }

  /**
   * Sets whether this item is openable or not.
   * @param isOpenable the boolean defining whether or not this item is openable.
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
   * Sets spells to an ArrayList of all the spells that exist in the JSON.
   * @param spells the list of spells in this game.
   * Used in initItems() when initializing all the spells upon startup.
   */
  public void setSpells(ArrayList<String> spells) {
    this.spells = spells;
  }
}
