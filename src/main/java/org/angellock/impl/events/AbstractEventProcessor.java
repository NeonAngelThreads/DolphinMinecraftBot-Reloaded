package org.angellock.impl.events;

import org.angellock.impl.Start;
import org.angellock.impl.events.handlers.IPacketHandler;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.PacketErrorEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.geysermc.mcprotocollib.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractEventProcessor<T extends MinecraftPacket> extends SessionAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&l&9PacketHandlers"));
    protected long time_elapse = System.currentTimeMillis();
    private final long DELAY;
    protected List<IActions<T>> actionList = new ArrayList<>();

    public AbstractEventProcessor(long filterDelay){
        this.DELAY = filterDelay;
    }
    public AbstractEventProcessor(){
        this(0);
    }
    public AbstractEventProcessor(IActions<T> action, long filterDelay){
        this(filterDelay);
        this.actionList.add(action);
    }
    public AbstractEventProcessor(IActions<T> action){
        this(action, 0);
    }

    @Override
    public void packetReceived(Session session, Packet packet){
        if (System.currentTimeMillis() - this.time_elapse < this.DELAY){
            return;
        }
        try {
            if(packet != null && this.isTargetPacket(packet)){
                for (IActions<T> reacts : this.actionList) {
                    reacts.onAction((T) packet);
                }
            }
        } catch (ClassCastException omit) {
            return;
        }
    }

    @Override
    public void packetError(PacketErrorEvent event) {
        log.warn(ConsoleTokens.standardizeText(ConsoleTokens.YELLOW + "A packet error was detected: "+ConsoleTokens.GRAY+"At event " + ConsoleTokens.GOLD + event));
        log.error(ConsoleTokens.standardizeText(ConsoleTokens.GRAY + event.getCause().toString()));
        event.setSuppress(false);
    }


    protected abstract boolean isTargetPacket(Packet packet);
    public SessionAdapter addExtraAction(IActions<T> action){
        this.actionList.add(action);
        return this;
    }
}
