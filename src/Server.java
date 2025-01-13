import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Server {
    private static Map<String, Socket> players = new HashMap<>();
    private static Map<String, Puissance4> games = new HashMap<>();
    private static Map<String, List<String>> history = new HashMap<>();
    private static Map<String, String> waitingResponses = new HashMap<>();


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(30000);
        System.out.println("Serveur en attente de connexions sur le port 12345...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public static synchronized String connect(String playerName, Socket socket) {
        if (players.containsKey(playerName) || playerName.isBlank()) {
            return "ERR Nom de joueur invalide ou déjà pris.";
        }
        players.put(playerName, socket);
        history.putIfAbsent(playerName, new ArrayList<>());
        return "OK";
    }

    public static synchronized String listPlayers() {
        return String.join(", ", players.keySet());
    }

    public static synchronized String disconnect(String playerName) {
        players.remove(playerName);
        return "OK";
    }

    public static synchronized String ask(String playerName, String opponent) {
    if (!players.containsKey(opponent)) {
        return "ERR Adversaire non trouvé.";
    }

    // Envoie une demande au joueur spécifié
    Socket opponentSocket = players.get(opponent);
    try {
        PrintWriter opponentOut = new PrintWriter(opponentSocket.getOutputStream(), true);
        opponentOut.println(playerName + " vous invite à jouer une partie de Puissance 4.");

        // Envoi d'une confirmation au joueur qui a envoyé la demande
        waitingResponses.put(opponent, playerName);

        long startTime = System.currentTimeMillis();
        while (waitingResponses.containsKey(opponent)) {
            try {
                Thread.sleep(100); // Attente pour éviter un CPU 100%
            } catch (InterruptedException e) {
                return "ERR Interruption lors de l'attente de la réponse.";
            }
            
            // Timeout après 30 secondes
            if (System.currentTimeMillis() - startTime > 30000) {
                waitingResponses.remove(opponent);
                games.remove(playerName);
                return "ERR Temps écoulé. Demande annulée.";
            }
            
    }
    return "lancement de la partie";
    } catch (IOException e) {
        return "ERR Impossible d'envoyer la demande à " + opponent;
    }
}

public static synchronized String accept(String playerName, String opponent) {
    if (!games.containsKey(opponent) || !games.get(opponent).equals(playerName)) {
        return "ERR Demande invalide.";
    }
    waitingResponses.remove(playerName);
    // Créer un nouvel objet Game pour la partie
    Puissance4 game = new Puissance4(playerName, opponent);
    
    // Associer cette partie aux deux joueurs dans ongoingGames
    games.put(playerName, game);
    games.put(opponent, game);
    
    // Démarrer un nouveau thread pour la partie
    Thread partie = new Thread(new GameThread(game, players.get(playerName), players.get(opponent)));
    partie.start();
    
    // Retourner un message de confirmation
    return "OK Partie acceptée entre " + playerName + " et " + opponent;
}


public static synchronized String reject(String playerName, String opponent) {
    if (!waitingResponses.containsKey(playerName) || !waitingResponses.get(playerName).equals(opponent)) {
        return "ERR Aucune demande valide trouvée.";
    }

    waitingResponses.remove(playerName);
    games.remove(opponent);
    return "OK Demande refusée.";
}

    public static synchronized void addHistory(String playerName, String result) {
        history.get(playerName).add(result);
    }

    public static synchronized List<String> getHistory(String playerName) {
        return history.getOrDefault(playerName, Collections.emptyList());
    }
}