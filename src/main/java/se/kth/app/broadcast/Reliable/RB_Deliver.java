package se.kth.app.broadcast.Reliable;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class RB_Deliver implements KompicsEvent, Serializable{

    public final KAddress src;
    public final KompicsEvent payload;
    public final String id;

    public RB_Deliver(String id, KAddress src, KompicsEvent payload) {
        this.id = id;
        this.src = src;
        this.payload = payload;
    }
}
