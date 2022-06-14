package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class map extends JPanel implements ActionListener {
//create window and interaction with button key
	private Dimension dimension;
    private final Font font = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;

    private final int blockSize = 24;
    private final int numberOfBlocks = 19;
    private final int screenSize = numberOfBlocks * blockSize;
    private final int maxGhost = 12;
    private final int pacmanSpeed = 6;

    private int ghostQuantity = 6;
    private int lives, score;//actual lives var
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image heart, ghost; // the heart is for image
    private Image up, down, left, right;
    //for key button and animation
    private int pacman_x, pacman_y, x_direction, y_direction;
    private int req_dx, req_dy;

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    private final short levelData[] = {
        // short is a 16-bit signed two's complement integer, that can store value from inclusive range [-215, 215-1]
    	 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
         0, 19, 26, 26, 18, 26, 26, 26, 22,  0, 19, 26, 26, 26, 18, 26, 26, 22,  0,
         0, 21,  0,  0, 21,  0,  0,  0, 21,  0, 21,  0,  0,  0, 21,  0,  0, 21,  0,
         0, 17, 26, 26, 16, 26, 18, 26, 24, 26, 24, 26, 18, 26, 16, 26, 26, 20,  0,
         0, 21,  0,  0, 21,  0, 21,  0,  0,  0,  0,  0, 21,  0, 21,  0,  0, 21,  0,
         0, 25, 26, 26, 20,  0, 25, 26, 22,  0, 19, 26, 28,  0, 17, 26, 26, 28,  0,
         0,  0,  0,  0, 21,  0,  0,  0, 21,  0, 21,  0,  0,  0, 21,  0,  0,  0,  0,
        10, 10, 14,  0, 21,  0, 19, 26, 24, 26, 24, 26, 22,  0, 21,  0, 11, 10, 10, 
         0,  0,  0,  0, 21,  0, 21,  0,  0,  0,  0,  0, 21,  0, 21,  0,  0,  0,  0,
        27, 26, 26, 26, 16, 26, 20,  0, 11, 10, 14,  0, 17, 26, 16, 26, 26, 26, 30,
         0,  0,  0,  0, 21,  0, 21,  0,  0,  0,  0,  0, 21,  0, 21,  0,  0,  0,  0,
        10, 10, 14,  0, 21,  0, 25, 26, 18, 26, 18, 26, 28,  0, 21,  0, 11, 10, 10, 
         0,  0,  0,  0, 21,  0,  0,  0, 21,  0, 21,  0,  0,  0, 21,  0,  0,  0,  0,
         0, 19, 26, 26, 20,  0, 19, 26, 28,  0, 25, 26, 22,  0, 17, 26, 26, 22,  0,
         0, 21,  0,  0, 21,  0, 21,  0,  0,  0,  0,  0, 21,  0, 21,  0,  0, 21,  0, 
         0, 17, 26, 26, 16, 26, 24, 26, 18, 26, 18, 26, 24, 26, 16, 26, 26, 20,  0,
         0, 21,  0,  0, 21,  0,  0,  0, 21,  0, 21,  0,  0,  0, 21,  0,  0, 21,  0,
         0, 25, 26, 26, 24, 26, 26, 26, 28,  0, 25, 26, 26, 26, 24, 26, 26, 28,  0,
         0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 
    };


    public map() {

        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }
    
    
    private void loadImages() {//load images
    	down = new ImageIcon("D:/Documents/Filbert 2/java/java/src/pacman/sauce/down.gif").getImage();
    	up = new ImageIcon("D:/Documents/Filbert 2/java/java/src/pacman/sauce/up.gif").getImage();
    	left = new ImageIcon("D:/Documents/Filbert 2/java/java/src/pacman/sauce/left.gif").getImage();
    	right = new ImageIcon("D:/Documents/Filbert 2/java/java/src/pacman/sauce/right.gif").getImage();
        ghost = new ImageIcon("D:/Documents/Filbert 2/java/java/src/pacman/sauce/ghost.gif").getImage();
        heart = new ImageIcon("D:/Documents/Filbert 2/java/java/src/pacman/sauce/heart.png").getImage();
        
    }
       private void initVariables() {

        screenData = new short[numberOfBlocks * numberOfBlocks];
        dimension = new Dimension(800, 800);
        ghost_x = new int[maxGhost];
        ghost_dx = new int[maxGhost];
        ghost_y = new int[maxGhost];
        ghost_dy = new int[maxGhost];
        ghostSpeed = new int[maxGhost];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this);
        timer.start();
    }

    private void alive(Graphics2D graphic) {//if still alive game continues

        if (dying) {//set according to bool

            death();

        } else {

            movePacman();
            drawPacman(graphic);
            moveGhosts(graphic);
            checkMaze();
        }
    }

    private void introScreen(Graphics2D graphic) {
 
    	String start = "Press SPACE to start";
        graphic.setColor(Color.yellow);
        graphic.drawString(start, (screenSize)/3, 220);// set the title to the middle
    }

    private void drawScore(Graphics2D g) {
        g.setFont(font);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, screenSize / 2 + 96, screenSize + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, screenSize + 1, this);
        }
    }

    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        while (i < numberOfBlocks * numberOfBlocks && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (ghostQuantity < maxGhost) {
                ghostQuantity++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {

    	lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D graphic) {

        int pos;
        int count;

        for (int i = 0; i < ghostQuantity; i++) {
            if (ghost_x[i] % blockSize == 0 && ghost_y[i] % blockSize == 0) {
                pos = ghost_x[i] / blockSize + numberOfBlocks * (int) (ghost_y[i] / blockSize);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(graphic, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void movePacman() {

        int pos;
        short ch;

        if (pacman_x % blockSize == 0 && pacman_y % blockSize == 0) {
            pos = pacman_x / blockSize + numberOfBlocks * (int) (pacman_y / blockSize);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    x_direction = req_dx;
                    y_direction = req_dy;
                }
            }

            // Check for standstill
            if ((x_direction == -1 && y_direction == 0 && (ch & 1) != 0)
                    || (x_direction == 1 && y_direction == 0 && (ch & 4) != 0)
                    || (x_direction == 0 && y_direction == -1 && (ch & 2) != 0)
                    || (x_direction == 0 && y_direction == 1 && (ch & 8) != 0)) {
                x_direction = 0;
                y_direction = 0;
            }
        } 
        pacman_x = pacman_x + pacmanSpeed * x_direction;
        pacman_y = pacman_y + pacmanSpeed * y_direction;
    }

    private void drawGhost(Graphics2D graphic, int x, int y) {
    	graphic.drawImage(ghost, x, y, this);
        }


    private void drawPacman(Graphics2D graphic) {

        if (req_dx == -1) {
        	graphic.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
        	graphic.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
        	graphic.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
        	graphic.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D graphic) {

        short i = 0;
        int x, y;

        for (y = 0; y < screenSize; y += blockSize) {
            for (x = 0; x < screenSize; x += blockSize) {

                graphic.setColor(new Color(0,72,251));
                graphic.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	graphic.fillRect(x, y, blockSize, blockSize);
                 }

                if ((screenData[i] & 1) != 0) { 
                    graphic.drawLine(x, y, x, y + blockSize - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    graphic.drawLine(x, y, x + blockSize - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    graphic.drawLine(x + blockSize - 1, y, x + blockSize - 1,
                            y + blockSize - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    graphic.drawLine(x, y + blockSize - 1, x + blockSize - 1,
                            y + blockSize - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    graphic.setColor(new Color(255,255,255));
                    graphic.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    private void initGame() {

    	lives = 3;
        score = 0;
        initLevel();
        ghostQuantity = 6;
        currentSpeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < numberOfBlocks * numberOfBlocks; i++) {
            screenData[i] = levelData[i];//the maze
        }

        continueLevel();
    }

    private void continueLevel() {

    	int dx = 1;
        int random;

        for (int i = 0; i < ghostQuantity; i++) {

            ghost_y[i] = 4 * blockSize; //start position
            ghost_x[i] = 4 * blockSize;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * blockSize;  //start position
        pacman_y = 11 * blockSize;
        x_direction = 0;	//reset direction move
        y_direction = 0;
        req_dx = 0;		// reset direction controls
        req_dy = 0;
        dying = false;
    }

 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphic = (Graphics2D) g;

        graphic.setColor(Color.black);//backgrounf
        graphic.fillRect(0, 0, dimension.width, dimension.height);

        drawMaze(graphic);
        drawScore(graphic);

        if (inGame) {
            alive(graphic);
        } else {
            introScreen(graphic);
        }

        Toolkit.getDefaultToolkit().sync();
        graphic.dispose();
    }


    //controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {

            int key = event.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }

	
    @Override
    public void actionPerformed(ActionEvent event) {
        repaint();
    }
		
}


// //Start
// // ******************************
// // *S     **********  # *********
// // ****   ******         ********
// // ***   ******   *****  ********
// // **  *******  ******** ********
// // **    #      ******** ********
// // **          ********    ******
// // ** ***  ***********  ** ******
// // **  **  ***********  **  *****
// // *** **   **********  *** *****
// // *#  ***     *******   **   #**
// // **********    ******   *** ***
// // ************   *****  ********
// // *************    #    ********
// // **************      **********
// // ********#   ****       *******
// // ***********  ***    **  ******
// // **********  *****  ****   ****
// // ***********  ***   ****** #***
// // ******* #***      ************
// // *******   *******  ***********
// // ********    ******  **********
// // **********  *******        ***
// // *********** ******* *****  ***
// // **********  ***     ***** ****
// // **********   *  ********* ****
// // ***********     ********* ****
// // ***********  ****      **  ***
// // ******E   #        **       #*
// // ******************************
// //End

// // 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22
