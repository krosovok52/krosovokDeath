package ru.krosovok.krosovokdeath.listener;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import ru.krosovok.krosovokdeath.data.DeathInfo;
import ru.krosovok.krosovokdeath.manager.DeathManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DeathListener implements Listener {
    private final DeathManager deathManager;

    public DeathListener(DeathManager deathManager) {
        this.deathManager = deathManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String deathMessage = event.getDeathMessage();
        String killerName = getKillerName(player, deathMessage);
        String cause = getDeathCause(player.getLastDamageCause(), deathMessage, killerName);

        DeathInfo deathInfo = new DeathInfo(
                player.getName(),
                killerName,
                player.getLocation(),
                cause,
                LocalDateTime.now()
        );

        deathManager.handleDeath(deathInfo);
    }

    private String getKillerName(Player player, String deathMessage) {
        if (deathMessage != null && deathMessage.endsWith(" was slain by magic")) {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player && ((Player) entity).isOp()) {
                    return entity.getName() + " (командой)";
                }
            }
            return "Консоль (командой)";
        }

        Player killerPlayer = player.getKiller();
        if (killerPlayer != null) return killerPlayer.getName();

        EntityDamageEvent lastDamage = player.getLastDamageCause();
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();

            if (damager instanceof LivingEntity && !(damager instanceof Player)) {
                return translateEntityName(((LivingEntity) damager).getType().name());
            }

            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof LivingEntity) {
                    LivingEntity shooter = (LivingEntity) projectile.getShooter();
                    return shooter instanceof Player ?
                            shooter.getName() :
                            translateEntityName(shooter.getType().name());
                }
            }
        }
        return null;
    }

    private String getDeathCause(EntityDamageEvent damage, String deathMessage, String killerName) {
        if (deathMessage != null && deathMessage.endsWith(" was slain by magic")) {
            return "убит командой /kill";
        }

        if (damage == null) {
            return parseCustomDeathMessage(deathMessage);
        }

        switch (damage.getCause()) {
            case CONTACT:
                if (damage.getEntity() != null && damage.getEntity().getLocation().getBlock().getType() == Material.CACTUS) {
                    return "укололся насмерть";
                }
                return "получил повреждения";
            case FALL: return "упал с высоты";
            case FIRE: return "сгорел в огне";
            case FIRE_TICK: return "горит в огне";
            case LAVA: return "утонул в лаве";
            case DROWNING: return "утонул";
            case SUFFOCATION: return "задохнулся в блоке";
            case ENTITY_ATTACK:
                return killerName != null ?
                        "убит " + killerName :
                        "атакован существом";
            case PROJECTILE: return "поражён снарядом";
            case VOID: return "упал в пустоту";
            case LIGHTNING: return "поражён молнией";
            case SUICIDE: return "покончил с собой";
            case STARVATION: return "умер от голода";
            case POISON: return "отравился";
            case MAGIC: return "умер от магии";
            case WITHER: return "иссох от визера";
            case FALLING_BLOCK: return "раздавлен падающим блоком";
            case DRAGON_BREATH: return "убит дыханием дракона";
            case HOT_FLOOR: return "поджарился на магме";
            case CRAMMING: return "раздавлен толпой";
            case FLY_INTO_WALL: return "получил кинетический удар";
            case DRYOUT: return "высох без воды";
            default:
                return damage.getCause().name().toLowerCase()
                        .replace("_", " ");
        }
    }

    private String parseCustomDeathMessage(String deathMessage) {
        if (deathMessage == null) return "неизвестная причина";

        String processed = deathMessage.replaceFirst("^\\w+\\s+", "");

        if (!processed.isEmpty()) {
            processed = processed.substring(0, 1).toLowerCase() + processed.substring(1);
        }

        return processed.isEmpty() ? "неизвестная причина" : processed;
    }

    private String translateEntityName(String englishName) {
        Map<String, String> mobTranslations = new HashMap<>();
        mobTranslations.put("blaze", "ифрит");
        mobTranslations.put("cave_spider", "пещерный паук");
        mobTranslations.put("creeper", "крипер");
        mobTranslations.put("drowned", "утопленник");
        mobTranslations.put("elder_guardian", "древний страж");
        mobTranslations.put("enderman", "эндермен");
        mobTranslations.put("endermite", "эндермит");
        mobTranslations.put("evoker", "вызыватель");
        mobTranslations.put("ghast", "гаст");
        mobTranslations.put("guardian", "страж");
        mobTranslations.put("husk", "кадавр");
        mobTranslations.put("magma_cube", "лавовый куб");
        mobTranslations.put("phantom", "фантом");
        mobTranslations.put("piglin_brute", "брутальный пиглин");
        mobTranslations.put("pillager", "разбойник");
        mobTranslations.put("ravager", "разоритель");
        mobTranslations.put("shulker", "шалкер");
        mobTranslations.put("silverfish", "чешуйница");
        mobTranslations.put("skeleton", "скелет");
        mobTranslations.put("slime", "слизень");
        mobTranslations.put("spider", "паук");
        mobTranslations.put("stray", "зимогор");
        mobTranslations.put("vex", "вредина");
        mobTranslations.put("vindicator", "поборник");
        mobTranslations.put("warden", "хранитель");
        mobTranslations.put("witch", "ведьма");
        mobTranslations.put("wither_skeleton", "скелет-иссушитель");
        mobTranslations.put("zoglin", "зоглин");
        mobTranslations.put("zombie", "зомби");
        mobTranslations.put("zombie_villager", "зомби-житель");
        mobTranslations.put("bee", "пчела");
        mobTranslations.put("dolphin", "дельфин");
        mobTranslations.put("goat", "коза");
        mobTranslations.put("iron_golem", "железный голем");
        mobTranslations.put("llama", "лама");
        mobTranslations.put("panda", "панда");
        mobTranslations.put("piglin", "пиглин");
        mobTranslations.put("polar_bear", "белый медведь");
        mobTranslations.put("trader_llama", "лама торговца");
        mobTranslations.put("wolf", "волк");
        mobTranslations.put("zombified_piglin", "зомбифицированный пиглин");
        mobTranslations.put("allay", "элей");
        mobTranslations.put("axolotl", "аксолотль");
        mobTranslations.put("bat", "летучая мышь");
        mobTranslations.put("camel", "верблюд");
        mobTranslations.put("cat", "кошка");
        mobTranslations.put("chicken", "курица");
        mobTranslations.put("cod", "треска");
        mobTranslations.put("cow", "корова");
        mobTranslations.put("donkey", "осёл");
        mobTranslations.put("fox", "лисица");
        mobTranslations.put("frog", "лягушка");
        mobTranslations.put("glow_squid", "светящийся спрут");
        mobTranslations.put("horse", "лошадь");
        mobTranslations.put("mooshroom", "грибная корова");
        mobTranslations.put("mule", "мул");
        mobTranslations.put("ocelot", "оцелот");
        mobTranslations.put("parrot", "попугай");
        mobTranslations.put("pig", "свинья");
        mobTranslations.put("pufferfish", "иглобрюх");
        mobTranslations.put("rabbit", "кролик");
        mobTranslations.put("salmon", "лосось");
        mobTranslations.put("sheep", "овца");
        mobTranslations.put("skeleton_horse", "лошадь-скелет");
        mobTranslations.put("sniffer", "нюхач");
        mobTranslations.put("snow_golem", "снежный голем");
        mobTranslations.put("squid", "спрут");
        mobTranslations.put("strider", "страйдер");
        mobTranslations.put("tadpole", "головастик");
        mobTranslations.put("tropical_fish", "тропическая рыба");
        mobTranslations.put("turtle", "черепаха");
        mobTranslations.put("villager", "житель");
        mobTranslations.put("wandering_trader", "странствующий торговец");
        mobTranslations.put("ender_dragon", "Дракон Края");
        mobTranslations.put("wither", "Иссушитель");
        mobTranslations.put("illusioner", "иллюзионист");
        mobTranslations.put("zombie_horse", "лошадь-зомби");

        return mobTranslations.getOrDefault(englishName.toLowerCase(), englishName);
    }
}