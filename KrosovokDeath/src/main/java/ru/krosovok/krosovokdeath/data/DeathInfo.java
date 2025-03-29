package ru.krosovok.krosovokdeath.data;

import org.bukkit.Location;
import java.time.LocalDateTime;

public class DeathInfo {
    private final String playerName;
    private final String killerName;
    private final Location deathLocation;
    private final String deathMessage;
    private final LocalDateTime deathTime;

    public DeathInfo(String playerName, String killerName, Location deathLocation,
                     String deathMessage, LocalDateTime deathTime) {
        this.playerName = playerName;
        this.killerName = killerName;
        this.deathLocation = deathLocation;
        this.deathMessage = deathMessage;
        this.deathTime = deathTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getKillerName() {
        return killerName;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public LocalDateTime getDeathTime() {
        return deathTime;
    }
}