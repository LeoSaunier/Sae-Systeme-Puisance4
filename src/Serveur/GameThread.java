import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        boolean player1Turn = true; // Détermine quel joueur doit jouer

        while (!game.isGameOver()) {
            if (isPlayerDisconnected(player1Socket)){
                game.sendGameUpdate(player2Socket, "L'adversaire s'est déconnecté. Vous avez gagné !");
                break;
            }
            if (isPlayerDisconnected(player2Socket)){
                game.sendGameUpdate(player1Socket, "L'adversaire s'est déconnecté. Vous avez gagné !");
                break;
            }
            if (player1Turn) {
                // Tour du joueur 1
                game.sendGameUpdate(player1Socket, "C'est à vous de jouer ! Voici l'état du jeu :\n" + game.getBoard());
                game.sendGameUpdate(player2Socket, "L'adversaire joue. Voici l'état du jeu :\n" + game.getBoard());

                int move = getPlayerMove(player1Socket);
                if (game.makeMove(game.getPlayer1(), move)) {
                    player1Turn = false; // Passe au tour du joueur 2
                } else {
                    game.sendGameUpdate(player1Socket, "Mouvement invalide. Essayez encore.");
                }
            } else {
                // Tour du joueur 2
                game.sendGameUpdate(player1Socket, "L'adversaire joue. Voici l'état du jeu :\n" + game.getBoard());
                game.sendGameUpdate(player2Socket, "C'est à vous de jouer ! Voici l'état du jeu :\n" + game.getBoard());

                int move = getPlayerMove(player2Socket);
                if (game.makeMove(game.getPlayer2(), move)) {
                    player1Turn = true; // Passe au tour du joueur 1
                } else {
                    game.sendGameUpdate(player2Socket, "Mouvement invalide. Essayez encore.");
                }
            }
        }

        // Partie terminée
        game.sendGameUpdate(player1Socket, "La partie est terminée ! Voici l'état final du jeu :\n" + game.getBoard());
        game.sendGameUpdate(player2Socket, "La partie est terminée ! Voici l'état final du jeu :\n" + game.getBoard());
        Server.endGame(game.getPlayer1(), game.getPlayer2());
    } catch (IOException e) {
        e.printStackTrace();
    }finally {
        try {
            if (player1Socket != null && !player1Socket.isClosed()) {
                player1Socket.close();
            }
            if (player2Socket != null && !player2Socket.isClosed()) {
                player2Socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


    private int getPlayerMove(Socket playerSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        String input = in.readLine(); // Lire la commande du joueur

        if (input == null) {
            throw new IOException("Le joueur s'est déconnecté."); // Signaler une déconnexion
        }

        input = input.trim(); // Nettoyer l'entrée

        if (input.startsWith("mv ")) {
            String[] parts = input.split(" "); // Diviser la commande
            if (parts.length == 2) {
                try {
                    return Integer.parseInt(parts[1])-1; // Convertir la colonne en entier
                } catch (NumberFormatException e) {
                    return -1; // Entrée invalide si le chiffre n'est pas correct
                }
            }
        }

        return -1; // Commande invalide
    }

    private boolean isPlayerDisconnected(Socket playerSocket) {
    try {
        PrintWriter out = new PrintWriter(playerSocket.getOutputStream(), true);
        out.println("PING"); // Test si le joueur est toujours connecté
        return false; // Le joueur est connecté
    } catch (IOException e) {
        return true; // Le joueur est déconnecté
    }
}


}
