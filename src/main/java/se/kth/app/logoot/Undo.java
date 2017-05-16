package se.kth.app.logoot;

/**
 * Created by sindrikaldal on 16/05/17.
 */
public class Undo {

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
