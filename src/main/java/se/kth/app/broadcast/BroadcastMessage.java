package se.kth.app.broadcast;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by Vilhelm on 28.4.2017.
 */
public class BroadcastMessage implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5669431156447202367L;

    public final KAddress src;
    public final KompicsEvent payload;

    public BroadcastMessage(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BroadcastMessage that = (BroadcastMessage) o;

        if (src != null ? !src.equals(that.src) : that.src != null) return false;
        return payload != null ? payload.equals(that.payload) : that.payload == null;

    }

    @Override
    public int hashCode() {
        int result = src != null ? src.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}
