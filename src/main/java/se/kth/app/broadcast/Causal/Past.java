package se.kth.app.broadcast.Causal;

import se.kth.app.broadcast.BroadcastMessage;
import se.sics.ktoolbox.util.network.KAddress;

import java.io.Serializable;

/**
 * Created by vilhelm on 2017-05-18.
 */
public class Past implements Serializable {

    public final BroadcastMessage message;
    public final KAddress src;


    public Past(BroadcastMessage message, KAddress src) {
        this.message = message;
        this.src = src;
    }

}
