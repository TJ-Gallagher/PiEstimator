import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.Timer;

public class Player implements KeyListener, Serializable{
    private static final long serialVersionUID = 2L;
    
    
    private final int PACMAN_SPEED = 6;


    private int pacsLeft, score;
    private int[] dx, dy; // maybe
    private int req_dx, req_dy;
    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private boolean inGame = false; // might need or nah
    private Board localBoard;
    private String name; //
    private volatile ObjectOutputStream ooss;

    public void setOOS(ObjectOutputStream input){
        ooss = input;
    }

    public void update(){
        try {
            ooss.writeObject(localBoard);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    

    private boolean taggerBool;

    public int getReqdx(){
        return req_dx;
    }
    public int getReqdy(){
        return req_dy;
    }
    public int getPacx(){
        return pacman_x;
    }
    public int getPacy(){
        return pacman_y;
    }
    public int getPacdx(){
        return pacmand_x;
    }
    public int getPacdy(){
        return pacmand_y;
    }

    public void setReqdx(int input){
        req_dx = input;
    }
    public void setReqdy(int input){
        req_dy = input;
    }
    public void setPacx(int input){
        pacman_x = input;
    }
    public void setPacy(int input){
        pacman_y = input;
    }
    public void setPacdx(int input){
        pacmand_x = input;
    }
    public void setPacdy(int input){
        pacmand_y = input;
    }


    public boolean getTaggerBool(){
        return taggerBool;
    }
    public void setTaggerBool(boolean isTagger){
        taggerBool = isTagger;
    }




    public Player(Board board){
        localBoard = board;
    }

    //  class TAdapter extends KeyAdapter { //whats req_dx and dy????
       public void repeat(){
        localBoard.movePacman(req_dx, req_dy, pacman_x, pacman_y, pacmand_x, pacmand_y, PACMAN_SPEED, this );
       }
       
        @Override
        public void keyPressed(KeyEvent e) {

            System.out.println("key pressed "+e);
            int key = e.getKeyCode();

            
                if (key == KeyEvent.VK_LEFT) {
                    
                    req_dx = -1;
                    req_dy = 0;
                    localBoard.movePacman(req_dx, req_dy, pacman_x, pacman_y, pacmand_x, pacmand_y, PACMAN_SPEED, this );
                    update();
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                    localBoard.movePacman(req_dx, req_dy, pacman_x, pacman_y, pacmand_x, pacmand_y, PACMAN_SPEED, this );
                    update();
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                    localBoard.movePacman(req_dx, req_dy, pacman_x, pacman_y, pacmand_x, pacmand_y, PACMAN_SPEED, this );
                    update();
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                    localBoard.movePacman(req_dx, req_dy, pacman_x, pacman_y, pacmand_x, pacmand_y, PACMAN_SPEED, this );
                    update();
                } else if (key == KeyEvent.VK_ESCAPE /*&& timer.isRunning() */) {
                    inGame = false;
                } else if (key == KeyEvent.VK_PAUSE) {
                    //We dont need this but it would something cool I got no clue how to synchrinize the timer maybe having a check and set function in board
                    //if (timer.isRunning()) {
                        //timer.stop();
                   // } else {
                        //timer.start();
                    //}
                }
           
                else if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
                    System.out.println("pressed s");
                    localBoard.setInGame(true);
                    localBoard.initGame(); //work on int game
                    update();
                }
            
        }
        @Override
        public void keyTyped(KeyEvent e){

        }

        @Override
        public void keyReleased(KeyEvent e) { //if it equals 0 why do you keep going?

            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                req_dx = 0;
                req_dy = 0;
            }
        }
    //}


    //getMethods

    //setMethods
}
