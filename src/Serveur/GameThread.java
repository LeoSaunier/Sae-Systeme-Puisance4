import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            boolean player1Turn = true; // Détermine quel joueur doit jouer

            while (!game.isGameOver()) {
                // Vérifie si un joueur est déconnecté
                if (isPlayerDisconnected(player1Socket)) {
                    notifyWin(player2Socket, "L'adversaire s'est déconnecté. Vous avez gagné !");

                    break;
                }
                if (isPlayerDisconnected(player2Socket)) {
                    notifyWin(player1Socket, "L'adversaire s'est déconnecté. Vous avez gagné !");
                    break;
                }

                Socket currentPlayerSocket = player1Turn ? player1Socket : player2Socket;
                Socket opponentSocket = player1Turn ? player2Socket : player1Socket;
                String currentPlayer = player1Turn ? game.getPlayer1() : game.getPlayer2();

                try {
                    game.sendGameUpdate(currentPlayerSocket, "C'est à vous de jouer ! Voici l'état du jeu :\n" + game.getBoard());
                    game.sendGameUpdate(opponentSocket, "L'adversaire joue. Voici l'état du jeu :\n" + game.getBoard());

                    int move = getPlayerMove(currentPlayerSocket);
                    if (game.makeMove(currentPlayer, move)) {
                        player1Turn = !player1Turn;
                    } else {
                        game.sendGameUpdate(currentPlayerSocket, "Mouvement invalide. Essayez encore.");
                    }
                } catch (IOException e) {
                    handleDisconnection(opponentSocket, currentPlayer);
                    break;
                }
            }

            // Partie terminée, notifie les joueurs
            if (game.isGameOver()) {
                game.sendGameUpdate(player1Socket, "La partie est terminée ! Voici l'état final du jeu :\n" + game.getBoard());
                game.sendGameUpdate(player2Socket, "La partie est terminée ! Voici l'état final du jeu :\n" + game.getBoard());
            }

            // Supprime la partie des parties en cours
            Server.endGame(game.getPlayer1(), game.getPlayer2());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int getPlayerMove(Socket playerSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        String input = in.readLine();

        if (input == null) {
            throw new IOException("Le joueur s'est déconnecté.");
        }

        input = input.trim();
        if (input.startsWith("mv ")) {
            try {
                return Integer.parseInt(input.split(" ")[1]) - 1;
            } catch (NumberFormatException e) {
                throw new IOException("Colonne invalide. Entrez un entier.");
            }
        }

        throw new IOException("Commande invalide. Utilisez 'mv <colonne>'.");
    }

    private boolean isPlayerDisconnected(Socket playerSocket) {
        try {
            playerSocket.sendUrgentData(0xFF); // Test la connexion du joueur
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    private void handleDisconnection(Socket opponentSocket, String disconnectedPlayer) {
        try {
            if (opponentSocket != null && !opponentSocket.isClosed()) {
                game.sendGameUpdate(opponentSocket, "L'adversaire (" + disconnectedPlayer + ") s'est déconnecté. Vous avez gagné !");
                // Remettre le joueur dans le contexte général du serveur
                Server.releasePlayer(disconnectedPlayer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        game.setGameOver(true); // Met fin au jeu
        Server.endGame(game.getPlayer1(), game.getPlayer2()); // Supprime la partie des parties en cours
    }

    private void notifyWin(Socket playerSocket, String message) {
    try {
        if (playerSocket != null && !playerSocket.isClosed()) {
            game.sendGameUpdate(playerSocket, message);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


}
