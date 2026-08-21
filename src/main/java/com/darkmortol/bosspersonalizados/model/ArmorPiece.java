package com.darkmortol.bosspersonalizados.model;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa una pieza de armadura o el arma del boss: material + encantamientos.
 * material == null significa "ninguna pieza equipada" en ese slot.
 */
public class ArmorPiece {

    private String material; // ej: "DIAMOND_HELMET" o "NETHERITE_SWORD"
    private final Map<String, Integer> encantamientos = new HashMap<>(); // nombre enchant -> nivel

    public ArmorPiece() {}

    public ArmorPiece(String material) {
        this.material = material;
    }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public boolean isVacio() { return material == null || material.equalsIgnoreCase("NONE"); }

    public Map<String, Integer> getEncantamientos() { return encantamientos; }

    public void setEncantamiento(String key, int nivel) {
        if (nivel <= 0) encantamientos.remove(key);
        else encantamientos.put(key, nivel);
    }

    public ItemStack toItemStack() {
        if (isVacio()) return null;
        org.bukkit.Material mat = org.bukkit.Material.matchMaterial(material);
        if (mat == null) return null;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        for (Map.Entry<String, Integer> entry : encantamientos.entrySet()) {
            Enchantment ench = org.bukkit.Registry.ENCHANTMENT.get(org.bukkit.NamespacedKey.minecraft(entry.getKey().toLowerCase()));
            if (ench != null && meta != null) {
                meta.addEnchant(ench, entry.getValue(), true);
            }
        }
        item.setItemMeta(meta);
        return item;
    }
}
