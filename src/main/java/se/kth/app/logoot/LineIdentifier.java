package se.kth.app.logoot;

import java.util.List;

/**
 * Created by sindrikaldal on 15/05/17.
 */
public class LineIdentifier {

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
}
