package me.jahaziel.weapons.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<String, Map<UUID, Long>> cd = new HashMap<>();

    public static boolean isOnCooldown(String ability, UUID player) {
        Map<UUID, Long> m = cd.get(ability);
        if (m == null) return false;
        Long until = m.get(player);
        if (until == null) return false;
        if (System.currentTimeMillis() >= until) { m.remove(player); return false; }
        return true;
    }

    public static void setCooldown(String ability, UUID player, long seconds) {
        cd.putIfAbsent(ability, new HashMap<>());
        cd.get(ability).put(player, System.currentTimeMillis() + seconds * 1000L);
    }

    public static int getRemaining(String ability, UUID player) {
        Map<UUID, Long> m = cd.get(ability);
        if (m == null) return 0;
        Long until = m.get(player);
        if (until == null) return 0;
        long left = until - System.currentTimeMillis();
        return left > 0 ? (int) (left / 1000) : 0;
    }
}
