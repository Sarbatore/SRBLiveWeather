package fr.srbliveweather;

import com.google.gson.JsonObject;
import fr.srbliveweather.commands.SetWeatherCommand;
import fr.srbliveweather.commands.SyncCommand;
import fr.srbliveweather.services.APIService;
import fr.srbliveweather.services.WeatherService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Objects;

public final class SRBLiveWeather extends JavaPlugin {
    public String city = "Paris";
    public String weather = "Clear";
    public int time = 0;
    public double temperature = 0.0;

    private BukkitTask timer;

    private FileConfiguration config;
    private FileConfiguration langConfig;
    private File langFile;

    @Override
    public void onEnable() {
        // Load the configuration files
        saveDefaultConfig();
        loadLangConfig();
        loadCache();

        // Initialize the configurations
        this.config = this.getConfig();
        this.langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
        }
        this.langConfig = YamlConfiguration.loadConfiguration(langFile);

        // Set the default configuration values

        config.options().copyDefaults(true);
        saveConfig();

        // Register commands
        Objects.requireNonNull(getCommand("liveweathersync")).setExecutor(new SyncCommand(this));
        Objects.requireNonNull(getCommand("liveweatherset")).setExecutor(new SetWeatherCommand(this));

        // Disable the weather and time cycle
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doWeatherCycle false");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule doDaylightCycle false");

        // Start the timer
        timer = Bukkit.getScheduler().runTaskTimer(this, () -> {
            setCiy(this.city);
        }, 0L, getPluginConfig().getInt("autosyncinterval") * 60 * 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Retrieves the plugin configuration.
     *
     * @return The plugin configuration.
     */
    public FileConfiguration getPluginConfig() {
        return this.config;
    }

    /**
     *
     *
     */
    private void loadCache() {
        File cacheFile = new File(getDataFolder(), "cache.yml");
        YamlConfiguration cache = YamlConfiguration.loadConfiguration(cacheFile);

        if (!cacheFile.exists()) {
            cache.set("city", this.city);
            cache.set("weather", this.weather);
            cache.set("time", this.time);

            try {
                cache.save(cacheFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.city = cache.getString("city");
        this.weather = cache.getString("weather");
        this.time = cache.getInt("time");
    }

    /**
     *
     *
     */
    private void saveCache() {
        File cacheFile = new File(getDataFolder(), "cache.yml");
        FileConfiguration cacheConfig = YamlConfiguration.loadConfiguration(cacheFile);

        cacheConfig.set("city", this.city);
        cacheConfig.set("weather", this.weather);
        cacheConfig.set("time", this.time);

        try {
            cacheConfig.save(cacheFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the language configuration file.
     */
    public void loadLangConfig() {
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
        }

        this.langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Retrieves a language message from the language configuration file.
     * The message is retrieved based on the specified path.
     * The message is then translated to include color codes.
     *
     * @param path The path to the message in the language configuration file.
     * @return The translated message.
     */
    public String getLangMessage(String path) {
        String message = this.langConfig.getString(path);

        if (message == null) {
            return "Message not found for path: " + path;
        }

        String translatedMessage = ChatColor.translateAlternateColorCodes('&', message);

        return translatedMessage;
    }

    /**
     * Set the city weather and time
     *
     * @return
     */
    public void setCiy(String city) {
        try {
            // Get the latitude and longitude of a city
            double[] coords = APIService.getCoords(city);

            if (coords != null) {
                this.city = city;

                // Advert players
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6SRB Live Weather&7] &aThe weather and time have been synchronized with the city of " + city + "."));

                //Bukkit.getLogger().info("Latitude: " + coords[0]);
                //Bukkit.getLogger().info("Longitude: " + coords[1]);

                // Get the weather of a city
                JsonObject weatherJSON = APIService.getWeather(coords[0], coords[1]);

                this.weather = weatherJSON.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();
                this.temperature = weatherJSON.get("main").getAsJsonObject().get("temp").getAsDouble();

                // Format the time
                this.time = WeatherService.timezoneToTime(weatherJSON.get("timezone").getAsInt());

                //Bukkit.getLogger().info("The time in the timezone is: " + this.time);

                // Save the cache
                saveCache();

                // Set the weather and time in the game
                syncWeather();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the weather and time in the game
     *
     */
    public void syncWeather() {
        WeatherService.setWeather(this.weather);
        WeatherService.setTime(this.time);
    }
}
