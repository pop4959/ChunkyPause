package org.popcraft.chunkypause;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.chunky.api.ChunkyAPI;

import java.util.List;

public final class ChunkyPause extends JavaPlugin implements Listener {
    private ChunkyAPI chunky;
    private int players = -1;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.chunky = Bukkit.getServer().getServicesManager().load(ChunkyAPI.class);
        if (chunky != null && chunky.version() == 0) {
            this.players = getConfig().getInt("players", -1);
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final int newPlayerCount = Integer.parseInt(args[0]);
        this.players = newPlayerCount;
        sender.sendMessage("Max player count changed to %d".formatted(newPlayerCount));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Server server = event.getPlayer().getServer();
        final int playerCount = server.getOnlinePlayers().size();
        if (players >= 0 && playerCount > players) {
            server.getWorlds().forEach(world -> chunky.pauseTask(world.getName()));
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Server server = event.getPlayer().getServer();
        final int playerCount = server.getOnlinePlayers().size() - 1;
        if (players >= 0 && playerCount <= players) {
            server.getWorlds().forEach(world -> chunky.continueTask(world.getName()));
        }
    }
}
