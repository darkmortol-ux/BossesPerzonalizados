package com.darkmortol.bosspersonalizados.model;

import com.darkmortol.bosspersonalizados.ability.AbilityType;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BossDefinition {

    public enum Slot { CASCO, PECHO, PIERNAS, BOTAS, ARMA }

    private int id;
    private String nombre;
    private EntityType tipoMob;

    private final Set<AbilityType> habilidades = new HashSet<>();
    private final Map<Slot, ArmorPiece> equipo = new EnumMap<>(Slot.class);

    private double vida = 20.0;
    private double dano = 4.0;
    private int experiencia = 10;

    private final SpawnConfig spawnConfig = new SpawnConfig();

    public BossDefinition() {}

    public BossDefinition(int id, String nombre, EntityType tipoMob) {
        this.id = id;
        this.nombre = nombre;
        this.tipoMob = tipoMob;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public EntityType getTipoMob() { return tipoMob; }
    public void setTipoMob(EntityType tipoMob) { this.tipoMob = tipoMob; }

    public Set<AbilityType> getHabilidades() { return habilidades; }

    public Map<Slot, ArmorPiece> getEquipo() { return equipo; }

    public ArmorPiece getPieza(Slot slot) {
        return equipo.computeIfAbsent(slot, s -> new ArmorPiece());
    }

    public double getVida() { return vida; }
    public void setVida(double vida) { this.vida = vida; }

    public double getDano() { return dano; }
    public void setDano(double dano) { this.dano = dano; }

    public int getExperiencia() { return experiencia; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; }

    public SpawnConfig getSpawnConfig() { return spawnConfig; }
}
