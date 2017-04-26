package se.kth.app.broadcast.Reliable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BestEffort.BestEffortBroadcast;
import se.kth.app.broadcast.BestEffort.GBEB_Broadcast;
import se.kth.app.broadcast.BestEffort.GBEB_Deliver;
import se.kth.app.broadcast.BestEffort.OriginatedData;
import se.sics.kompics.*;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.HashSet;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class EagerReliableBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(EagerReliableBroadcast.class);
    private String logPrefix = "";

    protected final Positive<BestEffortBroadcast> gbeb = requires(BestEffortBroadcast.class);
    protected final Negative<ReliableBroadcast> rb = provides(ReliableBroadcast.class);


    //******* Fields ******
    private KAddress selfAdr;
    private HashSet<KompicsEvent> delivered;

    public EagerReliableBroadcast(Init init) {
        this.selfAdr = init.selfAdr;
        this.delivered = init.delivered;

    }

    //******* Handlers ******
    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}Eager reliable broadcast STARTING...", logPrefix);
        }
    };
    protected final Handler<RB_Broadcast> rbBroadcastHandler = new Handler<RB_Broadcast>() {

        @Override
        public void handle(RB_Broadcast broadcastMessage) {
            trigger(new GBEB_Broadcast(broadcastMessage.src, new OriginatedData(selfAdr, broadcastMessage.payload)), gbeb);
        }
    };

    protected final Handler<GBEB_Deliver> gbebDeliverHandler = new Handler<GBEB_Deliver>() {
        @Override
        public void handle(GBEB_Deliver data) {
            OriginatedData originatedData = (OriginatedData) data.payload;
            if (!delivered.contains(originatedData.payload)) {
                delivered.add(originatedData.payload);
                trigger(new RB_Deliver(originatedData.src, originatedData.payload), rb);
                trigger(new GBEB_Broadcast(originatedData.src, new OriginatedData(selfAdr, originatedData.payload)), gbeb);
            }
        }
    };

    {
        subscribe(rbBroadcastHandler, rb);
        subscribe(gbebDeliverHandler, gbeb);
    }


    public static class Init extends se.sics.kompics.Init<EagerReliableBroadcast> {

        public HashSet<KompicsEvent> delivered;
        public final KAddress selfAdr;

        public Init(KAddress selfAdr) {
            this.selfAdr = selfAdr;
            this.delivered = new HashSet<>();
        }
    }

    {
        subscribe(handleStart, control);
    }

}
