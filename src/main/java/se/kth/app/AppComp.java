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

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.app.broadcast.BestEffort.BestEffortBroadcast;
import se.kth.app.broadcast.BroadcastMessage;
import se.kth.app.broadcast.Causal.CRB_Broadcast;
import se.kth.app.broadcast.Causal.CRB_Deliver;
import se.kth.app.broadcast.Causal.CausalBroadcast;
import se.kth.app.logoot.*;
import se.kth.app.logoot.Operation.Operation;
import se.kth.app.logoot.Operation.OperationType;
import se.kth.app.sim.SimulationResultMap;
import se.kth.app.sim.SimulationResultSingleton;
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.ktoolbox.croupier.CroupierPort;
import se.sics.ktoolbox.croupier.event.CroupierSample;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.network.KAddress;
import se.sics.ktoolbox.util.network.KContentMsg;
import se.sics.ktoolbox.util.network.KHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
  Positive<BestEffortBroadcast> gbeb = requires(BestEffortBroadcast.class);
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
    subscribe(handlePing, networkPort);
    subscribe(handlePong, networkPort);
    subscribe(handleCRBDeliver, crb);
  }

  Handler handleStart = new Handler<Start>() {
    @Override
    public void handle(Start event) {
      LOG.info("{}starting...", logPrefix);
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
              LOG.info("{} Received a patch from: " + crb_deliver.src, logPrefix);
              logoot.patch((Patch) payload);
          } else if (payload instanceof Undo) {
              LOG.info("{} Received a undo from: " + crb_deliver.src, logPrefix);
              logoot.undo((Undo) payload);
          } else if (payload instanceof Redo) {
              //LOG.info("{} Received a redo from: " + crb_deliver.src + " " + crb_deliver.id, logPrefix);
              logoot.redo((Redo) payload);
          }
          LOG.info("{} Document after", logPrefix);
          logoot.printDocument();
          res.put(selfAdr.getId().toString(), logoot.getDocumentClone());
      }
  };


  ClassMatchedHandler handlePing
    = new ClassMatchedHandler<Ping, KContentMsg<?, ?, Ping>>() {

      @Override
      public void handle(Ping content, KContentMsg<?, ?, Ping> container) {
        LOG.info("{}received ping from:{}", logPrefix, container.getHeader().getSource());
        trigger(container.answer(new Pong()), networkPort);
      }
    };

  ClassMatchedHandler handlePong
    = new ClassMatchedHandler<Pong, KContentMsg<?, KHeader<?>, Pong>>() {

      @Override
      public void handle(Pong content, KContentMsg<?, KHeader<?>, Pong> container) {
        LOG.info("{}received pong from:{}", logPrefix, container.getHeader().getSource());
      }
    };

    private void correctOrderTest() {
        if(messageCounter == 1) {
            randomID = (selfAdr.toString() + String.valueOf(messageCounter)).hashCode();
            LOG.info("{} " + " randomvalue " + String.valueOf(randomID));
            List<String> text = new ArrayList<>(Arrays.asList(selfAdr.toString() + " Sending message!"));
            Patch patch = logoot.createPatch(randomID, 1, text.size(), text, new Site(selfAdr.getId().hashCode(),
                    timestamp), 1, OperationType.INSERT);
            trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
            timestamp++;
            messageCounter++;
        } else if (messageCounter == 2 && selfId.equals("1")) {
            LOG.info("{} sending undo.", logPrefix);
            Undo undo = new Undo(randomID);
            String messageId = DigestUtils.sha1Hex(selfAdr.toString() + new java.util.Date() + messageCounter);
            LOG.info("{} ID: " + messageId, logPrefix);
            trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, undo)), crb);
            messageCounter++;
            timestamp++;
        } else if (messageCounter == 2 && !selfId.equals("1")) {
            LOG.info("HELLO HELLO");
            randomID = (selfAdr.toString() + String.valueOf(messageCounter)).hashCode();
            List<String> text = new ArrayList<>(Arrays.asList(selfAdr.toString() + " Sending second message!"));
            Patch patch = logoot.createPatch(randomID, Integer.MAX_VALUE, text.size(), text,
                    new Site(selfAdr.getId().hashCode(), timestamp), 1, OperationType.INSERT);
            trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
            messageCounter++;
            timestamp++;
        }
        else if (messageCounter == 3 && selfId.equals("1")) {
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
