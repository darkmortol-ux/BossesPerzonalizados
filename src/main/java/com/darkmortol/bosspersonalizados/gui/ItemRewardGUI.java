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
 * Pantalla para asignar una recompensa en item que el boss soltará al morir.
 * El jugador coloca el item en el PRIMER slot de su hotbar (slot 1, el más a la
 * izquierda) mientras esta GUI está abierta -su propio inventario sigue visible
 * y usable abajo- y confirma con el botón. Si hay más de 1 unidad en ese slot,
 * se toma el stack completo como recompensa.
 */
public class ItemRewardGUI {

    public static final int SLOT_INFO = 13;
    public static final int SLOT_CONFIRMAR = 20;
    public static final int SLOT_SALTAR = 24;

    public static final int SLOT_ATRAS = 18;
    public static final int SLOT_CANCELAR = 22;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.ITEM_REWARD);
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text("Recompensa en ítem", NamedTextColor.LIGHT_PURPLE));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        BossDefinition def = sesion.getDefinicion();
        ItemStack actual = def.getRecompensaItem();

        inv.setItem(SLOT_INFO, new ItemBuilder(Material.CHEST)
                .nombre("Recompensa en ítem (opcional)", NamedTextColor.YELLOW)
                .lore(List.of(
                        "1. Coloca el ítem en el PRIMER slot",
                        "   de tu hotbar (el más a la izquierda,",
                        "   abajo de todo).",
                        "2. Si pones más de 1 unidad, se toma",
                        "   el stack completo como recompensa.",
                        "3. Click en Confirmar.",
                        "",
                        actual != null
                                ? "Actual: " + actual.getAmount() + "x " + actual.getType().name()
                                : "Actual: sin recompensa de ítem"
                ), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_CONFIRMAR, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .nombre("Confirmar ítem del slot 1", NamedTextColor.GREEN)
                .lore(List.of("Toma lo que haya en el primer slot", "de tu hotbar como recompensa"), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_SALTAR, new ItemBuilder(Material.GRAY_DYE)
                .nombre("No dar recompensa en ítem", NamedTextColor.WHITE)
                .build());

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
    }
}
