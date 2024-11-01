package fr.srbliveweather.commands;

import fr.srbliveweather.SRBLiveWeather;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetWeatherCommand implements CommandExecutor {
    private final SRBLiveWeather plugin;

    public SetWeatherCommand(SRBLiveWeather plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command has the correct number of arguments
        if (args.length < 1) {
            return false;
        }

        if (!sender.hasPermission("liveweather.set")) {
            sender.sendMessage(plugin.getLangMessage("commands.no-permission"));
            return true;
        }

        // Make a string of all args
        String city = String.join(" ", args);
        Bukkit.getLogger().info(city);

        // Check if the argument is a string
        if (!city.matches("^[a-zA-Z ]+$")) {
            sender.sendMessage(plugin.getLangMessage("commands.liveweatherset.invalid-city"));
            return true;
        }

        plugin.setCiy(city);

        return true;
    }
}