/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.UploadedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Sageewan Subendran
 */
public class Server {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private ExecutorService executorPool;
    private final int PORT = 8081;
    
    //list to store images uploaded from server
    public static List<UploadedImage> imageList;
    
    //counter to store amount earned from sales
    public static int salesAmount = 0;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            executorPool = Executors.newCachedThreadPool(); 
            
            //initialize image list
            imageList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try {
            while (true) {
                System.out.println("Server is running...");
                clientSocket = serverSocket.accept();
                System.out.println("Client has connected...starting new controller");

                //pass the client to server controller class to monitor client requests
                ServerController serverController = new ServerController(clientSocket);

                //start the runnable thread
                executorPool.execute(serverController);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorPool.shutdown();
        stop();
    }
    
    //stop the server
    public void stop(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
