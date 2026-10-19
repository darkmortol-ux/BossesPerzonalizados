package com.darkmortol.bosspersonalizados.model;

import com.darkmortol.bosspersonalizados.ability.AbilityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Estado en vivo de un boss ya spawneado en el mundo. */
public class BossInstance {

    private final int bossId;
    private final UUID entityUuid;
    private final Map<AbilityType, Long> proximoUsoMillis = new HashMap<>();
    private final Set<Integer> fasesUsadas = new HashSet<>(); // umbrales de % de vida ya disparados

    // True mientras el boss esta "congelado" en su punto de aparicion (sin IA, invulnerable)
    // porque el jugador que lo atacaba murio o se alejo mas del radio de ataque.
    private boolean congelado = false;

    public BossInstance(int bossId, UUID entityUuid) {
        this.bossId = bossId;
        this.entityUuid = entityUuid;
    }

    public int getBossId() { return bossId; }
    public UUID getEntityUuid() { return entityUuid; }

    public boolean estaListaLaHabilidad(AbilityType tipo) {
        long ahora = System.currentTimeMillis();
        return ahora >= proximoUsoMillis.getOrDefault(tipo, 0L);
    }

    public void marcarUsada(AbilityType tipo, long cooldownMs) {
        proximoUsoMillis.put(tipo, System.currentTimeMillis() + cooldownMs);
    }

    public boolean yaUsoFase(int umbralPorcentaje) {
        return fasesUsadas.contains(umbralPorcentaje);
    }

    public void marcarFaseUsada(int umbralPorcentaje) {
        fasesUsadas.add(umbralPorcentaje);
    }

    public boolean isCongelado() { return congelado; }
    public void setCongelado(boolean congelado) { this.congelado = congelado; }
}
