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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Past past = (Past) o;

        if (message != null ? !message.equals(past.message) : past.message != null) return false;
        return src != null ? src.equals(past.src) : past.src == null;

    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (src != null ? src.hashCode() : 0);
        return result;
    }
}
