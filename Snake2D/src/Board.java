import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {
    //width and height of the board
    int board_width = 400;
    int board_height = 400;

    //max no of dots on the board, dot size and no of dots to start of
    int max_dots = 400*400;
    int dot_size = 10;
    int dot_no;

    //x & y co-ordinates of the snake
    int[] x = new int[max_dots];
    int[] y = new int[max_dots];

    //x & y co-ordinates of the apple
    int apple_xPos;
    int apple_yPos;

    //Image objects of snake head,body & apple
    Image head, body, apple;

    //timer to generate action event for the snake to move
    Timer timer;
    int delay = 150;

    //key adapter control directions
    boolean left_direction = true;
    boolean right_direction = false;
    boolean up_direction = false;
    boolean down_direction = false;

    //to check running of the game
    boolean isGame_Running = true;

    //restart button
    JButton restartButton;


    Board(){
        controlAdapter direction_Controls = new controlAdapter();
        addKeyListener(direction_Controls);
        setFocusable(true);
        setPreferredSize(new Dimension(board_width, board_height));
        setBackground(Color.black);
        gameBody();
        loadImages();

        //Initializing a JButton for the Restart button after the game ends
        restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        restartButton.setVisible(false); //initially setting the visibility to false when the game starts

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restartButton);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
    }

    //Initialize Game
    public void gameBody(){
        dot_no = 3;

        //Initialize snakes position
        x[0] = 250;
        y[0] = 250;
        for(int i=1;i<dot_no;i++){
            x[i] = x[0] + dot_size * i;
            y[i] = y[0];
        }

        //Apple's random position generator
        locateApple();

        //so this delay of 150ms which means 0.15s is mapped to the actual time
        //and whenever this timer is incremented the action event is generated which helps to move the snake
        timer = new Timer(delay, this);
        timer.start();
    }

    //Load Images from the resources to the Image object
    public void loadImages(){
        //Snake's head Image
        ImageIcon headIcon = new ImageIcon("src/resources/head.png");
        head = headIcon.getImage();

        //Snake's body Image
        ImageIcon bodyIcon = new ImageIcon("src/resources/dot.png");
        body = bodyIcon.getImage();

        //Apple's Image
        ImageIcon appleIcon = new ImageIcon("src/resources/apple.png");
        apple = appleIcon.getImage();
    }

    //paintComponent is a method of JPanel which helps to draw in the panel except for setting background color
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g); //inheriting the parent object
        drawImages(g);
    }
    //draw images at specified locations
    public void drawImages(Graphics g){
        if(isGame_Running) {
            g.drawImage(apple, apple_xPos, apple_yPos, this); //apple image
            for (int i = 0; i < dot_no; i++) {
                if (i == 0) g.drawImage(head, x[0], y[0], this); //head image
                else g.drawImage(body, x[i], y[i], this); //body image
            }
        }
        else{
            gameOver(g);
            timer.stop();
            restartButton.setVisible(true);
        }
    }

    //Randomize Apple's position
    public void locateApple(){
        //so as my snake will be moving according to the dot_size so the random pos of apple should also be a
        //multiple of dot_size
        apple_xPos = ((int)(Math.random()*39)) * dot_size;
        apple_yPos = ((int)(Math.random()*39)) * dot_size;
    }

    //check for collision with body and border
    public void checkCollision(){
        //collision with body
        for(int i=1;i<dot_no;i++){
            if(i>4 && x[0] == x[i] && y[0] == y[i]) isGame_Running = false;
        }

        //collision with border left,right,up,down
        if(x[0]<0 || x[0]>=board_width) isGame_Running = false;
        if(y[0]<0 || y[0]>=board_height) isGame_Running = false;
    }

    //Game over message and score
    public void gameOver(Graphics g){
        String gameOver_msg = "GAME OVER";

        int score = (dot_no-3)*10;
        String score_msg = "SCORE: "+Integer.toString(score);

        Font font = new Font("Helvatica", Font.BOLD, 14);
        FontMetrics fontMetrics = getFontMetrics(font);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(gameOver_msg, (board_width - fontMetrics.stringWidth(gameOver_msg))/2, (board_height/4));
        g.drawString(score_msg, (board_width - fontMetrics.stringWidth(score_msg))/2, (board_height/2));

    }

    //so the actionPerformed method tells the compiler that whenever the action event occurs we need to
    //move the snake
    @Override
    public void actionPerformed(ActionEvent actionEvent){
        if(isGame_Running) {
            eatApple(); //checking whether apple is present in this position or not
            checkCollision(); //to checking for collision with body or boundary
            moveSnake(); //moving the snake to a new position according to the control direction
            repaint(); //redrawing all the images at the new position
        }
    }

    //for restarting the game
    public void restartGame() {
        isGame_Running = true;
        dot_no = 3;
        x[0] = 250;
        y[0] = 250;
        for (int i = 1; i < dot_no; i++) {
            x[i] = x[0] + dot_size * i;
            y[i] = y[0];
        }
        locateApple();
        timer.restart();
        restartButton.setVisible(false);
        repaint();
    }

    //moving the snake
    public void moveSnake(){
        for(int i=dot_no-1;i>0;i--){
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if(left_direction) x[0] -= dot_size;
        if(right_direction) x[0] += dot_size;
        if(up_direction) y[0] -= dot_size;
        if(down_direction) y[0] += dot_size;
    }

    //Making the snake eat the apple
    public void eatApple(){
        if(apple_xPos == x[0] && apple_yPos == y[0]){
            dot_no++; //increasing the snake body count
            locateApple(); //randomizing new position for the apple
        }
    }

    //Implement Controls
    private class controlAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent keyEvent){ //keyEvent is similar to an actionEvent
            int key = keyEvent.getKeyCode();
            if(key == keyEvent.VK_LEFT && !right_direction){
                left_direction = true;
                up_direction = false;
                down_direction = false;
            }
            if(key == keyEvent.VK_RIGHT && !left_direction){
                right_direction = true;
                up_direction = false;
                down_direction = false;
            }
            if(key == keyEvent.VK_UP && !down_direction){
                up_direction = true;
                left_direction = false;
                right_direction = false;
            }
            if(key == keyEvent.VK_DOWN && !up_direction){
                down_direction = true;
                left_direction = false;
                right_direction = false;
            }
        }
    }
}
