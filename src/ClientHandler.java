import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private String playerName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            while (true) {
                String input = in.readLine();
                if (input == null) break;

                String[] tokens = input.split(" ", 2);
                String command = tokens[0];
                String argument = tokens.length > 1 ? tokens[1] : "";

                switch (command) {
                    case "connect":
                        playerName = argument;
                        out.println(Server.connect(playerName, socket));
                        break;
                    case "list":
                        out.println(Server.listPlayers());
                        break;
                    case "disconnect":
                        out.println(Server.disconnect(playerName));
                        return;
                    case "ask":
                        String opponent = argument;
                        out.println(Server.ask(playerName, opponent));
                        break;
                    case "accept":
                        out.println(Server.accept(playerName, argument));
                        break;
                    case "reject":
                        out.println(Server.reject(playerName, argument));
                        break;
                    case "history":
                        out.println(String.join("\n", Server.getHistory(playerName)));
                        break;
                    default:
                        out.println("ERR Commande inconnue.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (playerName != null) {
                Server.disconnect(playerName);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
