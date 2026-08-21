package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.BossKeys;
import com.darkmortol.bosspersonalizados.model.ArmorPiece;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantSelectionGUI {

    public static final int SLOT_ATRAS = 45;
    public static final int SLOT_PIEZA_SIGUIENTE = 49;
    public static final int SLOT_SIGUIENTE_PASO = 53;

    /** Devuelve, en orden, los slots que tienen una pieza equipada (para recorrerlos uno por uno). */
    public static List<BossDefinition.Slot> piezasEquipadas(BossDefinition def) {
        List<BossDefinition.Slot> lista = new ArrayList<>();
        for (BossDefinition.Slot slot : BossDefinition.Slot.values()) {
            if (!def.getPieza(slot).isVacio()) lista.add(slot);
        }
        return lista;
    }

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.ENCHANT_SELECTION);
        BossDefinition.Slot slotActual = sesion.getSlotEnEdicion();
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text("Encantar: " + (slotActual != null ? slotActual.name() : ""), NamedTextColor.LIGHT_PURPLE));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        BossDefinition def = sesion.getDefinicion();
        BossDefinition.Slot slotActual = sesion.getSlotEnEdicion();
        if (slotActual == null) return;
        ArmorPiece pieza = def.getPieza(slotActual);
        ItemStack base = pieza.toItemStack();
        if (base == null) return;

        int i = 0;
        for (Enchantment ench : org.bukkit.Registry.ENCHANTMENT) {
            if (!ench.canEnchantItem(new ItemStack(base.getType()))) continue;
            String key = ench.getKey().getKey();
            int nivel = pieza.getEncantamientos().getOrDefault(key, 0);
            inv.setItem(i, new ItemBuilder(nivel > 0 ? Material.ENCHANTED_BOOK : Material.BOOK)
                    .nombre(key + (nivel > 0 ? " (Nivel " + nivel + ")" : ""), nivel > 0 ? NamedTextColor.GREEN : NamedTextColor.WHITE)
                    .lore(List.of("Click izquierdo: subir nivel (máx " + ench.getMaxLevel() + ")", "Click derecho: bajar nivel"), NamedTextColor.GRAY)
                    .pdcString(BossKeys.ENCHANT_KEY, key)
                    .pdcInt(BossKeys.MATERIAL_INDEX, ench.getMaxLevel())
                    .build());
            i++;
            if (i >= 45) break;
        }

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_PIEZA_SIGUIENTE, new ItemBuilder(Material.CHEST).nombre("Siguiente pieza ▶", NamedTextColor.YELLOW).build());
        inv.setItem(SLOT_SIGUIENTE_PASO, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Continuar al Respawn ▶", NamedTextColor.GREEN).build());
    }
}
