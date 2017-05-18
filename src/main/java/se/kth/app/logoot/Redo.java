package se.kth.app.logoot;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 16/05/17.
 */
public class Redo implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5669432326447202367L;

    private int patchID;

    public Redo(int patchID) {
        this.patchID = patchID;
    }

    public int getPatchID() {
        return patchID;
    }

    public void setPatchID(int patchID) {
        this.patchID = patchID;
    }
}
