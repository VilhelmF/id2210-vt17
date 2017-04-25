package se.kth.app.broadcast.Causal;

import se.kth.app.broadcast.Reliable.ReliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class CausalOrderReliableBroadcast extends ComponentDefinition {

    protected final Positive<ReliableBroadcast> rb = requires(ReliableBroadcast.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Negative<CausalBroadcast> crb = provides(CausalBroadcast.class);

    
}
