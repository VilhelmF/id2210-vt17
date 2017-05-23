package se.kth.app.broadcast.BestEffort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BroadcastMessage;
import se.kth.app.broadcast.Causal.CausalData;
import se.kth.app.broadcast.Reliable.ReliableBroadcast;
import se.kth.croupier.util.CroupierHelper;
import se.kth.networking.CroupierMessage;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class GossipingBestEffortBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(GossipingBestEffortBroadcast.class);
    private String logPrefix = "";


    //***** Ports *******
    protected final Positive<ReliableBroadcast> rbd = requires(ReliableBroadcast.class);
    protected final Positive<CroupierPort> ps = requires(CroupierPort.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Negative<BestEffortBroadcast> gbeb = provides(BestEffortBroadcast.class);

    //***** Fields *******
    //private HashMap<String, KompicsEvent> past;
    private List<OriginatedData> past;
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

        }
    };

    protected final Handler<GBEB_Broadcast> broadcastHandler = new Handler<GBEB_Broadcast>() {
        @Override
        public void handle(GBEB_Broadcast message) {
            try {
                past.add(new OriginatedData(self, message.payload));
            } catch (Exception e) {
               e.printStackTrace();
            }
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
                KHeader header = new BasicHeader(self, peer, Transport.TCP);
                KContentMsg msg = new BasicContentMsg(header, new HistoryRequest());
                trigger(msg, net);
            }
        }
    };

    protected final ClassMatchedHandler handleHistoryRequest
            = new ClassMatchedHandler<HistoryRequest, KContentMsg<?, ?, HistoryRequest>>() {

        @Override
        public void handle(HistoryRequest content, KContentMsg<?, ?, HistoryRequest> container) {
            trigger(container.answer(new HistoryResponse(new ArrayList<>(past))), net);
        }
    };

    protected final ClassMatchedHandler handleHistoryResponse
            = new ClassMatchedHandler<HistoryResponse, KContentMsg<?, ?, HistoryResponse>>() {

        @Override
        public void handle(HistoryResponse content, KContentMsg<?, ?, HistoryResponse> container) {
            List<OriginatedData> unseen = new ArrayList<>(content.history);
            unseen.removeAll(past);

            for (OriginatedData obj : unseen) {
                try {

                    trigger(new GBEB_Deliver(obj.src, obj.payload), gbeb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            past.addAll(unseen);
        }
    };

    public static class Init extends se.sics.kompics.Init<GossipingBestEffortBroadcast> {
        private List<OriginatedData> past;
        private KAddress self;

        public Init(KAddress address) {
            this.self = address;
            this.past = new ArrayList<>();
        }

    }

    {
        subscribe(broadcastHandler, gbeb);
        subscribe(sampleHandler, ps);
        subscribe(handleHistoryRequest, net);
        subscribe(handleHistoryResponse, net);
        subscribe(handleStart, control);
    }


}
