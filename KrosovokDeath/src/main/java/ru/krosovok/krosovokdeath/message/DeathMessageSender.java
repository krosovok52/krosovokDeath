package ru.krosovok.krosovokdeath.message;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.krosovok.krosovokdeath.data.DeathInfo;
import ru.krosovok.krosovokdeath.util.DeathLogger;
import ru.krosovok.krosovokdeath.util.TimeUtils;

public class DeathMessageSender {
    private final DeathLogger deathLogger;
    private FileConfiguration config;

    public DeathMessageSender(DeathLogger deathLogger, FileConfiguration config) {
        this.deathLogger = deathLogger;
        this.config = config;
    }

    private void sendHelp(CommandSender sender) {
        String authorMessage = "§x§6§4§0§8§F§BАвтор плагина: §x§f§f§0§a§a§ckrosov_ok\n"
                + "§x§6§4§0§8§F§BМой телеграмм канал: §x§f§f§0§a§a§ct.me/programsKrosovok §7(§8Ссылка кликабельная§7)";
        String commandsHelp = " \n"
                + "§6Команды:\n"
                + "§f/kdeath reload §7- Перезагрузить конфиг\n"
                + "§f/kdeath toggle §7- Вкл/выкл уведомления\n"
                + "§f/kdeath help §7- Показать это сообщение\n"
                + "\n"
                + "§daliases: [kd]";


        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.spigot().sendMessage(
                    new ComponentBuilder("§x§6§4§0§8§F§BАвтор плагина: §x§f§f§0§a§a§ckrosov_ok\n")
                            .color(net.md_5.bungee.api.ChatColor.GOLD)
                            .append("§x§6§4§0§8§F§BМой телеграмм канал: §x§f§f§0§a§a§ct.me/programsKrosovok §7(§8Ссылка кликабельная§7)")
                            .color(net.md_5.bungee.api.ChatColor.WHITE)
                            .underlined(true)
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://t.me/programsKrosovok"))
                            .create());
            player.sendMessage(commandsHelp);
        } else {
            sender.sendMessage(authorMessage);
            sender.sendMessage(commandsHelp);
        }
    }

    public void sendDeathMessage(DeathInfo deathInfo) {
        String permission = config.getString("notifications.permission", "krosovokdeath.notify");
        boolean soundEnabled = config.getBoolean("notifications.sound.enabled", false);

        String[] message = formatDeathMessage(deathInfo);
        String title = ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.death-title", "&4[Смерть]"));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                player.sendMessage(title);
                for (String line : message) {
                    player.sendMessage(line);
                }

                if (soundEnabled) {
                    playSound(player);
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(title));
        for (String line : message) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(line));
        }
    }
    private String[] formatDeathMessage(DeathInfo deathInfo) {
        String time = TimeUtils.formatTime(deathInfo.getDeathTime());

        Location loc = deathInfo.getDeathLocation();
        String coords = String.format("%.1f, %.1f, %.1f",
                loc.getX(), loc.getY(), loc.getZ());

        String killerLine;
        if (deathInfo.getKillerName() != null) {
            if (deathInfo.getKillerName().endsWith("(командой)")) {
                killerLine = ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.admin-kill", "&6Администратор: &f{killer}")
                                .replace("{killer}", deathInfo.getKillerName()));
            } else {
                killerLine = ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.killer", "&6Убийца: &f{killer}")
                                .replace("{killer}", deathInfo.getKillerName()));
            }
        } else {
            killerLine = ChatColor.translateAlternateColorCodes('&',
                    config.getString("messages.cause", "&6Причина: &f{cause}")
                            .replace("{cause}", deathInfo.getDeathMessage()));
        }

        // Формируем все части сообщения
        return new String[] {
                ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.player", "&6Игрок: &f{player}")
                                .replace("{player}", deathInfo.getPlayerName())),
                killerLine,
                ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.coordinates", "&6Координаты: &f{coords}")
                                .replace("{coords}", coords)),
                ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.world", "&6Мир: &f{world}")
                                .replace("{world}", loc.getWorld().getName())),
                ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.time", "&6Время: &f{time} ({timezone})")
                                .replace("{time}", time)
                                .replace("{timezone}", TimeUtils.getTimeZone().getId())),
                ChatColor.translateAlternateColorCodes('&',
                        config.getString("messages.separator", "&4════════════════════════════"))
        };
    }
    private void playSound(Player player) {
        try {
            Sound sound = Sound.valueOf(
                    config.getString("notifications.sound.type", "ENTITY_EXPERIENCE_ORB_PICKUP"));
            float pitch = (float) config.getDouble("notifications.sound.pitch", 1.0);
            float volume = (float) config.getDouble("notifications.sound.volume", 1.0);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Указан неверный тип звука в конфиге!");
        }
    }

    public void reloadConfig(FileConfiguration newConfig) {
        this.config = newConfig;
    }
}