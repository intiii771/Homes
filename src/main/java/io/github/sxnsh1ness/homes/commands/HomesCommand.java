package io.github.sxnsh1ness.homes.commands;

import io.github.sxnsh1ness.homes.config.ConfigManager;
import io.github.sxnsh1ness.homes.database.DatabaseManager;
import io.github.sxnsh1ness.homes.database.Home;
import io.github.sxnsh1ness.homes.utils.LuckPermsHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

public class HomesCommand implements CommandExecutor {

    private final DatabaseManager databaseManager;

    public HomesCommand(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!"));
            return true;
        }

        List<Home> homes = databaseManager.getHomes(player.getUniqueId());
        int playerLimit = LuckPermsHelper.getHighestLimit(player);
        String limitDisplay = playerLimit == -1 ? "∞" : String.valueOf(playerLimit);

        if (homes.isEmpty()) {
            String message = ConfigManager.getMessage("no-homes");
            player.sendMessage(Component.text(message));
            player.sendMessage(Component.text("Используйте /sethome <название> чтобы создать дом."));
            return true;
        }

        String header = ConfigManager.getMessage("homes-header");
        player.sendMessage(Component.text(header));

        String title = ConfigManager.getMessage("homes-title",
                Map.of("count", String.valueOf(homes.size()), "limit", limitDisplay));
        player.sendMessage(Component.text(title));

        player.sendMessage(Component.text(ConfigManager.getMessage("homes-header")));

        for (Home home : homes) {
            String homeLine = String.format("§8• §b§l%s §8- §a%s §8(§7%.0f, %.0f, %.0f§8)",
                    home.getName(),
                    home.getWorldName(),
                    home.getX(),
                    home.getY(),
                    home.getZ()
            );
            player.sendMessage(Component.text(homeLine));
        }

        String footer = ConfigManager.getMessage("homes-footer");
        player.sendMessage(Component.text(footer));

        String hint = ConfigManager.getMessage("homes-hint");
        player.sendMessage(Component.text(hint));
        return true;
    }
}
