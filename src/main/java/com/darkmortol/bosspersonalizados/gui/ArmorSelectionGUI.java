package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.ArmorPiece;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ArmorSelectionGUI {

    // Tiers disponibles para armadura, en orden de ciclo. "NONE" = sin pieza.
    public static final String[] TIERS_ARMADURA = {"NONE", "LEATHER", "IRON", "GOLDEN", "DIAMOND", "NETHERITE"};
    // Tiers disponibles para el arma (espada/hacha). Cuero y cobre no tienen version de arma.
    public static final String[] TIERS_ARMA = {"WOODEN", "STONE", "IRON", "GOLDEN", "DIAMOND", "NETHERITE"};

    public static final int SLOT_CASCO = 10;
    public static final int SLOT_PECHO = 19;
    public static final int SLOT_PIERNAS = 28;
    public static final int SLOT_BOTAS = 37;
    public static final int SLOT_TIPO_ARMA = 13;
    public static final int SLOT_MATERIAL_ARMA = 22;
    public static final int SLOT_ATRAS = 48;
    public static final int SLOT_CANCELAR = 49;
    public static final int SLOT_SIGUIENTE = 50;

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.ARMOR_SELECTION);
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text("Elige la armadura (opcional)", NamedTextColor.BLUE));
        holder.setInventory(inv);
        refrescar(inv, sesion);
        return inv;
    }

    public static void refrescar(Inventory inv, WizardSession sesion) {
        BossDefinition def = sesion.getDefinicion();

        inv.setItem(SLOT_CASCO, piezaItem(def.getPieza(BossDefinition.Slot.CASCO), "Casco", "_HELMET"));
        inv.setItem(SLOT_PECHO, piezaItem(def.getPieza(BossDefinition.Slot.PECHO), "Pechera", "_CHESTPLATE"));
        inv.setItem(SLOT_PIERNAS, piezaItem(def.getPieza(BossDefinition.Slot.PIERNAS), "Pantalones", "_LEGGINGS"));
        inv.setItem(SLOT_BOTAS, piezaItem(def.getPieza(BossDefinition.Slot.BOTAS), "Botas", "_BOOTS"));

        ArmorPiece arma = def.getPieza(BossDefinition.Slot.ARMA);
        String tipoArma = tipoDeArma(arma.getMaterial()); // NONE / SWORD / AXE
        inv.setItem(SLOT_TIPO_ARMA, new ItemBuilder(iconoTipoArma(tipoArma))
                .nombre("Tipo de arma: " + tipoArma, NamedTextColor.GOLD)
                .lore(List.of("Click para cambiar entre Ninguna / Espada / Hacha"), NamedTextColor.GRAY)
                .build());

        String tierArma = tierDeArma(arma.getMaterial());
        inv.setItem(SLOT_MATERIAL_ARMA, new ItemBuilder(tipoArma.equals("NONE") ? Material.GRAY_DYE : Material.matchMaterial(tierArma + (tipoArma.equals("AXE") ? "_AXE" : "_SWORD")))
                .nombre("Material del arma: " + tierArma, NamedTextColor.AQUA)
                .lore(List.of("Click para cambiar el material"), NamedTextColor.GRAY)
                .build());

        inv.setItem(SLOT_ATRAS, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(SLOT_CANCELAR, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(SLOT_SIGUIENTE, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
    }

    private static org.bukkit.inventory.ItemStack piezaItem(ArmorPiece pieza, String nombreSlot, String sufijo) {
        String tier = pieza.isVacio() ? "NONE" : pieza.getMaterial().replace(sufijo, "");
        Material mat = pieza.isVacio() ? Material.GRAY_DYE : Material.matchMaterial(tier + sufijo);
        return new ItemBuilder(mat != null ? mat : Material.GRAY_DYE)
                .nombre(nombreSlot + ": " + tier, NamedTextColor.YELLOW)
                .lore(List.of("Click izquierdo: siguiente material", "Click derecho: material anterior"), NamedTextColor.GRAY)
                .build();
    }

    public static String siguienteTier(String actual, String[] tiers) {
        int idx = indexOf(actual, tiers);
        return tiers[(idx + 1) % tiers.length];
    }

    public static String anteriorTier(String actual, String[] tiers) {
        int idx = indexOf(actual, tiers);
        return tiers[(idx - 1 + tiers.length) % tiers.length];
    }

    private static int indexOf(String actual, String[] tiers) {
        for (int i = 0; i < tiers.length; i++) if (tiers[i].equals(actual)) return i;
        return 0;
    }

    private static String tipoDeArma(String material) {
        if (material == null) return "NONE";
        if (material.endsWith("_AXE")) return "AXE";
        if (material.endsWith("_SWORD")) return "SWORD";
        return "NONE";
    }

    private static String tierDeArma(String material) {
        if (material == null) return "IRON";
        return material.replace("_AXE", "").replace("_SWORD", "");
    }

    private static Material iconoTipoArma(String tipo) {
        return switch (tipo) {
            case "SWORD" -> Material.IRON_SWORD;
            case "AXE" -> Material.IRON_AXE;
            default -> Material.GRAY_DYE;
        };
    }
}
