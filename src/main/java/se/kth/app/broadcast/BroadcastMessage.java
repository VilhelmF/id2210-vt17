package se.kth.app.broadcast;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by Vilhelm on 28.4.2017.
 */
public class BroadcastMessage implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5669431156447202367L;

    //public final KompicsEvent payload;
    public final String payload;

    public BroadcastMessage(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BroadcastMessage that = (BroadcastMessage) o;

        return payload != null ? payload.equals(that.payload) : that.payload == null;

    }

    @Override
    public int hashCode() {
        return payload != null ? payload.hashCode() : 0;
    }
}
