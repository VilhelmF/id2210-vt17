package se.kth.app.logoot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.logoot.Operation.Operation;
import se.kth.app.logoot.Operation.OperationType;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.ktoolbox.util.network.KContentMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Logoot extends ComponentDefinition {

    //*******************************CONNECTIONS********************************
    protected final Positive<Network> net = requires(Network.class);
    //**************************************************************************


    protected HashMap<Integer, Patch> historyBuffer;
    protected List<LineIdentifier> identifierTable;
    protected List<String> document;
    protected HashMap<LineIdentifier, Integer> cemetery;
    private static final Logger LOG = LoggerFactory.getLogger(Logoot.class);
    private final int BASE = 100;


    public Logoot() {
        historyBuffer = new HashMap<>();
        identifierTable = new ArrayList<>();
        document = new ArrayList<>();
        cemetery = new HashMap<>();

        //Inserting <0,NA,NA> and <MAX,NA,NA> to mark beginning and end of document
        LineIdentifier min = new LineIdentifier();
        LineIdentifier max = new LineIdentifier();
        min.addPosition(new Position(0,-1, -1));
        max.addPosition(new Position(BASE - 1, -1, -1));
        identifierTable.add(min);
        identifierTable.add(max);
    }


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

        for (int i = 0; i < str.length(); i++) {
            d = Integer.parseInt(str.substring(i, i + 1));
            if (p.getPositions().size() < i && d == p.getPositions().get(i).getDigit()) {
                s = p.getPositions().get(i).getSiteID();
                c = p.getPositions().get(i).getClockValue();
            } else if (q.getPositions().size() < i && d == q.getPositions().get(i).getDigit()) {
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
                int degree;
                if(!cemetery.containsKey(op.getId())){
                    degree = 1;
                } else {
                    degree = cemetery.get(op.getId()) + 1;
                }
                if (degree == 1) {
                    int position = positionBinarySearch(op.getId());
                    document.add(position - 1, op.getContent());
                    identifierTable.add(position, op.getId());
                } else {
                    cemetery.put(op.getId(), degree);
                }
            } else {
                int position = positionBinarySearch(op.getId());
                int degree = 0;
                LineIdentifier li = identifierTable.get(position);
                for (Position p : li.getPositions()) {
                    LOG.info("identifierTable: <" + p.getDigit() + ", " + p.getSiteID() + ", " + p.getClockValue() + ">");
                }
                LineIdentifier t = op.getId();
                for (Position p : t.getPositions()) {
                    LOG.info("operation: <" + p.getDigit() + ", " + p.getSiteID() + ", " + p.getClockValue() + ">");
                }
                if (identifierTable.get(position).equals(op.getId())) {
                    document.remove(position);
                    identifierTable.remove(position);
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
        int digit = 0;
        for(int i = 0; i < index; i++) {
            digit *= BASE;
            if(positions.size() > i) {
                digit += positions.get(i).getDigit();
            }
        }
        return digit;
        /*
        String digit = Integer.toString(positions.get(0).getDigit());

        if (index < digit.length()) {
            LOG.info("Prefix with index: " + index + " digit: " + digit.substring(0,index));
            return Integer.parseInt(digit.substring(0, index));
        } else {
            int diff = index - digit.length();

            for (int i = 0; i < diff; i++) {
                digit += "0";
            }
            LOG.info("Prefix with index: " + index + " digit: " + digit);
            return Integer.parseInt(digit);
        }

        /*
        for (int i = 0; i < index; i++) {
            try {
                digit += Integer.toString(positions.get(i).getDigit());
            } catch (IndexOutOfBoundsException e) {
                digit += "0";
            }
            LOG.info("Prefix at index " + i + " digit: " + digit);
        }

        return Integer.parseInt(digit);
        */
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
        int middle = 0;

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

        return middle;
    }

    {
        subscribe(deliverPatchHandler, net);
        subscribe(deliverUndoHandler, net);
        subscribe(deliverRedoHandler, net);
    }

    public void printDocument() {
        for (String line : document) {
            System.out.println(line);
        }
    }

    public List<LineIdentifier> getIdentifierTable() {
        return identifierTable;
    }


    public static void main(String[] args) {
        System.out.println("Here we go!");
        Logoot logoot = new Logoot();
        int vectorClock = 0;
        int site = 1;
        List<LineIdentifier> lol = logoot.getIdentifierTable();
        List<LineIdentifier> lineIdentifiers = logoot.generateLineID(lol.get(0), lol.get(1), 20, 10, new Position(1, vectorClock, site));
        vectorClock++;
        Collections.sort(lineIdentifiers);
        for (LineIdentifier identifier : lineIdentifiers) {
            for (Position position : identifier.getPositions()) {
                System.out.print("<" + position.getDigit() + ", " + position.getSiteID() + ", " + position.getClockValue() + ">");
            }
            System.out.println();
        }
        List<Operation> operations = new ArrayList<>();
        int lineNumber = 1;
        for (LineIdentifier li : lineIdentifiers) {
            String lineText = "This is line number: " + lineNumber;
            operations.add(new Operation(OperationType.INSERT, li, lineText));
            lineNumber++;
        }
        Patch patch = new Patch(1, operations, 1);
        logoot.execute(patch);
        for (String docLine : logoot.document) {
            LOG.info(docLine);
        }

        lineIdentifiers = logoot.generateLineID(lol.get(10), lol.get(12), 2, 10, new Position(2, vectorClock, site));
        vectorClock++;
        Collections.sort(lineIdentifiers);
        operations = new ArrayList<>();
        for (LineIdentifier li : lineIdentifiers) {
            String lineText = "This is a new linenumber!!! " + lineNumber;
            operations.add(new Operation(OperationType.INSERT, li, lineText));
            lineNumber++;
        }
        patch = new Patch(2, operations, 1);
        logoot.execute(patch);
        for (String docLine : logoot.document) {
            LOG.info(docLine);
        }
        //lineIdentifiers = logoot.generateLineID(lol.get(1), lol.get(5), 2, 10, new Position(3, vectorClock, site));
        lineIdentifiers = new ArrayList<>();
        lineIdentifiers.add(logoot.identifierTable.get(3));
        lineIdentifiers.add(logoot.identifierTable.get(4));
        lineIdentifiers.add(logoot.identifierTable.get(5));
        Collections.sort(lineIdentifiers);
        vectorClock++;
        operations = new ArrayList<>();
        for (LineIdentifier li : lineIdentifiers) {
            String lineText = "This is a new linenumber!!! " + lineNumber;
            operations.add(new Operation(OperationType.DELETE, li, lineText));
            lineNumber++;
        }
        patch = new Patch(3, operations, 1);
        logoot.execute(patch);
        for (String docLine : logoot.document) {
            LOG.info(docLine);
        }

    }

}
