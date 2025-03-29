package ru.krosovok.krosovokdeath.util;

import org.bukkit.plugin.java.JavaPlugin;
import ru.krosovok.krosovokdeath.data.DeathInfo;

import java.io.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DeathLogger {
    private final JavaPlugin plugin;
    private final File logFile;
    private final DateTimeFormatter formatter;

    public DeathLogger(JavaPlugin plugin) {
        this.plugin = plugin;
        this.formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                .withZone(TimeUtils.getTimeZone()); // Используем зону из TimeUtils

        File logsFolder = new File(plugin.getDataFolder(), "logs");
        if (!logsFolder.exists()) logsFolder.mkdirs();

        this.logFile = new File(logsFolder, "deaths.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл логов: " + e.getMessage());
            }
        }
    }

    public void logDeath(DeathInfo deathInfo, String format) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            String time = formatter.format(deathInfo.getDeathTime()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());

            if ("compact".equalsIgnoreCase(format)) {
                writer.write(String.format("[%s] %s | %s | %.1f,%.1f,%.1f | %s",
                        time,
                        deathInfo.getPlayerName(),
                        deathInfo.getKillerName() != null ?
                                deathInfo.getKillerName() : deathInfo.getDeathMessage(),
                        deathInfo.getDeathLocation().getX(),
                        deathInfo.getDeathLocation().getY(),
                        deathInfo.getDeathLocation().getZ(),
                        deathInfo.getDeathLocation().getWorld().getName()));
            } else {
                writer.write("════════════════\n");
                writer.write("Игрок: " + deathInfo.getPlayerName() + "\n");
                writer.write((deathInfo.getKillerName() != null ?
                        "Убийца: " + deathInfo.getKillerName() :
                        "Причина: " + deathInfo.getDeathMessage()) + "\n");
                writer.write(String.format("Координаты: %.1f,%.1f,%.1f\n",
                        deathInfo.getDeathLocation().getX(),
                        deathInfo.getDeathLocation().getY(),
                        deathInfo.getDeathLocation().getZ()));
                writer.write("Мир: " + deathInfo.getDeathLocation().getWorld().getName() + "\n");
                writer.write("Время: " + time + "\n");
                writer.write("Зона: " + TimeUtils.getTimeZone().getId() + "\n\n");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка записи в лог: " + e.getMessage());
        }
    }
}