package ru.krosovok.krosovokdeath.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DeathCommandExecutor {
    private final JavaPlugin plugin;

    public DeathCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void executeCommands(Player player, List<String> commands, int delayTicks) {
        new BukkitRunnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex >= commands.size()) {
                    cancel();
                    return;
                }

                String command = commands.get(currentIndex)
                        .replace("{player}", player.getName())
                        .replace("{x}", String.valueOf(player.getLocation().getX()))
                        .replace("{y}", String.valueOf(player.getLocation().getY()))
                        .replace("{z}", String.valueOf(player.getLocation().getZ()))
                        .replace("{world}", player.getLocation().getWorld().getName());

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                currentIndex++;
            }
        }.runTaskTimer(plugin, delayTicks, delayTicks);
    }
}