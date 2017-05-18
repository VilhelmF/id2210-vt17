package se.kth.app.broadcast.Causal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BroadcastMessage;
import se.kth.app.broadcast.Reliable.RB_Broadcast;
import se.kth.app.broadcast.Reliable.RB_Deliver;
import se.kth.app.broadcast.Reliable.ReliableBroadcast;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class CausalOrderReliableBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(CausalOrderReliableBroadcast.class);
    private String logPrefix = "";

    //***** Ports *******
    protected final Positive<ReliableBroadcast> rb = requires(ReliableBroadcast.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Negative<CausalBroadcast> crb = provides(CausalBroadcast.class);

    //***** Fields *******
    private HashMap<String, Past> past;
    private HashSet<KompicsEvent> delivered;
    private final KAddress selfAdr;

    public CausalOrderReliableBroadcast(Init init) {
        this.past = init.past;
        this.delivered = init.delivered;
        this.selfAdr = init.selfAdr;
    }

    //***** Handlers *******
    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}Causal Broadcast STARTING...", logPrefix);
        }
    };

    protected final Handler<CRB_Broadcast> crbBroadcastHandler = new Handler<CRB_Broadcast>() {
        @Override
        public void handle(CRB_Broadcast crb_broadcast) {
            trigger(new RB_Broadcast(crb_broadcast.id, crb_broadcast.src, new CausalData(new HashMap<>(past), crb_broadcast.payload)), rb);
            past.put(crb_broadcast.id, new Past((BroadcastMessage) crb_broadcast.payload, selfAdr));
        }
    };

    protected final Handler<RB_Deliver> rbDeliverHandler = new Handler<RB_Deliver>() {
        @Override
        public void handle(RB_Deliver rb_deliver) {
            CausalData data = (CausalData) rb_deliver.payload;
            if (!delivered.contains(data.payload)) {
                for (String key : data.past.keySet()) {
                    Past pastObject = data.past.get(key);
                    if (!delivered.contains(pastObject.message)) {
                        trigger(new CRB_Deliver(key, pastObject.src, pastObject.message), crb);
                        delivered.add(pastObject.message);
                        if (!past.containsKey(key)) {
                            past.put(key, pastObject);
                        }
                    }
                }
                trigger(new CRB_Deliver(rb_deliver.id, rb_deliver.src, data.payload), crb);
                delivered.add(data.payload);
                if (!past.containsKey(rb_deliver.id)) {
                    past.put(rb_deliver.id, new Past((BroadcastMessage) data.payload, rb_deliver.src));
                }
            }
        }
    };


    public static class Init extends se.sics.kompics.Init<CausalOrderReliableBroadcast> {

        public HashMap<String, Past> past;
        public HashSet<KompicsEvent> delivered;
        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
            this.delivered = new HashSet<>();
            this.past = new HashMap<>();
        }
    }

    {
        subscribe(handleStart, control);
        subscribe(rbDeliverHandler, rb);
        subscribe(crbBroadcastHandler, crb);
    }

}
