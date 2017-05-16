package se.kth.logoot;

import se.kth.networking.NetAddress;

public class Position {

    private int digit;
    private NetAddress src;
    private int clockValue;

    public Position(int digit, NetAddress src, int clockValue) {
        this.digit = digit;
        this.src = src;
        this.clockValue = clockValue;
    }

    public int getDigit() {
        return digit;
    }

    public void setDigit(int digit) {
        this.digit = digit;
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
