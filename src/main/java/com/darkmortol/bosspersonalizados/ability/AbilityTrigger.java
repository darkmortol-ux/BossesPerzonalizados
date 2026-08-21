package com.darkmortol.bosspersonalizados.ability;

public enum AbilityTrigger {
    /** Se activa por cooldown aleatorio, sin condicion especial. */
    COOLDOWN,
    /** Se activa (saltando el cooldown normal) al cruzar cierto % de vida hacia abajo. */
    VIDA_BAJA,
    /** Solo puede activarse por cooldown si hay un jugador muy cerca. */
    PROXIMIDAD
}
