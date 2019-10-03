package multusession_1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Server server;
    private PrintWriter out;

    private Scanner in;
    private static final String HOST = "localhost";
    private static final int PORT = 80;

    private Socket clientSocket = null;

    private static int clients_count = 0;

    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.out = new PrintWriter(socket.getOutputStream());
            this.in = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            server.sendMessageToAllClients("Новый участник вошёл в чат!");
            server.sendMessageToAllClients("Клиентов в чате = " + clients_count);

            while (true) {
                if (in.hasNext()) {
                    String clientMessage = in.nextLine();
                    if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                    }
                   if (clientMessage.equalsIgnoreCase("hi")){

                       server.sendMessageClient("hi",this);
                       // sendMsg("hi");

                      //  server.
                    }

                    System.out.println(clientMessage);
                    server.sendMessageToAllClients("Message from server: " + clientMessage);
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            out.println(msg);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
