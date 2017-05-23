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

    public RB_Deliver(KAddress src, KompicsEvent payload) {
        this.src = src;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RB_Deliver that = (RB_Deliver) o;

        if (src != null ? !src.equals(that.src) : that.src != null) return false;
        return (payload != null ? !payload.equals(that.payload) : that.payload != null);

    }

    @Override
    public int hashCode() {
        int result = src != null ? src.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}
