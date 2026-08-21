package com.darkmortol.bosspersonalizados.ability;

import com.darkmortol.bosspersonalizados.model.BossInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/** Logica de ejecucion de una habilidad concreta. */
public interface BossAbility {

    AbilityType getType();

    /** Cooldown minimo/maximo en segundos entre usos (se elige aleatorio dentro del rango). */
    int getCooldownMinSegundos();
    int getCooldownMaxSegundos();

    /** Ejecuta la habilidad sobre el boss sobre un objetivo cercano. */
    void ejecutar(LivingEntity boss, Player objetivo, BossInstance instancia);
}
