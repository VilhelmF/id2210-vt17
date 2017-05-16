package se.kth.app.logoot;

import se.kth.app.logoot.Operation.Operation;
import se.kth.app.logoot.Operation.OperationType;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.util.network.KContentMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Logoot extends ComponentDefinition {

    protected List<Patch> historyBuffer = new ArrayList<>();
    protected List<LineIdentifier> identifierTable = new ArrayList<>();
    protected final Positive<Network> net = requires(Network.class);


    /**
     * Generates N identifiers between the line identifier p and the line identifier q
     * @param p
     * @param q
     * @param boundary
     * @param site
     * @return
     */
    public LineIdentifier generateLineID(LineIdentifier p, LineIdentifier q, int N, int boundary, Position site) {

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
            positions.add(constructID(r + ThreadLocalRandom.current().nextInt(1, step + 1), p, q, site));
            r += step;
        }

        return new LineIdentifier(positions);
    }

    public Position constructID(int r, LineIdentifier p, LineIdentifier q, Position site) {

        Position id = new Position();

        int s, d, c;

        String str = Integer.toString(r);

        for (int i = 1; i < str.length(); i++) {
            d = Integer.parseInt(str.substring(i, i + 1));
            if (d == p.getPositions().get(i).getDigit()) {
                s = p.getPositions().get(i).getSiteID();
                c = p.getPositions().get(i).getClockValue();
            } else if (d == p.getPositions().get(i).getDigit()) {
                s = q.getPositions().get(i).getSiteID();
                c = q.getPositions().get(i).getClockValue();
            } else {
                s = site.getSiteID();
                c = site.getClockValue() + 1;
            }
            id = new Position(d, s, c); //TODO
        }

        return id;
    }

    public void execute(Patch patch) {
        for (Operation op : patch.getOperations()) {
            if (op.getType().equals(OperationType.INSERT)) {

            } else {

            }
        }
    }

    protected final ClassMatchedHandler deliverPatchHandler
            = new ClassMatchedHandler<Patch, KContentMsg<?, ?, Patch>>() {

        @Override
        public void handle(Patch content, KContentMsg<?, ?, Patch> container) {
            execute(content);
            historyBuffer.add(content);
        }
    };

    protected final ClassMatchedHandler deliverUndoHandler
            = new ClassMatchedHandler<Undo, KContentMsg<?, ?, Undo>>() {

        @Override
        public void handle(Undo content, KContentMsg<?, ?, Undo> container) {

        }
    };


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

    {
        subscribe(deliverPatchHandler, net);
        subscribe(deliverUndoHandler, net);
    }


}
