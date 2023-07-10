import javax.swing.*;
import java.awt.*;

public class Snake extends JFrame {
    Board gameBoard;

    Snake(){
        gameBoard = new Board();
        add(gameBoard); //adding the board in the JFrame
        pack(); //it makes the frame resize to board dimension
        setResizable(false); //so that user cannot resize the window
        setVisible(true); //setting visibility of the frame to true
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //should stop running the code in the console as we hit x
    }
    public static void main(String[] args) {
        //initializing Snake class obj where Snake is acting as JFrame
        Snake snakeGame = new Snake();
        snakeGame.setTitle("Snake 2D"); //giving name for the game
        snakeGame.setLocation(500,200); //setting the location of the window
        ImageIcon icon = new ImageIcon("src/resources/gameIcon.png"); //icon for the snake 2d game
        snakeGame.setIconImage(icon.getImage());
    }
}