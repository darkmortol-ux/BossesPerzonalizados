package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.BossDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Guarda el progreso de un jugador mientras arma un boss paso a paso. */
public class WizardSession {

    public enum Paso { NOMBRE_MOB, HABILIDADES, ARMADURA, ENCANTAMIENTOS, RESPAWN, RADIOS, RECOMPENSA_MONEDAS, RECOMPENSA_ITEM, STATS_VIDA, STATS_DANO, STATS_EXP, FINALIZADO }

    private static final Map<UUID, WizardSession> SESIONES = new HashMap<>();

    private final UUID jugador;
    private final BossDefinition definicion;
    private Paso paso;
    private BossDefinition.Slot slotEnEdicion; // usado en la pantalla de encantamientos

    private WizardSession(UUID jugador, BossDefinition definicion) {
        this.jugador = jugador;
        this.definicion = definicion;
        this.paso = Paso.NOMBRE_MOB;
    }

    public static WizardSession iniciar(UUID jugador, String nombre) {
        WizardSession sesion = new WizardSession(jugador, new BossDefinition(-1, nombre, null));
        SESIONES.put(jugador, sesion);
        return sesion;
    }

    public static WizardSession get(UUID jugador) {
        return SESIONES.get(jugador);
    }

    public static void cancelar(UUID jugador) {
        SESIONES.remove(jugador);
    }

    public BossDefinition getDefinicion() { return definicion; }
    public Paso getPaso() { return paso; }
    public void setPaso(Paso paso) { this.paso = paso; }
    public UUID getJugador() { return jugador; }

    public BossDefinition.Slot getSlotEnEdicion() { return slotEnEdicion; }
    public void setSlotEnEdicion(BossDefinition.Slot slot) { this.slotEnEdicion = slot; }
}
