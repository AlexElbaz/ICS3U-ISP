package textAdventure;

import java.util.ArrayList;

public class Inventory {
  private ArrayList<Item> items;
  private long maxWeight;
  private long currentWeight;

  /**
   * Initializes a new Inventory with an ArrayList for items to be stored in,
   *  the max weight that this inventory can hold,
   *  and the current weight (the weight of all the items combined) in this inventory.
   * @param maxWeight the max weight that this inventory can hold.
   */
  public Inventory(long maxWeight) {
    this.items = new ArrayList<Item>();
    this.maxWeight = maxWeight;
    this.currentWeight = 0;
  }

  /**
   * @return the max weight that this inventory can hold.
   */
  public long getMaxWeight() {
    return maxWeight;
  }

  /**
   * Sets the max weight that this inventory can hold.
   * @param maxWeight the max weight that this inventory can hold.
   */
  public void setMaxWeight(long maxWeight){
    this.maxWeight = maxWeight;
  }

  /**
   * @return the current weight (the weight of all the items in the inventory combined) of this inventory.
   */
  public long getCurrentWeight() {
    return currentWeight;
  }

  /**
   * @return the ArrayList of items in this inventory.
   */
  public ArrayList<Item> getItems() {
    return items;
  }

  /**
   * Adds a specified Item to this inventory if there is enough space left in this inventory to do so.
   *  If there is not enough space, the specified Item will not be added and the player will be informed.
   * @param item the specified Item to be added to this inventory.
   * @return true if there is enough space in the inventory to hold the item, false if there is not enough space.
   */
  public boolean addItem(Item item) {
    if (item.getWeight() + currentWeight <= maxWeight) {
      // Checks if the specified Item's weight plus the weight of all the other items in this inventory is less
      //  than or equal to the max weight of this inventory (meaning there is enough space to add the specified Item).
      currentWeight += item.getWeight();
        // Updates the current weight of this inventory by adding the specified Item's weight to currentWeight.
      return items.add(item);
    } else {
      System.out.println("There is no room to add the item.");
      return false;
    }
  }

  /**
   * Removes a specified Item from this inventory.
   * @param item the specified Item to be removed from this inventory.
   * @return true if the item exists in this inventory, false if the item does not exist in this inventory.
   */
  public boolean removeItem(Item item) {
    currentWeight -= item.getWeight();
      // Updates the current weight of this inventory by subtracting the specified Item's weight from currentWeight.
    return items.remove(item);
  }

  /**
   * @return a String that displays the items, the max weight, and the remaining space left in this inventory.
   */
  public String viewInventory() {
    String output;
    if (items.size() != 0){ // If there are items in this inventory.
      output = "You have: ";
      for (Item item : items) {
        output += item.getName() + " ";
          // Creates a new output string and adds the names of all the items that are in this inventory to it.
      }
  } else {
    output = "There is nothing in here.";
  }
    return output + "\nYou can hold " + maxWeight + " pounds worth of items, or " + (maxWeight - currentWeight) + " more pound(s) worth of items.";
  }
}
