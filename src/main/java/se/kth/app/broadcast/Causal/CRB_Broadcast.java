package se.kth.app.broadcast.Causal;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class CRB_Broadcast implements KompicsEvent, Serializable {

    public final KompicsEvent payload;

    public CRB_Broadcast(KompicsEvent payload) {
        this.payload = payload;
    }
}
