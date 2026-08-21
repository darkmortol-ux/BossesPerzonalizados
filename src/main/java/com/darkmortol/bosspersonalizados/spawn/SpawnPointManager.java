package com.darkmortol.bosspersonalizados.spawn;

import com.darkmortol.bosspersonalizados.BossKeys;
import com.darkmortol.bosspersonalizados.BossPersonalizadosPlugin;
import com.darkmortol.bosspersonalizados.model.ArmorPiece;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import com.darkmortol.bosspersonalizados.model.BossInstance;
import com.darkmortol.bosspersonalizados.model.SpawnConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnPointManager {

    private final BossPersonalizadosPlugin plugin;
    private final Map<Integer, BossInstance> instanciasActivas = new HashMap<>();

    public SpawnPointManager(BossPersonalizadosPlugin plugin) {
        this.plugin = plugin;
    }

    public void iniciar() {
        // Revisa cada 20 ticks (1 segundo) si hay jugadores cerca de algun punto de spawn.
        Bukkit.getScheduler().runTaskTimer(plugin, this::revisarTodos, 40L, 20L);
    }

    public Map<Integer, BossInstance> getInstanciasActivas() { return instanciasActivas; }

    private void revisarTodos() {
        for (BossDefinition def : plugin.getStorage().todos().values()) {
            SpawnConfig cfg = def.getSpawnConfig();
            if (!cfg.isUbicado()) continue;
            if (!cfg.puedeRespawnear()) continue;

            World mundo = Bukkit.getWorld(cfg.getMundo());
            if (mundo == null) continue;

            Location spawnLoc = new Location(mundo, cfg.getX(), cfg.getY(), cfg.getZ());
            Player masCercano = null;
            double distanciaMin = Double.MAX_VALUE;
            for (Player p : mundo.getPlayers()) {
                double dist = p.getLocation().distance(spawnLoc);
                if (dist <= cfg.getRadioDeteccion() && dist < distanciaMin) {
                    distanciaMin = dist;
                    masCercano = p;
                }
            }

            if (masCercano != null) {
                spawnear(def, spawnLoc);
            }
        }
    }

    private void spawnear(BossDefinition def, Location loc) {
        LivingEntity entidad = (LivingEntity) loc.getWorld().spawnEntity(loc, def.getTipoMob());
        entidad.customName(Component.text(def.getNombre(), NamedTextColor.DARK_RED));
        entidad.setCustomNameVisible(true);
        entidad.getPersistentDataContainer().set(BossKeys.BOSS_INSTANCE_ID, org.bukkit.persistence.PersistentDataType.INTEGER, def.getId());

        if (entidad.getAttribute(Attribute.MAX_HEALTH) != null) {
            entidad.getAttribute(Attribute.MAX_HEALTH).setBaseValue(def.getVida());
            entidad.setHealth(def.getVida());
        }
        if (entidad.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
            entidad.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(def.getDano());
        }

        EntityEquipment eq = entidad.getEquipment();
        if (eq != null) {
            aplicarPieza(eq, def.getPieza(BossDefinition.Slot.CASCO), "casco");
            aplicarPieza(eq, def.getPieza(BossDefinition.Slot.PECHO), "pecho");
            aplicarPieza(eq, def.getPieza(BossDefinition.Slot.PIERNAS), "piernas");
            aplicarPieza(eq, def.getPieza(BossDefinition.Slot.BOTAS), "botas");
            ArmorPiece arma = def.getPieza(BossDefinition.Slot.ARMA);
            if (!arma.isVacio()) eq.setItemInMainHand(arma.toItemStack());
            eq.setHelmetDropChance(0f);
            eq.setChestplateDropChance(0f);
            eq.setLeggingsDropChance(0f);
            eq.setBootsDropChance(0f);
            eq.setItemInMainHandDropChance(0f);
        }

        BossInstance instancia = new BossInstance(def.getId(), entidad.getUniqueId());
        instanciasActivas.put(def.getId(), instancia);
        def.getSpawnConfig().setVivoActualmente(true);

        plugin.getAbilityRunner().registrarInstancia(def, entidad, instancia);
    }

    private void aplicarPieza(EntityEquipment eq, ArmorPiece pieza, String tipo) {
        if (pieza.isVacio()) return;
        var item = pieza.toItemStack();
        switch (tipo) {
            case "casco" -> eq.setHelmet(item);
            case "pecho" -> eq.setChestplate(item);
            case "piernas" -> eq.setLeggings(item);
            case "botas" -> eq.setBoots(item);
        }
    }

    public void manejarMuerte(int bossId) {
        BossDefinition def = plugin.getStorage().obtener(bossId);
        if (def == null) return;
        SpawnConfig cfg = def.getSpawnConfig();
        cfg.setVivoActualmente(false);
        cfg.setUltimaMuerteMillis(System.currentTimeMillis());
        if (!cfg.isRespawnHabilitado()) cfg.setMuertoParaSiempre(true);
        instanciasActivas.remove(bossId);
        plugin.getStorage().guardar();
    }
}
