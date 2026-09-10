package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.BossDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Pantalla para asignar una recompensa en ítems que el boss soltará al morir.
 * El jugador coloca los ítems en los 9 slots de su hotbar (los que están abajo
 * de todo, del 1 al 9) mientras esta GUI está abierta -su propio inventario
 * sigue visible y usable abajo- y confirma con el botón. Se toma CUALQUIER
 * ítem presente en cualquiera de los 9 slots del hotbar (con su cantidad
 * completa cada uno) como recompensa.
 */
public class ItemRewardGUI {

    public static final int SLOT_INFO = 13;
    public static final int SLOT_CONFIRMAR = 20;
    public static final int SLOT_SALTAR = 24;

    public static final int SLOT_ATRAS = 18;
    public static final int SLOT_CANCELAR = 22;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.ITEM_REWARD);
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text("Recompensa en ítems", NamedTextColor.LIGHT_PURPLE));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        BossDefinition def = sesion.getDefinicion();
        List<ItemStack> actuales = def.getRecompensaItems();

        inv.setItem(SLOT_INFO, new ItemBuilder(Material.CHEST)
                .nombre("Recompensa en ítems (opcional)", NamedTextColor.YELLOW)
                .lore(List.of(
                        "1. Coloca los ítems que quieras dar",
                        "   de recompensa en los 9 slots de tu",
                        "   hotbar (los de abajo de todo).",
                        "2. Podés usar 1 solo slot o los 9.",
                        "   Se toma lo que haya en CADA slot",
                        "   con su cantidad completa.",
                        "3. Click en Confirmar.",
                        "",
                        actuales.isEmpty()
                                ? "Actual: sin recompensa de ítems"
                                : "Actual: " + actuales.size() + " tipo(s) de ítem asignado(s)"
                ), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_CONFIRMAR, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .nombre("Confirmar ítems del hotbar", NamedTextColor.GREEN)
                .lore(List.of("Toma lo que haya en los 9 slots", "de tu hotbar como recompensa"), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_SALTAR, new ItemBuilder(Material.GRAY_DYE)
                .nombre("No dar recompensa en ítems", NamedTextColor.WHITE)
                .build());

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
    }
}
