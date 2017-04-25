package se.kth.app.broadcast.Reliable;

import se.sics.kompics.PortType;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class ReliableBroadcast extends PortType {
    {
        indication(RB_Deliver.class);
        request(RB_Broadcast.class);
    }
}
