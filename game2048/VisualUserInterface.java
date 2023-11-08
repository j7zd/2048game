package game2048;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.Font;
import java.awt.event.KeyEvent;

/**
 * The VisualUserInterface class represents the graphical user interface of the 2048 game.
 * It extends the JFrame class and implements the KeyListener interface to handle user input.
 * The class contains methods to draw the game board and prompt the user to restart the game.
 */
public class VisualUserInterface{
    /**
     * A JFrame that represents the game window for the 2048 game.
     * It implements the KeyListener interface to handle user input.
     */
    private static class GameFrame extends JFrame implements KeyListener {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            draw(g);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            handleKeypress(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // do nothing
        }
        @Override
        public void keyTyped(KeyEvent e) {
            // do nothing
        }

        GameFrame() {
            super("2048");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 500);
            setVisible(true);
            addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
        }
    }
    
    private static GameFrame frame = new GameFrame();
    private static int[][] board = new int[4][4];
    private static final int CELL_SIZE = 100;
    private static Game game;
    private static int gameStatus = 0;

    /**
     * Draws the game board and score on the screen.
     * @param g the Graphics object to draw on
     */
    private static void draw(Graphics g) {
        int x = 50;
        int y = 50;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int value = board[i][j];
                drawCell(g, x, y, value);
                x += CELL_SIZE;
            }
            x = 50;
            y += CELL_SIZE;
        }
        
        int score = game.getScore();
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreStr = "Score: " + score;
        int strWidth = g.getFontMetrics().stringWidth(scoreStr);
        g.drawString(scoreStr, 250 - strWidth / 2, 490);

        if (gameStatus != 0) {
            g.setColor(new Color(0, 0, 0, 0.5f));
            g.fillRect(0, 0, 500, 500);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            if (gameStatus == 1) {
                String prompt = "You won!";
                strWidth = g.getFontMetrics().stringWidth(prompt);
                g.drawString(prompt, 250 - strWidth / 2, 200);
            } else {
                String prompt = "You lost!";
                strWidth = g.getFontMetrics().stringWidth(prompt);
                g.drawString(prompt, 250 - strWidth / 2, 200);
            }
            
            String fscore = "Final score: " + score;
            strWidth = g.getFontMetrics().stringWidth(fscore);
            g.drawString(fscore, 250 - strWidth / 2, 225);

            String prompt = "Play again? (y/n)";
            strWidth = g.getFontMetrics().stringWidth(prompt);
            g.drawString(prompt, 250 - strWidth / 2, 250);
        }
    }

    /**
     * Draws a single cell on the game board with the given value at the specified coordinates.
     * @param g the Graphics object to draw on
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     * @param value the value of the cell to be drawn
     */
    private static void drawCell(Graphics g, int x, int y, int value) {
        Color bgColor = Color.LIGHT_GRAY;
        Color textColor = value > 4 ? Color.WHITE : Color.DARK_GRAY;
        final int[] colors = {0xeee4da, 0xede0c8, 0xf2b179, 0xf59563, 0xf67c5f, 0xf65e3b, 0xedcf72, 0xedcc61, 0xedc850, 0xedc53f, 0xedc22e};

        if (value != 0) {
            int log = (int) (Math.log(value) / Math.log(2));
            bgColor = new Color(colors[log - 1]);
        }

        g.setColor(bgColor);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

        if (value == 0)
            return;

        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String valueStr = String.valueOf(value);
        int strWidth = g.getFontMetrics().stringWidth(valueStr);
        g.drawString(valueStr, x + CELL_SIZE / 2 - strWidth / 2, y + CELL_SIZE / 2 + 10);
    }
    
    /**
     * Handles the key press event and processes the move accordingly.
     * If the game is over, it prompts the user to restart the game.
     *
     * @param e the key event to be handled
     */
    private static void handleKeypress(KeyEvent e) {
        if (gameStatus != 0) {
            restartPrompt(e);
            return;
        }
        int status = -1;
        // for some reason up and left, and down and right are switched
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                status = game.processMove('u');
                break;
            case KeyEvent.VK_RIGHT:
                status = game.processMove('d');
                break;
            case KeyEvent.VK_UP:
                status = game.processMove('l');
                break;
            case KeyEvent.VK_DOWN:
                status = game.processMove('r');
                break;
            default:
                break;
        }
        if (status == -1)
            return;
        board = game.getBoard();
        frame.repaint();
        if (status == 0)
            return;
        gameStatus = status;
    }

    /**
     * Prompts the user to restart the game or exit the application based on the key pressed.
     * If the 'Y' key is pressed, the game is restarted. If the 'N' key is pressed, the application is exited.
     * @param e the KeyEvent that triggered the method call
     */
    private static void restartPrompt(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Y:
                startGame();
                break;
            case KeyEvent.VK_N:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    
    /**
     * It initializes the game board to all zeros.
     */
    public VisualUserInterface() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                board[i][j] = 0;
        }
    }

    /**
     * Starts the game by creating a new instance of the Game class, starting the game, getting the board, setting the game status to 0, painting the graphics, and requesting focus.
     */
    public static void startGame() {
        game = new Game();
        game.startGame();
        board = game.getBoard();
        gameStatus = 0;
        frame.paint(frame.getGraphics());
        frame.requestFocus();
    }
}
