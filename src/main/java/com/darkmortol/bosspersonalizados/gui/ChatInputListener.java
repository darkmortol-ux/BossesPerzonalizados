package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.BossPersonalizadosPlugin;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import com.darkmortol.bosspersonalizados.spawn.BossSpawnEggItem;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatInputListener implements Listener {

    private final BossPersonalizadosPlugin plugin;

    public ChatInputListener(BossPersonalizadosPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        WizardSession sesion = WizardSession.get(e.getPlayer().getUniqueId());
        if (sesion == null) return;
        if (sesion.getPaso() != WizardSession.Paso.STATS_VIDA
                && sesion.getPaso() != WizardSession.Paso.STATS_DANO
                && sesion.getPaso() != WizardSession.Paso.STATS_EXP) return;

        e.setCancelled(true);
        String texto = PlainTextComponentSerializer.plainText().serialize(e.message()).trim();

        Bukkit.getScheduler().runTask(plugin, () -> procesar(sesion, texto));
    }

    private void procesar(WizardSession sesion, String texto) {
        var jugador = Bukkit.getPlayer(sesion.getJugador());
        if (jugador == null) return;
        BossDefinition def = sesion.getDefinicion();

        try {
            switch (sesion.getPaso()) {
                case STATS_VIDA -> {
                    def.setVida(Double.parseDouble(texto));
                    sesion.setPaso(WizardSession.Paso.STATS_DANO);
                    jugador.sendMessage(Component.text("Ahora escribe el DAÑO del boss (numero, ej: 6):", NamedTextColor.GOLD));
                }
                case STATS_DANO -> {
                    def.setDano(Double.parseDouble(texto));
                    sesion.setPaso(WizardSession.Paso.STATS_EXP);
                    jugador.sendMessage(Component.text("Por último, escribe la EXPERIENCIA que da al morir (numero, ej: 25):", NamedTextColor.GOLD));
                }
                case STATS_EXP -> {
                    def.setExperiencia(Integer.parseInt(texto));
                    finalizar(sesion);
                }
                default -> {}
            }
        } catch (NumberFormatException ex) {
            jugador.sendMessage(Component.text("Eso no es un número válido, intentá de nuevo.", NamedTextColor.RED));
        }
    }

    private void finalizar(WizardSession sesion) {
        var jugador = Bukkit.getPlayer(sesion.getJugador());
        if (jugador == null) return;
        BossDefinition def = sesion.getDefinicion();

        if (sesion.isEdicion()) {
            // /boss editar: la definición ya tiene su ID original, solo se sobreescribe en el registro.
            plugin.getStorage().registrar(def);
            jugador.sendMessage(Component.text("Boss '" + def.getNombre() + "' (#" + def.getId() + ") actualizado.", NamedTextColor.GREEN));
        } else {
            int id = plugin.getStorage().reservarNuevoId();
            def.setId(id);
            plugin.getStorage().registrar(def);

            jugador.getInventory().addItem(BossSpawnEggItem.crear(def));
            jugador.sendMessage(Component.text("¡Boss '" + def.getNombre() + "' (#" + id + ") creado! ", NamedTextColor.GREEN)
                    .append(Component.text("Coloca el huevo para crear su punto de aparición.", NamedTextColor.YELLOW)));
        }

        WizardSession.cancelar(sesion.getJugador());
    }
}
