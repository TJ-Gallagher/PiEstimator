
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener, Serializable {

    private static final long serialVersionUID = 1L;
    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private Color mazeColor;

    private boolean inGame = false;
    private boolean dying = false;

    private static int BLOCK_SIZE = 24;
    private static int N_BLOCKS = 15;
    private static int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private static int PAC_ANIM_DELAY = 2;
    private static int PACMAN_ANIM_COUNT = 4;
    
    //private final int MAX_GHOSTS = 12;
    //private final int PACMAN_SPEED = 6; //changing from not final and moving it to player

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    //private int pacsLeft, score; //changing from not final and moving it to player keep score as a tempScore to be added to players
    private int[] dx, dy; //I think this is being moved to player
   // private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Player playerOne;
    private Player playerTwo;

    //private Image ghost;
    //private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    //private Image pacman3up, pacman3down, pacman3left, pacman3right;
    //private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y; //changing from not final and moving it to player
    private int req_dx, req_dy, view_dx, view_dy; //being moved to player

    private final short levelData[] = {
        19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
        25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
        1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
        1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
        1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
        9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };

    //private final int validSpeeds[] = {1, 2, 3, 4, 6, 8}; // Ghost stuff maybe can be used for tagger idk
    // private final int maxSpeed = 6; // Ghost stuff maybe can be used for tagger idk
   // private int currentSpeed = 3; // Ghost stuff maybe can be used for tagger idk

    private static short[] screenData; 
    private Timer timer;

    public Board() {

        //loadImages();
        initVariables();
        initBoard();
        
    }
    public void setPlayer(Player p){
        if(playerOne == null){
            playerOne = p;
        }
        else{
            playerTwo = p;
        }

    }
    private void initBoard() {
        
       // addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        dx = new int[4]; // ?
        dy = new int[4]; // ?
        
        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        initGame();
    }

    private void doAnim() { // Do we need this or is this like a picture thing?

        pacAnimCount--; //?

        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {
        System.out.println("playing");
        if (dying) {

            death();

        } else {

            //movePacman(); is this the issue
            drawPacman(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
    }
     
    //need to fix for individual score kinda s tuff
    //private void drawScore(Graphics2D g) {

        //int i;
        //String s;

       // g.setFont(smallFont);
       // g.setColor(new Color(96, 128, 255));
       // s = "Score: " + score;
       // g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        //????? idk what the purpose of this was
       // for (i = 0; i < pacsLeft; i++) {
          //  g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
       // }
  //  }

    private void checkMaze() { //Checks to see if the dots are gone

        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            //score += 50; NEED TO FIX FOR THE INDVIDUAL

            initLevel();
        }
    }

    private void death() {

        //pacsLeft--;

        //if (pacsLeft == 0) {
           // inGame = false;
        //}
        inGame = false; //temp
        continueLevel();
    }
    public void movePacman(int req_dx, int req_dy, int pacman_x, int pacman_y, int pacmand_x, int pacmand_y, int PACMAN_SPEED, Player a) {  //Probabbly should be moved to player
        System.out.println("movePacman made it");
        int pos;
        short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            //a.setPacdx(req_dx);
            pacmand_y = req_dy;
            //a.setPacdy(req_dy);
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                //score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                   // a.setPacdx(req_dx);
                    pacmand_y = req_dy;
                   // a.setPacdy(req_dy);
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
               // a.setPacdx(0);
                pacmand_y = 0;
                //a.setPacdy(0);
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
      // a.setPacx(pacman_x + PACMAN_SPEED * a.getPacdx());
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
       // a.setPacy(pacman_x + PACMAN_SPEED * a.getPacdy());

       a.setPacdx(pacmand_x);
       a.setPacdy(pacmand_y);
       a.setPacx(pacman_x);
       a.setPacy(pacman_y);
       a.setReqdx(req_dx);
       a.setReqdy(req_dy);
        
       
       
    }

    private void drawPacman(Graphics2D g2d) {
        System.out.println("trying to draw pacman");
        g2d.setColor(Color.white);
        if(playerOne != null){
        g2d.drawRect(playerOne.getPacx(), playerOne.getPacy(), 20, 20);
        }
        if(playerTwo != null){
        g2d.drawRect(playerTwo.getPacx(), playerTwo.getPacy(), 20, 20);
        }
    }
    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    public void initGame() {
        System.out.println("initGame made it");
        //pacsLeft = 3;
        //score = 0;
        initLevel();
        //currentSpeed = 3;
    }

    private void initLevel() {
        System.out.println("initLevel made it");

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {
        System.out.println("continueLevel made it");

        short i;
        int dx = 1;
        int random;

       //resseting the players will have to fix later

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;
    }

    public void setInGame(boolean input){
        inGame = input;
    }

   // private void loadImages() {

       // ghost = new ImageIcon("images/ghost.png").getImage();
       // pacman1 = new ImageIcon("images/pacman.png").getImage();
       // pacman2up = new ImageIcon("images/up1.png").getImage();
       // pacman3up = new ImageIcon("images/up2.png").getImage();
       // pacman4up = new ImageIcon("images/up3.png").getImage();
       // pacman2down = new ImageIcon("images/down1.png").getImage();
       // pacman3down = new ImageIcon("images/down2.png").getImage();
       // pacman4down = new ImageIcon("images/down3.png").getImage();
       // pacman2left = new ImageIcon("images/left1.png").getImage();
       // pacman3left = new ImageIcon("images/left2.png").getImage();
       // pacman4left = new ImageIcon("images/left3.png").getImage();
       // pacman2right = new ImageIcon("images/right1.png").getImage();
      //  pacman3right = new ImageIcon("images/right2.png").getImage();
       // pacman4right = new ImageIcon("images/right3.png").getImage();

   // }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        //drawScore(g2d);
        doAnim();

        if (inGame) {
            playGame(g2d);
            if(playerOne != null){
                playerOne.repeat();
            }
            if(playerTwo != null){
                playerTwo.repeat();
            }
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }
    
}