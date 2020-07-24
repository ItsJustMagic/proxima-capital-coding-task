package com.solution;

import java.io.*;

public class App {
    public static void main( String[] args )
    {
            String streamEndoint = "wss://stream.binance.com:9443/ws/btcusdt@depth";
            String snapshotEndpoint = "https://www.binance.com/api/v1/depth?symbol=BTCUSDT&limit=1000";
            //create LocalOrderBook object and pass in the endpoints and call start.

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            Double btcQuantity = null;
            String input = null;
            System.out.print("Please enter a number ");
            try {
                while ((input = bufferedReader.readLine()) != null) {
                    try {
                        btcQuantity = Double.parseDouble(input);
                        break;
                    } catch  (NumberFormatException e){
                        System.out.print("Please enter a number ");
                    }
                }
            } catch (IOException e) {
                System.out.println("Something went wrong... exiting...");
                System.exit(1);
            }

            LocalOrderBook lob = new LocalOrderBook(streamEndoint, snapshotEndpoint, btcQuantity);
            lob.start();

    }
}
