package se.kth.app.logoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sindrikaldal on 17/05/17.
 */
public class Test {

    public static void main(String[] args) {
        Position position1 = new Position(131,1,4);
        Position position2 = new Position(2471,5,23);
        Position position3 = new Position(131,3,2);

        LineIdentifier lineIdentifier = new LineIdentifier();
        lineIdentifier.getPositions().add(position1);

        LineIdentifier lineIdentifier2 = new LineIdentifier();
        lineIdentifier2.getPositions().add(position1);
        lineIdentifier2.getPositions().add(position2);

        LineIdentifier lineIdentifier3 = new LineIdentifier();
        lineIdentifier3.getPositions().add(position3);

        List<LineIdentifier> identifiers = new ArrayList<>();
        identifiers.add(lineIdentifier3);
        identifiers.add(lineIdentifier2);
        identifiers.add(lineIdentifier);

        Collections.sort(identifiers);

        for (LineIdentifier identifier : identifiers) {
            for (Position position : identifier.getPositions()) {
                System.out.print("<" + position.getDigit() + ", " + position.getSiteID() + ", " + position.getClockValue() + ">");
            }
            System.out.println();
        }
    }
}
