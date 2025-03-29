package ru.krosovok.krosovokdeath;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Sound;
import ru.krosovok.krosovokdeath.listener.DeathListener;
import ru.krosovok.krosovokdeath.manager.DeathManager;
import ru.krosovok.krosovokdeath.util.TimeUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public final class KrosovokDeath extends JavaPlugin implements TabCompleter {
    private DeathManager deathManager;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        TimeUtils.init(getConfig());

        deathManager = new DeathManager(this);
        getServer().getPluginManager().registerEvents(new DeathListener(deathManager), this);
        getCommand("kdeath").setExecutor(this);
        getCommand("kdeath").setTabCompleter(this);

        String startupMessage = "§x§6§4§0§8§F§B┏ §x§F§6§3§5§D§2§lK§x§F§3§3§B§D§0§lr§x§F§0§4§1§C§F§lo§x§E§C§4§7§C§D§ls§x§E§9§4§D§C§C§lo§x§E§6§5§3§C§A§lv§x§E§3§5§A§C§9§lo§x§D§F§6§0§C§7§lk§x§D§C§6§6§C§5§lD§x§D§9§6§C§C§4§le§x§D§6§7§2§C§2§la§x§D§2§7§8§C§1§lt§x§C§F§7§E§B§F§lh §x§f§f§0§a§a§cv" + getDescription().getVersion() + " §aактивирован!\n" +
                "§x§6§4§0§8§F§B┣ §fРазработчик: §x§f§f§0§a§a§ckrosov_ok\n" +
                "§x§6§4§0§8§F§B┗ §fМой Телеграм канал: §x§f§f§0§a§a§ct.me/programsKrosovok\n";

        getLogger().info(ChatColor.stripColor(startupMessage.replace("§x§6§4§0§8§F§B", "").replace("§x§f§f§0§a§a§c", "")));

        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("krosovokdeath.notify")) {
                player.sendMessage(startupMessage.split("\n"));
                player.spigot().sendMessage(new ComponentBuilder("")
                        .append("§7[§aНажмите для получения помощи§7]")
                        .color(net.md_5.bungee.api.ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kdeath help"))
                        .create());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.5f);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("time")) {
            ZoneId zone = TimeUtils.getTimeZone();
            sender.sendMessage("§6Текущая временная зона: §f" + zone.getId());
            sender.sendMessage("§6Текущее время: §f" + TimeUtils.formatTime(LocalDateTime.now()));
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("krosovokdeath.reload")) {
                    sender.sendMessage("§cУ вас нет прав на эту команду!");
                    return true;
                }

                reloadConfig();
                deathManager.reloadConfig(getConfig());

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                }

                String status = getConfig().getBoolean("notifications.enabled") ? "§aВКЛ" : "§cВЫКЛ";
                sender.sendMessage("§x§D§9§3§6§F§3§lk§x§D§2§4§F§E§7§ld §f| §aКонфиг перезагружен! §fУведомления: " + status);
                return true;

            case "toggle":
                if (!sender.hasPermission("krosovokdeath.toggle")) {
                    sender.sendMessage("§cУ вас нет прав на эту команду!");
                    return true;
                }

                boolean newState = !getConfig().getBoolean("notifications.enabled");
                getConfig().set("notifications.enabled", newState);
                saveConfig();

                String toggleMessage = "§x§D§9§3§6§F§3§lk§x§D§2§4§F§E§7§ld §f| Уведомления " +
                        (newState ? "§aвключены" : "§cвыключены") +
                        " §fигроком §e" + sender.getName();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("krosovokdeath.notify")) {
                        player.sendMessage(toggleMessage);
                        if (newState) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                        }
                    }
                }
                Bukkit.getConsoleSender().sendMessage(toggleMessage);

                sender.sendMessage("§x§D§9§3§6§F§3§lk§x§D§2§4§F§E§7§ld §f| " +
                        (newState ? "§aУведомления включены" : "§cУведомления выключены"));
                return true;

            default:
                sender.sendMessage("§x§D§9§3§6§F§3§lk§x§D§2§4§F§E§7§ld §f| §cНеизвестная команда. Используйте §f/kdeath help");
                return false;
        }
    }

    private void sendHelp(CommandSender sender) {
        String pluginInfo = "§x§6§4§0§8§F§B┏ §x§F§6§3§5§D§2§lK§x§F§3§3§B§D§0§lr§x§F§0§4§1§C§F§lo§x§E§C§4§7§C§D§ls§x§E§9§4§D§C§C§lo§x§E§6§5§3§C§A§lv§x§E§3§5§A§C§9§lo§x§D§F§6§0§C§7§lk§x§D§C§6§6§C§5§lD§x§D§9§6§C§C§4§le§x§D§6§7§2§C§2§la§x§D§2§7§8§C§1§lt§x§C§F§7§E§B§F§lh §x§f§f§0§a§a§cv" + getDescription().getVersion() + "\n" +
                "§x§6§4§0§8§F§B┣ §fАвтор: §x§f§f§0§a§a§ckrosov_ok\n" +
                "§x§6§4§0§8§F§B┗ §fТелеграм: §x§f§f§0§a§a§ct.me/programsKrosovok";

        StringBuilder commands = new StringBuilder("§x§E§3§8§1§3§1Доступные команды:\n");
        if (sender.hasPermission("krosovokdeath.reload")) {
            commands.append("§e/kdeath reload §7- Перезагрузить конфиг\n");
        }
        if (sender.hasPermission("krosovokdeath.toggle")) {
            commands.append("§e/kdeath toggle §7- Вкл/выкл уведомления\n");
        }
        commands.append("§e/kdeath help §7- Показать это сообщение\n");
        commands.append(" \n");
        commands.append("§x§6§4§0§8§F§BAliases: §7[kd]");

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage(pluginInfo.split("\n"));

            player.spigot().sendMessage(new ComponentBuilder("")
                    .append("§7[§x§6§4§0§8§F§BНажмите для перехода в Telegram§7]\n")
                    .color(net.md_5.bungee.api.ChatColor.of("#6408FB"))
                    .underlined(true)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://t.me/programsKrosovok"))
                    .create());
            player.sendMessage(commands.toString());

            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }
        else {
            sender.sendMessage(pluginInfo.split("\n"));
            sender.sendMessage(commands.toString());
        }
    }

    private void playReloadSound(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, -2f, 1.5f);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "toggle", "help");
        }
        return Collections.emptyList();
    }

    @Override
    public void onDisable() {
        String disabledMsg = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("messages.plugin-disabled", "§x§D§9§3§6§F§3§lk§x§D§2§4§F§E§7§ld §cотключен"));
        getServer().getOnlinePlayers().forEach(p -> {
            if (p.hasPermission(getConfig().getString("notifications.permission"))) {
                p.sendMessage(disabledMsg);
            }
        });
        getLogger().info(disabledMsg);
    }
}