package com.darkmortol.bosspersonalizados;

import com.darkmortol.bosspersonalizados.ability.AbilityRunner;
import com.darkmortol.bosspersonalizados.command.BossCommand;
import com.darkmortol.bosspersonalizados.gui.ChatInputListener;
import com.darkmortol.bosspersonalizados.gui.GuiListener;
import com.darkmortol.bosspersonalizados.spawn.BossEventListener;
import com.darkmortol.bosspersonalizados.spawn.SpawnPointManager;
import com.darkmortol.bosspersonalizados.storage.BossStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class BossPersonalizadosPlugin extends JavaPlugin {

    private BossStorage storage;
    private SpawnPointManager spawnPointManager;
    private AbilityRunner abilityRunner;

    @Override
    public void onEnable() {
        BossKeys.inicializar(this);

        this.storage = new BossStorage(this);
        this.spawnPointManager = new SpawnPointManager(this);
        this.abilityRunner = new AbilityRunner(this);

        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatInputListener(this), this);
        getServer().getPluginManager().registerEvents(new BossEventListener(this), this);

        var comando = new BossCommand(this);
        getCommand("boss").setExecutor(comando);

        spawnPointManager.iniciar();
        abilityRunner.iniciar();

        getLogger().info("BossPersonalizados habilitado. " + storage.todos().size() + " bosses cargados.");
    }

    @Override
    public void onDisable() {
        if (storage != null) storage.guardar();
    }

    public BossStorage getStorage() { return storage; }
    public SpawnPointManager getSpawnPointManager() { return spawnPointManager; }
    public AbilityRunner getAbilityRunner() { return abilityRunner; }
}
