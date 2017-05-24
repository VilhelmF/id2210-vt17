package se.kth.app.sim;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

import java.util.List;

/**
 * Created by vilhelm on 2017-05-23.
 */
public class LogootTest {

    private static final int NUM_PEERS = 10;
    private static final String TESTKEY = "TestCase";
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();
    final static Logger LOG = LoggerFactory.getLogger(LogootTest.class);

    @Test
    public void causalOrderTest() {
        res.put(TESTKEY, "causalOrderTest");
        runSimulation();
        List baseDoc = res.get("1", List.class);
        for (Object s : baseDoc) {
            System.out.println("Value: " + s);
        }
    }

    @Test
    public void correctOrderTest() {
        res.put(TESTKEY, "correctOrderTest");
        runSimulation();

        List baseDoc = res.get("1", List.class);
        for (Object i : baseDoc) {
            System.out.println(i);
        }
        Assert.assertEquals(2*NUM_PEERS-3, baseDoc.size());
        for(int i = 2; i <= NUM_PEERS; i++) {
            List compareDoc = res.get(String.valueOf(i), List.class);
            Assert.assertTrue(baseDoc.equals(compareDoc));
        }
    }

    @Test
    public void removeTest() {
        res.put(TESTKEY, "removeTest");
        runSimulation();
        List baseDoc = res.get("1", List.class);
        for(int i = 2; i <= NUM_PEERS; i++) {
            List compareDoc = res.get(String.valueOf(i), List.class);
            Assert.assertEquals(baseDoc, compareDoc);
        }
    }

    private void runSimulation() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleBoot(NUM_PEERS);
        simpleBootScenario.simulate(LauncherComp.class);
    }
}
