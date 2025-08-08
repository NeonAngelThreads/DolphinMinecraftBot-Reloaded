package org.angellock.impl.extensions;

import org.angellock.impl.AbstractRobot;
import org.angellock.impl.RobotPlayer;
import org.angellock.impl.events.packets.LoginHandler;
import org.angellock.impl.events.packets.PlayerLogInfo;
import org.angellock.impl.events.packets.SystemChatHandler;
import org.angellock.impl.providers.AbstractPlugin;
import org.angellock.impl.util.ConsoleDecorations;
import org.angellock.impl.util.ConsoleTokens;
import org.angellock.impl.util.TextComponentSerializer;
import org.geysermc.mcprotocollib.auth.GameProfile;
import org.geysermc.mcprotocollib.protocol.data.game.ClientCommand;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.HandPreference;
import org.geysermc.mcprotocollib.protocol.data.game.setting.ChatVisibility;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import org.geysermc.mcprotocollib.protocol.packet.common.serverbound.ServerboundCustomPayloadPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatAckPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundInteractPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundSetCarriedItemPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundSwingPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundUseItemPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.time.Instant;
import java.util.*;

public class BaseDefaultPlugin extends AbstractPlugin {
    protected static final Logger log = LoggerFactory.getLogger("BotEntity");
    private static final String VERSION = "0.0.0";
    private static final String NAME = "Base-default-plugin";

    private final Map<UUID, GameProfile> onlinePlayers = new HashMap<>();

    @Override
    public String getPluginName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public void onDisable() {
        log.info("disabling plugin: {}, {}", super.getName(), this.getVersion());
    }

    @Override
    public void onLoad() {
        log.info("loading plugin: {}, {}", super.getName(), this.getVersion());
    }

    @Override
    public void onEnable(AbstractRobot robotEntity) {

        getListeners().add(new LoginHandler().addExtraAction(packet -> {
            log.info(ConsoleTokens.standardizeText(ConsoleDecorations.BOLD.toString() + ConsoleTokens.AQUA + "Successfully logged-in to server world."));

            this.schedulerThread = new Thread(() -> {
                while (true){
                    try {
                        Thread.sleep(1500L);
                        if (!robotEntity.getSession().isConnected()){
                            break;
                        }

                        robotEntity.sendPacket(new ServerboundChatCommandPacket("login " + robotEntity.getPassword()));


                    } catch (InterruptedException e) {
                        throw new RuntimeException();
                    }
                }
            });
            this.schedulerThread.start();

        }));

        getListeners().add(new SystemChatHandler().addExtraAction((packet) -> {
            TextComponentSerializer componentSerializer = new TextComponentSerializer();
            String msg = componentSerializer.serialize(packet.getContent());
            if (msg.contains("/reg <密码> <密码>")){
                robotEntity.resetVerify();
            }
            else if (msg.contains("You are logged in!")){
                joinGame(robotEntity);
            }
            log.info(msg);
        }));

        getListeners().add(new PlayerLogInfo.UpdateHandler().addExtraAction((updatePacket) -> {
                PlayerListEntry[] players = updatePacket.getEntries();

                for (PlayerListEntry player : players) {
                    UUID profileUUID = player.getProfileId();
                    GameProfile playerProfile = player.getProfile();
                    this.onlinePlayers.put(profileUUID, playerProfile);
                    if (playerProfile != null) {
                        log.info(ConsoleTokens.GRAY+"["+ConsoleTokens.GREEN+"+"+ConsoleTokens.GRAY+"]"+this.getLogMsg(playerProfile));
                    }
                }
            }
        ));

        getListeners().add(new PlayerLogInfo.RemoveHandler().addExtraAction((packet -> {
            if(packet.getProfileIds().isEmpty()) {
                return;
            }
            UUID logoutPlayer = packet.getProfileIds().get(0);
            if (this.onlinePlayers.get(logoutPlayer) == null) {
                return;
            }
            GameProfile player = this.onlinePlayers.get(logoutPlayer);

            log.info(ConsoleTokens.GRAY+"["+ConsoleTokens.DARK_RED+"-"+ConsoleTokens.GRAY+"]"+this.getLogMsg(player));
            this.onlinePlayers.remove(logoutPlayer);
        })));

    }

    public String getLogMsg(GameProfile player){

        //log.info(ConsoleTokens.standardizeText(ConsoleTokens.GRAY + Arrays.toString(players)));
        List<GameProfile.Property> playerProperty = player.getProperties();
        String state = (playerProperty.isEmpty()) ? ConsoleTokens.GRAY+" ["+ConsoleTokens.DARK_RED+"盗版"+ConsoleTokens.GRAY+"] " : ConsoleTokens.GRAY+" ["+ConsoleTokens.GREEN+"正版"+ConsoleTokens.GRAY+"] ";
        String playerName = player.getName();
        UUID playerUUID = player.getId();

        return ConsoleTokens.standardizeText(ConsoleTokens.AQUA + playerName + state + ConsoleTokens.GRAY + playerUUID);

//        if(!playerProperty.isEmpty()){
//            log.info(ConsoleTokens.standardizeText(ConsoleTokens.YELLOW + playerName + "的正版皮肤: " + ConsoleTokens.GRAY + playerProperty));
//        }
        //TODO Move this code to skin recorder class
    }

    public void joinGame(AbstractRobot player){
        player.sendPacket(new ServerboundClientInformationPacket("en-us", 12, ChatVisibility.FULL, true, new ArrayList<>(), HandPreference.LEFT_HAND, true, true));
                    player.sendPacket(new ServerboundSetCarriedItemPacket(1));
                    player.sendPacket(new ServerboundUseItemPacket(
                            Hand.MAIN_HAND,
                            (int) Instant.now().toEpochMilli(),
                            (float) Math.random()*90,
                            (float) Math.random()*90
                    ));
    }
}
