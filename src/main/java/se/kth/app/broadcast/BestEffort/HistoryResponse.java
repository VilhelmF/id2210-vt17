package se.kth.app.broadcast.BestEffort;

import se.kth.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class HistoryResponse implements KompicsEvent, Serializable {

    protected final HashMap<KAddress, KompicsEvent> history;

    public HistoryResponse(HashMap<KAddress, KompicsEvent> history) {
        this.history = history;
    }
}
