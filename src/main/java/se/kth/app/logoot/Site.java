package se.kth.app.logoot;

import java.io.Serializable;

/**
 * Created by sindrikaldal on 18/05/17.
 */
public class Site implements Serializable{

    private static final long serialVersionUID = -5669431156447293867L;

    private int id;
    private int clock;

    public Site(int id, int clock) {
        this.id = id;
        this.clock = clock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }
}
