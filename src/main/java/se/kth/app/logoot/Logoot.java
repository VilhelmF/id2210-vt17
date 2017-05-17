package se.kth.app.logoot;

import se.kth.app.logoot.Operation.Operation;
import se.kth.app.logoot.Operation.OperationType;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.util.network.KContentMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Logoot extends ComponentDefinition {

    protected HashMap<Integer, Patch> historyBuffer = new HashMap<>();
    protected List<LineIdentifier> identifierTable = new ArrayList<>();
    protected HashMap<LineIdentifier, Integer> cemetery = new HashMap<>();
    protected final Positive<Network> net = requires(Network.class);


    /**
     * Generates N identifiers between the line identifier p and the line identifier q
     * @param p
     * @param q
     * @param boundary
     * @param site
     * @return
     */
    public List<LineIdentifier> generateLineID(LineIdentifier p, LineIdentifier q, int N, int boundary, Position site) {

        List<LineIdentifier> identifiers = new ArrayList<>();

        int index = 0;

        int interval = 0;

        while (interval < N) {
            index++;
            interval = prefix(q.getPositions(), index) - prefix(p.getPositions(), index) - 1;
        }

        int step = Math.min(interval/N, boundary);
        int r = prefix(p.getPositions(), index);

        for (int i = 1; i <= N; i++) {
            identifiers.add(constructID(r + ThreadLocalRandom.current().nextInt(1, step + 1), p, q, site));
            r += step;
        }

        return identifiers;
    }

    public LineIdentifier constructID(int r, LineIdentifier p, LineIdentifier q, Position site) {

        LineIdentifier id = new LineIdentifier();

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
            id.addPosition(new Position(d, s, c)); //TODO
        }

        return id;
    }

    public void execute(Patch patch) {
        for (Operation op : patch.getOperations()) {
            if (op.getType().equals(OperationType.INSERT)) {
                int degree = cemetery.get(op.getId()) + 1;
                if (degree == 1) {
                    int position = positionBinarySearch(op.getId());
                    //TODO document.insert
                    //TODO idTable.insert(position, id);

                } else {
                    cemetery.put(op.getId(), degree);
                }
            } else {
                int position = positionBinarySearch(op.getId());
                int degree = 0;
                if (identifierTable.get(position).equals(op.getId())) {
                    // TODO document.remove
                    // TODO idT able.remove(position, id);
                } else {
                    degree = cemetery.get(op.getId()) - 1;
                }
                cemetery.put(op.getId(), degree);
            }
        }
    }

    protected final ClassMatchedHandler deliverPatchHandler
            = new ClassMatchedHandler<Patch, KContentMsg<?, ?, Patch>>() {

        @Override
        public void handle(Patch content, KContentMsg<?, ?, Patch> container) {
            execute(content);
            content.setDegree(1);
            historyBuffer.put(content.getId(), content);
        }
    };

    protected final ClassMatchedHandler deliverUndoHandler
            = new ClassMatchedHandler<Undo, KContentMsg<?, ?, Undo>>() {

        @Override
        public void handle(Undo content, KContentMsg<?, ?, Undo> container) {
            Patch patch = historyBuffer.get(content.getPatchID());
            patch.decrementDegree();
            if (patch.getDegree() == 0) {
                execute(inverse(patch));
            }
        }
    };

    protected final ClassMatchedHandler deliverRedoHandler
            = new ClassMatchedHandler<Redo, KContentMsg<?, ?, Redo>>() {

        @Override
        public void handle(Redo content, KContentMsg<?, ?, Redo> container) {
            Patch patch = historyBuffer.get(content.getPatchID());
            patch.incrementDegree();
            if (patch.getDegree() == 1) {
                execute(patch);
            }
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

    public Patch inverse(Patch patch) {

        Patch inversePatch = new Patch(patch.getId(), patch.getOperations(), patch.getDegree());

        for (Operation op : inversePatch.getOperations()) {
            if (op.getType().equals(OperationType.INSERT)) {
                op.setType(OperationType.DELETE);
            } else {
                op.setType(OperationType.INSERT);
            }
        }
        return inversePatch;
    }

    public int positionBinarySearch(LineIdentifier identifier) {

        int low = 0;
        int high = identifierTable.size() - 1;
        int middle;

        while(low <= high ) {
            middle = (low + high) / 2;
            if (identifier.compareTo(identifierTable.get(middle)) == 1){
                low = middle + 1;
            } else if (identifierTable.get(middle).compareTo(identifier) == 1) {
                high = middle - 1;
            } else { // The element has been found
                return middle;
            }
        }

        return low;
    }

    {
        subscribe(deliverPatchHandler, net);
        subscribe(deliverUndoHandler, net);
        subscribe(deliverRedoHandler, net);
    }


}
