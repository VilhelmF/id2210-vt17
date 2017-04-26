package se.kth.app.broadcast.Causal;

import se.kth.app.broadcast.BestEffort.OriginatedData;
import se.kth.app.broadcast.Reliable.RB_Broadcast;
import se.kth.app.broadcast.Reliable.RB_Deliver;
import se.kth.app.broadcast.Reliable.ReliableBroadcast;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class CausalOrderReliableBroadcast extends ComponentDefinition {

    //***** Ports *******
    protected final Positive<ReliableBroadcast> rb = requires(ReliableBroadcast.class);
    protected final Negative<CausalBroadcast> crb = provides(CausalBroadcast.class);

    //***** Fields *******
    private HashMap<KAddress, KompicsEvent> past;
    private HashSet<KompicsEvent> delivered;
    private final KAddress selfAdr;

    public CausalOrderReliableBroadcast(Init init) {
        this.past = init.past;
        this.delivered = init.delivered;
        this.selfAdr = init.selfAdr;
    }

    //***** Handlers *******
    protected final Handler<CRB_Broadcast> crbBroadcastHandler = new Handler<CRB_Broadcast>() {
        @Override
        public void handle(CRB_Broadcast crb_broadcast) {
            trigger(new RB_Broadcast(selfAdr, new CausalData(past, crb_broadcast.payload)), rb);
            delivered.add(crb_broadcast.payload);
        }
    };

    protected final Handler<RB_Deliver> rbDeliverHandler = new Handler<RB_Deliver>() {
        @Override
        public void handle(RB_Deliver rb_deliver) {
            CausalData data = (CausalData) rb_deliver.payload;
            if (!delivered.contains(data.payload)) {
                for (KAddress key : data.past.keySet()) {
                    if (!delivered.contains(data.past.get(key))) {
                        trigger(new CRB_Deliver(key, data.past.get(key)), crb);
                        delivered.add(data.past.get(key));
                        if (!past.containsKey(key)) {
                            past.put(key, data.past.get(key));
                        }
                    }
                }
                trigger(new CRB_Deliver(rb_deliver.src, data.payload), crb);
                delivered.add(data.payload);
                if (!past.containsKey(rb_deliver.src)) {
                    past.put(rb_deliver.src, data.payload);
                }
            }
        }
    };



    public static class Init extends se.sics.kompics.Init<CausalOrderReliableBroadcast> {

        public HashMap<KAddress, KompicsEvent> past;
        public HashSet<KompicsEvent> delivered;
        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
            this.delivered = new HashSet<>();
            this.past = new HashMap<>();
        }
    }


}
