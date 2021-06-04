package textAdventure;

import java.util.ArrayList;

public class Inventory {
  private ArrayList<Item> items;
  private long maxWeight;
  private long currentWeight;

  /**
   * initializes the new inventory's arraylist of items in the inventory, the max weight that the inventory can hold, and the current weight (the weight of all the items) of the inventory
   * @param maxWeight the max weight that the inventory can hold
   */
  public Inventory(long maxWeight) {
    this.items = new ArrayList<Item>();
    this.maxWeight = maxWeight;
    this.currentWeight = 0;
  }

  /**
   * returns the max weight that the inventory that calls it can hold
   * @return the max weight that the inventory that calls it can hold
   */
  public long getMaxWeight() {
    return maxWeight;
  }

  /**
   * sets the max weight that the inventory that calls it can hold
   * @param maxWeight the max weight that the inventory that calls it can hold
   */
  public void setMaxWeight(long maxWeight){
    this.maxWeight = maxWeight;
  }

  /**
   * returns the current weight (the weight of all the items in the inventory) of the inventory that calls it
   * @return the current weight (the weight of all the items in the inventory) of the inventory that calls it
   */
  public long getCurrentWeight() {
    return currentWeight;
  }

  /**
   * returns the arraylist of items that is in the inventory that calls it
   * @return the arraylist of items that is in the inventory that calls it
   */
  public ArrayList<Item> getItems() {
    return items;
  }

  /**
   * adds the specified item to the inventory that calls it
   * @param item the specified item
   * @return true if there is enough room in the inventory to hold the item, false if there is not enough room
   */
  public boolean addItem(Item item) {
    if (item.getWeight() + currentWeight <= maxWeight) { // checks if the specified item's weight and the weight of all the other items in the inventory is less than or equal to the max weight of the inventory that calls this method
      currentWeight += item.getWeight(); // updates the current weight of the inventory that calls it by adding the specified item's weight
      return items.add(item);
    } else {
      System.out.println("There is no room to add the item.");
      return false;
    }
  }

  /**
   * removes the specified item from the inventory that calls it
   * @param item the specified item
   * @return true if the item exists in the inventory that calls it, false if the item does not exist in the inventory that calls it
   */
  public boolean removeItem(Item item) {
    currentWeight -= item.getWeight(); // updates the current weight of the inventory that calls it by subtracting the specified item's weight
    return items.remove(item);
  }

  /**
   * returns a string that shows the items, the max weight, and the remaining space left in the inventory that calls it
   * @return a string that shows the items, the max weight, and the remaining space left in the inventory that calls it
   */
  public String viewInventory() {
    String output;
    if (items.size() != 0){ // if there are items in the inventory that calls this method
      output = "You see: ";
      for (Item item : items) {
        output += item.getName() + " "; // creates a new string and adds the names of all the items that are in the inventory that calls this method
      }
  } else {
    output = "There is nothing in your inventory.";
  }
    return output + "\nYou can hold " + maxWeight + " pounds worth of items, or " + (maxWeight - currentWeight) + " more pound(s) worth of items.";
  }
}
