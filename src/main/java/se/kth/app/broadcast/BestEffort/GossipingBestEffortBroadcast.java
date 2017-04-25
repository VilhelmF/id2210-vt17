package se.kth.app.broadcast.BestEffort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.croupier.util.CroupierHelper;
import se.kth.networking.Message;
import se.kth.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.network.KAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class GossipingBestEffortBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(GossipingBestEffortBroadcast.class);

    //***** Ports *******
    protected final Negative<BestEffortBroadcast> gbeb = provides(BestEffortBroadcast.class);
    protected final Positive<CroupierPort> ps = requires(CroupierPort.class);
    protected final Positive<Network> net = requires(Network.class);

    //***** Fields *******
    private HashMap<NetAddress, KompicsEvent> past;
    private NetAddress self;


    public GossipingBestEffortBroadcast(Init init) {
        this.past = init.past;
        this.self = init.self;
    }

    //***** Handlers *******
    protected final Handler<GBEB_Broadcast> broadcastHandler = new Handler<GBEB_Broadcast>() {
        @Override
        public void handle(GBEB_Broadcast GBEB_broadcast) {
            past.put(GBEB_broadcast.src, GBEB_broadcast.payload);
        }
    };

    protected final Handler<CroupierSample> sampleHandler = new Handler<CroupierSample>() {
        @Override
        public void handle(CroupierSample croupierSample) {
            if (croupierSample.publicSample.isEmpty()) {
                return;
            }
            List<KAddress> sample = CroupierHelper.getSample(croupierSample);
            for (KAddress peer : sample) {
                trigger(new Message(self, new NetAddress(peer.getIp(), peer.getPort()), new OriginatedData(null, new HistoryRequest())), net);
            }
        }
    };

    protected final ClassMatchedHandler<OriginatedData, Message> bebDeliverHandler = new ClassMatchedHandler<OriginatedData, Message>() {
        @Override
        public void handle(OriginatedData deliver, Message message) {
            if (message.payload instanceof HistoryRequest) {
                trigger(new Message(self, message.getSource(), new HistoryResponse(past)), net);
            } else if (message.payload instanceof HistoryResponse) {
                HistoryResponse response = (HistoryResponse) message.payload;
                HashMap<NetAddress, KompicsEvent> unseen = difference(response.history, past);

                for (NetAddress key : unseen.keySet()) {
                    trigger(new GBEB_Deliver(unseen.get(key)), gbeb);
                }
                past.putAll(unseen);
            }
        }
    };

    public HashMap<NetAddress, KompicsEvent> difference(HashMap history, HashMap past) {
        HashMap<NetAddress, KompicsEvent> unseen = new HashMap<>();
        unseen.putAll(history);
        unseen.putAll(past);
        unseen.keySet().removeAll(past.keySet());
        return unseen;
    }

    {
        subscribe(broadcastHandler, gbeb);
        subscribe(sampleHandler, ps);
        subscribe(bebDeliverHandler, net);
    }

    public static class Init extends se.sics.kompics.Init<GossipingBestEffortBroadcast> {
        private HashMap<NetAddress, KompicsEvent> past;
        private NetAddress self;

        public Init() {
            this.past = new HashMap<>();
            try {
                this.self = new NetAddress(InetAddress.getLocalHost(), 12345);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

    }

}
