package Seller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Base64;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Sageewan Subendran
 */
public class SellerController implements Initializable {

    @FXML
    private Button browseButton;
    @FXML
    private TextField titleTF;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField priceTF;
    @FXML
    private Button uploadButton;
    @FXML
    private Label amountLabel;

    private final String SERVER_NAME = "localhost";
    private final int PORT = 8081;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    Image image;
    File file;

    /**
     * boolean to check if seller program is uploading an image currently
     */
    AtomicBoolean uploading = new AtomicBoolean(false);

    //This method is called when the seller window starts
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //initailize connection to server and input and output stream
        try {
            socket = new Socket(SERVER_NAME, PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SellerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //start getting details of images sold and sales from server
        startGettingDetailsFromServer();

    }

    //This method is called when BrowseImage Button is clicked
    @FXML
    private void browseButtonClicked(ActionEvent event) {
        //FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image File Path");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("IMAGE FILES", "*.jpg", "*.png")
        );

        //get the selected file
        file = fileChooser.showOpenDialog(browseButton.getScene().getWindow());

        //if user does not select a file
        if (file == null) {
            System.out.println("no file is selected");
            return;
        }

        //convert the file to image        
        image = new Image(file.toURI().toString());

        //set preview of the file
        imageView.setImage(image);

        //set title of image without file extension. User can edit the title
        titleTF.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));

    }

    //This method is called when Upload Button is clicked
    @FXML
    private void uploadButtonClicked(ActionEvent event) {

        //check if image is there or not
        if (file == null) {
            System.out.println("No image is selected");
            return;
        }

        int price = 0; //default price will be 0
        //check if entered price is in int format or not
        try {
            price = Integer.parseInt(priceTF.getText());
        } catch (Exception e) {
            System.out.println("Price has to be in numbers.");
            return;
        }

        //Title Validation
        if (titleTF.getText() == null || "".equals(titleTF.getText())) {
            System.out.println("Title cannot be empty.");
            return;
        }

        sendImageToServer(file, titleTF.getText(), price);
        System.out.println("File uploaded to server.");

        //clear all fields after image is upoaded
        image = null;
        titleTF.setText("");
        priceTF.setText("0");
        imageView.setImage(null);
    }

    //send some request to server
    public void sendRequest(String request) {
        try {
            objectOutputStream.writeObject(request);
        } catch (IOException ex) {
            Logger.getLogger(SellerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //get response from the server
    public String getResponse() {
        String response = "";
        try {
            response = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SellerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    //This method is used to send image to the server
    public void sendImageToServer(File file, String title, int price) {

        //set boolean uploading to true before starting image upload
        uploading.set(true);

        //get iamge format
        String format = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
        try {

            //send request to let server know that seller is going to upload a image
            sendRequest("sendImage");

            //send format
            sendRequest(format);

            //send title
            sendRequest(title);

            //send price
            sendRequest(price + "");

            //convert image to string
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            String encodedfile = Base64.getEncoder().encodeToString(bytes);

            //send the converted string 
            sendRequest(encodedfile);

            //get response from server
            System.out.println(getResponse());
        } catch (IOException ex) {
            Logger.getLogger(SellerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //set boolean uploading to false after finishing image upload
        uploading.set(false);
    }

    //To get images sold and sales amount from server
    public void startGettingDetailsFromServer() {
        /**
         * To get sales amount information, we start a thread
         * which will run every 5 seconds. After every 5 seconds the information
         * will be refreshed from the current values stored at server.
         */
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {                
                while (true) {          
                    sendRequest("getTotalSales");
                    String sales = getResponse();
                    Platform.runLater(new Runnable() {
                        //Platform.runLater() is used to update UI elements from other thread
                        @Override
                        public void run() {
                            amountLabel.setText(sales);
                        }
                    });                  
                    Thread.sleep(5000);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();        
    }
}
