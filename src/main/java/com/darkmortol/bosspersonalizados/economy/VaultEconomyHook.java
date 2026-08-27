package com.darkmortol.bosspersonalizados.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Enganche opcional con Vault. Si Vault y un plugin de economía no están
 * presentes, las recompensas en monedas simplemente no se entregan
 * (se loguea un aviso una sola vez) sin romper el resto del plugin.
 */
public class VaultEconomyHook {

    private Economy economia;
    private boolean avisoMostrado = false;

    public VaultEconomyHook(JavaPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) this.economia = rsp.getProvider();
    }

    public boolean disponible() {
        return economia != null;
    }

    public void depositar(Player jugador, double monto, JavaPlugin plugin) {
        if (monto <= 0) return;
        if (economia == null) {
            if (!avisoMostrado) {
                plugin.getLogger().warning("No se pudo entregar la recompensa en monedas: Vault o un plugin de economía no están instalados.");
                avisoMostrado = true;
            }
            return;
        }
        economia.depositPlayer(jugador, monto);
    }
}
