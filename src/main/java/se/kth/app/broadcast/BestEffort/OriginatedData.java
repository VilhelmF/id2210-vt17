package se.kth.app.broadcast.BestEffort;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 23/02/17.
 */
public class OriginatedData implements KompicsEvent, Serializable {
    public final KAddress src;
    public final KompicsEvent payload;

    public OriginatedData(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OriginatedData that = (OriginatedData) o;

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
