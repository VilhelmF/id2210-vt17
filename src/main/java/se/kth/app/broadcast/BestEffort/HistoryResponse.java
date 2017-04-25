package se.kth.app.broadcast.BestEffort;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class HistoryResponse implements KompicsEvent, Serializable {

    protected final HashMap<NetAddress, KompicsEvent> history;

    public HistoryResponse(HashMap<NetAddress, KompicsEvent> history) {
        this.history = history;
    }
}
