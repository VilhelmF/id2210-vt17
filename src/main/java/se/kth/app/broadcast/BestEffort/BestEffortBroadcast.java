package se.kth.app.broadcast.BestEffort;

import se.kth.networking.CroupierMessage;
import se.sics.kompics.PortType;

/**
 * Created by sindrikaldal on 24/04/17.
 */
public class BestEffortBroadcast extends PortType {
    {
        request(GBEB_Broadcast.class);
        request(CroupierMessage.class);
        indication(GBEB_Deliver.class);
    }
}
