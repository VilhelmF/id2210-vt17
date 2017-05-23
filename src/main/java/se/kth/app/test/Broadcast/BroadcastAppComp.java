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
package se.kth.app.test.Broadcast;

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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
@SuppressWarnings("Duplicates")
public class BroadcastAppComp extends ComponentDefinition {

  private static final Logger LOG = LoggerFactory.getLogger(BroadcastAppComp.class);
  private String logPrefix = " ";

  //*******************************CONNECTIONS********************************
  Positive<CroupierPort> croupierPort = requires(CroupierPort.class);
  Positive<CausalBroadcast> crb = requires(CausalBroadcast.class);
  //**************************************************************************

  // Simulation Result Map
  private final SimulationResultMap res = SimulationResultSingleton.getInstance();

  private KAddress selfAdr;
  private int messageCounter;
  private Logoot logoot;
  private int randomID;

  public BroadcastAppComp(Init init) {
    selfAdr = init.selfAdr;
    logoot = init.logoot;

    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    messageCounter = 1;
    subscribe(handleStart, control);
    subscribe(handleCroupierSample, croupierPort);
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

            if(messageCounter == 1) {

                List<LineIdentifier> lineIdentifiers = logoot.getFirstLine(1, new Site(selfAdr.getId().hashCode(), 0));

                randomID = ThreadLocalRandom.current().nextInt(0,Integer.MAX_VALUE);
                LOG.info("{} " + " randomvalue " + String.valueOf(randomID));
                List<Operation> operations = new ArrayList<>();
                for (LineIdentifier li : lineIdentifiers) {
                    String lineText = selfAdr.toString() + " Sending message!";
                    operations.add(new Operation(OperationType.INSERT, li, lineText));
                }
                Patch patch = new Patch(randomID, operations, 1);
                trigger(new CRB_Broadcast(selfAdr, new BroadcastMessage(selfAdr, patch)), crb);
                res.put("sent-" + selfAdr.getId().toString(), "");

                messageCounter++;
            }
    }
  };

  Handler handleCRBDeliver = new Handler<CRB_Deliver>() {

      @Override
      public void handle(CRB_Deliver crb_deliver) {


          BroadcastMessage broadcastMessage = (BroadcastMessage) crb_deliver.payload;

          String num = res.get("received-" + broadcastMessage.src.getId().toString(), String.class);

          if (num == null) {
              res.put("received-" + broadcastMessage.src.getId().toString(), "1");
          } else {
              res.put("received-" + broadcastMessage.src.getId().toString(), Integer.toString(Integer.parseInt(num) + 1));
          }
      }
  };

  public static class Init extends se.sics.kompics.Init<BroadcastAppComp> {

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
