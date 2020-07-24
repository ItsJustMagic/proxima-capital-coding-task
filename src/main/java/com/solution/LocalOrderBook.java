package com.solution;

import javafx.util.Pair;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/*
* This class acts as a wrapper that coordinates the events from the stream and updating the local order book.
* */

public class LocalOrderBook {

    String streamEndoint;
    String snapshotEndpoint;
    Double btcQuantity;

    Snapshot snapshot;
    Queue<Payload> payloadQueue;

    /*
    * Constructor for the class
    * @param String streamEndoint - the stream endpoint to open a websocket to.
    * @param String snapshotEndpoint - the API url to retrieve a snapshot through get request.
    * @param Double btcQuantity - the amount of BTC.
    * */
    public LocalOrderBook(String streamEndoint, String snapshotEndpoint, Double btcQuantity) {
        this.streamEndoint = streamEndoint;
        this.snapshotEndpoint = snapshotEndpoint;
        this.btcQuantity = btcQuantity;
        payloadQueue = new LinkedList<>();
    }

    /*
    * Must call this function to begin the creating of the local book order.
    * */
    public void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ConnectionToServer cts = new ConnectionToServer(this);
            //create the socket stream.
            container.connectToServer(cts, new URI(streamEndoint));
            reader.readLine();
        } catch(DeploymentException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void setSnapShot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }
    protected Snapshot getSnapshot() {
        return this.snapshot;
    }

    /*
    * This method polls the queue of events and updates the order book.
    * Method is called whenever a new payload is added.
    * */
    protected void updateOrderBook() {
        while (!payloadQueue.isEmpty() && snapshot != null) {
            Payload payload = payloadQueue.poll();
            ArrayList<Pair<Double, Double>> bids = payload.getBids();
            //drop the outdated events that were buffered.
            if (payload.getFinalUpdateID() <= snapshot.getLastUpdateId()) {
//                System.out.println("Dropped");
                continue;
            }
            for (Pair<Double, Double> bid: bids) {
                if (bid.getValue() == 0) {
                    snapshot.removeBids(bid);
                } else {
                    snapshot.updateBids(bid);
                }
            }

            ArrayList<Pair<Double, Double>> asks = payload.getAsks();

            for (Pair<Double, Double> ask: asks) {
                if (ask.getValue() == 0) {
                    snapshot.removeAsks(ask);
                } else {
                    snapshot.updateAsks(ask);
                }
            }
            Pair<Double, Double> price = snapshot.getAveragePrice(this.btcQuantity);
            System.out.printf("\rFor a quantity of %.5f BTC, the weighted buy price = %.5f weighted sell price = %.5f ", this.btcQuantity, price.getKey(), price.getValue());

        }
    }

    /*
    * Function too add an event to the queue/buffer when received from websocket stream
    * @param Payload payload - the payload object to add to the queue.
    * */
    protected void addPayload(Payload payload) {
//        payload.printPayload();
        payloadQueue.add(payload);
        //checks if snapshot is empty or not. This is called here as it was necessary to obtain a snapshot after
        // the stream of events has been started.
        if (snapshot == null) {
            snapshot = new Snapshot(snapshotEndpoint);
        }
        updateOrderBook();
    }
}