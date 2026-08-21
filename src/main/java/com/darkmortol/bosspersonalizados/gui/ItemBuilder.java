package com.darkmortol.bosspersonalizados.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder nombre(String texto, NamedTextColor color) {
        meta.displayName(Component.text(texto, color).decoration(TextDecoration.ITALIC, false));
        return this;
    }

    public ItemBuilder lore(List<String> lineas, NamedTextColor color) {
        List<Component> comp = new ArrayList<>();
        for (String linea : lineas) comp.add(Component.text(linea, color).decoration(TextDecoration.ITALIC, false));
        meta.lore(comp);
        return this;
    }

    public ItemBuilder loreLine(String linea, NamedTextColor color) {
        List<Component> actual = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        actual.add(Component.text(linea, color).decoration(TextDecoration.ITALIC, false));
        meta.lore(actual);
        return this;
    }

    public ItemBuilder pdcString(NamespacedKey key, String valor) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, valor);
        return this;
    }

    public ItemBuilder pdcInt(NamespacedKey key, int valor) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, valor);
        return this;
    }

    public ItemBuilder brillo() {
        meta.setEnchantmentGlintOverride(true);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
