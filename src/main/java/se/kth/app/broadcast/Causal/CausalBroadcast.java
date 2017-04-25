package se.kth.app.broadcast.Causal;

import se.sics.kompics.PortType;

/**
 * Created by sindrikaldal on 25/04/17.
 */
public class CausalBroadcast extends PortType{
    {
        request(CRB_Broadcast.class);
        indication(CRB_Deliver.class);
    }
}
