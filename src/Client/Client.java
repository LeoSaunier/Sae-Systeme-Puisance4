import java.io.*;
import java.net.*;

public class Client {
    
        public static void main(String[] args) {
            if (args.length < 2) {
                System.out.println("Usage : java Client <server_address> <server_port>");
                return;
            }
    
            String SERVER_ADDRESS = args[0];
            int SERVER_PORT;

        try {
            SERVER_PORT = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Erreur : le port doit être un entier valide.");
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connecté au serveur Puissance 4.");

            // Lancer le thread de lecture
            Thread readThread = new ReadThread(in);
            // Lancer le thread d'écriture
            Thread writeThread = new WriteThread(out, userInput);

            // Démarrer les deux threads
            readThread.start();
            writeThread.start();

            // Attendre que le thread d'écriture se termine
            writeThread.join();

            if(readThread.isAlive()) {
                // Si le thread de lecture est encore actif, on l'interrompt
                readThread.interrupt();
            }
            socket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
