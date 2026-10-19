package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.SpawnConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Pantalla para configurar la cantidad mínima de jugadores que deben estar
 * dentro del radio de detección para que el boss pueda aparecer. Sirve para
 * evitar que un boss pensado para grupo aparezca cuando pasa un solo jugador.
 * Mínimo permitido: 1 jugador (no se puede bajar de ahí).
 */
public class MinPlayersGUI {

    public static final int SLOT_MOSTRAR = 13;

    public static final int SLOT_MAS_1 = 28;
    public static final int SLOT_MAS_5 = 29;
    public static final int SLOT_MAS_10 = 30;
    public static final int SLOT_MENOS_1 = 32;
    public static final int SLOT_MENOS_5 = 33;
    public static final int SLOT_MENOS_10 = 34;

    public static final int SLOT_ATRAS = 36;
    public static final int SLOT_CANCELAR = 40;
    public static final int SLOT_SIGUIENTE = 44;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.MIN_PLAYERS);
        Inventory inv = Bukkit.createInventory(holder, 45, Component.text("Jugadores mínimos para aparecer", NamedTextColor.AQUA));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();
        int cantidad = cfg.getJugadoresMinimos();

        inv.setItem(SLOT_MOSTRAR, new ItemBuilder(Material.PLAYER_HEAD)
                .nombre("Jugadores mínimos: " + cantidad, NamedTextColor.AQUA)
                .lore(List.of(
                        "Cantidad de jugadores que deben estar",
                        "dentro del radio de detección al mismo",
                        "tiempo para que el boss pueda aparecer.",
                        "",
                        "Mínimo: 1 jugador."
                ), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_MAS_1, boton(Material.LIME_STAINED_GLASS_PANE, "+1 jugador"));
        inv.setItem(SLOT_MAS_5, boton(Material.LIME_STAINED_GLASS_PANE, "+5 jugadores"));
        inv.setItem(SLOT_MAS_10, boton(Material.LIME_STAINED_GLASS_PANE, "+10 jugadores"));
        inv.setItem(SLOT_MENOS_1, boton(Material.RED_STAINED_GLASS_PANE, "-1 jugador"));
        inv.setItem(SLOT_MENOS_5, boton(Material.RED_STAINED_GLASS_PANE, "-5 jugadores"));
        inv.setItem(SLOT_MENOS_10, boton(Material.RED_STAINED_GLASS_PANE, "-10 jugadores"));

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
    }

    private static ItemStack boton(Material mat, String nombre) {
        return new ItemBuilder(mat).nombre(nombre, NamedTextColor.YELLOW).build();
    }
}
