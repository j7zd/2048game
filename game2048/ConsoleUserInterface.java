package game2048;

import java.util.Scanner;

/**
 * This class represents the console user interface for the 2048 game.
 */
public class ConsoleUserInterface {
    /**
     * Prints the game board to the console.
     *
     * @param board the 2D integer array representing the game board
     */
    private static void printBoard(int[][] board) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++)
                System.out.print(board[x][y] + " ");
            System.out.println();
        }
    }

    /**
     * Starts the game. 
     * The game board is displayed on the console and the user is prompted to enter 
     * moves until the game is won or lost. The final score is displayed and the user 
     * is prompted to play again or exit.
     */
    public static void startGame() {
        Game game = new Game();
        game.startGame();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int[][] board = game.getBoard();
            printBoard(board);

            char input;
            int status;
            while (true) {
                try {
                    input = scanner.nextLine().charAt(0);
                    try {
                        status = game.processMove(input);
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Invalid input. Please enter a move (u/l/d/r)");
                }
            }

            if (status == 0)
                continue;

            board = game.getBoard();
            printBoard(board);
            
            if (status == 1) {
                System.out.println("You won!");
            } else if (status == 2) {
                System.out.println("You lost!");
            }
            System.out.println("Final score: " + game.getScore());
            System.out.println("Play again? (y/n)");
            while (true) {
                try {
                    input = scanner.nextLine().charAt(0);
                    break;
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Invalid input. Please enter 'y' or 'n'");
                }
            }
            if (input == 'y') {
                game = new Game();
                game.startGame();
            } else {
                scanner.close();
                return;
            }
        }
    }
}
