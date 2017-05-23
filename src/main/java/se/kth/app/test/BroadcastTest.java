/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.app.test;

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.sim.ScenarioGen;
import se.kth.app.sim.SimulationResultMap;
import se.kth.app.sim.SimulationResultSingleton;
import se.kth.app.test.Broadcast.BroadcastScenarioGen;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lars Kroll <lkroll@kth.se>
 */

@SuppressWarnings("Duplicates")
public class BroadcastTest {

    private static final int NUM_PEERS = 10;
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();
    final static Logger LOG = LoggerFactory.getLogger(BroadcastTest.class);


    @Test
    public void basicBroadcastTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BroadcastScenarioGen.basicBroadcastTest(NUM_PEERS);
        simpleBootScenario.simulate(LauncherComp.class);

        for (int i = 1; i <= NUM_PEERS; i++) {
            for (int j = 1; j <= NUM_PEERS; j++) {
                Assert.assertEquals("1", res.get("peer" + j + "-receivedFrom" + i, String.class));
            }
        }
    }

    @Test
    public void broadcastTestAllCorrectDeliverFromCorrectNode() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BroadcastScenarioGen.broadcastTestChurn(NUM_PEERS, NUM_PEERS / 5);
        simpleBootScenario.simulate(LauncherComp.class);

        List<Integer> corruptNodes = new ArrayList<>();


        for (int i = 1; i <= NUM_PEERS; i++) {
            if (res.get("corrupt-" + i, String.class) != null) {
                corruptNodes.add(i);
            }
        }

        for (int i = 1; i <= NUM_PEERS; i++) {
            if (!corruptNodes.contains(i)) {
                if (res.get("sent-" + i, String.class) != null) {
                    for (int j = 1; j <= NUM_PEERS; j++) {
                        if (!corruptNodes.contains(j))
                        Assert.assertEquals(Integer.toString(1), res.get("peer" + j + "-receivedFrom" + i, String.class));
                    }
                }
                //LOG.info("received-" + i + "= " + res.get("received-" + i, String.class));
                //Assert.assertEquals(Integer.toString(NUM_PEERS), res.get("received-" + i, String.class));
            } else {
                LOG.info("CORRUPT : received-" + i + "= " + res.get("received-" + i, String.class));
            }
        }
    }

    @Test
    public void broadcastTestOneCorrectDeliversAllCorrectDeliver() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = BroadcastScenarioGen.broadcastTestChurn(NUM_PEERS, NUM_PEERS / 5);
        simpleBootScenario.simulate(LauncherComp.class);

        List<Integer> corruptNodes = new ArrayList<>();
        List<Integer> correctNodes = new ArrayList<>();

        for (int i = 1; i <= NUM_PEERS; i++) {
            if (res.get("corrupt-" + i, String.class) != null) {
                corruptNodes.add(i);
            } else {
                correctNodes.add(i);
            }
        }

        for (Integer i : corruptNodes) {
            if (res.get("sent-" + i, String.class) != null) {
                for (Integer j : correctNodes) {
                    if (res.get("peer" + j + "-receivedFrom" + i, String.class) != null) {
                        for (Integer k : correctNodes) {
                            Assert.assertEquals(Integer.toString(1), res.get("peer" + k + "-receivedFrom" + i, String.class));
                        }
                    }
                }
            }
        }
    }




}
