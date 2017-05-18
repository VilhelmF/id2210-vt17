package se.kth.app.logoot;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 16/05/17.
 */
public class Undo implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5669431156447293867L;

    private int patchID;

    public Undo(int patchID) {
        this.patchID = patchID;
    }

    public int getPatchID() {
        return patchID;
    }

    public void setPatchID(int patchID) {
        this.patchID = patchID;
    }
}
