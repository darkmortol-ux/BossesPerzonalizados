package com.darkmortol.bosspersonalizados.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class CoinRewardGUI {

    public static final int SLOT_MENOS = 11;
    public static final int SLOT_MOSTRAR = 13;
    public static final int SLOT_MAS = 15;

    public static final int SLOT_ATRAS = 18;
    public static final int SLOT_CANCELAR = 22;
    public static final int SLOT_SIGUIENTE = 26;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.COIN_REWARD);
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text("Recompensa en monedas", NamedTextColor.GOLD));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        double monedas = sesion.getDefinicion().getRecompensaMonedas();

        inv.setItem(SLOT_MENOS, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .nombre("-50 monedas", NamedTextColor.RED).build());

        inv.setItem(SLOT_MOSTRAR, new ItemBuilder(Material.GOLD_INGOT)
                .nombre("Recompensa: " + (long) monedas + " monedas", NamedTextColor.YELLOW)
                .lore(List.of("Se entrega al jugador que mate al boss", "(requiere Vault + un plugin de economía)"), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_MAS, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .nombre("+50 monedas", NamedTextColor.GREEN).build());

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
    }
}
