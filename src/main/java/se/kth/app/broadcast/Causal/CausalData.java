package se.kth.app.broadcast.Causal;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sindrikaldal on 26/04/17.
 */
public class CausalData implements KompicsEvent, Serializable {

    public final List<Past> past;
    public final KompicsEvent payload;

    public CausalData(List<Past> past, KompicsEvent payload) {
        this.past = past;
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CausalData that = (CausalData) o;

        if (past != null ? !past.equals(that.past) : that.past != null) return false;
        return payload != null ? payload.equals(that.payload) : that.payload == null;

    }

    @Override
    public int hashCode() {
        int result = past != null ? past.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}
