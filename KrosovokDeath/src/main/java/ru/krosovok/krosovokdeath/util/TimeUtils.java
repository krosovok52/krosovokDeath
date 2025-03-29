package ru.krosovok.krosovokdeath.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private static ZoneId timeZone = ZoneId.systemDefault();

    public static void init(FileConfiguration config) {
        try {
            timeZone = ZoneId.of(config.getString("timezone", "Europe/Moscow"));
        } catch (Exception e) {
            Bukkit.getLogger().warning("Неверная временная зона в конфиге, используется значение по умолчанию (Europe/Moscow)");
            timeZone = ZoneId.of("Europe/Moscow");
        }
    }

    public static String formatTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(timeZone)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    public static ZoneId getTimeZone() {
        return timeZone;
    }
}