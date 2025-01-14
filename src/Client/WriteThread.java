import java.io.*;

public class WriteThread extends Thread {
    private PrintWriter out;
    private BufferedReader userInput;

    public WriteThread(PrintWriter out, BufferedReader userInput) {
        this.out = out;
        this.userInput = userInput;
    }

    @Override
    public void run() {
        try {
            String command;
            while (true) {
                System.out.print("> ");
                command = userInput.readLine();
                out.println(command);
                if (command.equalsIgnoreCase("exit")) {
                    out.println("disconnect");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi des données au serveur : " + e.getMessage());
        }
    }
}
