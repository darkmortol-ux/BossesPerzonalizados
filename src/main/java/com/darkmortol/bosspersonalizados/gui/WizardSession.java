package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.model.BossDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Guarda el progreso de un jugador mientras arma un boss paso a paso (creación o edición). */
public class WizardSession {

    public enum Paso {
        NOMBRE_MOB, HABILIDADES, ARMADURA, ENCANTAMIENTOS, RESPAWN, RADIOS, TIEMPO_GRACIA,
        RECOMPENSA_MONEDAS, RECOMPENSA_ITEM, STATS_VIDA, STATS_DANO, STATS_EXP, FINALIZADO
    }

    private static final Map<UUID, WizardSession> SESIONES = new HashMap<>();

    private final UUID jugador;
    private final BossDefinition definicion;
    private Paso paso;
    private BossDefinition.Slot slotEnEdicion; // usado en la pantalla de encantamientos
    private final boolean edicion; // true = /boss editar (se guarda sobre un boss existente al terminar)

    private WizardSession(UUID jugador, BossDefinition definicion, boolean edicion) {
        this.jugador = jugador;
        this.definicion = definicion;
        this.paso = Paso.NOMBRE_MOB;
        this.edicion = edicion;
    }

    /** Inicia el wizard para crear un boss nuevo. */
    public static WizardSession iniciar(UUID jugador, String nombre) {
        WizardSession sesion = new WizardSession(jugador, new BossDefinition(-1, nombre, null), false);
        SESIONES.put(jugador, sesion);
        return sesion;
    }

    /**
     * Inicia el wizard para editar un boss existente. Se trabaja sobre una COPIA
     * de la definición: si el jugador cancela, el boss original en el registro
     * queda intacto. Solo se sobreescribe al terminar el wizard.
     */
    public static WizardSession editar(UUID jugador, BossDefinition definicionExistente) {
        BossDefinition copia = BossDefinition.copiar(definicionExistente);
        WizardSession sesion = new WizardSession(jugador, copia, true);
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
    public boolean isEdicion() { return edicion; }

    public BossDefinition.Slot getSlotEnEdicion() { return slotEnEdicion; }
    public void setSlotEnEdicion(BossDefinition.Slot slot) { this.slotEnEdicion = slot; }
}
