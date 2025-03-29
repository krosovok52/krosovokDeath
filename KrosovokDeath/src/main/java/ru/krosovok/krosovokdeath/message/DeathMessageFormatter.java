package ru.krosovok.krosovokdeath.message;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import ru.krosovok.krosovokdeath.data.DeathInfo;

import java.time.format.DateTimeFormatter;

public class DeathMessageFormatter {
    private FileConfiguration config;
    private final DateTimeFormatter timeFormatter;

    public DeathMessageFormatter(FileConfiguration config) {
        this.config = config;
        this.timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    }

    public String[] format(DeathInfo deathInfo) {
        String time = deathInfo.getDeathTime().format(timeFormatter);
        Location loc = deathInfo.getDeathLocation();
        String coords = String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ());

        String killer = deathInfo.getKillerName() != null ?
                config.getString("messages.killer").replace("{killer}", deathInfo.getKillerName()) :
                config.getString("messages.cause").replace("{cause}", deathInfo.getDeathMessage());

        return new String[] {
                config.getString("messages.death-title"),
                config.getString("messages.player").replace("{player}", deathInfo.getPlayerName()),
                killer,
                config.getString("messages.coordinates").replace("{coords}", coords),
                config.getString("messages.world").replace("{world}", loc.getWorld().getName()),
                config.getString("messages.time").replace("{time}", time),
                config.getString("messages.separator")
        };
    }

    public void reloadConfig(FileConfiguration newConfig) {
        if (newConfig != null) {
            this.config = newConfig;
        }
    }
}