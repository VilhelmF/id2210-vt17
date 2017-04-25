package se.kth.app.broadcast.ReliableBroadcast;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;


/**
 * Created by vilhelm on 2017-04-25.
 */
public class Reliable_Send implements KompicsEvent, Serializable {
    public final NetAddress src;
    public final KompicsEvent payload;

    public Reliable_Send(NetAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

}
