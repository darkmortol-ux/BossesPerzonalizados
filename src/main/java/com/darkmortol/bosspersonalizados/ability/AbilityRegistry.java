package com.darkmortol.bosspersonalizados.ability;

import com.darkmortol.bosspersonalizados.model.BossInstance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Registro central: por cada AbilityType define su logica de ejecucion (BossAbility),
 * su cooldown en segundos y su tipo de disparo (trigger).
 */
public final class AbilityRegistry {

    private static final Random RANDOM = new Random();

    public static class AbilityConfig {
        public final BossAbility ability;
        public final AbilityTrigger trigger;
        public final double triggerValor; // % de vida para VIDA_BAJA, bloques para PROXIMIDAD

        AbilityConfig(BossAbility ability, AbilityTrigger trigger, double triggerValor) {
            this.ability = ability;
            this.trigger = trigger;
            this.triggerValor = triggerValor;
        }
    }

    private static final Map<AbilityType, AbilityConfig> REGISTRO = new EnumMap<>(AbilityType.class);

    private AbilityRegistry() {}

    public static AbilityConfig get(AbilityType tipo) {
        return REGISTRO.get(tipo);
    }

    public static Map<AbilityType, AbilityConfig> all() {
        return REGISTRO;
    }

    private static void reg(AbilityType tipo, int cdMin, int cdMax, AbilityTrigger trigger, double triggerValor, Ejecutor logica) {
        BossAbility ability = new BossAbility() {
            @Override public AbilityType getType() { return tipo; }
            @Override public int getCooldownMinSegundos() { return cdMin; }
            @Override public int getCooldownMaxSegundos() { return cdMax; }
            @Override public void ejecutar(LivingEntity boss, Player objetivo, BossInstance instancia) {
                logica.run(boss, objetivo, instancia);
            }
        };
        REGISTRO.put(tipo, new AbilityConfig(ability, trigger, triggerValor));
    }

    @FunctionalInterface
    private interface Ejecutor {
        void run(LivingEntity boss, Player objetivo, BossInstance instancia);
    }

