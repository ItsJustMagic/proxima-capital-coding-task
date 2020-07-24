package com.solution;
import java.util.*;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*
* This class handles the payloads received from the stream.
* Converts json string into json object and organises the data into variables.
* */
public class Payload {
    private String eventType; //e
    private Double eventTime; //E
    private String symbol; //s
    private Double firstUpdateID; //U
    private Double finalUpdateID; //u
    private ArrayList<Pair<Double, Double>> bids; //bids to be updated/removed
    private ArrayList<Pair<Double, Double>> asks; //asks to be updated/removed

    /*
    * Payload constructor that takes in a json string and processes it
    * @param String payload - the json in a string format
    * */
    public Payload(String payload){
        JSONObject event = new JSONObject(payload);
        this.eventType = event.getString("e");
        this.eventTime = event.getDouble("E");
        this.symbol = event.getString("s");
        this.firstUpdateID = event.getDouble("U");
        this.finalUpdateID = event.getDouble("u");
        bids = new ArrayList<>();
        asks = new ArrayList<>();

        //load bids into JSON array
        JSONArray bidArray = event.getJSONArray("b");

        Iterator itr = bidArray.iterator();
        while (itr.hasNext()) {
            JSONArray element = (JSONArray)itr.next();
            Pair<Double, Double> bid = new Pair(element.getDouble(0), element.getDouble(1));
            bids.add(bid);
        }

        //load asks into JSON array
        JSONArray askArray = event.getJSONArray("a");

        Iterator askItr = askArray.iterator();
        while (askItr.hasNext()) {
            JSONArray element = (JSONArray)askItr.next();
            Pair<Double, Double> ask = new Pair(element.getDouble(0), element.getDouble(1));
            asks.add(ask);
        }
    }

    //basic getters
    protected String getEventType() {
        return this.eventType;
    }

    protected Double getEventTime() {
        return this.eventTime;
    }

    protected String getSymbol() {
        return this.symbol;
    }

    protected Double getFirstUpdateID() {
        return this.firstUpdateID;
    }
    protected Double getFinalUpdateID() {
        return this.finalUpdateID;
    }

    protected ArrayList<Pair<Double, Double>> getBids() {
        return this.bids;
    }

    protected ArrayList<Pair<Double, Double>> getAsks() {
        return this.asks;
    }

    //printing the payload for debugging purposes.
    protected void printPayload() {
        System.out.println(this.eventType);
        System.out.println(this.eventTime);
        System.out.println(this.symbol);
        System.out.println(this.firstUpdateID);
        System.out.println(this.finalUpdateID);
        System.out.println("Bids");
        for (Pair<Double, Double> bid: bids) {
            System.out.println(bid.getKey() + " " + bid.getValue());
        }
        System.out.println("Asks");
        for (Pair<Double, Double> ask: asks) {
            System.out.println(ask.getKey() + " " + ask.getValue());
        }
    }
}
