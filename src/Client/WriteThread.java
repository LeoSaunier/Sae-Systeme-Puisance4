import java.io.*;

public class WriteThread extends Thread {
    private PrintWriter out;
    private BufferedReader userInput;


    public WriteThread(PrintWriter out, BufferedReader userInput) {
        this.out = out;
        this.userInput = userInput;
    }

    public void run() {
        try {
            String command;
            while (true) {
                System.out.print("> ");
                command = userInput.readLine();
                if (command == null) break; // Gestion des EOF ou interruption
                out.println(command);
                if (command.equalsIgnoreCase("disconnect")) {
                    out.println("disconnect");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi des données au serveur : " + e.getMessage());
        } 
    }
}
