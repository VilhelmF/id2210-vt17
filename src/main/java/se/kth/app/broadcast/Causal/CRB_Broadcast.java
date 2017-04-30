package se.kth.app.broadcast.Causal;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class CRB_Broadcast implements KompicsEvent, Serializable {

    public final KAddress src;
    public final KompicsEvent payload;
    public final String id;

    public CRB_Broadcast(String id, KAddress src, KompicsEvent payload) {
        this.id = id;
        this.src = src;
        this.payload = payload;
    }
}
