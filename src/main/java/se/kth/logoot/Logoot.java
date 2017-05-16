package se.kth.logoot;

import java.util.ArrayList;
import java.util.List;

public class Logoot {

    /**
     * Generates N identifiers between the line identifier p and the line identifier q
     * @param p
     * @param q
     * @param boundary
     * @param site
     * @return
     */
    public LineIdentifier generateLineID(LineIdentifier p, LineIdentifier q, int N, int boundary, int site) {

        List<Position> positions = new ArrayList<>();

        int index = 0;
        int interval = 0;

        while (interval < N) {
            index++;
            interval = prefix(q.getPositions(), index) - prefix(p.getPositions(), index) - 1;
        }

        int step = Math.min(interval/N, boundary);
        int r = prefix(p.getPositions(), index);

        for (int i = 1; i <= N; i++) {
            positions.add(constructID());
            r += step;
        }

        return new LineIdentifier(positions);
    }

    public Position constructID() {
        return null;
    }

    public void execute() {

    }

    public void deliver() {

    }

    public int prefix(List<Position> positions, int index) {
        String digit = "";


        for (int i = 0; i < index; i++) {
            try {
                digit += Integer.toString(positions.get(i).getDigit());
            } catch (IndexOutOfBoundsException e) {
                digit += "0";
            }
        }

        return Integer.parseInt(digit);

    }
}
