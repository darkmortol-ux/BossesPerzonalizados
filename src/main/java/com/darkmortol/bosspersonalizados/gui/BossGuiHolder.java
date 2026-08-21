package com.darkmortol.bosspersonalizados.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BossGuiHolder implements InventoryHolder {

    public enum GuiType { MOB_SELECTION, ABILITY_SELECTION, ARMOR_SELECTION, ENCHANT_SELECTION, RESPAWN_CONFIG, RADIUS_CONFIG }

    private final GuiType tipo;
    private Inventory inventory;

    public BossGuiHolder(GuiType tipo) {
        this.tipo = tipo;
    }

    public GuiType getTipo() { return tipo; }

    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    @Override
    public Inventory getInventory() { return inventory; }
}
