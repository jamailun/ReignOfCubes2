package fr.jamailun.reignofcubes2.configuration.kits;

import fr.jamailun.reignofcubes2.ReignOfCubes2;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SlotDefinition {

    private final EquipmentSlot equipmentSlot;
    private final int inventorySlot;

    public SlotDefinition(EquipmentSlot equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
        this.inventorySlot = -1;
    }

    public SlotDefinition(int inventorySlot) {
        this.equipmentSlot = null;
        this.inventorySlot = inventorySlot;
    }

    public static SlotDefinition fromString(String string) {
        try {
            EquipmentSlot slot =  EquipmentSlot.valueOf(string);
            return new SlotDefinition(slot);
        } catch (IllegalArgumentException ignored) {}

        try {
            int slot = Integer.parseInt(string);
            return new SlotDefinition(slot);
        } catch (IllegalArgumentException ignored) {}

        ReignOfCubes2.error("Invalid slot definition: '" + string + "'.");
        return null;
    }

    public void equip(PlayerInventory inventory, ItemStack item) {
        if(equipmentSlot != null) {
            inventory.setItem(equipmentSlot, item);
        } else {
            inventory.setItem(inventorySlot, item);
        }
    }

    @Override
    public String toString() {
        if(equipmentSlot != null) {
            return equipmentSlot.name();
        }
        return String.valueOf(inventorySlot);
    }
}
