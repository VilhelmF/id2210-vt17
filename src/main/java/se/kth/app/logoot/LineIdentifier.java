package se.kth.app.logoot;

import java.util.List;

/**
 * Created by sindrikaldal on 15/05/17.
 */
public class LineIdentifier implements Comparable {

    private List<Position> positions;

    public LineIdentifier() {
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
        return this.positions.size() > other.positions.size() ? 1 : 0;
    }
}
