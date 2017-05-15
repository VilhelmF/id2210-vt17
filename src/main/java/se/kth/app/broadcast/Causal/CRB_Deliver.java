package se.kth.app.broadcast.Causal;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class CRB_Deliver implements KompicsEvent, Serializable {

    //public final KAddress src;
    public final KompicsEvent payload;
    public final String id;

    public CRB_Deliver(String id, KompicsEvent payload) {
        this.id = id;
        //this.src = src;
        this.payload = payload;
    }
}
