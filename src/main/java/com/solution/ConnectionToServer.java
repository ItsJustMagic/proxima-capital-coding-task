package com.solution;

import com.google.gson.Gson;

import javax.websocket.*;

/*
* This class handles the events coming from the server. Mainly concerned with onMessage where we get an event from the server
* */
@ClientEndpoint
public class ConnectionToServer {

    private static Gson gson = new Gson();
    private LocalOrderBook lob;
    private Payload previous;

    /*
    * Receives a local order book object to pass the received events in a queue.
    * @Param LocalOrderBook lob - passed into the constructor to hold a reference to a LocalOrderBook object
    * */
    public ConnectionToServer(LocalOrderBook lob) {
        this.lob = lob;
    }
    @OnOpen
   public void onOpen(Session session) {
       System.out.println("Websocket opened");
   }

   @OnMessage
   public void onMessage(String message, Session session) {
       Payload event = new Payload(message);
       if (previous != null ) {
           //checks if the the events being received are in sync.
           if (event.getFirstUpdateID() != previous.getFinalUpdateID()+1) {
               System.out.println("out of sync");
               System.exit(0);
           }
       }
       previous = event;
//       event.printPayload();
       lob.addPayload(event);

   }

   @OnClose
   public void onClose(Session session, CloseReason closeReason) {
       System.out.println("Closed connection");
       System.exit(0);
   }

}
