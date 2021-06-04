package textAdventure;

import java.util.ArrayList;

public class Character {
    private Inventory inventory;
    private String description;
    private String name;

    /**
     * initializes the new character's inventory, description, and name
     * @param inventory the arraylist of items, the max weight that the character can hold, and the weight of the items currently in the character's inventory
     * @param description the description of the character
     * @param name the name of the character
     */
    public Character(Inventory inventory, String description, String name) {
        this.inventory = inventory;
        this.description = description;
        this.name = name;
    }

    /**
     * initializes the new character's inventory
     * @param inventory the arraylist of items, the max weight that the character can hold, and the weight of the items currently in the character's inventory
     */
    public Character(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * gets the arraylist of items that is in the character's inventory
     * @return the arraylist of items that is in the character's inventory
     */
    public ArrayList<Item> getItems() {
        return inventory.getItems();
    }

    /**
     * gets the character's inventory
     * @return the character's inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * sets the character's inventory with the specified inventory
     * @param inventory the specified inventory that will become the character's inventory
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * gets the character's description
     * @return the character's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets the character's description with the specified description
     * @param description the specified description that will become the character's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * gets the character's name
     * @return the character's name
     */
    public String getName() {
        return name;
    }

    /**
     * sets the character's name with the specified name
     * @param name the specified name that will become the character's name
     */
    public void setName(String name) {
        this.name = name;
    }
}
