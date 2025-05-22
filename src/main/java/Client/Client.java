package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    static InputStream i;
    static OutputStream os;
    private static String username;
    public static void main(String[] args) throws Exception {

        try (Socket socket = new Socket("localhost", 12345)) {
            i = socket.getInputStream();
            os = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            Scanner in = new Scanner(i);
            PrintWriter out = new PrintWriter(os, true);

            // --- LOGIN PHASE ---
            System.out.println("===== Welcome to CS Music Room =====");


            boolean loggedIn = false;
            while (!loggedIn) {
                System.out.print("Username: ");
                String username = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();


                sendLoginRequest(username, password);

                String response = in.nextLine();
                if (response.equals("LOGIN_SUCCESS")) {
                    System.out.println("Login Successful");
                    loggedIn = true;
                }
                else {
                    System.out.println("Login Failed");
                }
            }

            // --- ACTION MENU LOOP ---
            while (true) {
                printMenu();
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> enterChat(scanner);
                    case "2" -> uploadFile(scanner);
                    case "3" -> requestDownload(scanner);
                    case "0" -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Enter chat box");
        System.out.println("2. Upload a file");
        System.out.println("3. Download a file");
        System.out.println("0. Exit");
    }

    private static void sendLoginRequest(String username, String password) throws IOException {
        String request = "login " + username + " " + password + "\n";
        os.write(request.getBytes());
        os.flush();
    }
    private static void enterChat(Scanner scanner) throws IOException {
        System.out.println("You have entered the chat (type /exit to leave):");

        ClientReceiver receiver = new ClientReceiver(i); // i = InputStream from socket
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();

        String message_string;
        while (true) {
            message_string = scanner.nextLine();

            if (message_string.equalsIgnoreCase("/exit")) {
                sendChatMessage(message_string);
                break;
            }
            sendChatMessage(message_string);
        }
        receiverThread.interrupt();
    }


    private static void sendChatMessage(String message_to_send) throws IOException {
        os.write((message_to_send + "\n").getBytes());
        os.flush();
    }

    private static void uploadFile(Scanner scanner) throws IOException {

        File user = new File("resources/Client/" + username);
        File[] files =user.listFiles() ;
        if (files == null || files.length == 0) {
            System.out.println("No files to upload.");
            return;
        }

        // Show available files
        System.out.println("Select a file to upload:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("Enter file number: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (choice < 0 || choice >= files.length) {
            System.out.println("Invalid choice.");
            return;
        }
        File file = files[choice];
        String metadata = "UPLOAD:" + file.getName() + ":" + file.length() + "\n";
        os.write(metadata.getBytes());
        os.flush();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            System.out.println("File uploaded successfully: " + file.getName());
        }
    }

    private static void requestDownload(Scanner scanner) throws IOException {
        String listRequest = "List_Files\n";
        os.write(listRequest.getBytes());
        os.flush();
        Scanner in = new Scanner(i);
        List<String> availableFiles = new ArrayList<>();

        System.out.println("Available files for download:");
        String line;
        while (!(line = in.nextLine()).equals("END_OF_LIST")) {
            availableFiles.add(line);
        }

        if (availableFiles.isEmpty()) {
            System.out.println("No files available on server.");
            return;
        }

        for (int j = 0; j < availableFiles.size(); j++) {
            System.out.println((j + 1) + ". " + availableFiles.get(j));
        }
        System.out.print("Enter the number of the file to download: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (choice < 0 || choice >= availableFiles.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String fileToDownload = availableFiles.get(choice);
        String downloadRequest = "DOWNLOAD:" + fileToDownload + "\n";
        os.write(downloadRequest.getBytes());
        os.flush();
        String sizeLine = in.nextLine();
        long fileSize;
        try {
            fileSize = Long.parseLong(sizeLine);
        } catch (NumberFormatException e) {
            System.out.println("Server returned invalid file size.");
            return;
        }
        File saveDir = new File("resources/Client/" + username);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File outputFile = new File(saveDir, fileToDownload);
        FileOutputStream fos = new FileOutputStream(outputFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        long totalRead = 0;

        while (totalRead < fileSize && (bytesRead = i.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
        }

        fos.close();

        System.out.println("File downloaded successfully: " + outputFile.getAbsolutePath());
    }
}
