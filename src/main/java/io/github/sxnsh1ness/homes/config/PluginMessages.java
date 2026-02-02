package io.github.sxnsh1ness.homes.config;

import io.github.sxnsh1ness.homes.HomesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class PluginMessages {

    private static File file;
    private static FileConfiguration messages;
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer HEX_COLORS = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    public static void loadMessages() {
        file = new File(HomesPlugin.getInstance().getDataFolder(), "messages.yml");
        if (!file.exists()) HomesPlugin.getInstance().saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(file);
    }

    private static Component parseMessage(String message) {
        if (message == null || message.isEmpty()) return Component.empty();
        return Component.text(message);
    }


    public static void send(@NotNull CommandSender sender, @NotNull String key, String... replacements) {
        String rawMessage = messages.getString("messages." + key);

        if (rawMessage == null || rawMessage.isEmpty()) {
            sender.sendMessage(parseMessage("Сообщение не найдено: " + key));
        }

        String processedMessage = applyPlaceholders(rawMessage, replacements);

        assert processedMessage != null;
        Component message = colorizeRGB(processedMessage);
        sender.sendMessage(message);
    }

    public static void sendList(CommandSender sender, String path, String... placeholders) {
        List<String> messagesStr = messages.getStringList("messages." + path);
        if (messagesStr.isEmpty()) return;

        for (String str : messagesStr) {
            String processedMessage = applyPlaceholders(str, placeholders);

            Component message = colorizeRGB(processedMessage);
            sender.sendMessage(message);
        }
    }

    public static void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(file);
    }

    private static String applyPlaceholders(String message, String... placeholders) {
        if (message == null || placeholders.length == 0) {
            return message;
        }

        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be provided in key-value pairs (even number of arguments).");
        }

        String result = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            String key = placeholders[i];
            String value = placeholders[i + 1];
            if (key != null) {
                result = result.replace(key, value != null ? value : "null");
            }
        }
        return result;
    }

    public static void saveMessages() {
        try {
            messages.save(file);
        } catch (Exception ignored) {}
    }

    private static TextComponent colorize(String message) {
        if (message == null) return null;

        return LEGACY.deserialize(message);
    }

    private static Component colorizeRGB(String message) {
        if (message == null) return null;

        return HEX_COLORS.deserialize(message);
    }
}
