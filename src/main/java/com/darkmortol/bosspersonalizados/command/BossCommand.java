package com.darkmortol.bosspersonalizados.command;

import com.darkmortol.bosspersonalizados.BossPersonalizadosPlugin;
import com.darkmortol.bosspersonalizados.gui.MobSelectionGUI;
import com.darkmortol.bosspersonalizados.gui.WizardSession;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BossCommand implements CommandExecutor {

    private final BossPersonalizadosPlugin plugin;

    public BossCommand(BossPersonalizadosPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bosspersonalizados.admin")) {
            sender.sendMessage(Component.text("No tenés permiso para usar este comando.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Component.text("Uso: /boss <crear|lista|editar|eliminar|eliminarpunto|cancelar>", NamedTextColor.YELLOW));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "crear" -> crear(sender, args);
            case "lista" -> lista(sender);
            case "editar" -> editar(sender, args);
            case "eliminar" -> eliminar(sender, args);
            case "eliminarpunto" -> eliminarPunto(sender, args);
            case "cancelar" -> cancelar(sender);
            default -> sender.sendMessage(Component.text("Subcomando desconocido. Uso: /boss <crear|lista|editar|eliminar|eliminarpunto|cancelar>", NamedTextColor.YELLOW));
        }
        return true;
    }

    private void crear(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(Component.text("Solo un jugador puede crear bosses.", NamedTextColor.RED)); return; }
        if (args.length < 2) { player.sendMessage(Component.text("Uso: /boss crear <nombre> [tipo_de_mob]", NamedTextColor.YELLOW)); return; }

        String nombre = args[1];
        WizardSession sesion = WizardSession.iniciar(player.getUniqueId(), nombre);

        if (args.length >= 3) {
            // Uso especial para Ender Dragon / Wither (no tienen huevo de spawn) o para saltar la GUI de mob.
            try {
                EntityType tipo = EntityType.valueOf(args[2].toUpperCase());
                sesion.getDefinicion().setTipoMob(tipo);
                sesion.setPaso(WizardSession.Paso.HABILIDADES);
                player.openInventory(com.darkmortol.bosspersonalizados.gui.AbilitySelectionGUI.build(sesion));
                return;
            } catch (IllegalArgumentException ex) {
                player.sendMessage(Component.text("Tipo de mob inválido: " + args[2], NamedTextColor.RED));
                WizardSession.cancelar(player.getUniqueId());
                return;
            }
        }

        player.openInventory(MobSelectionGUI.build());
    }

    private void editar(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(Component.text("Solo un jugador puede editar bosses.", NamedTextColor.RED)); return; }
        if (args.length < 2) { player.sendMessage(Component.text("Uso: /boss editar <id> [tipo_de_mob]", NamedTextColor.YELLOW)); return; }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            player.sendMessage(Component.text("El ID debe ser un número.", NamedTextColor.RED));
            return;
        }

        BossDefinition existente = plugin.getStorage().obtener(id);
        if (existente == null) { player.sendMessage(Component.text("No existe un boss con ID " + id, NamedTextColor.RED)); return; }

        // Se edita sobre una COPIA: si cancelás, el boss original queda intacto.
        WizardSession sesion = WizardSession.editar(player.getUniqueId(), existente);
        player.sendMessage(Component.text("Editando boss #" + id + " (" + existente.getNombre() + "). Los cambios se guardan al terminar el wizard.", NamedTextColor.AQUA));

        if (args.length >= 3) {
            try {
                EntityType tipo = EntityType.valueOf(args[2].toUpperCase());
                sesion.getDefinicion().setTipoMob(tipo);
                sesion.setPaso(WizardSession.Paso.HABILIDADES);
                player.openInventory(com.darkmortol.bosspersonalizados.gui.AbilitySelectionGUI.build(sesion));
                return;
            } catch (IllegalArgumentException ex) {
                player.sendMessage(Component.text("Tipo de mob inválido: " + args[2], NamedTextColor.RED));
                WizardSession.cancelar(player.getUniqueId());
                return;
            }
        }

        player.openInventory(MobSelectionGUI.build());
    }

    private void lista(CommandSender sender) {
        var todos = plugin.getStorage().todos();
        if (todos.isEmpty()) { sender.sendMessage(Component.text("Todavía no hay bosses creados.", NamedTextColor.YELLOW)); return; }
        sender.sendMessage(Component.text("=== Bosses creados ===", NamedTextColor.GOLD));
        for (BossDefinition def : todos.values()) {
            String estado = !def.getSpawnConfig().isUbicado() ? "sin ubicar"
                    : def.getSpawnConfig().isVivoActualmente() ? "vivo" : "muerto/esperando";
            sender.sendMessage(Component.text("#" + def.getId() + " " + def.getNombre() + " (" + def.getTipoMob() + ") - " + estado, NamedTextColor.WHITE));
        }
    }

    private void eliminar(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage(Component.text("Uso: /boss eliminar <id>", NamedTextColor.YELLOW)); return; }
        try {
            int id = Integer.parseInt(args[1]);
            if (plugin.getStorage().obtener(id) == null) { sender.sendMessage(Component.text("No existe un boss con ID " + id, NamedTextColor.RED)); return; }

            // Despawnea la entidad viva (si la hay) y limpia todo el seguimiento antes de borrar el registro.
            plugin.getAbilityRunner().despawnBoss(id);
            plugin.getSpawnPointManager().quitarInstanciaActiva(id);
            plugin.getStorage().eliminar(id);

            sender.sendMessage(Component.text("Boss #" + id + " eliminado: registro y punto de aparición borrados.", NamedTextColor.GREEN));
        } catch (NumberFormatException ex) {
            sender.sendMessage(Component.text("El ID debe ser un número.", NamedTextColor.RED));
        }
    }

    private void eliminarPunto(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage(Component.text("Uso: /boss eliminarpunto <id>", NamedTextColor.YELLOW)); return; }
        try {
            int id = Integer.parseInt(args[1]);
            BossDefinition def = plugin.getStorage().obtener(id);
            if (def == null) { sender.sendMessage(Component.text("No existe un boss con ID " + id, NamedTextColor.RED)); return; }
            if (!def.getSpawnConfig().isUbicado()) { sender.sendMessage(Component.text("El boss #" + id + " todavía no tiene punto de aparición ubicado.", NamedTextColor.YELLOW)); return; }

            // Despawnea la entidad viva (si la hay), pero conserva habilidades/armadura/respawn/radios del boss.
            plugin.getAbilityRunner().despawnBoss(id);
            plugin.getSpawnPointManager().quitarInstanciaActiva(id);
            def.getSpawnConfig().resetear();
            plugin.getStorage().guardar();

            sender.sendMessage(Component.text("Punto de aparición del boss #" + id + " eliminado. La configuración del boss se conserva.", NamedTextColor.GREEN));

            if (sender instanceof Player player) {
                player.getInventory().addItem(com.darkmortol.bosspersonalizados.spawn.BossSpawnEggItem.crear(def));
                player.sendMessage(Component.text("Te di un nuevo huevo para volver a ubicarlo donde quieras.", NamedTextColor.YELLOW));
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(Component.text("El ID debe ser un número.", NamedTextColor.RED));
        }
    }

    private void cancelar(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        WizardSession.cancelar(player.getUniqueId());
        player.closeInventory();
        sender.sendMessage(Component.text("Creación de boss cancelada.", NamedTextColor.YELLOW));
    }
}
