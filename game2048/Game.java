package game2048;

import java.util.Random;

/**
 * This class represents the game logic for the 2048 game.
 */
class Game {
    /**
     * Represents a cell in the 2048 game board.
     */
    private class GameCell {
        private int value = 0;

        /**
         * Returns the value of the tile.
         * If the tile is empty, returns 0.
         * Otherwise, returns the tile's value.
         *
         * @return the value of the tile
         */
        int getValue() {
            if (value == 0)
                return 0;
            return 1 << value;
        }

        /**
         * Sets the value of the tile.
         * 
         * @param value the value to set the tile to
         * @throws IllegalArgumentException if the value is not between 0 and 2048 or is not a power of 2
         */
        void setValue(int value) {
            if (value == 0) {
                this.value = 0;
                return;
            }
            if (value < 2 || value > 2048)
                throw new IllegalArgumentException("Value must be between 0 and 2048");
            int newValue = 0;
            while (value > 1) {
                if (value % 2 != 0)
                    throw new IllegalArgumentException("Value must be a power of 2");
                value /= 2;
                newValue++;
            }
            this.value = newValue;
        }
    }

    private GameCell[][] board = new GameCell[4][4];
    private Random random = new Random();
    private boolean hasReached8 = false;
    private int score = 0;

    /**
     * Adds a random cell to the game board.
     */
    private void addRandomCell() {
        boolean hasEmptyCell = false;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4 && !hasEmptyCell; x++) {
                if (board[x][y].getValue() == 0) {
                    hasEmptyCell = true;
                    break;
                }
            }
        }
        if (!hasEmptyCell)
            return;
        
        int x, y, value;
        if (hasReached8) {
            int r = random.nextInt(100);
            if (r == 99)
                value = 8;
            else {
                if (r >= 90)
                    value = 4;
                else
                    value = 2;
            }
        }
        else
            value = 2;
        do {
            x = random.nextInt(4);
            y = random.nextInt(4);
        } while (board[x][y].getValue() != 0);
        board[x][y].setValue(value);
    }

    /**
     * Checks if the given integer is within the bounds of the game board.
     * @param a the integer to check
     * @return true if the integer is within the bounds of the game board, false otherwise
     */
    private static boolean isInBounds(int a) {
        return a >= 0 && a < 16;
    }

    /**
     * Checks if the given coordinates are within the bounds of the 2048 game board.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the coordinates are within bounds, false otherwise
     */
    private static boolean isInBounds(int x, int y) {
        return x >= 0 && x < 4 && y >= 0 && y < 4;
    }

    /**
     * Initializes the game board by creating new GameCell objects for each cell and adding a random cell.
     */
    void startGame() {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++)
                board[x][y] = new GameCell();
        }
        addRandomCell();
    }

    /**
     * Processes a move in the game by shifting the tiles in the specified direction and merging adjacent tiles with the same value.
     * 
     * @param move the direction of the move ('u' for up, 'd' for down, 'l' for left, 'r' for right)
     * @return an integer representing the result of the move: 0 if the game continues, 1 if the player wins, 2 if the game is over
     * @throws IllegalArgumentException if the move is invalid
     */
    int processMove(char move) {
        int dir, mx, my;
        
        switch (move) {
            case 'u':
                dir = 1; mx = 0; my = -1;
                break;
            case 'd':
                dir = -1; mx = 0; my = 1;
                break;
            case 'l':
                dir = 1; mx = -1; my = 0;
                break;
            case 'r':
                dir = -1; mx = 1; my = 0;
                break;
            default:
                throw new IllegalArgumentException("Invalid move");
        }
        
        int moveScore = 0;
        boolean[][] hasMerged = new boolean[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                hasMerged[i][j] = false;
            }
        }
        boolean reached2048 = false, hasChanged = false;

        for (int i = (dir == 1) ? 0 : 15; isInBounds(i); i += dir) {
            int x = i % 4, y = i / 4;
            if (board[x][y].getValue() == 0)
                continue;
            int nx = x + mx, ny = y + my;
            while (isInBounds(nx, ny) && board[nx][ny].getValue() == 0) {
                nx += mx;
                ny += my;
            }
            if (isInBounds(nx, ny) && board[nx][ny].getValue() == board[x][y].getValue() && !hasMerged[nx][ny]) {
                board[nx][ny].setValue(board[nx][ny].getValue() << 1);
                board[x][y].setValue(0);
                moveScore += board[nx][ny].getValue();
                if (board[nx][ny].getValue() == 8)
                    hasReached8 = true;
                hasMerged[nx][ny] = true;
                if (board[nx][ny].getValue() == 2048)
                    reached2048 = true;
                hasChanged = true;
            } else {
                nx -= mx; ny -= my;
                if (nx != x || ny != y) {
                    board[nx][ny].setValue(board[x][y].getValue());
                    board[x][y].setValue(0);
                    hasChanged = true;
                }
            }
        }

        score += moveScore;
        if (reached2048)
            return 1;
        if (hasChanged)
            addRandomCell();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                if (board[i][j].getValue() == 0)
                    return 0;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++)
                if (board[i][j].getValue() == board[i][j + 1].getValue())
                    return 0;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++)
                if (board[i][j].getValue() == board[i + 1][j].getValue())
                    return 0;
        }
        return 2;
    }

    /**
     * Returns the current score of the game.
     *
     * @return the current score of the game
     */
    int getScore() {
        return score;
    }

    /**
     * Returns a 2D integer array representing the current state of the game board.
     * Each element in the array represents the value of the corresponding tile on the board.
     * @return a 2D integer array representing the current state of the game board
     */
    int[][] getBoard() {
        int[][] ret = new int[4][4];
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++)
                ret[x][y] = board[x][y].getValue();
        }
        return ret;
    }
}