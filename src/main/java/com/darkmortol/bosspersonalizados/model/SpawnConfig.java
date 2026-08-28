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

    // Tiempo de gracia: al colocar el huevo, el boss no puede aparecer hasta que
    // pase este tiempo, para que el staff que lo coloco pueda alejarse.
    private int tiempoGraciaSegundos = 30;
    private long momentoColocacionMillis = 0L;

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

    public int getTiempoGraciaSegundos() { return tiempoGraciaSegundos; }
    public void setTiempoGraciaSegundos(int tiempoGraciaSegundos) { this.tiempoGraciaSegundos = Math.max(0, tiempoGraciaSegundos); }

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
        this.momentoColocacionMillis = System.currentTimeMillis();
    }

    /**
     * Igual que ubicar(), pero sin reiniciar el reloj del tiempo de gracia.
     * Se usa exclusivamente al cargar bosses.yml en el arranque del servidor,
     * para no reactivar la gracia de un punto que ya llevaba tiempo colocado.
     */
    public void cargarUbicacion(String mundo, double x, double y, double z) {
        this.mundo = mundo;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ubicado = true;
    }

    public long getMomentoColocacionMillis() { return momentoColocacionMillis; }
    public void setMomentoColocacionMillis(long momentoColocacionMillis) { this.momentoColocacionMillis = momentoColocacionMillis; }

    /** Quita el punto de aparición (sin tocar habilidades/armadura/respawn configurados). */
    public void resetear() {
        this.mundo = null;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.ubicado = false;
        this.vivoActualmente = false;
        this.muertoParaSiempre = false;
        this.ultimaMuerteMillis = 0L;
        this.momentoColocacionMillis = 0L;
    }

    /** True mientras siga corriendo el tiempo de gracia luego de colocar el punto de aparición. */
    public boolean enTiempoDeGracia() {
        if (tiempoGraciaSegundos <= 0) return false;
        return (System.currentTimeMillis() - momentoColocacionMillis) < tiempoGraciaSegundos * 1000L;
    }

    /** Segundos restantes de gracia (0 si ya terminó o no aplica). */
    public long segundosDeGraciaRestantes() {
        if (!enTiempoDeGracia()) return 0;
        long restanteMs = (tiempoGraciaSegundos * 1000L) - (System.currentTimeMillis() - momentoColocacionMillis);
        return Math.max(0, restanteMs / 1000L);
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

    /** Copia todos los campos de "otro" sobre esta instancia (usado al editar un boss existente). */
    public void copiarDesde(SpawnConfig otro) {
        this.respawnHabilitado = otro.respawnHabilitado;
        this.respawnMinutos = otro.respawnMinutos;
        this.radioDeteccion = otro.radioDeteccion;
        this.radioAtaque = otro.radioAtaque;
        this.mundo = otro.mundo;
        this.x = otro.x;
        this.y = otro.y;
        this.z = otro.z;
        this.ubicado = otro.ubicado;
        this.tiempoGraciaSegundos = otro.tiempoGraciaSegundos;
        this.momentoColocacionMillis = otro.momentoColocacionMillis;
        this.vivoActualmente = otro.vivoActualmente;
        this.ultimaMuerteMillis = otro.ultimaMuerteMillis;
        this.muertoParaSiempre = otro.muertoParaSiempre;
    }
}
