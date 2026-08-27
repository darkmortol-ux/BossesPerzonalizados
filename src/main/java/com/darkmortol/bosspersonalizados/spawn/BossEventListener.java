package com.darkmortol.bosspersonalizados.spawn;

import com.darkmortol.bosspersonalizados.BossKeys;
import com.darkmortol.bosspersonalizados.BossPersonalizadosPlugin;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class BossEventListener implements Listener {

    private final BossPersonalizadosPlugin plugin;

    public BossEventListener(BossPersonalizadosPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onColocarHuevo(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = e.getItem();
        if (item == null || item.getItemMeta() == null) return;
        Integer bossId = item.getItemMeta().getPersistentDataContainer().get(BossKeys.SPAWN_EGG_BOSS_ID, PersistentDataType.INTEGER);
        if (bossId == null) return;
        e.setCancelled(true);

        BossDefinition def = plugin.getStorage().obtener(bossId);
        Player player = e.getPlayer();
        if (def == null) {
            player.sendMessage(Component.text("Ese boss ya no existe en el registro.", NamedTextColor.RED));
            return;
        }

        Location loc = e.getClickedBlock().getLocation().add(0.5, 1, 0.5);
        def.getSpawnConfig().ubicar(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
        plugin.getStorage().guardar();

        item.setAmount(item.getAmount() - 1);
        player.sendMessage(Component.text("Punto de aparición creado para '" + def.getNombre() + "' (#" + def.getId() + ").", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onMuerte(EntityDeathEvent e) {
        Integer bossId = e.getEntity().getPersistentDataContainer().get(BossKeys.BOSS_INSTANCE_ID, PersistentDataType.INTEGER);
        if (bossId == null) return;

        BossDefinition def = plugin.getStorage().obtener(bossId);
        if (def != null) {
            Player killer = e.getEntity().getKiller();
            if (killer != null) {
                killer.giveExp(def.getExperiencia());
                plugin.getEconomia().depositar(killer, def.getRecompensaMonedas(), plugin);
            }
            if (def.getRecompensaItem() != null) {
                e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), def.getRecompensaItem().clone());
            }
        }

        plugin.getAbilityRunner().quitarInstancia(bossId);
        plugin.getSpawnPointManager().manejarMuerte(bossId);
    }
}
