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
 * Pantalla para configurar cuántos segundos de "gracia" tiene el staff para
 * alejarse del punto de aparición antes de que el boss pueda invocarse.
 */
public class GraceTimeGUI {

    public static final int SLOT_MOSTRAR = 13;

    public static final int SLOT_MAS_30S = 28;
    public static final int SLOT_MAS_1MIN = 29;
    public static final int SLOT_MAS_5MIN = 30;
    public static final int SLOT_MENOS_30S = 32;
    public static final int SLOT_MENOS_1MIN = 33;
    public static final int SLOT_MENOS_5MIN = 34;

    public static final int SLOT_ATRAS = 36;
    public static final int SLOT_CANCELAR = 40;
    public static final int SLOT_SIGUIENTE = 44;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.GRACE_TIME);
        Inventory inv = Bukkit.createInventory(holder, 45, Component.text("Tiempo de gracia", NamedTextColor.AQUA));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();

        inv.setItem(SLOT_MOSTRAR, new ItemBuilder(Material.CLOCK)
                .nombre("Tiempo de gracia: " + formatear(cfg.getTiempoGraciaSegundos()), NamedTextColor.AQUA)
                .lore(List.of(
                        "Tiempo desde que se coloca el huevo",
                        "hasta que el boss puede aparecer,",
                        "para que el staff se aleje primero."
                ), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_MAS_30S, boton(Material.LIME_STAINED_GLASS_PANE, "+30 segundos"));
        inv.setItem(SLOT_MAS_1MIN, boton(Material.LIME_STAINED_GLASS_PANE, "+1 minuto"));
        inv.setItem(SLOT_MAS_5MIN, boton(Material.LIME_STAINED_GLASS_PANE, "+5 minutos"));
        inv.setItem(SLOT_MENOS_30S, boton(Material.RED_STAINED_GLASS_PANE, "-30 segundos"));
        inv.setItem(SLOT_MENOS_1MIN, boton(Material.RED_STAINED_GLASS_PANE, "-1 minuto"));
        inv.setItem(SLOT_MENOS_5MIN, boton(Material.RED_STAINED_GLASS_PANE, "-5 minutos"));

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
    }

    private static String formatear(int segundos) {
        if (segundos <= 0) return "sin gracia (aparece de inmediato)";
        int min = segundos / 60;
        int seg = segundos % 60;
        if (min == 0) return seg + " segundos";
        if (seg == 0) return min + (min == 1 ? " minuto" : " minutos");
        return min + (min == 1 ? " minuto " : " minutos ") + seg + " segundos";
    }

    private static ItemStack boton(Material mat, String nombre) {
        return new ItemBuilder(mat).nombre(nombre, NamedTextColor.YELLOW).build();
    }
}
