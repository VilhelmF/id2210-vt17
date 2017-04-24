package se.kth.app.broadcast;

import se.sics.kompics.PortType;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class BestEffortBroadcast extends PortType {

    {
        request(BEB_Broadcast.class);
        indication(BEB_Deliver.class);
    }
}
