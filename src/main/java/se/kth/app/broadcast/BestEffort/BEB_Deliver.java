package se.kth.app.broadcast.BestEffort;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class BEB_Deliver implements KompicsEvent, Serializable {

    public final KompicsEvent payload;

    public BEB_Deliver(KompicsEvent payload) {
        this.payload = payload;
    }
}
