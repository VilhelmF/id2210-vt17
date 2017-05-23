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
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();
    final static Logger LOG = LoggerFactory.getLogger(LogootTest.class);

    @Test
    public void correctOrderTest() {
        res.put("TestCase", "correctOrderTest");
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleBoot(NUM_PEERS);
        simpleBootScenario.simulate(LauncherComp.class);
        List baseDoc = res.get("1", List.class);
        for(int i = 2; i <= NUM_PEERS; i++) {
            List compareDoc = res.get(String.valueOf(i), List.class);
            Assert.assertTrue(baseDoc.equals(compareDoc));
        }
    }

    @Test
    public void removeTest() {
        res.put("TestCase", "removeTest");
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.simpleBoot(NUM_PEERS);
        simpleBootScenario.simulate(LauncherComp.class);
        List baseDoc = res.get("1", List.class);
        for(int i = 2; i <= NUM_PEERS; i++) {
            List compareDoc = res.get(String.valueOf(i), List.class);
            Assert.assertEquals(baseDoc, compareDoc);
        }
    }
}
