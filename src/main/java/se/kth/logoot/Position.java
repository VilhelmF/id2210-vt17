package se.kth.logoot;

import se.kth.networking.NetAddress;

public class Position {

    private int i;
    private NetAddress src;
    private int clockValue;

    public Position(int i, NetAddress src, int clockValue) {
        this.i = i;
        this.src = src;
        this.clockValue = clockValue;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public NetAddress getSrc() {
        return src;
    }

    public void setSrc(NetAddress src) {
        this.src = src;
    }

    public int getClockValue() {
        return clockValue;
    }

    public void setClockValue(int clockValue) {
        this.clockValue = clockValue;
    }
}
