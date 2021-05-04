package textAdventure;

import java.util.ArrayList;

public class Character {
    private Inventory inventory;
    private String description;
    private String name;

    public Character(Inventory inventory, String description, String name) {
        this.inventory = inventory;
        this.description = description;
        this.name = name;
    }

    public ArrayList<Item> getItems() {
        return inventory.getItems();
    }

    public Character(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
