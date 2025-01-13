import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class GameThread implements Runnable {
    private Puissance4 game;
    private Socket player1Socket;
    private Socket player2Socket;

    public GameThread(Puissance4 game, Socket player1Socket, Socket player2Socket) {
        this.game = game;
        this.player1Socket = player1Socket;
        this.player2Socket = player2Socket;
    }

    @Override
    public void run() {
        try {
            while (!game.isGameOver()) {
                // Tour du joueur 1
                game.sendGameUpdate(player1Socket, "C'est à vous de jouer ! Voici l'état du jeu :\n" + game.getBoard());
                game.sendGameUpdate(player2Socket, "L'adversaire joue. Voici l'état du jeu :\n" + game.getBoard());

                // Attendre que le joueur 1 fasse un mouvement
                int move = getPlayerMove(player1Socket);
                game.makeMove(game.getPlayer1(), move);

                if (game.isGameOver()) break;

                // Tour du joueur 2
                game.sendGameUpdate(player1Socket, "L'adversaire joue. Voici l'état du jeu :\n" + game.getBoard());
                game.sendGameUpdate(player2Socket, "C'est à vous de jouer ! Voici l'état du jeu :\n" + game.getBoard());

                // Attendre que le joueur 2 fasse un mouvement
                move = getPlayerMove(player2Socket);
                game.makeMove(game.getPlayer2(), move);
            }

            // Game Over
            if (game.isGameOver()) {
                game.sendGameUpdate(player1Socket, "La partie est terminée !\n" + game.getBoard());
                game.sendGameUpdate(player2Socket, "La partie est terminée !\n" + game.getBoard());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getPlayerMove(Socket playerSocket) throws IOException {
        // Logic to receive a move from the player
        // For simplicity, assume that move is an integer
        // Read move from player (Socket -> BufferedReader)
        return 0; // Placeholder for actual move reading logic
    }
}
