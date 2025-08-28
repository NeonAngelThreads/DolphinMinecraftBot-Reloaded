package org.angellock.impl.events;

import org.angellock.impl.events.bukkit.ActiveListener;
import org.angellock.impl.events.bukkit.Event;
import org.angellock.impl.util.ConsoleTokens;
import org.geysermc.mcprotocollib.network.Session;
import org.geysermc.mcprotocollib.network.event.session.PacketErrorEvent;
import org.geysermc.mcprotocollib.network.event.session.SessionAdapter;
import org.geysermc.mcprotocollib.network.packet.Packet;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractEventProcessor<T extends MinecraftPacket> extends SessionAdapter {
    private static final Logger log = LoggerFactory.getLogger(ConsoleTokens.colorizeText("&l&9PacketHandlers"));
    protected long time_elapse = System.currentTimeMillis();
    private final long DELAY;
    protected List<IActions<T>> actionList = new ArrayList<>();
    protected IActions<T> preAction = (T) -> {
    };

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
                T packet1 = (T) packet;
                this.preAction.onAction(packet1);
                for (IActions<T> reacts : this.actionList) {
                    reacts.onAction(packet1);
                }
            }
        } catch (ClassCastException omit) {
            return;
        } catch (IllegalArgumentException e) {
            log.warn(ConsoleTokens.colorizeText("&6 {}"), e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            log.warn(ConsoleTokens.colorizeText("&7 {}"), throwable.getStackTrace());
        }
    }

    @Override
    public void packetError(PacketErrorEvent event) {
        log.warn(ConsoleTokens.colorizeText("&eA packet error was detected: &7At event &6" + event));
        log.error(ConsoleTokens.colorizeText("&7" + event.getCause().toString()));
        event.setSuppress(true);
    }

    protected void dispatch(Event event) {
        HandlerMapper mapper = event.getMapper();
        for (ActiveListener listener : mapper.getRegisteredListenersInOrder()) {
            try {
                listener.call(event);
            } catch (Throwable throwable) {
                log.error(ConsoleTokens.colorizeText("&6Could not pass event &7{}"), throwable.getClass(), throwable);
            }
        }
    }

    protected abstract boolean isTargetPacket(Packet packet);
    public SessionAdapter addExtraAction(IActions<T> action){
        this.actionList.add(action);
        return this;
    }
}
