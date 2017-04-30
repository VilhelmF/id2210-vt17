package se.kth.app.broadcast.BestEffort;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class GBEB_Broadcast implements KompicsEvent, Serializable {

    public final KAddress src;
    public final KompicsEvent payload;
    public final String id;

    public GBEB_Broadcast(String id, KAddress src, KompicsEvent payload) {
        this.id = id;
        this.src = src;
        this.payload = payload;
    }
}
