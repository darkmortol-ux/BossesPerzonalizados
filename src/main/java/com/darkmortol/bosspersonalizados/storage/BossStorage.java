package com.darkmortol.bosspersonalizados.storage;

import com.darkmortol.bosspersonalizados.ability.AbilityType;
import com.darkmortol.bosspersonalizados.model.ArmorPiece;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import com.darkmortol.bosspersonalizados.model.SpawnConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class BossStorage {

    private final JavaPlugin plugin;
    private final File archivo;
    private YamlConfiguration yaml;

    private final Map<Integer, BossDefinition> bosses = new LinkedHashMap<>();
    private int siguienteId = 1;

    public BossStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.archivo = new File(plugin.getDataFolder(), "bosses.yml");
        cargar();
    }

    public void cargar() {
        if (!archivo.exists()) {
            plugin.getDataFolder().mkdirs();
            try { archivo.createNewFile(); } catch (IOException e) { plugin.getLogger().warning("No se pudo crear bosses.yml: " + e.getMessage()); }
        }
        yaml = YamlConfiguration.loadConfiguration(archivo);
        bosses.clear();
        siguienteId = yaml.getInt("siguiente-id", 1);

        ConfigurationSection root = yaml.getConfigurationSection("bosses");
        if (root == null) return;

        for (String key : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(key);
            if (sec == null) continue;
            try {
                int id = Integer.parseInt(key);
                BossDefinition def = new BossDefinition(id, sec.getString("nombre", "Boss"), EntityType.valueOf(sec.getString("tipo-mob", "ZOMBIE")));
                def.setVida(sec.getDouble("vida", 20.0));
                def.setDano(sec.getDouble("dano", 4.0));
                def.setExperiencia(sec.getInt("experiencia", 10));
                def.setRecompensaMonedas(sec.getDouble("recompensa-monedas", 500.0));
                if (sec.isItemStack("recompensa-item")) {
                    def.setRecompensaItem(sec.getItemStack("recompensa-item"));
                }

                for (String hab : sec.getStringList("habilidades")) {
                    try { def.getHabilidades().add(AbilityType.valueOf(hab)); } catch (IllegalArgumentException ignored) {}
                }

                ConfigurationSection equipoSec = sec.getConfigurationSection("equipo");
                if (equipoSec != null) {
                    for (String slotKey : equipoSec.getKeys(false)) {
                        ConfigurationSection piezaSec = equipoSec.getConfigurationSection(slotKey);
                        if (piezaSec == null) continue;
                        ArmorPiece pieza = new ArmorPiece(piezaSec.getString("material"));
                        ConfigurationSection enchSec = piezaSec.getConfigurationSection("encantamientos");
                        if (enchSec != null) {
                            for (String enchKey : enchSec.getKeys(false)) {
                                pieza.setEncantamiento(enchKey, enchSec.getInt(enchKey));
                            }
                        }
                        try {
                            BossDefinition.Slot slot = BossDefinition.Slot.valueOf(slotKey);
                            def.getEquipo().put(slot, pieza);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }

                SpawnConfig spawn = def.getSpawnConfig();
                ConfigurationSection spawnSec = sec.getConfigurationSection("spawn");
                if (spawnSec != null) {
                    spawn.setRespawnHabilitado(spawnSec.getBoolean("respawn-habilitado", false));
                    spawn.setRespawnMinutos(spawnSec.getInt("respawn-minutos", 0));
                    spawn.setRadioDeteccion(spawnSec.getDouble("radio-deteccion", 15.0));
                    spawn.setRadioAtaque(spawnSec.getDouble("radio-ataque", 20.0));
                    if (spawnSec.getBoolean("ubicado", false)) {
                        spawn.ubicar(spawnSec.getString("mundo"), spawnSec.getDouble("x"), spawnSec.getDouble("y"), spawnSec.getDouble("z"));
                    }
                }

                bosses.put(id, def);
            } catch (Exception e) {
                plugin.getLogger().warning("Error cargando boss '" + key + "': " + e.getMessage());
            }
        }
    }

    public void guardar() {
        yaml.set("siguiente-id", siguienteId);
        yaml.set("bosses", null);

        for (BossDefinition def : bosses.values()) {
            String base = "bosses." + def.getId();
            yaml.set(base + ".nombre", def.getNombre());
            yaml.set(base + ".tipo-mob", def.getTipoMob().name());
            yaml.set(base + ".vida", def.getVida());
            yaml.set(base + ".dano", def.getDano());
            yaml.set(base + ".experiencia", def.getExperiencia());
            yaml.set(base + ".recompensa-monedas", def.getRecompensaMonedas());
            if (def.getRecompensaItem() != null) {
                yaml.set(base + ".recompensa-item", def.getRecompensaItem());
            }

            java.util.List<String> habs = new java.util.ArrayList<>();
            for (AbilityType tipo : def.getHabilidades()) habs.add(tipo.name());
            yaml.set(base + ".habilidades", habs);

            for (Map.Entry<BossDefinition.Slot, ArmorPiece> entry : def.getEquipo().entrySet()) {
                ArmorPiece pieza = entry.getValue();
                if (pieza.isVacio()) continue;
                String piezaBase = base + ".equipo." + entry.getKey().name();
                yaml.set(piezaBase + ".material", pieza.getMaterial());
                for (Map.Entry<String, Integer> ench : pieza.getEncantamientos().entrySet()) {
                    yaml.set(piezaBase + ".encantamientos." + ench.getKey(), ench.getValue());
                }
            }

            SpawnConfig spawn = def.getSpawnConfig();
            String spawnBase = base + ".spawn";
            yaml.set(spawnBase + ".respawn-habilitado", spawn.isRespawnHabilitado());
            yaml.set(spawnBase + ".respawn-minutos", spawn.getRespawnMinutos());
            yaml.set(spawnBase + ".radio-deteccion", spawn.getRadioDeteccion());
            yaml.set(spawnBase + ".radio-ataque", spawn.getRadioAtaque());
            yaml.set(spawnBase + ".ubicado", spawn.isUbicado());
            if (spawn.isUbicado()) {
                yaml.set(spawnBase + ".mundo", spawn.getMundo());
                yaml.set(spawnBase + ".x", spawn.getX());
                yaml.set(spawnBase + ".y", spawn.getY());
                yaml.set(spawnBase + ".z", spawn.getZ());
            }
        }

        try {
            yaml.save(archivo);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar bosses.yml: " + e.getMessage());
        }
    }

    public int reservarNuevoId() {
        int id = siguienteId;
        siguienteId++;
        return id;
    }

    public void registrar(BossDefinition def) {
        bosses.put(def.getId(), def);
        guardar();
    }

    public void eliminar(int id) {
        bosses.remove(id);
        guardar();
    }

    public BossDefinition obtener(int id) {
        return bosses.get(id);
    }

    public Map<Integer, BossDefinition> todos() {
        return bosses;
    }
}
