package textAdventure;

import java.util.ArrayList;

public class Character {
    private Inventory inventory;
    private String description;
    private String name;

    /**
     * Initializes a new Character with an Inventory, description, and name.
     * @param inventory the ArrayList of items, the max weight that this character can hold,
     *  and the weight of the items currently in this character's inventory.
     * @param description the description of this character.
     * @param name the name of this character.
     */
    public Character(Inventory inventory, String description, String name) {
        this.inventory = inventory;
        this.description = description;
        this.name = name;
    }

    /**
     * Initializes a new Character with an Inventory.
     * @param inventory the ArrayList of items, the max weight that this character can hold,
     *  and the weight of the items currently in this character's inventory.
     */
    public Character(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * @return the ArrayList of items in this character's inventory.
     */
    public ArrayList<Item> getItems() {
        return inventory.getItems();
    }

    /**
     * @return this character's Inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets this character's inventory to the specified Inventory.
     * @param inventory the specified Inventory that will become this character's inventory.
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Gets this character's description.
     * @return this character's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets this character's description to the specified description.
     * @param description the specified description that will become this character's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets this character's name.
     * @return this character's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets this character's name to the specified name.
     * @param name the specified name that will become this character's name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
