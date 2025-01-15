import java.io.BufferedReader;
import java.io.IOException;

public class ReadThread extends Thread {
    private BufferedReader in;

    public ReadThread(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String serverMessage;
            while (!Thread.currentThread().isInterrupted() && (serverMessage = in.readLine()) != null) {
                System.out.println("Serveur : " + serverMessage);

            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) { // Si ce n'est pas une fermeture volontaire
                System.err.println("Erreur lors de la lecture du serveur : " + e.getMessage());
            }
        }finally {
            System.out.println("Thread de lecture arrêté.");
        }
    }
}
