package me.jahaziel.weapons.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {

    private static final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public static void init() {
        cooldowns.clear();
    }

    public static boolean isOnCooldown(String key, UUID player) {
        Map<UUID, Long> map = cooldowns.get(key);
        if (map == null) return false;
        Long until = map.get(player);
        return until != null && until > System.currentTimeMillis();
    }

    public static long getRemaining(String key, UUID player) {
        Map<UUID, Long> map = cooldowns.get(key);
        if (map == null) return 0;
        Long until = map.get(player);
        if (until == null) return 0;
        return Math.max(0, until - System.currentTimeMillis());
    }

    public static void setCooldown(String key, UUID player, long millis) {
        Map<UUID, Long> map = cooldowns.computeIfAbsent(key, k -> new HashMap<>());
        map.put(player, System.currentTimeMillis() + millis);
    }
}

