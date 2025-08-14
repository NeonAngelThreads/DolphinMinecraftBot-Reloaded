package org.angellock.impl;

import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;

public class ChatMessageManager implements ISendable{

    protected static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&7ChatMessageManager"));
    private final Queue<String> chatMessageQueue = new ArrayDeque<>();
    private final Session serverSession;

    public ChatMessageManager(Session serverSession) {
        this.serverSession = serverSession;
    }

    public void putMessage(String msg){
        this.chatMessageQueue.offer(msg);
    }

    public void pollMessage(){
        String removal = this.chatMessageQueue.poll();
        if(removal != null) {
            this.sendMessagePacket(removal);
        }
    }

    private void sendMessagePacket(String message){
        MinecraftPacket msgPacket = new ServerboundChatPacket(message, Instant.now().toEpochMilli(), 0L, null, 0, new BitSet());
        log.info(ConsoleTokens.colorizeText("&7Sending in-game chat message: &b&l&o{}"), message);
        this.sendPacket(msgPacket);
    }

    public Queue<String> getChatMessageQueue() {
        return chatMessageQueue;
    }

    @Override
    public void sendPacket(MinecraftPacket packet) {
        this.serverSession.send(packet);
    }
}
