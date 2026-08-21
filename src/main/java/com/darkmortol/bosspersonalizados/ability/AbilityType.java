package com.darkmortol.bosspersonalizados.ability;

import org.bukkit.Material;

/**
 * Las 30 habilidades disponibles para configurar en un boss.
 * displayMaterial se usa como icono en la GUI de seleccion.
 */
public enum AbilityType {

    // ===== OFENSIVAS =====
    GOLPE_PESADO(Categoria.OFENSIVA, Material.IRON_SWORD, "Golpe Pesado", "Daño extra y empuje al golpear de cerca"),
    EMBESTIDA(Categoria.OFENSIVA, Material.WARPED_FUNGUS_ON_A_STICK, "Embestida", "Carga hacia el jugador mas cercano"),
    LLUVIA_FLECHAS(Categoria.OFENSIVA, Material.ARROW, "Lluvia de Flechas", "Dispara varias flechas en area"),
    ONDA_CHOQUE(Categoria.OFENSIVA, Material.ANVIL, "Onda de Choque", "Daño en area alrededor del boss"),
    CRITICO_GARANTIZADO(Categoria.OFENSIVA, Material.NETHERITE_SWORD, "Golpe Crítico", "El siguiente golpe hace el doble de daño"),
    VENENO_GOLPE(Categoria.OFENSIVA, Material.SPIDER_EYE, "Veneno en el Golpe", "Aplica Veneno al golpear"),
    DEBILITAR(Categoria.OFENSIVA, Material.POTION, "Debilitar", "Aplica Debilidad al jugador cercano"),
    LENTITUD_AREA(Categoria.OFENSIVA, Material.SOUL_SAND, "Lentitud en Área", "Aplica Lentitud en un radio"),
    EXPLOSION_CONTROLADA(Categoria.OFENSIVA, Material.TNT, "Explosión Controlada", "Explosión sin romper bloques"),
    RAYO(Categoria.OFENSIVA, Material.TRIDENT, "Rayo", "Invoca un rayo sobre el jugador"),

    // ===== DEFENSIVAS =====
    ESCUDO_TEMPORAL(Categoria.DEFENSIVA, Material.SHIELD, "Escudo Temporal", "Reduce el daño recibido unos segundos"),
    REGENERACION(Categoria.DEFENSIVA, Material.GOLDEN_APPLE, "Regeneración", "Recupera vida gradualmente"),
    INMUNIDAD_TEMPORAL(Categoria.DEFENSIVA, Material.TOTEM_OF_UNDYING, "Inmunidad 2s", "2 segundos de invulnerabilidad"),
    REFLEJO_DANO(Categoria.DEFENSIVA, Material.CACTUS, "Reflejo de Daño", "Devuelve parte del daño recibido"),
    TELETRANSPORTE_HUIDA(Categoria.DEFENSIVA, Material.ENDER_PEARL, "Huida", "Se aleja si su vida baja de cierto %"),
    ABSORCION(Categoria.DEFENSIVA, Material.GOLDEN_CARROT, "Absorción", "Otorga corazones amarillos temporales"),
    FURIA(Categoria.DEFENSIVA, Material.BLAZE_POWDER, "Furia", "Con vida baja: +daño y +velocidad"),
    FASE_INVULNERABLE(Categoria.DEFENSIVA, Material.OBSIDIAN, "Fase Invulnerable", "Inmune unos segundos al cruzar umbrales de vida"),

    // ===== INVOCACION =====
    CREAR_SUBDITOS(Categoria.INVOCACION, Material.ZOMBIE_HEAD, "Crear Súbditos", "Invoca mobs aliados"),
    INVOCAR_OLEADA(Categoria.INVOCACION, Material.ROTTEN_FLESH, "Invocar Oleada", "Invoca varios mobs débiles temporales"),
    CLON_FANTASMA(Categoria.INVOCACION, Material.GLASS, "Clon Fantasma", "Invoca una copia con menos vida"),
    REFUERZOS_FASE(Categoria.INVOCACION, Material.IRON_INGOT, "Refuerzos por Fase", "Invoca súbditos al cruzar % de vida"),
    TORRETA(Categoria.INVOCACION, Material.DISPENSER, "Torreta", "Invoca una entidad que dispara proyectiles"),
    FAMILIAR_GUARDIAN(Categoria.INVOCACION, Material.SHIELD, "Familiar Guardián", "Invoca un mob que solo defiende al boss"),

    // ===== CONTROL / UTILIDAD =====
    EMPUJE_AREA(Categoria.CONTROL, Material.SLIME_BALL, "Empuje en Área", "Knockback masivo alrededor del boss"),
    CEGUERA(Categoria.CONTROL, Material.INK_SAC, "Ceguera", "Aplica Ceguera al jugador"),
    CONFUSION(Categoria.CONTROL, Material.PUFFERFISH, "Confusión", "Aplica Náusea al jugador"),
    SALTO_REPENTINO(Categoria.CONTROL, Material.FEATHER, "Salto Repentino", "El boss se reposiciona de golpe"),
    GRITO_ALARMA(Categoria.CONTROL, Material.GOAT_HORN, "Grito de Alarma", "Atrae mobs hostiles cercanos en su ayuda"),
    MARCA_OBJETIVO(Categoria.CONTROL, Material.SPECTRAL_ARROW, "Marca de Objetivo", "El jugador marcado recibe más daño");

    public enum Categoria { OFENSIVA, DEFENSIVA, INVOCACION, CONTROL }

    private final Categoria categoria;
    private final Material icono;
    private final String nombre;
    private final String descripcion;

    AbilityType(Categoria categoria, Material icono, String nombre, String descripcion) {
        this.categoria = categoria;
        this.icono = icono;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() { return categoria; }
    public Material getIcono() { return icono; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
