package se.kth.app.logoot;

public class Position implements Comparable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (digit != position.digit) return false;
        if (siteID != position.siteID) return false;
        return clockValue == position.clockValue;

    }

    @Override
    public int hashCode() {
        int result = digit;
        result = 31 * result + siteID;
        result = 31 * result + clockValue;
        return result;
    }

    @Override
    public int compareTo(Object o) {
        Position other = (Position) o;

        if (this.digit <= other.digit) {
            if (this.digit < other.digit) {
                return -1;
            } else {
                if (this.siteID <= other.siteID) {
                    if (this.siteID < other.siteID) {
                        return -1;
                    } else {
                        if (this.clockValue <= other.clockValue) {
                            if (this.clockValue < other.clockValue) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            return 1;
                        }
                    }
                } else {
                    return 1;
                }
            }
        } else {
            return 1;
        }
    }
}
