package com.darkmortol.bosspersonalizados.model;

public class SpawnConfig {

    private boolean respawnHabilitado = false;
    private int respawnMinutos = 0; // 0 = solo una vez si respawnHabilitado es false

    private double radioDeteccion = 15.0; // distancia a la que aparece cuando se acerca un jugador
    private double radioAtaque = 20.0;    // distancia maxima a la que perseguira/atacara

    // Ubicacion del punto de spawn (se completa al colocar el huevo)
    private String mundo;
    private double x, y, z;
    private boolean ubicado = false;

    // Estado en tiempo de ejecucion
    private boolean vivoActualmente = false;
    private long ultimaMuerteMillis = 0L;
    private boolean muertoParaSiempre = false; // true cuando era "una sola vez" y ya murio

    public boolean isRespawnHabilitado() { return respawnHabilitado; }
    public void setRespawnHabilitado(boolean respawnHabilitado) { this.respawnHabilitado = respawnHabilitado; }

    public int getRespawnMinutos() { return respawnMinutos; }
    public void setRespawnMinutos(int respawnMinutos) { this.respawnMinutos = Math.max(0, respawnMinutos); }

    public double getRadioDeteccion() { return radioDeteccion; }
    public void setRadioDeteccion(double radioDeteccion) { this.radioDeteccion = radioDeteccion; }

    public double getRadioAtaque() { return radioAtaque; }
    public void setRadioAtaque(double radioAtaque) { this.radioAtaque = radioAtaque; }

    public String getMundo() { return mundo; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public boolean isUbicado() { return ubicado; }

    public void ubicar(String mundo, double x, double y, double z) {
        this.mundo = mundo;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ubicado = true;
    }

    public boolean isVivoActualmente() { return vivoActualmente; }
    public void setVivoActualmente(boolean vivoActualmente) { this.vivoActualmente = vivoActualmente; }

    public long getUltimaMuerteMillis() { return ultimaMuerteMillis; }
    public void setUltimaMuerteMillis(long ultimaMuerteMillis) { this.ultimaMuerteMillis = ultimaMuerteMillis; }

    public boolean isMuertoParaSiempre() { return muertoParaSiempre; }
    public void setMuertoParaSiempre(boolean muertoParaSiempre) { this.muertoParaSiempre = muertoParaSiempre; }

    /** Puede volver a aparecer si esta habilitado el respawn y ya paso el intervalo. */
    public boolean puedeRespawnear() {
        if (muertoParaSiempre) return false;
        if (vivoActualmente) return false;
        if (!respawnHabilitado) return ultimaMuerteMillis == 0L; // primera vez
        long intervaloMs = respawnMinutos * 60_000L;
        return (System.currentTimeMillis() - ultimaMuerteMillis) >= intervaloMs;
    }
}
