package se.kth.app.broadcast.Reliable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BestEffort.BestEffortBroadcast;
import se.kth.app.broadcast.BestEffort.GBEB_Broadcast;
import se.kth.app.broadcast.BestEffort.OriginatedData;
import se.kth.networking.Message;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashSet;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class EagerReliableBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(EagerReliableBroadcast.class);

    protected final Positive<BestEffortBroadcast> gbeb = requires(BestEffortBroadcast.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Negative<ReliableBroadcast> rb = provides(ReliableBroadcast.class);


    //******* Fields ******
    private HashSet<KompicsEvent> delivered = new HashSet<>();

    protected final Handler<RB_Broadcast> rbBroadcastHandler = new Handler<RB_Broadcast>() {

        @Override
        public void handle(RB_Broadcast broadcastMessage) {
            LOG.info("SENDING NEW ORIGINATED DATA");
            trigger(new GBEB_Broadcast(broadcastMessage.src, new OriginatedData(broadcastMessage.src, broadcastMessage.payload)), gbeb);
        }
    };

    protected final ClassMatchedHandler<OriginatedData, Message> bebDeliverHandler = new ClassMatchedHandler<OriginatedData, Message>() {

        @Override
        public void handle(OriginatedData data, Message context) {
            LOG.info("RB: Received OriginatedData from net");
            LOG.info("Payload : " + data.payload.toString());
            LOG.info("Delivered size : " + delivered.size());
            if (!delivered.contains(data.payload)) {
                delivered.add(data.payload);
                LOG.info("Delivered contains : " + delivered.contains(data.payload));
                trigger(new RB_Deliver(data.src, data.payload), rb);
                trigger(new GBEB_Broadcast(context.getSource(), data), gbeb);
            } else {
                LOG.info("DIDN'T SEND BEB_BROADCAST. HAD DATA");
            }
        }
    };

    {
        subscribe(rbBroadcastHandler, rb);
        subscribe(bebDeliverHandler, net);
    }


}
