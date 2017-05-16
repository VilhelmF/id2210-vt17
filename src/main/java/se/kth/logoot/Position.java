package se.kth.logoot;

import se.kth.networking.NetAddress;

public class Position {

    private int digit;
    private int siteID;
    private int clockValue;

    public Position() {

    }

    public Position(int digit, int siteID, int clockValue) {
        this.digit = digit;
        this.siteID = siteID;
        this.clockValue = clockValue;
    }

    public int getDigit() {
        return digit;
    }

    public void setDigit(int digit) {
        this.digit = digit;
    }

    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int getClockValue() {
        return clockValue;
    }

    public void setClockValue(int clockValue) {
        this.clockValue = clockValue;
    }
}
