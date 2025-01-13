import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ReadThread extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;

    public ReadThread(BufferedReader in, PrintWriter out, BufferedReader userInput) {
        this.in = in;
        this.out = out;
        this.userInput = userInput;
    }

    @Override
    public void run() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Serveur : " + serverMessage);

                // Si le serveur envoie une demande d'invitation pour jouer
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du serveur : " + e.getMessage());
        }
    }
}
