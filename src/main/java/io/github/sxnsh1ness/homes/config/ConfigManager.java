package io.github.sxnsh1ness.homes.config;

import io.github.sxnsh1ness.homes.HomesPlugin;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private static File file;
    @Getter
    private static FileConfiguration config;
    private static final Map<String, Integer> groupLimits = new HashMap<>();
    @Getter
    private static int defaultLimit;


    public static void loadConfig() {
        file = new File(HomesPlugin.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()) {
            HomesPlugin.getInstance().saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        if (config.contains("group-limits")) {
            for (String group : config.getConfigurationSection("group-limits").getKeys(false)) {
                int limit = config.getInt("group-limits." + group);
                groupLimits.put(group.toLowerCase(), limit);
            }
        }

        defaultLimit = config.getInt("default-limit", 2);
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static int getLimitForGroup(String group) {
        if (group == null) return defaultLimit;

        return groupLimits.getOrDefault(group.toLowerCase(), defaultLimit);
    }

    public static String getMessage(String key) {
        return translateColors(config.getString("messages." + key, "&cСообщение не найдено: " + key));
    }

    public static String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    private static String translateColors(String message) {
        return message.replace("&", "§");
    }
}
