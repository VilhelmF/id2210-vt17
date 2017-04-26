package se.kth.app.broadcast.Causal;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sindrikaldal on 26/04/17.
 */
public class CausalData implements KompicsEvent, Serializable {

    public final HashMap<KAddress, KompicsEvent> past;
    public final KompicsEvent payload;

    public CausalData(HashMap<KAddress, KompicsEvent> past, KompicsEvent payload) {
        this.past = past;
        this.payload = payload;
    }
}
