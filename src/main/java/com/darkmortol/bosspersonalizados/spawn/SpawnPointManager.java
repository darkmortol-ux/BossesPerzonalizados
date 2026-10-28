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

    /**
     * Se ejecuta una sola vez al arrancar el plugin (onEnable). Los mapas de instancias
     * activas de SpawnPointManager y AbilityRunner viven solo en memoria: si el servidor se
     * reinicia o el plugin se recarga mientras un boss estaba vivo, esos mapas quedan vacios
     * aunque la entidad siga existiendo en el mundo (y, si justo estaba congelada, su estado
     * sin IA e invulnerable queda guardado en la entidad y nadie vuelve a tocarlo).
     * Este metodo busca esas entidades "huerfanas" por su marca de bossId, las vuelve a
     * registrar para que el plugin las siga gestionando (habilidades, retorno/congelamiento),
     * y las reactiva por si habian quedado congeladas. Si no encuentra la entidad (se perdio
     * de verdad), libera el punto de spawn para que vuelva a aparecer con normalidad.
     */
    public void recuperarInstanciasActivas() {
        boolean huboCambios = false;
        for (BossDefinition def : plugin.getStorage().todos().values()) {
            SpawnConfig cfg = def.getSpawnConfig();
            if (!cfg.isUbicado() || !cfg.isVivoActualmente()) continue;

            World mundo = Bukkit.getWorld(cfg.getMundo());
            if (mundo == null) continue;

            // Fuerza la carga del chunk del punto de spawn para poder encontrar la entidad.
            mundo.getChunkAt((int) Math.floor(cfg.getX()) >> 4, (int) Math.floor(cfg.getZ()) >> 4).load();

            LivingEntity encontrada = null;
            for (org.bukkit.entity.Entity e : mundo.getEntities()) {
                if (!(e instanceof LivingEntity le)) continue;
                Integer idGuardado = le.getPersistentDataContainer()
                        .get(BossKeys.BOSS_INSTANCE_ID, org.bukkit.persistence.PersistentDataType.INTEGER);
                if (idGuardado != null && idGuardado == def.getId()) { encontrada = le; break; }
            }

            if (encontrada == null) {
                // La entidad ya no existe (se perdio en algun reinicio anterior a esta
                // version): liberamos el punto para que vuelva a aparecer normalmente.
                cfg.setVivoActualmente(false);
                huboCambios = true;
                continue;
            }

            encontrada.setAI(true);
            encontrada.setInvulnerable(false);
            encontrada.setRemoveWhenFarAway(false);

            BossInstance instancia = new BossInstance(def.getId(), encontrada.getUniqueId());
            instanciasActivas.put(def.getId(), instancia);
            plugin.getAbilityRunner().registrarInstancia(def, encontrada, instancia);
        }
        if (huboCambios) plugin.getStorage().guardar();
    }

    private void revisarTodos() {
        for (BossDefinition def : plugin.getStorage().todos().values()) {
            SpawnConfig cfg = def.getSpawnConfig();
            if (!cfg.isUbicado()) continue;
            if (!cfg.puedeRespawnear()) continue;
            if (cfg.enTiempoDeGracia()) continue; // el staff todavía se está alejando

            World mundo = Bukkit.getWorld(cfg.getMundo());
            if (mundo == null) continue;

            Location spawnLoc = new Location(mundo, cfg.getX(), cfg.getY(), cfg.getZ());
            int jugadoresEnRango = 0;
            for (Player p : mundo.getPlayers()) {
                if (p.getLocation().distance(spawnLoc) <= cfg.getRadioDeteccion()) {
                    jugadoresEnRango++;
                }
            }

            if (jugadoresEnRango >= cfg.getJugadoresMinimos()) {
                spawnear(def, spawnLoc);
            }
        }
    }

    private void spawnear(BossDefinition def, Location loc) {
        LivingEntity entidad = (LivingEntity) loc.getWorld().spawnEntity(loc, def.getTipoMob());
        entidad.customName(Component.text(def.getNombre(), NamedTextColor.DARK_RED));
        entidad.setCustomNameVisible(true);
        // Evita que el boss desaparezca solo por el despawn natural de mobs hostiles
        // cuando no hay jugadores cerca (por ejemplo, mientras esta congelado en el spawn).
        entidad.setRemoveWhenFarAway(false);
        entidad.getPersistentDataContainer().set(BossKeys.BOSS_INSTANCE_ID, org.bukkit.persistence.PersistentDataType.INTEGER, def.getId());

        if (entidad.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            entidad.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(def.getVida());
            entidad.setHealth(def.getVida());
        }
        if (entidad.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
            entidad.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(def.getDano());
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

        if (!cfg.isRespawnHabilitado()) {
            // Boss de "una sola vez": al morir se borra automaticamente el punto de aparicion.
            cfg.resetear();
        } else {
            cfg.setVivoActualmente(false);
            cfg.setUltimaMuerteMillis(System.currentTimeMillis());
        }

        instanciasActivas.remove(bossId);
        plugin.getStorage().guardar();
    }

    /** Limpia el seguimiento local de instancia activa (usado al eliminar/despawnear un boss a la fuerza). */
    public void quitarInstanciaActiva(int bossId) {
        instanciasActivas.remove(bossId);
    }
}
