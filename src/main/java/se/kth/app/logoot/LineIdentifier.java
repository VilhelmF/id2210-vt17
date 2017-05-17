package se.kth.app.logoot;

import se.kth.app.logoot.Operation.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sindrikaldal on 15/05/17.
 */
public class LineIdentifier implements Comparable {

    private List<Position> positions;

    public LineIdentifier() {
        this.positions = new ArrayList<>();
    }

    public LineIdentifier(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public void addPosition(Position position) {
        this.positions.add(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineIdentifier that = (LineIdentifier) o;

        return positions != null ? positions.equals(that.positions) : that.positions == null;

    }

    @Override
    public int hashCode() {
        return positions != null ? positions.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        LineIdentifier other = (LineIdentifier) o;

        int index = Math.min(this.positions.size(), other.positions.size());

        for (int i = 0; i < index; i++) {
            Position myPosition = this.positions.get(i);
            Position otherPosition = other.positions.get(i);

            if (!myPosition.equals(otherPosition)) {
                return myPosition.compareTo(otherPosition);
            }
        }

        if (this.positions.size() == other.positions.size()) {
            return 0;
        }

        return this.positions.size() > other.positions.size() ? 1 : -1;
    }

    public String printPositions() {

        StringBuilder str = new StringBuilder();

        for (Position pos : this.positions) {
            str.append("<" + pos.getDigit() + ", " + pos.getSiteID() + ", " + pos.getClockValue() + ">");
        }

        return str.toString();
    }
}
