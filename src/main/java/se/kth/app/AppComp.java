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
import se.kth.app.test.Ping;
import se.kth.app.test.Pong;
import se.kth.networking.CroupierMessage;
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

  public AppComp(Init init) {
    selfAdr = init.selfAdr;
    logPrefix = "<nid:" + selfAdr.getId() + ">";
    LOG.info("{}initiating...", logPrefix);

    messageCounter = 1;
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

            if (croupierSample.publicSample.isEmpty()) {
                return;
            }

            // Test to send the sample to Gossiping Broadcast
            int randomNum = ThreadLocalRandom.current().nextInt(0, 101);
            if(messageCounter < 6) {
                String messageId = DigestUtils.sha1Hex(selfAdr.toString() + new java.util.Date() + messageCounter);
                LOG.info("{} Sendig message:  " + messageId, logPrefix);
                trigger(new CRB_Broadcast(messageId, selfAdr, new BroadcastMessage(String.valueOf(messageCounter))), crb);
                messageCounter++;
            }
            /*
            if(messageCounter < 10) {
                String messageId = selfAdr.toString() + String.valueOf(messageCounter);
                trigger(new CRB_Broadcast(messageId, selfAdr, new BroadcastMessage(String.valueOf(messageCounter))), crb);
                LOG.info("{} Senging" + "ID: " + messageId + " Message: " + String.valueOf(messageCounter), logPrefix);
                messageCounter++;
            }*/
            trigger(new CroupierMessage(croupierSample), gbeb);
            /*
            List<KAddress> sample = CroupierHelper.getSample(croupierSample);
            for (KAddress peer : sample) {
            KHeader header = new BasicHeader(selfAdr, peer, Transport.UDP);
            KContentMsg msg = new BasicContentMsg(header, new Ping());
            trigger(msg, networkPort);
            }
            */
    }
  };

  Handler handleCRBDeliver = new Handler<CRB_Deliver>() {

      @Override
      public void handle(CRB_Deliver crb_deliver) {
          BroadcastMessage tst = (BroadcastMessage) crb_deliver.payload;
          msgs.put(crb_deliver.id, tst.message);
          quicktest.add(tst.message);
          LOG.info("{} received crb delivery." + "ID: " + crb_deliver.id + " Message: " + tst.message, logPrefix);
          if(tst.message.equals("5")) {
              for (String key : msgs.keySet()) {
                  String msg = msgs.get(key);
                  LOG.info("{} ID: " + key + " Message: " + msg, logPrefix);
              }
              LOG.info("{} ___ ----- ____ ----- ___", logPrefix);
              for (String message : quicktest
                   ) {
                 LOG.info("{}  " + message, logPrefix);
              }


          }
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

  public static class Init extends se.sics.kompics.Init<AppComp> {

    public final KAddress selfAdr;
    public final Identifier gradientOId;

    public Init(KAddress selfAdr, Identifier gradientOId) {
      this.selfAdr = selfAdr;
      this.gradientOId = gradientOId;
    }
  }
}
