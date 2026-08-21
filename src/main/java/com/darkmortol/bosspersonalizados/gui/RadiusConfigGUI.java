package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.SpawnConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RadiusConfigGUI {

    public static final int SLOT_DETECCION_MOSTRAR = 11;
    public static final int SLOT_DETECCION_MAS = 20;
    public static final int SLOT_DETECCION_MENOS = 2;

    public static final int SLOT_ATAQUE_MOSTRAR = 15;
    public static final int SLOT_ATAQUE_MAS = 24;
    public static final int SLOT_ATAQUE_MENOS = 6;

    public static final int SLOT_ATRAS = 36;
    public static final int SLOT_CANCELAR = 40;
    public static final int SLOT_SIGUIENTE = 44;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.RADIUS_CONFIG);
        Inventory inv = Bukkit.createInventory(holder, 45, Component.text("Radios de detección y ataque", NamedTextColor.DARK_AQUA));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();

        inv.setItem(SLOT_DETECCION_MOSTRAR, new ItemBuilder(Material.SPYGLASS)
                .nombre("Radio de detección: " + (int) cfg.getRadioDeteccion() + " bloques", NamedTextColor.AQUA)
                .lore(java.util.List.of("Distancia a la que aparece cuando", "un jugador se acerca"), NamedTextColor.GRAY).build());
        inv.setItem(SLOT_DETECCION_MAS, boton(Material.LIME_STAINED_GLASS_PANE, "+5 bloques"));
        inv.setItem(SLOT_DETECCION_MENOS, boton(Material.RED_STAINED_GLASS_PANE, "-5 bloques"));

        inv.setItem(SLOT_ATAQUE_MOSTRAR, new ItemBuilder(Material.IRON_SWORD)
                .nombre("Radio de ataque: " + (int) cfg.getRadioAtaque() + " bloques", NamedTextColor.RED)
                .lore(java.util.List.of("Distancia máxima a la que", "perseguirá y atacará"), NamedTextColor.GRAY).build());
        inv.setItem(SLOT_ATAQUE_MAS, boton(Material.LIME_STAINED_GLASS_PANE, "+5 bloques"));
        inv.setItem(SLOT_ATAQUE_MENOS, boton(Material.RED_STAINED_GLASS_PANE, "-5 bloques"));

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
    }

    private static ItemStack boton(Material mat, String nombre) {
        return new ItemBuilder(mat).nombre(nombre, NamedTextColor.YELLOW).build();
    }
}
