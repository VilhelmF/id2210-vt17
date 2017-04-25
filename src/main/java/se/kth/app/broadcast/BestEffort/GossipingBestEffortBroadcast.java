package se.kth.app.broadcast.BestEffort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.networking.Message;
import se.kth.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class GossipingBestEffortBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(GossipingBestEffortBroadcast.class);

    //***** Ports *******
    protected final Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);
    protected final Positive<CroupierPort> ps = requires(CroupierPort.class);
    protected final Positive<Network> net = requires(Network.class);

    //***** Fields *******
    private HashMap<NetAddress, KompicsEvent> past;
    private HashMap<NetAddress, KompicsEvent> unseen;


    public GossipingBestEffortBroadcast(Init init) {
        this.past = init.past;
        this.unseen = init.unseen;
    }

    //***** Handlers *******
    protected final Handler<BEB_Broadcast> broadcastHandler = new Handler<BEB_Broadcast>() {
        @Override
        public void handle(BEB_Broadcast beb_broadcast) {
            past.put(beb_broadcast.src, beb_broadcast.payload);
        }
    };

    protected final Handler<CroupierSample> sampleHandler = new Handler<CroupierSample>() {
        @Override
        public void handle(CroupierSample croupierSample) {
            //TODO
        }
    };

    protected final ClassMatchedHandler<BEB_Deliver, Message> bebDeliverHandler = new ClassMatchedHandler<BEB_Deliver, Message>() {
        @Override
        public void handle(BEB_Deliver beb_deliver, Message message) {
            past.put(message.getSource(), beb_deliver.payload);
        }
    };


    {
        subscribe(broadcastHandler, beb);
        subscribe(sampleHandler, ps);
        subscribe(bebDeliverHandler, net);
    }

    public static class Init extends se.sics.kompics.Init<GossipingBestEffortBroadcast> {
        private HashMap<NetAddress, KompicsEvent> past;
        private HashMap<NetAddress, KompicsEvent> unseen;
        private NetAddress src;

        public Init() {
            this.past = new HashMap<>();
            this.unseen = new HashMap<>();
        }
    }

}
