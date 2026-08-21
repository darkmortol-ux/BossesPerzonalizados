package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.SpawnConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class RespawnConfigGUI {

    public static final int SLOT_UNA_VEZ = 10;
    public static final int SLOT_CON_RESPAWN = 16;
    public static final int SLOT_MOSTRAR_MINUTOS = 13;
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
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.RESPAWN_CONFIG);
        Inventory inv = Bukkit.createInventory(holder, 45, Component.text("Configura el respawn", NamedTextColor.GOLD));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();

        inv.setItem(SLOT_UNA_VEZ, new ItemBuilder(!cfg.isRespawnHabilitado() ? Material.LIME_DYE : Material.GRAY_DYE)
                .nombre("Aparece una sola vez", !cfg.isRespawnHabilitado() ? NamedTextColor.GREEN : NamedTextColor.WHITE)
                .lore(List.of("El boss no vuelve a aparecer luego de morir"), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_CON_RESPAWN, new ItemBuilder(cfg.isRespawnHabilitado() ? Material.LIME_DYE : Material.GRAY_DYE)
                .nombre("Con respawn", cfg.isRespawnHabilitado() ? NamedTextColor.GREEN : NamedTextColor.WHITE)
                .lore(List.of("El boss vuelve a aparecer cada cierto tiempo"), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_MOSTRAR_MINUTOS, new ItemBuilder(Material.CLOCK)
                .nombre("Intervalo: " + cfg.getRespawnMinutos() + " minutos", NamedTextColor.AQUA)
                .build());

        inv.setItem(SLOT_MAS_1, boton(Material.LIME_STAINED_GLASS_PANE, "+1 minuto"));
        inv.setItem(SLOT_MAS_5, boton(Material.LIME_STAINED_GLASS_PANE, "+5 minutos"));
        inv.setItem(SLOT_MAS_10, boton(Material.LIME_STAINED_GLASS_PANE, "+10 minutos"));
        inv.setItem(SLOT_MENOS_1, boton(Material.RED_STAINED_GLASS_PANE, "-1 minuto"));
        inv.setItem(SLOT_MENOS_5, boton(Material.RED_STAINED_GLASS_PANE, "-5 minutos"));
        inv.setItem(SLOT_MENOS_10, boton(Material.RED_STAINED_GLASS_PANE, "-10 minutos"));

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
    }

    private static org.bukkit.inventory.ItemStack boton(Material mat, String nombre) {
        return new ItemBuilder(mat).nombre(nombre, NamedTextColor.YELLOW).build();
    }
}