    static {
        // ===== OFENSIVAS =====
        reg(AbilityType.GOLPE_PESADO, 8, 15, AbilityTrigger.PROXIMIDAD, 3.0, (boss, obj, inst) -> {
            obj.damage(boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE) != null
                    ? boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE).getValue() * 0.5 : 3.0, boss);
            Vector empuje = obj.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize().setY(0.4);
            obj.setVelocity(empuje.multiply(1.4));
        });

        reg(AbilityType.EMBESTIDA, 10, 20, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            Vector direccion = obj.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize();
            boss.setVelocity(direccion.multiply(1.8).setY(0.3));
        });

        reg(AbilityType.LLUVIA_FLECHAS, 6, 12, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            World w = boss.getWorld();
            for (int i = 0; i < 5; i++) {
                Location origen = boss.getEyeLocation().add(RANDOM.nextDouble() * 4 - 2, 4, RANDOM.nextDouble() * 4 - 2);
                org.bukkit.entity.Arrow flecha = w.spawn(origen, org.bukkit.entity.Arrow.class);
                Vector hacia = obj.getLocation().toVector().subtract(origen.toVector()).normalize();
                flecha.setVelocity(hacia.multiply(1.3));
                flecha.setShooter(boss);
            }
        });

        reg(AbilityType.ONDA_CHOQUE, 12, 20, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            for (Entity e : boss.getNearbyEntities(4, 3, 4)) {
                if (e instanceof Player p) {
                    p.damage(4.0, boss);
                    Vector empuje = p.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize().setY(0.5);
                    p.setVelocity(empuje.multiply(1.3));
                }
            }
        });

        reg(AbilityType.CRITICO_GARANTIZADO, 15, 25, AbilityTrigger.PROXIMIDAD, 4.0, (boss, obj, inst) -> {
            obj.damage(boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE) != null
                    ? boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE).getValue() * 2.0 : 6.0, boss);
        });

        reg(AbilityType.VENENO_GOLPE, 10, 18, AbilityTrigger.PROXIMIDAD, 3.0, (boss, obj, inst) ->
                obj.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1)));

        reg(AbilityType.DEBILITAR, 10, 18, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                obj.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 1)));

        reg(AbilityType.LENTITUD_AREA, 12, 20, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            for (Entity e : boss.getNearbyEntities(6, 4, 6)) {
                if (e instanceof Player p) p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
            }
        });

        reg(AbilityType.EXPLOSION_CONTROLADA, 15, 25, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                boss.getWorld().createExplosion(boss.getLocation(), 2.0f, false, false));

        reg(AbilityType.RAYO, 12, 22, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            boss.getWorld().strikeLightningEffect(obj.getLocation());
            obj.damage(5.0, boss);
        });

        // ===== DEFENSIVAS =====
        reg(AbilityType.ESCUDO_TEMPORAL, 20, 30, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                boss.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 1)));

        reg(AbilityType.REGENERACION, 20, 30, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                boss.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1)));

        reg(AbilityType.INMUNIDAD_TEMPORAL, 25, 35, AbilityTrigger.VIDA_BAJA, 25, (boss, obj, inst) ->
                boss.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 4)));

        reg(AbilityType.REFLEJO_DANO, 20, 30, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            // El reflejo real de daño se maneja en el listener de EntityDamageByEntityEvent.
            boss.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 0));
        });

        reg(AbilityType.TELETRANSPORTE_HUIDA, 30, 30, AbilityTrigger.VIDA_BAJA, 20, (boss, obj, inst) -> {
            Vector lejos = boss.getLocation().toVector().subtract(obj.getLocation().toVector()).normalize();
            Location destino = boss.getLocation().add(lejos.multiply(8));
            destino.setY(boss.getWorld().getHighestBlockYAt(destino) + 1);
            boss.teleport(destino);
        });

        reg(AbilityType.ABSORCION, 20, 30, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                boss.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1)));

        reg(AbilityType.FURIA, 999, 999, AbilityTrigger.VIDA_BAJA, 30, (boss, obj, inst) -> {
            boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            boss.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1));
        });

        reg(AbilityType.FASE_INVULNERABLE, 999, 999, AbilityTrigger.VIDA_BAJA, 50, (boss, obj, inst) ->
                boss.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 4)));

        // ===== INVOCACION =====
        reg(AbilityType.CREAR_SUBDITOS, 30, 45, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                invocarMobs(boss, 2, EntityType.ZOMBIE));

        reg(AbilityType.INVOCAR_OLEADA, 40, 60, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                invocarMobs(boss, 4, EntityType.SKELETON));

        reg(AbilityType.CLON_FANTASMA, 45, 60, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            List<Entity> clones = invocarMobs(boss, 1, boss.getType());
            for (Entity e : clones) {
                if (e instanceof LivingEntity le && le.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) != null) {
                    double mitad = boss.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getBaseValue() * 0.4;
                    le.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(mitad);
                    le.setHealth(Math.min(mitad, le.getHealth()));
                }
            }
        });

        reg(AbilityType.REFUERZOS_FASE, 999, 999, AbilityTrigger.VIDA_BAJA, 50, (boss, obj, inst) ->
                invocarMobs(boss, 3, EntityType.ZOMBIE));

        reg(AbilityType.TORRETA, 40, 60, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                invocarMobs(boss, 1, EntityType.PILLAGER));

        reg(AbilityType.FAMILIAR_GUARDIAN, 60, 90, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                invocarMobs(boss, 1, EntityType.IRON_GOLEM));

        // ===== CONTROL / UTILIDAD =====
        reg(AbilityType.EMPUJE_AREA, 12, 20, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            for (Entity e : boss.getNearbyEntities(5, 3, 5)) {
                if (e instanceof Player p) {
                    Vector empuje = p.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize().setY(0.6);
                    p.setVelocity(empuje.multiply(2.0));
                }
            }
        });

        reg(AbilityType.CEGUERA, 15, 25, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                obj.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0)));

        reg(AbilityType.CONFUSION, 15, 25, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                obj.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 150, 0)));

        reg(AbilityType.SALTO_REPENTINO, 10, 18, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            Vector direccion = obj.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize();
            boss.setVelocity(direccion.multiply(1.2).setY(0.7));
        });

        reg(AbilityType.GRITO_ALARMA, 30, 45, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) -> {
            for (Entity e : boss.getNearbyEntities(15, 6, 15)) {
                if (e instanceof Mob m && m != boss) {
                    m.setTarget(obj);
                }
            }
        });

        reg(AbilityType.MARCA_OBJETIVO, 20, 30, AbilityTrigger.COOLDOWN, 0, (boss, obj, inst) ->
                obj.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0)));
    }

    private static List<Entity> invocarMobs(LivingEntity boss, int cantidad, EntityType tipo) {
        World w = boss.getWorld();
        java.util.ArrayList<Entity> creados = new java.util.ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Location loc = boss.getLocation().clone().add(RANDOM.nextDouble() * 4 - 2, 0, RANDOM.nextDouble() * 4 - 2);
            creados.add(w.spawnEntity(loc, tipo));
        }
        return creados;
    }
}
