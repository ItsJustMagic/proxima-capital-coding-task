package com.solution;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.*;
import java.io.*;
import java.util.*;

/*
* This class is responsible for retrieving and holding a snapshot
* */
public class Snapshot {

    //buffer to hold the snapshot.
    StringBuffer snapshot;
    Double lastUpdateId;
    //map to hold the local order book.
    TreeMap<Double, Double> bids;
    TreeMap<Double, Double> asks;
    Double totalBidQuantity;
    Double totalAskQuantity;

    /*
    * The constructor makes an http request and loads the snapshot into the buffer.
    * @param String url - the url for the constructor to make a http request
    * */
    public Snapshot(String url) {
        try {
            //new get request given url
            URL request_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)request_url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //read response from the request and load into buffer
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                snapshot = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    snapshot.append(inputLine);
                }
                in.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        //converts the string into json object and loads into the treemap.
        if (snapshot != null) {
            bids = new TreeMap<>();
            asks = new TreeMap<>();
            totalBidQuantity = 0.000;
            totalAskQuantity = 0.000;

            JSONObject snapshotObject = new JSONObject(snapshot.toString());
            lastUpdateId = snapshotObject.getDouble("lastUpdateId");

            JSONArray bidArray = snapshotObject.getJSONArray("bids");

            Iterator itr = bidArray.iterator();
            while (itr.hasNext()) {
                JSONArray element = (JSONArray)itr.next();
                this.updateBids(new Pair<Double, Double>(element.getDouble(0), element.getDouble(1)));

            }
            JSONArray askArray = snapshotObject.getJSONArray("asks");
            itr = askArray.iterator();
            while (itr.hasNext()) {
                JSONArray element = (JSONArray)itr.next();
                this.updateAsks(new Pair<Double, Double>(element.getDouble(0), element.getDouble(1)));
            }
        }
        System.out.println("Snapshot created");
        System.out.println("Please enter a key to stop the program.");
    }
    //basic getters
    protected Double getLastUpdateId() {
        return this.lastUpdateId;
    }

    protected TreeMap<Double, Double> getBids() {
        return this.bids;
    }
    protected TreeMap<Double, Double> getAsks() {
        return this.asks;
    }

    @SuppressWarnings("Duplicates")
    protected void updateBids(Pair<Double, Double> event) {
        Double quantity = bids.get(event.getKey());
        if (quantity != null) {
            totalBidQuantity += (event.getValue() - quantity);
        } else {
            totalBidQuantity += event.getValue();
        }
        bids.put(event.getKey(), event.getValue());
    }

    @SuppressWarnings("Duplicates")
    protected void updateAsks(Pair<Double, Double> event) {
        Double quantity = asks.get(event.getKey());
        if (quantity != null) {
            totalAskQuantity += (event.getValue() - quantity);
        } else {
            totalAskQuantity += event.getValue();
        }
        asks.put(event.getKey(), event.getValue());
    }

    protected void removeBids(Pair<Double, Double> event) {
        Double response = bids.remove(event.getKey());
        if (response != null) {
            totalBidQuantity -= response;
        }
    }

    protected void removeAsks(Pair<Double, Double> event) {
        Double response = asks.remove(event.getKey());
        if (response != null) {
            totalAskQuantity -= response;
        }
    }

    //used to print the local order book for basic debugging.
    protected void printSnapShot() {
        System.out.println(this.lastUpdateId);
        System.out.println("printing bids ========================================");
        System.out.println("======================================================");
        System.out.println("======================================================");

        for (Map.Entry<Double, Double> e : bids.entrySet())
            System.out.println(e.getKey()+ " " + e.getValue());
        System.out.println("printing asks ========================================");
        System.out.println("======================================================");
        System.out.println("======================================================");

        for (Map.Entry<Double, Double> e : asks.entrySet())
            System.out.println(e.getKey()+ " " + e.getValue());
    }

    //basic debugging helper method.
    protected void printAverge() {
        Double bid = 0.0;
        for (Map.Entry<Double, Double> e : bids.entrySet())
            bid += e.getValue();

        Double ask = 0.0;
        for (Map.Entry<Double, Double> e : asks.entrySet())
            ask += e.getValue();

        System.out.println(bid + " " + ask);
    }

    /*
    * So highest bids = the best offer in the market
    * lowest asks = cheapest in the market
    * */
    @SuppressWarnings("Duplicates")
    protected Pair<Double, Double> getAveragePrice(Double quantity) {

        Double bidAverage = 0.0;
        Double bidQuantity = 0.0;
        Double maxQuantity = 0.0;
        //sort the bids from highest to lowest.
        for (Map.Entry<Double, Double> bid: bids.descendingMap().entrySet()) {
            maxQuantity = quantity;

            //if there is not enough quantity.
            if (quantity > totalBidQuantity) maxQuantity = totalBidQuantity;

            //if there is more quantity available than we need.
            if (bidQuantity + bid.getValue() > maxQuantity) {
                Double remainingToPurchase = maxQuantity - bidQuantity;
                bidQuantity += remainingToPurchase;
                bidAverage += ((remainingToPurchase/maxQuantity)*bid.getKey());
                break;
            } else {
                bidQuantity += bid.getValue();
                bidAverage += ((bid.getValue()/maxQuantity)*bid.getKey());
            }
        }
        Double askAverage = 0.0;
        Double askQuantity = 0.0;
        for (Map.Entry<Double, Double> ask: asks.entrySet()) {
            maxQuantity = quantity;

            //if there is not enough quantity
            if  (quantity > totalAskQuantity) maxQuantity = totalAskQuantity;

            //if there is more quantity available than we need.
            if (askQuantity + ask.getValue() > maxQuantity) {
                Double remainingToPurchase = maxQuantity - askQuantity;
                askQuantity += remainingToPurchase;
                askAverage += ((remainingToPurchase/maxQuantity)*ask.getKey());
                break;
            } else {
                askQuantity += ask.getValue();
                askAverage += ((ask.getValue()/maxQuantity)*ask.getKey());
            }
        }
        //weighted buy price/weighted sell price. - lowest price to buy and sell.
        Pair<Double, Double> ret = new Pair<>(bidAverage, askAverage);
        return ret;
    }
}