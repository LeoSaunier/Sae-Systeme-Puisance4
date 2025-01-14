import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 30000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connecté au serveur Puissance 4.");

            // Lancer le thread de lecture
            Thread readThread = new ReadThread(in, out, userInput);
            // Lancer le thread d'écriture
            Thread writeThread = new WriteThread(out, userInput);

            // Démarrer les deux threads
            readThread.start();
            writeThread.start();

            // Attendre que le thread d'écriture se termine
            writeThread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
