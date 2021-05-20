package textAdventure;

import java.util.ArrayList;

public class Inventory {
  private ArrayList<Item> items;
  private long maxWeight;
  private long currentWeight;

  public Inventory(long maxWeight) {
    this.items = new ArrayList<Item>();
    this.maxWeight = maxWeight;
    this.currentWeight = 0;
  }

  public long getMaxWeight() {
    return maxWeight;
  }

  public void updateMaxWeight(int maxWeight){
    this.maxWeight = maxWeight;
  }

  public long getCurrentWeight() {
    return currentWeight;
  }

  public ArrayList<Item> getItems() {
    return items;
  }

  public boolean addItem(Item item) {
    if (item.getWeight() + currentWeight <= maxWeight) {
      currentWeight += item.getWeight();
      return items.add(item);
    } else {
      System.out.println("There is no room to add the item.");
      return false;
    }
  }

  public boolean removeItem(Item item) {
    /*if (items.size() > 0) 
      return items.remove(item);
    else {
      System.out.println("You do not have this item.");
      return false;
    }*/
    currentWeight -= item.getWeight();
    return items.remove(item);
  }

  public void viewInventory() {
    System.out.print("You see: ");
    for (Item item : items) {
      System.out.print(item.getName() + " ");
    }
    System.out.println();
  }
}
