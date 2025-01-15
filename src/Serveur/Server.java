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
    private static final Map<String, Socket> players = new HashMap<>();
    private static final Map<String, Puissance4> games = new HashMap<>();
    private static final Map<String, List<String>> history = new HashMap<>();
    private static final Map<String, String> waitingResponses = new HashMap<>();


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

    public static String ask(String playerName, String opponent) {
        synchronized (waitingResponses) {
            if (!players.containsKey(opponent)) {
                return "ERR Adversaire non trouvé.";
            } else if (waitingResponses.containsKey(playerName)) {
                return "ERR Vous avez déjà une demande en attente.";
            } else if (opponent.equals(playerName)) {
                return "ERR Vous ne pouvez pas jouer contre vous-même.";
            }
    
            // Envoyer la demande
            Socket opponentSocket = players.get(opponent);
            try {
                PrintWriter opponentOut = new PrintWriter(opponentSocket.getOutputStream(), true);
                opponentOut.println(playerName + " vous invite à jouer une partie de Puissance 4.");
                waitingResponses.put(opponent, playerName);
            } catch (IOException e) {
                return "ERR Impossible d'envoyer la demande à " + opponent;
            }
        }
    
        // Attendre une réponse
        long startTime = System.currentTimeMillis();
        while (true) {
            synchronized (waitingResponses) {
                if (!waitingResponses.containsKey(opponent)) {
                    // La demande a été traitée (acceptée ou refusée)
                    return "Demande traitée.";
                }
            }
    
            // Vérifier le timeout (30 secondes)
            if (System.currentTimeMillis() - startTime > 30000) {
                synchronized (waitingResponses) {
                    waitingResponses.remove(opponent); // Annuler la demande en attente
                }
                return "ERR Temps écoulé. Demande annulée.";
            }
    
            try {
                Thread.sleep(100); // Éviter un verrouillage actif
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "ERR Interruption lors de l'attente.";
            }
        }
    }

    public static String accept(String playerName) {
        String demandeur;
        synchronized (waitingResponses) {
            if (!waitingResponses.containsKey(playerName)) {
                return "ERR Demande invalide.";
            }
    
            demandeur = waitingResponses.remove(playerName); // Retirer la demande
        }
    
        synchronized (games) {
            Puissance4 game = new Puissance4(playerName, demandeur);
    
            // Associer cette partie aux deux joueurs
            games.put(playerName, game);
            games.put(demandeur, game);
    
            Thread partie = new Thread(new GameThread(game, players.get(playerName), players.get(demandeur)));
            partie.start();
        }
    
        return "OK Partie acceptée.";
    }


    public static synchronized String reject(String playerName, String opponent) {
        if (!waitingResponses.containsKey(playerName) || !waitingResponses.get(playerName).equals(opponent)) {
            return "ERR Aucune demande valide trouvée.";
        }

        waitingResponses.remove(playerName);
        games.remove(opponent);
        waitingResponses.notifyAll();
        return "OK Demande refusée.";
    }

    public static synchronized void addHistory(String playerName, String result) {
        history.get(playerName).add(result);
    }

    public static synchronized List<String> getHistory(String playerName) {
        return history.getOrDefault(playerName, Collections.emptyList());
    }

    public static synchronized void endGame(String playerName, String opponent) {
        games.remove(playerName);
        games.remove(opponent);
        System.out.println("Partie terminée entre " + playerName + " et " + opponent + ".");
    }

    public static void releasePlayer(String playerName) {
        // Remet le joueur disponible pour d'autres interactions
        System.out.println("Le joueur " + playerName + " a été libéré et peut envoyer d'autres commandes.");
    }

    public static boolean isPlayerInGame(String playerName) {
        return games.containsKey(playerName);
    }
}