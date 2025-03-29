package ru.krosovok.krosovokdeath.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.krosovok.krosovokdeath.command.DeathCommandExecutor;
import ru.krosovok.krosovokdeath.data.DeathInfo;
import ru.krosovok.krosovokdeath.message.DeathMessageSender;
import ru.krosovok.krosovokdeath.util.DeathLogger;

import java.util.List;

public class DeathManager {
    private final JavaPlugin plugin;
    private final DeathLogger deathLogger;
    private final DeathMessageSender messageSender;
    private final DeathCommandExecutor commandExecutor;
    private FileConfiguration config;

    public DeathManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.deathLogger = new DeathLogger(plugin);
        this.messageSender = new DeathMessageSender(this.deathLogger, this.config);
        this.commandExecutor = new DeathCommandExecutor(plugin);
    }

    public void handleDeath(DeathInfo deathInfo) {
        if (config.getBoolean("logging.enabled", true)) {
            deathLogger.logDeath(deathInfo, config.getString("logging.format", "vertical"));
        }

        if (config.getBoolean("notifications.enabled", true)) {
            messageSender.sendDeathMessage(deathInfo);
        }
        if (config.getBoolean("death-commands.enabled", false)) {
            List<String> commands = config.getStringList("death-commands.commands");
            int delay = config.getInt("death-commands.delay-ticks", 20);
            Player player = Bukkit.getPlayer(deathInfo.getPlayerName());

            if (player != null && !commands.isEmpty()) {
                commandExecutor.executeCommands(player, commands, delay);
            }
        }
    }

    public void reloadConfig(FileConfiguration newConfig) {
        this.config = newConfig;
        this.messageSender.reloadConfig(newConfig);
    }
}