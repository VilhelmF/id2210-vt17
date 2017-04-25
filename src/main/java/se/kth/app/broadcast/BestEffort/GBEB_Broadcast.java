package se.kth.app.broadcast.BestEffort;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class GBEB_Broadcast implements KompicsEvent, Serializable {

    public final NetAddress src;
    public final KompicsEvent payload;

    public GBEB_Broadcast(NetAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }
}
