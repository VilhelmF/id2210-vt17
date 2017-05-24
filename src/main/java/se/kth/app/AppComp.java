/*
 * 2016 Royal Institute of Technology (KTH)
 *
 * LSelector is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BroadcastMessage;
import se.kth.app.broadcast.Causal.CRB_Broadcast;
import se.kth.app.broadcast.Causal.CRB_Deliver;
import se.kth.app.broadcast.Causal.CausalBroadcast;
import se.kth.app.logoot.*;
import se.kth.app.logoot.Operation.Operation;
import se.kth.app.logoot.Operation.OperationType;
import se.kth.app.sim.SimulationResultMap;
import se.kth.app.sim.SimulationResultSingleton;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class AppComp extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(AppComp.class);
    private String logPrefix = " ";

    //*******************************CONNECTIONS********************************
    Positive<Timer> timerPort = requires(Timer.class);
    Positive<Network> networkPort = requires(Network.class);
    Positive<CroupierPort> croupierPort = requires(CroupierPort.class);
    Positive<CausalBroadcast> crb = requires(CausalBroadcast.class);
    //**************************************************************************
    private KAddress selfAdr;

    private int messageCounter;
    private HashMap<String, String> msgs;
    private ArrayList<String> quicktest;
    private Logoot logoot;
    private int randomID;
    private String selfId;
    private SimulationResultMap res = SimulationResultSingleton.getInstance();
    private String testcase;
    private UUID timerId;
    public int messagesReceived = 0;
    private int timestamp;

    public AppComp(Init init) {
        selfAdr = init.selfAdr;
        logoot = init.logoot;

        logPrefix = "<nid:" + selfAdr.getId() + ">";
        LOG.info("{}initiating...", logPrefix);

        selfId = selfAdr.getId().toString();
        messageCounter = 1;
        timestamp = 0;
        testcase = res.get("TestCase", String.class);
        msgs = new HashMap<>();
        quicktest = new ArrayList<>();
        subscribe(handleStart, control);
        subscribe(handleCroupierSample, croupierPort);
        subscribe(handleCRBDeliver, crb);
        subscribe(timeoutHandler, timerPort);

        SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(0,10);
        TestTimeout timeout = new TestTimeout(spt);
        spt.setTimeoutEvent(timeout);
        trigger(spt, timerPort);
        timerId = timeout.getTimeoutId();
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting...", logPrefix);
        }
    };

    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timerPort);
    }


  Handler timeoutHandler = new Handler<TestTimeout>() {

      @Override
      public void handle(TestTimeout tt) {
          switch(testcase) {
              case "correctOrderTest":
                  correctOrderTest();
                  break;
              case "removeTest":
                  removeTest();
                  break;
              default:
                  break;
          }
      }
  };

  Handler handleCroupierSample = new Handler<CroupierSample>() {
        @Override
        public void handle(CroupierSample croupierSample) {

            switch(testcase) {
                case "correctOrderTest":
                    correctOrderTest();
                    break;
                case "removeTest":
                    removeTest();
                    break;
                case "causalOrderTest":
                    causalOrderTest();
                    break;
                default:
                    break;
            }
        }
  };

  Handler handleCRBDeliver = new Handler<CRB_Deliver>() {

      @Override
      public void handle(CRB_Deliver crb_deliver) {

          BroadcastMessage broadcastMessage = (BroadcastMessage) crb_deliver.payload;
          KompicsEvent payload = broadcastMessage.payload;
          timestamp++;

          if (payload instanceof Patch) {
              //LOG.info("{} Received a patch from: " + crb_deliver.src, logPrefix);
              logoot.patch((Patch) payload);
          } else if (payload instanceof Undo) {
              //LOG.info("{} Received a undo from: " + crb_deliver.src, logPrefix);
              logoot.undo((Undo) payload);
          } else if (payload instanceof Redo) {
              //LOG.info("{} Received a redo from: " + crb_deliver.src + " " + crb_deliver.id, logPrefix);
              logoot.redo((Redo) payload);
          }
          //LOG.info("{} Document after", logPrefix);
          //logoot.printDocument();
          res.put(selfAdr.getId().toString(), logoot.getDocumentClone());
      }
  };

  private void causalOrderTest() {
      LOG.info("I feel it coming.");
  }

  private void correctOrderTest() {

      if(messageCounter == 1) {
          randomID = (selfAdr.toString() + String.valueOf(messageCounter)).hashCode();
          List<String> text = new ArrayList<>(Arrays.asList(selfAdr.toString() + " Sending message!"));
          Patch patch = logoot.createPatch(randomID, 1, text.size(), text, new Site(selfAdr.getId().hashCode(),
                  timestamp), 1, OperationType.INSERT);
          trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
          timestamp++;
          messageCounter++;
      } else if (messageCounter == 2 && selfId.contains("1")) {
          Undo undo = new Undo(randomID);
          trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, undo)), crb);
          messageCounter++;
          timestamp++;
      } else if (messageCounter == 2 && !selfId.contains("1")) {
          randomID = (selfAdr.toString() + String.valueOf(messageCounter)).hashCode();
          List<String> text = new ArrayList<>(Arrays.asList(selfAdr.toString() + " Sending second message!"));
          Patch patch = logoot.createPatch(randomID, Integer.MAX_VALUE, text.size(), text,
                    new Site(selfAdr.getId().hashCode(), timestamp), 1, OperationType.INSERT);
          trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
          messageCounter++;
          timestamp++;
      } else if (messageCounter == 3 && selfId.equals("1")) {
          LOG.info("{} sending redo.", logPrefix);
          Redo redo = new Redo(randomID);
          trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, redo)), crb);
          messageCounter++;
          timestamp++;
      } else if (messageCounter == 4 && selfAdr.getId().toString().equals("1")) {
          messageCounter++;
          timestamp++;
      }
  }

  private void removeTest() {

      if(messageCounter == 1) {
          randomID = (selfAdr.toString() + String.valueOf(messageCounter)).hashCode();
          LOG.info("{} " + " randomvalue " + String.valueOf(randomID));
          List<String> text = new ArrayList<>(Arrays.asList(selfAdr.toString() + " Sending message!"));
          Patch patch = logoot.createPatch(randomID, 1, text.size(), text, new Site(selfAdr.getId().hashCode(),
                  timestamp), 1, OperationType.INSERT);
          trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
          timestamp++;
          messageCounter++;
      } else if (messageCounter == 2 && logoot.getDocumentSize() > 1) {
          int lineIndex = ThreadLocalRandom.current().nextInt(0, logoot.getDocumentSize() - 1);
          String text = logoot.getDocumentLine(lineIndex);
          LineIdentifier lineIdentifier = logoot.getIdentifier(lineIndex+1);
          Operation op = new Operation(OperationType.DELETE, lineIdentifier, text);
          List<Operation> operations = new ArrayList<>();
          operations.add(op);
          randomID = (selfAdr.toString() + String.valueOf(messageCounter)).hashCode();
          Patch patch = new Patch(randomID, operations, 1);
          trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
          timestamp++;
          messageCounter++;
      }
  }

  public static class TestTimeout extends Timeout {
      public TestTimeout(SchedulePeriodicTimeout spt) {
          super(spt);
      }
  }

  public static class Init extends se.sics.kompics.Init<AppComp> {

    public final KAddress selfAdr;
    public final Identifier gradientOId;
    public final Logoot logoot;

    public Init(KAddress selfAdr, Identifier gradientOId) {
      this.selfAdr = selfAdr;
      this.gradientOId = gradientOId;
      this.logoot = new Logoot();
    }
  }
}
