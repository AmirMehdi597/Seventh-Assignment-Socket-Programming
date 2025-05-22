package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private InputStream i;
    private OutputStream os;
    private List<ClientHandler> allClients;
    private String username;
    private int num = 0;

    public ClientHandler(Socket socket,List<ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.allClients =clients;
        this.i = socket.getInputStream();
        this.os = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(os, true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(i));

            while (true) {
                String message = reader.readLine();
                if (message == null) break;

                if (message.startsWith("login ")) {
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        String username = parts[1];
                        String password = parts[2];
                        handleLogin(username, password);
                    }
                    continue;
                }
                String formattedMessage = (username != null ? username : "Unknown") + ": " + message;
                System.out.println(formattedMessage);
                broadcast(formattedMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            num += 1;
            System.out.println("Connection closed for " + (username != null ? username : "Unknown"));
        }
    }


    private void sendMessage(String msg){
        try {
            PrintWriter writer = new PrintWriter(os, true);
            writer.println(msg);
        } catch (Exception e) {
            System.out.println("Error sending message to " + username + ": " + e.getMessage());
        }
    }
    private void broadcast(String msg) throws IOException {
        for (ClientHandler client : allClients) {
            if (client != this) {
                client.sendMessage(msg);
            }
        }
    }

    private void sendFileList(){
        try {
            File folder = new File("F:\\Seventh-Assignment-Socket-Programming\\src\\main\\resources\\Client\\user1");
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                sendMessage("no file found");
                return;
            }
            StringBuilder filenames = new StringBuilder();
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    if (file.length() > 0) {
                        filenames.append(", ");
                    }
                    filenames.append(file.getName());
                }
            }
            sendMessage("files found: " + filenames);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void sendFile(String fileName){
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                System.out.println("File " + fileName + " does not exist");
                return;
            }
            sendMessage("File: "+fileName+" "+file.length());
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void receiveFile(String filename, int fileLength)
    {
        try {
            byte[] buffer = new byte[fileLength];
            int totalBytesRead = 0;
            while (totalBytesRead != fileLength) {
                int bytesRead = i.read(buffer, totalBytesRead, fileLength - totalBytesRead);
                totalBytesRead += bytesRead;
            }
            if(totalBytesRead == fileLength){
                saveUploadedFile(filename, buffer);
                sendMessage("File uploaded successfully");
            }
            else {
                sendMessage("file upload failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void saveUploadedFile(String filename, byte[] data) throws IOException {
        File directory = new File("F:\\Seventh-Assignment-Socket-Programming\\src\\main\\resources\\Server\\Files");
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(directory, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
            System.out.println("File saved: " + file.getAbsolutePath());
        }
    }

    private void handleLogin(String username, String password) throws IOException, ClassNotFoundException {
        boolean authenticated = Server.authenticate(username, password);

        if (authenticated) {
            this.username = username;
            sendMessage("LOGIN_SUCCESS");
            System.out.println(username + " logged in successfully.");
        } else {
            sendMessage("LOGIN_FAILED");
            System.out.println("Failed login attempt for user: " + username);
        }
    }

}