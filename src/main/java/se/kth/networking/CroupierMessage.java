package se.kth.networking;

import se.sics.kompics.KompicsEvent;
import se.sics.ktoolbox.croupier.event.CroupierSample;

import java.io.Serializable;

/**
 * Created by vilhelm on 2017-04-26.
 */
public class CroupierMessage implements KompicsEvent, Serializable {

    public final CroupierSample croupierSample;

    public CroupierMessage(CroupierSample croupierSample) {
        this.croupierSample = croupierSample;
    }
}
