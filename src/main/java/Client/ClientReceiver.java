package Client;


import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ClientReceiver implements Runnable {
    private InputStream in;
    public ClientReceiver(InputStream i) {
        this.in = i;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Scanner scanner = new Scanner(in);
                while (scanner.hasNextLine()) {
                    String message = scanner.nextLine();
                    System.out.println("\n[Server] " + message);
                }
            }
        }catch (Exception e) {
            System.out.println("Connection to server lost.");
        }
    }

}
