package multusession_1;

import java.io.IOException;
import java.util.Scanner;

public class ClientJar {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Client client = new Client();
        while (true){
            System.out.println("Client menu:");
            System.out.println("1) Show all messages");
            System.out.println("2) Put message from server");
            System.out.println("3) Close connection");
            System.out.println("4) FuckOff mather fucker!");
            System.out.println("Input digit");
            int n = in.nextInt();
            if (n == 1)
                client.showDialog();
            if(n == 2){
                System.out.println("Input message and push enter");
                String message = in.nextLine();
                client.putMessage(message);
            }
            if (n == 3){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Connection closed, good buy!");
                break;
            }
            if (n == 4){
                System.out.println("No no no!");
            }
        }
    }
}
