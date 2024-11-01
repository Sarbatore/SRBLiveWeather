package fr.srbliveweather.services;

import org.bukkit.Bukkit;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class WeatherService {
    /**
     * Get the Minecraft weather based on the weather
     *
     * @param weather The weather
     * @return The Minecraft weather
     */
    public static String getMCWeather(String weather) {
        switch (weather) {
            case "Thunderstorm":
                return "thunder";
            case "Rain":
                return "rain";
            case "Snow":
                return "rain";
            default:
                return "clear";
        }
    }

    /**
     * Get the Minecraft time based on the time
     *
     * @param hour The time
     * @return The Minecraft time
     */
    public static String getMCTime(int hour) {
        String time = "day";

        if (hour >= 18 && hour < 22) {
            time = "night";
        } else if (hour >= 22 || hour < 6) {
            time = "midnight";
        }

        return time;
    }

    public static int timezoneToTime(int timezone) {
        // Get the current time
        ZonedDateTime now = ZonedDateTime.now();

        // Create a ZoneOffset with an offset of 7200 seconds
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timezone);

        // Convert the current time to the timezone with the specified offset
        ZonedDateTime offsetTime = now.withZoneSameInstant(offset);

        // weatherJSON.get("timezone").getAsInt()

        // Format the time
        return Integer.parseInt(DateTimeFormatter.ofPattern("HH").format(offsetTime));
    }

    /**
     * Set the world weather
     *
     * @param w The weather
     */
    public static void setWeather(String w) {
        String weather = getMCWeather(w);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather " + weather);

        //Bukkit.getLogger().info("Weather set to " + weather);
    }

    /**
     * Set the world time
     *
     * @param t The time
     */
    public static void setTime(int t) {
        String time = getMCTime(t);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "time set " + time);

        //Bukkit.getLogger().info("Time set to " + time);
    }
}
