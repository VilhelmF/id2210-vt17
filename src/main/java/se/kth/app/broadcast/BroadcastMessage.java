package se.kth.app.broadcast;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by Vilhelm on 28.4.2017.
 */
public class BroadcastMessage implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5669431156447202367L;

    public final String message;

    public BroadcastMessage(String message) {
        this.message = message;
    }


}
