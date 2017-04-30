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
}
