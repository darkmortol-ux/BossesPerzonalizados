package com.darkmortol.bosspersonalizados.ability;

import com.darkmortol.bosspersonalizados.BossPersonalizadosPlugin;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import com.darkmortol.bosspersonalizados.model.BossInstance;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AbilityRunner {

    private final BossPersonalizadosPlugin plugin;
    private final Random random = new Random();

    // bossId -> definicion / entidad / instancia, para revisarlos cada tick de habilidad
    private final Map<Integer, LivingEntity> entidadesActivas = new ConcurrentHashMap<>();
    private final Map<Integer, BossInstance> instanciasActivas = new ConcurrentHashMap<>();

    public AbilityRunner(BossPersonalizadosPlugin plugin) {
        this.plugin = plugin;
    }

    public void iniciar() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::revisarTodos, 60L, 20L);
    }

    public void registrarInstancia(BossDefinition def, LivingEntity entidad, BossInstance instancia) {
        entidadesActivas.put(def.getId(), entidad);
        instanciasActivas.put(def.getId(), instancia);
    }

    public void quitarInstancia(int bossId) {
        entidadesActivas.remove(bossId);
        instanciasActivas.remove(bossId);
    }

    private void revisarTodos() {
        for (Map.Entry<Integer, LivingEntity> entry : entidadesActivas.entrySet()) {
            int bossId = entry.getKey();
            LivingEntity boss = entry.getValue();
            if (boss == null || boss.isDead() || !boss.isValid()) {
                quitarInstancia(bossId);
                continue;
            }
            BossDefinition def = plugin.getStorage().obtener(bossId);
            BossInstance instancia = instanciasActivas.get(bossId);
            if (def == null || instancia == null) continue;

            Player objetivo = jugadorMasCercano(boss, def.getSpawnConfig().getRadioAtaque());
            if (objetivo == null) continue;

            double vidaPorcentaje = boss.getAttribute(Attribute.MAX_HEALTH) != null
                    ? (boss.getHealth() / boss.getAttribute(Attribute.MAX_HEALTH).getValue()) * 100.0 : 100.0;

            // 1) Habilidades por VIDA_BAJA: se disparan una sola vez por umbral cruzado.
            for (AbilityType tipo : def.getHabilidades()) {
                AbilityRegistry.AbilityConfig cfg = AbilityRegistry.get(tipo);
                if (cfg == null || cfg.trigger != AbilityTrigger.VIDA_BAJA) continue;
                int umbral = (int) cfg.triggerValor;
                if (vidaPorcentaje <= umbral && !instancia.yaUsoFase(umbral)) {
                    cfg.ability.ejecutar(boss, objetivo, instancia);
                    instancia.marcarFaseUsada(umbral);
                }
            }

            // 2) Habilidades por cooldown (con o sin requisito de proximidad).
            List<AbilityType> disponibles = new ArrayList<>();
            for (AbilityType tipo : def.getHabilidades()) {
                AbilityRegistry.AbilityConfig cfg = AbilityRegistry.get(tipo);
                if (cfg == null) continue;
                if (cfg.trigger == AbilityTrigger.VIDA_BAJA) continue;
                if (!instancia.estaListaLaHabilidad(tipo)) continue;
                if (cfg.trigger == AbilityTrigger.PROXIMIDAD && boss.getLocation().distance(objetivo.getLocation()) > cfg.triggerValor) continue;
                disponibles.add(tipo);
            }

            if (!disponibles.isEmpty()) {
                AbilityType elegida = disponibles.get(random.nextInt(disponibles.size()));
                AbilityRegistry.AbilityConfig cfg = AbilityRegistry.get(elegida);
                cfg.ability.ejecutar(boss, objetivo, instancia);
                int segundos = cfg.ability.getCooldownMinSegundos()
                        + random.nextInt(Math.max(1, cfg.ability.getCooldownMaxSegundos() - cfg.ability.getCooldownMinSegundos() + 1));
                instancia.marcarUsada(elegida, segundos * 1000L);
            }
        }
    }

    private Player jugadorMasCercano(LivingEntity boss, double radio) {
        Player masCercano = null;
        double distMin = Double.MAX_VALUE;
        for (org.bukkit.entity.Entity e : boss.getNearbyEntities(radio, radio, radio)) {
            if (e instanceof Player p && !p.isDead()) {
                double dist = p.getLocation().distance(boss.getLocation());
                if (dist < distMin) {
                    distMin = dist;
                    masCercano = p;
                }
            }
        }
        return masCercano;
    }
}
