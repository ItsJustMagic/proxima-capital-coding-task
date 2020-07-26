# To run the code
Due to using Java, I used maven to manage the dependencies.
~~~~
1. Navigate to the directory /proxima-capital-coding-task and run
- mvn compile                                                                            
- mvn package
- mvn install assembly:assembly
2. run java -cp ./target/task-solution-1.0-SNAPSHOT-jar-with-dependencies.jar com.solution.App
3. Enter a number in the argument to specify the quantity of BTC
~~~~
I wasn't sure of the exact method of arguments and output so the arguments is given one time as standard input and the program will output a weighted price for both buy and sell when an event is received.
If needed, I can change this to allow you to pipe in a list of inputs.

# Dependencies
1. JSR 356 Websocket library
2. org.json library to handle the processing of JSON objects received from web socket and API get request

# Data structures used
1. Java Treemap - used to keep a constantly sorted collection of prices and quick remove/put operations. Removes the need to loop over the entire collection of objects. (Somewhat serves the purpose of built in JSON objects in javascript and dictionaries in python)
2. Queue - used to store a first in first out sequence of events retrieved from the websocket.

# Notes
I sometimes had issues running the program with maven on the command line. I couldn't figure out why, the dependencies were not properly imported.
I used a Java IDE (Intellij) in the end and it solved the problem for me...
