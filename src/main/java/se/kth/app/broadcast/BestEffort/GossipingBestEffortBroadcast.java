package se.kth.app.broadcast.BestEffort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.croupier.util.CroupierHelper;
import se.kth.networking.CroupierMessage;
import se.kth.networking.Message;
import se.kth.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;
import se.sics.ktoolbox.util.network.basic.BasicContentMsg;
import se.sics.ktoolbox.util.network.basic.BasicHeader;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class GossipingBestEffortBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(GossipingBestEffortBroadcast.class);
    private String logPrefix = "";

    //***** Ports *******
    protected final Negative<BestEffortBroadcast> gbeb = provides(BestEffortBroadcast.class);
    protected final Positive<CroupierPort> ps = requires(CroupierPort.class);
    protected final Positive<Network> net = requires(Network.class);

    //***** Fields *******
    private HashMap<KAddress, KompicsEvent> past;
    private KAddress self;


    public GossipingBestEffortBroadcast(Init init) {
        this.past = init.past;
        this.self = init.self;
        logPrefix = "<nid:" + self.getId() + ">";
    }

    //***** Handlers *******
    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}Gossiping Broadcast STARTING...", logPrefix);
        }
    };

    protected final Handler<GBEB_Broadcast> broadcastHandler = new Handler<GBEB_Broadcast>() {
        @Override
        public void handle(GBEB_Broadcast GBEB_broadcast) {
            past.put(GBEB_broadcast.src, GBEB_broadcast.payload);
        }
    };

    protected final Handler<CroupierMessage> sampleHandler = new Handler<CroupierMessage>() {
        @Override
        public void handle(CroupierMessage croupierMessage) {
            CroupierSample croupierSample = croupierMessage.croupierSample;

            System.out.println("Received a croupier sample.");
            if (croupierSample.publicSample.isEmpty()) {
                return;
            }
            /*
            List<KAddress> sample = CroupierHelper.getSample(croupierSample);
            for (KAddress peer : sample) {
                KHeader header = new BasicHeader(self, peer, Transport.TCP);
                KContentMsg msg = new BasicContentMsg(header, new HistoryRequest());
                trigger(msg, net);
            }
            */
        }
    };

    protected final ClassMatchedHandler handleHistoryRequest
            = new ClassMatchedHandler<HistoryRequest, KContentMsg<?, ?, HistoryRequest>>() {

        @Override
        public void handle(HistoryRequest content, KContentMsg<?, ?, HistoryRequest> container) {
            trigger(container.answer(new HistoryResponse(past)), net);
        }
    };

    protected final ClassMatchedHandler handleHistoryResponse
            = new ClassMatchedHandler<HistoryResponse, KContentMsg<?, ?, HistoryResponse>>() {

        @Override
        public void handle(HistoryResponse content, KContentMsg<?, ?, HistoryResponse> container) {
            HashMap<KAddress, KompicsEvent> unseen = difference(content.history, past);

            for (KAddress key : unseen.keySet()) {
                trigger(new GBEB_Deliver(key, unseen.get(key)), gbeb);
            }
            past.putAll(unseen);
        }
    };

    public HashMap<KAddress, KompicsEvent> difference(HashMap history, HashMap past) {
        HashMap<KAddress, KompicsEvent> unseen = new HashMap<>();
        unseen.putAll(history);
        unseen.putAll(past);
        unseen.keySet().removeAll(past.keySet());
        return unseen;
    }

    public static class Init extends se.sics.kompics.Init<GossipingBestEffortBroadcast> {
        private HashMap<KAddress, KompicsEvent> past;
        private KAddress self;

        public Init(KAddress address) {
            this.self = address;
            this.past = new HashMap<>();
        }

    }

    {
        subscribe(broadcastHandler, gbeb);
        subscribe(sampleHandler, gbeb);
        subscribe(handleHistoryRequest, net);
        subscribe(handleHistoryResponse, net);
        subscribe(handleStart, control);
    }


}
