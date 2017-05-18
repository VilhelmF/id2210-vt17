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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CRB_Broadcast that = (CRB_Broadcast) o;

        if (src != null ? !src.equals(that.src) : that.src != null) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result = src != null ? src.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
