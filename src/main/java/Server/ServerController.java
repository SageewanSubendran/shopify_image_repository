/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.UploadedImage;
import static Server.Server.imageList;
import static Server.Server.salesAmount;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Work
 */
public class ServerController implements Runnable {

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ServerController(Socket socket) {
        try {
            //initialize input and output 
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //get client request
    public String getClientRequest() {
        String request = null;
        try {
            request = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return request;
    }

    //send response to client
    public void sendResponse(String response) {
        try {
            objectOutputStream.writeObject(response);
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //run in a thread...as long as server is running to monitor client incoming requests
    @Override
    public void run() {

        String clientRequest = null;

        while (true) {

            //get client request
            try {
                clientRequest = getClientRequest();
            } catch (Exception e) {
                closeConnection();
                break;
            }
            
            //if request is null, close the client
            if (clientRequest == null) {
                System.out.println("request is null");
                break;
            }

            String query = clientRequest;

            //take action based on the query received from the client
            switch (query) {
                //when seller uploads an image
                case "sendImage": {
                    try {
                        //create new uploadedImage object
                        UploadedImage ui = new UploadedImage();

                        //get image format and set it in object
                        String format = getClientRequest();
                        ui.setFormat(format);

                        //get image title and set it in object
                        String title = getClientRequest();
                        ui.setTitle(title);

                        //get image price and set it in object                        
                        String price = getClientRequest();
                        ui.setPrice(price);

                        //get image string and set it in object
                        String encoded = getClientRequest();
                        ui.setImageInString(encoded);

                        //add the image to list
                        imageList.add(ui);

                        

                        //after image is successfully saved, send success response to client
                        sendResponse("image saved on server");

                        System.out.println("length of list - " + imageList.size());
                    } catch (Exception ex) {
                        Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                //when server requests for images
                case "getImages": {
                    System.out.println("Buyer is requesting image list");

                    /**
                     * The images are sent as a single String in CSV format. One
                     * line consists of one single UploadedImage Object. Each
                     * line consist of 4 properties separated by "," {title,
                     * imageString, price, format}
                     */
                    String response = "";
                    for (int i = 0; i < imageList.size(); i++) {
                        UploadedImage ui = imageList.get(i);
                        response += ui.getTitle() + ",";
                        response += ui.getImageInString() + ",";
                        response += ui.getPrice() + ",";
                        response += ui.getFormat() + "\n";
                    }
                    sendResponse(response);
                    break;
                }
                //when buyer buys an image
                case "buyImage": {
                    System.out.println("Buyer bought an image");
                    String priceString = getClientRequest();
                    int price = Integer.parseInt(priceString);                    
                    salesAmount += price; // update amount earned by adding sold image price to salesAmount
                    System.out.println("Total Sales = " + salesAmount);
                    break;
                }                
                //when seller enquires about total sales
                case "getTotalSales": {
                    sendResponse(""+salesAmount); 
                    break;
                }
                
            }
        }
        closeConnection();
    }

    public void closeConnection() {
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
