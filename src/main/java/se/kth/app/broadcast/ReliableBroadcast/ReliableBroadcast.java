package se.kth.app.broadcast.ReliableBroadcast;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BestEffort.BestEffortBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.croupier.CroupierPort;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class ReliableBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(ReliableBroadcast.class);

    //***** Ports *******
    protected final Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);
    protected final Positive<CroupierPort> ps = requires(CroupierPort.class);
    protected final Positive<Network> net = requires(Network.class);

    //***** Fields *******
    private

    public ReliableBroadcast(Init init) {

    }

}
