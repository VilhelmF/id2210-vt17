package se.kth.app.broadcast.BestEffort;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class HistoryResponse implements KompicsEvent, Serializable {

    protected final HashMap<String, KompicsEvent> history;

    public HistoryResponse(HashMap<String, KompicsEvent> history) {
        this.history = history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryResponse that = (HistoryResponse) o;

        return history != null ? history.equals(that.history) : that.history == null;

    }

    @Override
    public int hashCode() {
        return history != null ? history.hashCode() : 0;
    }
}
