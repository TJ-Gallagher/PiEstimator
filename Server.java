import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program should allow the client to send it messages. The messages should then 
 * become visible to all other clients.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program creates a new thread for
 * every connection request.
 */

//THINGS TO WORK ON
//1. Issolate the pacman charchter into a seperate class so it can be used as an object and allow the game to understand the possibility of multiple pacman maybe have it wait for two objects to be created like a connecting screen
//2. Make there be a win goal aka program game logic check collisions maybe in its own thread constantly checking alongisde tagger vs nottagger varriables and logic
//3. (by this point basic tagging and connecting should work) fix the speed issue where you move right even after letting go of the key until you pick a new direction aka cant stand still unless there is a colision
//4. Add maybe boosts// extra // diffrent game mechanics for tagger and runner // maybe decide if we want extra lives as pacman has as an atribute for example
//5. (Extra stuff) maybe random generated map for each time you complete a level, maybe some kind of gambeling like when you die possiblity to roll for a new life or lose curenacy/points making it more exciting. Maybe change the colors of the blocks or being able to choose that

public class Server {
    public static final int LISTENING_PORT = 9876;
    static Board board;
    private static ArrayList<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();

    public static void main(String[] args) {
        Board b = new Board();
        board = b;
        while(connections.size()<2){
            ServerSocket listener;  // Listens for incoming connections.
       
            /* Accept and process connections forever, or until some error occurs. */
            try {
                listener = new ServerSocket(LISTENING_PORT);
                System.out.println("Listening on port " + LISTENING_PORT);
                while (true) {
                    Socket s = listener.accept();
                    ConnectionHandler handler = new ConnectionHandler(s);
                    handler.start();
                    board.setPlayer(handler.getPlayer());
                    handler.send(handler.getPlayer());
                    handler.send(board);
                    connections.add(handler);
                }
            }
            catch (Exception e) {
                System.out.println("Sorry, the server has shut down.");
                System.out.println("Error:  " + e);
                return;
            }
        } 
        synchronized(connections) {
            for(ConnectionHandler handler: connections) {
                //make sure you don't try to access the handler from two different threads simultainously
                synchronized(handler) {
                    handler.send("Start");
                }
            }
        }
    }
        //something to let it to know to start /call update in pacman
        
        

        //close window
        
    // end main()

    public Board getBoard(){
         return board;
    }

    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    private static class ConnectionHandler extends Thread {
        private Socket client;
        private String clientName;
        private ObjectOutputStream os;
        private ObjectInputStream is;
        private Player player;
        
        ConnectionHandler(Socket socket) {
            Player p = new Player(board);
             player = p;
            client = socket;
            try {
                //set up your streams, make sure this order is reversed on the client side!
                is = new ObjectInputStream(socket.getInputStream());
                os = new ObjectOutputStream(socket.getOutputStream());
                os.writeObject("connected");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public Player getPlayer(){
            return player;
        }
        
        //method to help us send information!
        public void send(Object input) {
            System.out.println("trying to send "+input);
            try {
                os.writeObject(input);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
        public void run() {
            while(true) {
                Board updateBoard = null;
                try {
                    //the name goes into input
                    Object input = is.readObject();
                    System.out.println("server received message");
                    if(input instanceof Board){
                        updateBoard = (Board)input;
                    }
                    //your code to send messages goes here.
                if(updateBoard != null) {
                    synchronized(connections) {
                        for(ConnectionHandler handler: connections) {
                            //make sure you don't try to access the handler from two different threads simultainously
                            synchronized(handler) {
                                board = updateBoard;
                                handler.send(board);
                                updateBoard = null;
                            }
                        }
                    }
                }
                }
                catch (Exception e){
                    System.out.println("Error on connection " + ": " + e);
                }
            }
        } 
    }

    public ArrayList<ConnectionHandler> getConnections(){
        return connections;
    }
    
}