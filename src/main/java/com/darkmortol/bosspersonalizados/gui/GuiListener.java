package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.BossKeys;
import com.darkmortol.bosspersonalizados.BossPersonalizadosPlugin;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import com.darkmortol.bosspersonalizados.model.SpawnConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GuiListener implements Listener {

    private final BossPersonalizadosPlugin plugin;

    public GuiListener(BossPersonalizadosPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof BossGuiHolder holder)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player)) return;

        WizardSession sesion = WizardSession.get(player.getUniqueId());
        if (sesion == null) { player.closeInventory(); return; }

        ItemStack clicked = e.getCurrentItem();
        int slot = e.getRawSlot();

        switch (holder.getTipo()) {
            case MOB_SELECTION -> manejarMob(player, sesion, clicked, slot);
            case ABILITY_SELECTION -> manejarHabilidades(player, sesion, clicked, slot, e);
            case ARMOR_SELECTION -> manejarArmadura(player, sesion, slot, e);
            case ENCHANT_SELECTION -> manejarEncantamientos(player, sesion, clicked, slot, e);
            case RESPAWN_CONFIG -> manejarRespawn(player, sesion, slot, e);
            case RADIUS_CONFIG -> manejarRadios(player, sesion, slot, e);
            case GRACE_TIME -> manejarTiempoGracia(player, sesion, slot, e);
            case COIN_REWARD -> manejarMonedas(player, sesion, slot, e);
            case ITEM_REWARD -> manejarItemRecompensa(player, sesion, slot, e);
        }
    }

    // ===== PASO 1: MOB =====
    private void manejarMob(Player player, WizardSession sesion, ItemStack clicked, int slot) {
        if (slot == 49) { cancelar(player, sesion); return; }
        if (clicked == null || clicked.getItemMeta() == null) return;
        String tipoStr = clicked.getItemMeta().getPersistentDataContainer().get(BossKeys.ENTITY_TYPE, PersistentDataType.STRING);
        if (tipoStr == null) return;
        sesion.getDefinicion().setTipoMob(EntityType.valueOf(tipoStr));
        sesion.setPaso(WizardSession.Paso.HABILIDADES);
        player.openInventory(AbilitySelectionGUI.build(sesion));
    }

    // ===== PASO 2: HABILIDADES =====
    private void manejarHabilidades(Player player, WizardSession sesion, ItemStack clicked, int slot, InventoryClickEvent e) {
        if (slot == 48) { player.openInventory(MobSelectionGUI.build()); sesion.setPaso(WizardSession.Paso.NOMBRE_MOB); return; }
        if (slot == 49) { cancelar(player, sesion); return; }
        if (slot == 50) {
            sesion.setPaso(WizardSession.Paso.ARMADURA);
            player.openInventory(ArmorSelectionGUI.build(sesion));
            return;
        }
        if (clicked == null || clicked.getItemMeta() == null) return;
        String habStr = clicked.getItemMeta().getPersistentDataContainer().get(BossKeys.ABILITY_TYPE, PersistentDataType.STRING);
        if (habStr == null) return;
        var tipo = com.darkmortol.bosspersonalizados.ability.AbilityType.valueOf(habStr);
        var set = sesion.getDefinicion().getHabilidades();
        if (!set.add(tipo)) set.remove(tipo);
        e.getInventory().setContents(AbilitySelectionGUI.build(sesion).getContents());
    }

    // ===== PASO 3: ARMADURA =====
    private void manejarArmadura(Player player, WizardSession sesion, int slot, InventoryClickEvent e) {
        BossDefinition def = sesion.getDefinicion();
        boolean izquierda = e.getClick() == ClickType.LEFT;

        if (slot == ArmorSelectionGUI.SLOT_ATRAS) { player.openInventory(AbilitySelectionGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.HABILIDADES); return; }
        if (slot == ArmorSelectionGUI.SLOT_CANCELAR) { cancelar(player, sesion); return; }
        if (slot == ArmorSelectionGUI.SLOT_SIGUIENTE) {
            List<BossDefinition.Slot> equipadas = EnchantSelectionGUI.piezasEquipadas(def);
            if (equipadas.isEmpty()) {
                sesion.setPaso(WizardSession.Paso.RESPAWN);
                player.openInventory(RespawnConfigGUI.build(sesion));
            } else {
                sesion.setSlotEnEdicion(equipadas.get(0));
                sesion.setPaso(WizardSession.Paso.ENCANTAMIENTOS);
                player.openInventory(EnchantSelectionGUI.build(sesion));
            }
            return;
        }

        BossDefinition.Slot slotArmadura = switch (slot) {
            case ArmorSelectionGUI.SLOT_CASCO -> BossDefinition.Slot.CASCO;
            case ArmorSelectionGUI.SLOT_PECHO -> BossDefinition.Slot.PECHO;
            case ArmorSelectionGUI.SLOT_PIERNAS -> BossDefinition.Slot.PIERNAS;
            case ArmorSelectionGUI.SLOT_BOTAS -> BossDefinition.Slot.BOTAS;
            default -> null;
        };

        if (slotArmadura != null) {
            String sufijo = switch (slotArmadura) {
                case CASCO -> "_HELMET";
                case PECHO -> "_CHESTPLATE";
                case PIERNAS -> "_LEGGINGS";
                case BOTAS -> "_BOOTS";
                default -> "";
            };
            var pieza = def.getPieza(slotArmadura);
            String actual = pieza.isVacio() ? "NONE" : pieza.getMaterial().replace(sufijo, "");
            String nuevo = izquierda ? ArmorSelectionGUI.siguienteTier(actual, ArmorSelectionGUI.TIERS_ARMADURA)
                                      : ArmorSelectionGUI.anteriorTier(actual, ArmorSelectionGUI.TIERS_ARMADURA);
            pieza.setMaterial(nuevo.equals("NONE") ? null : nuevo + sufijo);
            ArmorSelectionGUI.refrescar(e.getInventory(), sesion);
            return;
        }

        if (slot == ArmorSelectionGUI.SLOT_TIPO_ARMA) {
            var arma = def.getPieza(BossDefinition.Slot.ARMA);
            String actual = arma.isVacio() ? "NONE" : (arma.getMaterial().endsWith("_AXE") ? "AXE" : "SWORD");
            String[] tipos = {"NONE", "SWORD", "AXE"};
            int idx = java.util.Arrays.asList(tipos).indexOf(actual);
            String nuevo = tipos[(idx + 1) % tipos.length];
            String tierActual = arma.isVacio() ? "IRON" : arma.getMaterial().replace("_AXE", "").replace("_SWORD", "");
            arma.setMaterial(nuevo.equals("NONE") ? null : tierActual + (nuevo.equals("AXE") ? "_AXE" : "_SWORD"));
            ArmorSelectionGUI.refrescar(e.getInventory(), sesion);
            return;
        }

        if (slot == ArmorSelectionGUI.SLOT_MATERIAL_ARMA) {
            var arma = def.getPieza(BossDefinition.Slot.ARMA);
            if (arma.isVacio()) return;
            boolean esHacha = arma.getMaterial().endsWith("_AXE");
            String tierActual = arma.getMaterial().replace("_AXE", "").replace("_SWORD", "");
            String nuevoTier = izquierda ? ArmorSelectionGUI.siguienteTier(tierActual, ArmorSelectionGUI.TIERS_ARMA)
                                          : ArmorSelectionGUI.anteriorTier(tierActual, ArmorSelectionGUI.TIERS_ARMA);
            arma.setMaterial(nuevoTier + (esHacha ? "_AXE" : "_SWORD"));
            ArmorSelectionGUI.refrescar(e.getInventory(), sesion);
        }
    }

    // ===== PASO 4: ENCANTAMIENTOS =====
    private void manejarEncantamientos(Player player, WizardSession sesion, ItemStack clicked, int slot, InventoryClickEvent e) {
        BossDefinition def = sesion.getDefinicion();

        if (slot == EnchantSelectionGUI.SLOT_ATRAS) { player.openInventory(ArmorSelectionGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.ARMADURA); return; }
        if (slot == EnchantSelectionGUI.SLOT_PIEZA_SIGUIENTE) {
            List<BossDefinition.Slot> equipadas = EnchantSelectionGUI.piezasEquipadas(def);
            int idx = equipadas.indexOf(sesion.getSlotEnEdicion());
            sesion.setSlotEnEdicion(equipadas.get((idx + 1) % equipadas.size()));
            player.openInventory(EnchantSelectionGUI.build(sesion));
            return;
        }
        if (slot == EnchantSelectionGUI.SLOT_SIGUIENTE_PASO) {
            sesion.setPaso(WizardSession.Paso.RESPAWN);
            player.openInventory(RespawnConfigGUI.build(sesion));
            return;
        }
        if (clicked == null || clicked.getItemMeta() == null) return;
        ItemMeta meta = clicked.getItemMeta();
        String key = meta.getPersistentDataContainer().get(BossKeys.ENCHANT_KEY, PersistentDataType.STRING);
        if (key == null) return;
        int maxNivel = meta.getPersistentDataContainer().getOrDefault(BossKeys.MATERIAL_INDEX, PersistentDataType.INTEGER, 1);

        var pieza = def.getPieza(sesion.getSlotEnEdicion());
        int nivelActual = pieza.getEncantamientos().getOrDefault(key, 0);
        boolean izquierda = e.getClick() == ClickType.LEFT;
        int nuevoNivel = izquierda ? Math.min(maxNivel, nivelActual + 1) : Math.max(0, nivelActual - 1);
        pieza.setEncantamiento(key, nuevoNivel);
        EnchantSelectionGUI.refrescar(e.getInventory(), sesion);
    }

    // ===== PASO 5: RESPAWN =====
    private void manejarRespawn(Player player, WizardSession sesion, int slot, InventoryClickEvent e) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();

        if (slot == RespawnConfigGUI.SLOT_ATRAS) {
            List<BossDefinition.Slot> equipadas = EnchantSelectionGUI.piezasEquipadas(sesion.getDefinicion());
            if (equipadas.isEmpty()) { player.openInventory(ArmorSelectionGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.ARMADURA); }
            else { sesion.setSlotEnEdicion(equipadas.get(0)); player.openInventory(EnchantSelectionGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.ENCANTAMIENTOS); }
            return;
        }
        if (slot == RespawnConfigGUI.SLOT_CANCELAR) { cancelar(player, sesion); return; }
        if (slot == RespawnConfigGUI.SLOT_SIGUIENTE) {
            sesion.setPaso(WizardSession.Paso.RADIOS);
            player.openInventory(RadiusConfigGUI.build(sesion));
            return;
        }
        if (slot == RespawnConfigGUI.SLOT_UNA_VEZ) cfg.setRespawnHabilitado(false);
        else if (slot == RespawnConfigGUI.SLOT_CON_RESPAWN) cfg.setRespawnHabilitado(true);
        else if (slot == RespawnConfigGUI.SLOT_MAS_1) cfg.setRespawnMinutos(cfg.getRespawnMinutos() + 1);
        else if (slot == RespawnConfigGUI.SLOT_MAS_5) cfg.setRespawnMinutos(cfg.getRespawnMinutos() + 5);
        else if (slot == RespawnConfigGUI.SLOT_MAS_10) cfg.setRespawnMinutos(cfg.getRespawnMinutos() + 10);
        else if (slot == RespawnConfigGUI.SLOT_MENOS_1) cfg.setRespawnMinutos(cfg.getRespawnMinutos() - 1);
        else if (slot == RespawnConfigGUI.SLOT_MENOS_5) cfg.setRespawnMinutos(cfg.getRespawnMinutos() - 5);
        else if (slot == RespawnConfigGUI.SLOT_MENOS_10) cfg.setRespawnMinutos(cfg.getRespawnMinutos() - 10);
        else return;
        RespawnConfigGUI.refrescar(e.getInventory(), sesion);
    }

    // ===== PASO 6: RADIOS =====
    private void manejarRadios(Player player, WizardSession sesion, int slot, InventoryClickEvent e) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();

        if (slot == RadiusConfigGUI.SLOT_ATRAS) { player.openInventory(RespawnConfigGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.RESPAWN); return; }
        if (slot == RadiusConfigGUI.SLOT_CANCELAR) { cancelar(player, sesion); return; }
        if (slot == RadiusConfigGUI.SLOT_SIGUIENTE) {
            sesion.setPaso(WizardSession.Paso.TIEMPO_GRACIA);
            player.openInventory(GraceTimeGUI.build(sesion));
            return;
        }
        if (slot == RadiusConfigGUI.SLOT_DETECCION_MAS) cfg.setRadioDeteccion(cfg.getRadioDeteccion() + 5);
        else if (slot == RadiusConfigGUI.SLOT_DETECCION_MENOS) cfg.setRadioDeteccion(Math.max(1, cfg.getRadioDeteccion() - 5));
        else if (slot == RadiusConfigGUI.SLOT_ATAQUE_MAS) cfg.setRadioAtaque(cfg.getRadioAtaque() + 5);
        else if (slot == RadiusConfigGUI.SLOT_ATAQUE_MENOS) cfg.setRadioAtaque(Math.max(1, cfg.getRadioAtaque() - 5));
        else return;
        RadiusConfigGUI.refrescar(e.getInventory(), sesion);
    }

    // ===== PASO 6.5: TIEMPO DE GRACIA =====
    private void manejarTiempoGracia(Player player, WizardSession sesion, int slot, InventoryClickEvent e) {
        SpawnConfig cfg = sesion.getDefinicion().getSpawnConfig();

        if (slot == GraceTimeGUI.SLOT_ATRAS) { player.openInventory(RadiusConfigGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.RADIOS); return; }
        if (slot == GraceTimeGUI.SLOT_CANCELAR) { cancelar(player, sesion); return; }
        if (slot == GraceTimeGUI.SLOT_SIGUIENTE) {
            sesion.setPaso(WizardSession.Paso.RECOMPENSA_MONEDAS);
            player.openInventory(CoinRewardGUI.build(sesion));
            return;
        }
        if (slot == GraceTimeGUI.SLOT_MAS_30S) cfg.setTiempoGraciaSegundos(cfg.getTiempoGraciaSegundos() + 30);
        else if (slot == GraceTimeGUI.SLOT_MAS_1MIN) cfg.setTiempoGraciaSegundos(cfg.getTiempoGraciaSegundos() + 60);
        else if (slot == GraceTimeGUI.SLOT_MAS_5MIN) cfg.setTiempoGraciaSegundos(cfg.getTiempoGraciaSegundos() + 300);
        else if (slot == GraceTimeGUI.SLOT_MENOS_30S) cfg.setTiempoGraciaSegundos(cfg.getTiempoGraciaSegundos() - 30);
        else if (slot == GraceTimeGUI.SLOT_MENOS_1MIN) cfg.setTiempoGraciaSegundos(cfg.getTiempoGraciaSegundos() - 60);
        else if (slot == GraceTimeGUI.SLOT_MENOS_5MIN) cfg.setTiempoGraciaSegundos(cfg.getTiempoGraciaSegundos() - 300);
        else return;
        GraceTimeGUI.refrescar(e.getInventory(), sesion);
    }

    // ===== PASO 7: RECOMPENSA EN MONEDAS =====
    private void manejarMonedas(Player player, WizardSession sesion, int slot, InventoryClickEvent e) {
        BossDefinition def = sesion.getDefinicion();

        if (slot == CoinRewardGUI.SLOT_ATRAS) { player.openInventory(GraceTimeGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.TIEMPO_GRACIA); return; }
        if (slot == CoinRewardGUI.SLOT_CANCELAR) { cancelar(player, sesion); return; }
        if (slot == CoinRewardGUI.SLOT_SIGUIENTE) {
            sesion.setPaso(WizardSession.Paso.RECOMPENSA_ITEM);
            player.openInventory(ItemRewardGUI.build(sesion));
            return;
        }
        if (slot == CoinRewardGUI.SLOT_MAS) def.setRecompensaMonedas(def.getRecompensaMonedas() + 50);
        else if (slot == CoinRewardGUI.SLOT_MENOS) def.setRecompensaMonedas(def.getRecompensaMonedas() - 50);
        else return;
        CoinRewardGUI.refrescar(e.getInventory(), sesion);
    }

    // ===== PASO 8: RECOMPENSA EN ITEM =====
    private void manejarItemRecompensa(Player player, WizardSession sesion, int slot, InventoryClickEvent e) {
        BossDefinition def = sesion.getDefinicion();

        if (slot == ItemRewardGUI.SLOT_ATRAS) { player.openInventory(CoinRewardGUI.build(sesion)); sesion.setPaso(WizardSession.Paso.RECOMPENSA_MONEDAS); return; }
        if (slot == ItemRewardGUI.SLOT_CANCELAR) { cancelar(player, sesion); return; }

        if (slot == ItemRewardGUI.SLOT_SALTAR) {
            def.limpiarRecompensaItems();
            avanzarAVida(player, sesion);
            return;
        }

        if (slot == ItemRewardGUI.SLOT_CONFIRMAR) {
            // Los 9 slots del hotbar del jugador son los indices 0 a 8.
            List<ItemStack> recompensas = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                ItemStack itemEnSlot = player.getInventory().getItem(i);
                if (itemEnSlot == null || itemEnSlot.getType().isAir()) continue;
                recompensas.add(itemEnSlot.clone());
                player.getInventory().setItem(i, null);
            }

            if (recompensas.isEmpty()) {
                player.sendMessage(Component.text("No hay ningún ítem en tu hotbar.", NamedTextColor.RED));
                return;
            }

            def.setRecompensaItems(recompensas);
            player.sendMessage(Component.text("Recompensa asignada: " + recompensas.size() + " tipo(s) de ítem.", NamedTextColor.GREEN));
            avanzarAVida(player, sesion);
        }
    }

    private void avanzarAVida(Player player, WizardSession sesion) {
        sesion.setPaso(WizardSession.Paso.STATS_VIDA);
        player.closeInventory();
        player.sendMessage(Component.text("Escribe en el chat la VIDA del boss (numero, ej: 40):", NamedTextColor.GOLD));
    }

    private void cancelar(Player player, WizardSession sesion) {
        WizardSession.cancelar(sesion.getJugador());
        player.closeInventory();
        player.sendMessage(Component.text("Creación de boss cancelada.", NamedTextColor.RED));
    }
}
