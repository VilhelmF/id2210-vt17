package se.kth.app.logoot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.logoot.Operation.Operation;
import se.kth.app.logoot.Operation.OperationType;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

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
    public List<LineIdentifier> generateLineID(LineIdentifier p, LineIdentifier q, int N, int boundary, Site site) {

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

    public LineIdentifier constructID(int r, LineIdentifier p, LineIdentifier q, Site site) {

        LineIdentifier id = new LineIdentifier();

        int s, d, c;

        String str = Integer.toString(r);

        for (int i = 0; i < str.length(); i++) {
            d = Integer.parseInt(str.substring(i, i + 1));
            if (p.getPositions().size() > i && d == p.getPositions().get(i).getDigit()) {
                s = p.getPositions().get(i).getSiteID();
                c = p.getPositions().get(i).getClockValue();
            } else if (q.getPositions().size() > i && d == q.getPositions().get(i).getDigit()) {
                s = q.getPositions().get(i).getSiteID();
                c = q.getPositions().get(i).getClockValue();
            } else {
                s = site.getId();
                c = site.getClock() + 1;
            }
            id.addPosition(new Position(d, s, c));
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
                if (identifierTable.get(position).equals(op.getId())) {
                    document.remove(position - 1);
                    identifierTable.remove(position);
                } else {
                    degree = cemetery.get(op.getId()) - 1;
                }
                cemetery.put(op.getId(), degree);
            }
        }
    }

    public void patch(Patch content) {
        Patch patch = copyPatch(content);
        patch.setDegree(1);
        execute(patch);
        historyBuffer.put(patch.getId(), patch);
    }

    public void undo(Undo content) {
        Patch patch = copyPatch(historyBuffer.get(content.getPatchID()));
        patch.decrementDegree();

        if (patch.getDegree() == 0) {
            execute(inverse(patch));
        }
        historyBuffer.put(patch.getId(), patch);
    }

    public void redo(Redo content) {
        Patch patch = copyPatch(historyBuffer.get(content.getPatchID()));
        patch.incrementDegree();

        if (patch.getDegree() == 1) {
            execute(patch);
        }
        historyBuffer.put(patch.getId(), patch);
    }

    public int prefix(List<Position> positions, int index) {
        int digit = 0;
        for(int i = 0; i < index; i++) {
            digit *= BASE;
            if(positions.size() > i) {
                digit += positions.get(i).getDigit();
            }
        }
        return digit;
    }

    public Patch inverse(Patch patch) {

        Patch inversePatch = new Patch(patch.getId(), new ArrayList<Operation>(), patch.getDegree());

        for (Operation op : patch.getOperations()) {
            Operation newOp = new Operation(op.getType(), op.getId(), op.getContent());
            if (op.getType().equals(OperationType.INSERT)) {
                newOp.setType(OperationType.DELETE);
            } else {
                newOp.setType(OperationType.INSERT);
            }
            inversePatch.getOperations().add(newOp);
        }
        return inversePatch;
    }

    public Patch copyPatch(Patch patch) {
        List<Operation> newOperations = new ArrayList<>();


        for (Operation op : patch.getOperations()) {
            Operation newOperation = new Operation(op.getType(), op.getId(), op.getContent());
            newOperations.add(newOperation);
        }

        return new Patch(patch.getId(), newOperations, patch.getDegree());
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

    public void printDocument() {
        for (String line : document) {
            System.out.println(line);
        }
    }

    public List<LineIdentifier> getIdentifierTable() {
        return identifierTable;
    }

    public LineIdentifier getIdentifier(int index) {
        return this.identifierTable.get(index);
    }

    public List<LineIdentifier> getLineIdentifier(int startLine, int endLine, int lineCount, Site site) {
        return generateLineID(identifierTable.get(startLine), identifierTable.get(endLine), lineCount,
                10, site);
    }

    public List<LineIdentifier> getFirstLine(int lineCount, Site site) {
        return getLineIdentifier(0, 1, lineCount, site);
    }

    public List<LineIdentifier> getLastLine(int lineCount, Site site) {
        int identifierTableSize = identifierTable.size();
        return getLineIdentifier(identifierTableSize - 2, identifierTableSize - 1, lineCount, site);
    }

    public List<Operation> createOperations(OperationType type, List<LineIdentifier> lineIdentifiers, List<String> text) {
        if (lineIdentifiers.size() != text.size()) {
            LOG.info("Logoot: Line identifiers are not equal to lines of text.");
            return null;
        }
        List<Operation> operations = new ArrayList<>();
        int textIndex = 0;
        for (LineIdentifier li : lineIdentifiers) {
            operations.add(new Operation(type, li, text.get(textIndex)));
            textIndex++;
        }
        return operations;
    }

    public Patch createPatch(int id, List<Operation> operations, int degree) {
        return new Patch(id, operations, degree);
    }

    public Patch createPatch(int id, int line, int lineCount, List<String> text, Site site, int degree,
                             OperationType type) {
        List<LineIdentifier> lineIdentifiers;
        if (line == 1) {
           lineIdentifiers = getFirstLine(lineCount, site);
        } else if (line == Integer.MAX_VALUE) {
            lineIdentifiers = getLastLine(lineCount, site);
        } else {
            lineIdentifiers = getLineIdentifier(line, line + lineCount, lineCount, site);
        }
        return createPatch(id, lineIdentifiers, text, degree, type);
    }

    public Patch createPatch(int id, List<LineIdentifier> lineIdentifiers, List<String> text, int degree,
                             OperationType type) {
        List<Operation> operations = createOperations(type,lineIdentifiers, text);
        return new Patch(id, operations, degree);
    }

    public List<String> getDocumentClone() {
       return new ArrayList<>(this.document);
    }

    public int getDocumentSize() {
        return this.document.size();
    }

    public String getDocumentLine(int index) {
        return this.document.get(index);
    }

    public static void main(String[] args) {
        System.out.println("Here we go!");
        Logoot logoot = new Logoot();
        int vectorClock = 0;
        int site = 1;
        List<LineIdentifier> lol = logoot.getIdentifierTable();
        List<LineIdentifier> lineIdentifiers = logoot.generateLineID(lol.get(0), lol.get(1), 20, 10, new Site(vectorClock, site));
        vectorClock++;

        Collections.sort(lineIdentifiers);
        for (LineIdentifier identifier : lineIdentifiers) {
            identifier.printPositions();
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
        logoot.patch(patch);
        logoot.printDocument();

        lineIdentifiers = logoot.generateLineID(lol.get(10), lol.get(11), 2, 10, new Site(vectorClock, site));

        Collections.sort(lineIdentifiers);
        operations = new ArrayList<>();

        for (LineIdentifier li : lineIdentifiers) {
            String lineText = "This is a new linenumber!!! " + lineNumber;
            operations.add(new Operation(OperationType.INSERT, li, lineText));
            lineNumber++;
        }

        patch = new Patch(2, operations, 1);
        logoot.patch(patch);
        logoot.printDocument();

        lineIdentifiers = new ArrayList<>();
        lineIdentifiers.add(logoot.identifierTable.get(3));
        lineIdentifiers.add(logoot.identifierTable.get(4));
        lineIdentifiers.add(logoot.identifierTable.get(5));

        Collections.sort(lineIdentifiers);

        operations = new ArrayList<>();
        for (LineIdentifier li : lineIdentifiers) {
            int position = logoot.positionBinarySearch(li);
            operations.add(new Operation(OperationType.DELETE, li, logoot.document.get(position - 1)));
        }

        patch = new Patch(3, operations, 1);
        logoot.patch(patch);
        LOG.info("REMOVE DONE!!!");
        logoot.printDocument();


        logoot.undo(new Undo(3));
        LOG.info("UNDO DONE!!!");
        logoot.printDocument();

    }

}
