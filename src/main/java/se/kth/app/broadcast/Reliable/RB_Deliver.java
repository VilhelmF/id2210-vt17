package se.kth.app.broadcast.Reliable;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class RB_Deliver implements KompicsEvent, Serializable{

    protected final NetAddress src;
    protected final KompicsEvent payload;

    public RB_Deliver(NetAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }
}
