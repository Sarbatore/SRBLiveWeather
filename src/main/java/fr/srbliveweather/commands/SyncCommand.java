package fr.srbliveweather.commands;

import fr.srbliveweather.SRBLiveWeather;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SyncCommand implements CommandExecutor {
    private final SRBLiveWeather plugin;

    public SyncCommand(SRBLiveWeather plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("liveweather.sync")) {
            sender.sendMessage(plugin.getLangMessage("commands.no-permission"));
            return true;
        }

        plugin.syncWeather();

        return true;
    }
}