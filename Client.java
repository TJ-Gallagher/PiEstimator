import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Client extends JFrame {
/*
* Modify this example so that it opens a dialogue window using java swing, 
* takes in a user message and sends it
* to the server. The server should output the message back to all connected clients
* (you should see your own message pop up in your client as well when you send it!).
*  We will build on this project in the future to make a full fledged server based game,
*  so make sure you can read your code later! Use good programming practices.
*  ****HINT**** you may wish to have a thread be in charge of sending information 
*  and another thread in charge of receiving information.

     
*/

    JTextArea textArea = new JTextArea("Waiting for both players to join");
    private volatile ObjectInputStream oiss;
    private volatile ObjectOutputStream ooss;
    RecieverThread reciever1;

    public static void main(String[] args){
        Client ex = new Client();
    }
    

    public Client(){
        try{
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = new Socket(host.getHostName(), 9876);
        //ObjectOutputStream oos = null;
        ooss = new ObjectOutputStream(socket.getOutputStream());
        oiss = new ObjectInputStream(socket.getInputStream());
            System.out.println("received "+oiss.readObject());
        }
        catch (Exception e){
            e.printStackTrace();
        }
              
        //create frame
        JFrame frame=new JFrame("Client Gui");  
        frame.setLayout(new FlowLayout());
        //create elements for frame
        JLabel label = new JLabel("Enter Your Name:");
        JTextField inputField = new JTextField(20);
	    JButton button=new JButton("Enter");  
        //adds elements to frame
        frame.add(label);
        frame.add(inputField);
        frame.add(button);
        frame.add(textArea);

        frame.setSize(600, 600);
        
        frame.setVisible(true);

        RecieverThread reciever = new RecieverThread();
        reciever1 = reciever;
        reciever1.start();
       
        //Its own thread to send information
       button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = (String)inputField.getText();
            if(name!=null){
                try{
                    ooss.writeObject(name);
                }
                    catch(IOException a){
                        a.printStackTrace();
                }
            } 
        }

    });
    game();
}
    public void game(){
        boolean a = true;
        while(a){
          a = reciever1.getCheck();
        }
        
        //send message to server saying I'd like to connect!
        //when the server responds hide "frame" and "setVisible" yourself
        Board tempBoard = reciever1.getBoard();
        Player tempPlayer = reciever1.getPlayer();
        this.add(reciever1.getBoard());
        tempBoard.addKeyListener(tempPlayer);
        reciever1.setBoard(tempBoard);
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setVisible(true);

        while(true){
            update();
        }

    }


    public void update(){
        try {
            ooss.writeObject(reciever1.getBoard());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    


    //the client uses keys in player that updates server. the server should get updates on both sides.
    //client draws the board.

   



      //own thread to recive
    private class RecieverThread extends Thread {
        Player player;
        Board b;
        Pacman p;
        Boolean check = true;
        public Boolean getCheck(){
           return check;
        }
        public Player getPlayer(){
            return player;
          }
        public Board getBoard(){
          return b;
        }
        public void setBoard(Board input){
            b = input;
          }
        public void run(){
            while (true) {
                synchronized(this){
                    try{
                        Object a = oiss.readObject();
                        System.out.println("received "+a);
                       
                        if(a instanceof String && ((String)a).equals("Start" )){
                           check = false;
                        }
                        if(a instanceof String && ((String)a).equals("end")){
                            check = true;
                         }
                        
                        if(a instanceof Player){
                            player=(Player)a;
                            
                        }
                        if(a instanceof Board){
                            b = (Board)a;
                        }
                    }
                    catch(IOException a){

                    }
                    catch(ClassNotFoundException c){
                
                    }
                }
            }
        }
    }
}
            
     
    

