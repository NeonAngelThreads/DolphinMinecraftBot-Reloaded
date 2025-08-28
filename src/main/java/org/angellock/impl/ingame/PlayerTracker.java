package org.angellock.impl.ingame;

import org.geysermc.mcprotocollib.auth.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTracker {
    private final static Map<UUID, Player> onlinePlayers = new HashMap<>();
    private final static Map<Integer, UUID> UUIDMapping = new HashMap<>();
    private final static Map<String, UUID> playerUUIDMapping = new HashMap<>();

    public static Map<UUID, Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public static Map<Integer, UUID> getUUIDMapping() {
        return UUIDMapping;
    }

    private @Nullable
    static Player getPlayerById(int entityID) {
        UUID uuid = UUIDMapping.get(entityID);
        if (uuid != null) {
            return onlinePlayers.get(uuid);
        }
        return null;
    }

    public static Player getPlayerByUUID(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public @Nullable
    static Player getPlayerByName(String name) {
        UUID uuid = playerUUIDMapping.get(name);
        if (uuid != null) {
            return onlinePlayers.get(uuid);
        }
        return null;
    }

    public static void putPlayer(@NotNull Player player) {
        @Nullable GameProfile profile = player.getProfile();
        if (profile != null) {
            UUID uuid = profile.getId();
            onlinePlayers.put(uuid, player);
            playerUUIDMapping.put(profile.getName(), uuid);
            UUIDMapping.put(player.getId(), uuid);
        }
    }

    public static Map<String, UUID> getPlayerUUIDMapping() {
        return playerUUIDMapping;
    }
}
