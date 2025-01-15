import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Puissance4 {
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private char[][] board;
    private String player1;
    private String player2;
    private String currentPlayer;
    private boolean gameOver;

    public Puissance4(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.board = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }
        this.gameOver = false;
    }

    public boolean makeMove(String player, int col) {
        if (gameOver) return false;

        // Vérifier que c'est bien au tour du joueur actuel
        if (!player.equals(currentPlayer)) {
            return false;
        }

        // Vérifier si la colonne est valide
        if (col < 0 || col >= COLS || board[0][col] != '.') {
            return false;
        }

        // Trouver la ligne disponible dans la colonne
        int row = -1;
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][col] == '.') {
                row = i;
                break;
            }
        }

        // Placer le jeton
        board[row][col] = currentPlayer.equals(player1) ? 'X' : 'O';

        // Vérifier la victoire
        if (checkWin(row, col)) {
            gameOver = true;
            return true;
        }

        // Changer de joueur
        currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
        return true;
    }

    private boolean checkWin(int row, int col) {
        char token = board[row][col];
        return checkDirection(row, col, 0, 1, token) ||  // Horizontal
               checkDirection(row, col, 1, 0, token) ||  // Vertical
               checkDirection(row, col, 1, 1, token) ||  // Diagonal /
               checkDirection(row, col, 1, -1, token);   // Diagonal \
    }

    private boolean checkDirection(int row, int col, int rowDir, int colDir, char token) {
        int count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = row + i * rowDir;
            int c = col + i * colDir;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == token) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getBoard() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                boardString.append(board[i][j]).append(" ");
            }
            boardString.append("\n");
        }
        return boardString.toString();
    }

    public void sendGameUpdate(Socket playerSocket, String message) throws IOException {
        PrintWriter out = new PrintWriter(playerSocket.getOutputStream(), true);
        out.println(message);
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setGameOver(boolean b) {
        // TODO Auto-generated method stub
        this.gameOver = b;
    }
}
