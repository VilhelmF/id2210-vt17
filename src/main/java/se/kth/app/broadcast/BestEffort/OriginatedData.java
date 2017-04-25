package se.kth.app.broadcast.BestEffort;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 23/02/17.
 */
public class OriginatedData implements KompicsEvent, Serializable {
    public final NetAddress src;
    public final KompicsEvent payload;

    public OriginatedData(NetAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }
}
