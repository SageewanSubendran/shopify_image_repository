package Buyer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Model.UploadedImage;
import Alerts.Alerts;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class
 *
 * @author Sageewan Subendran
 */
public class BuyerController implements Initializable {

    @FXML
    private Label balanceLabel;
    @FXML
    private Button addBalanceButton;
    @FXML
    private TilePane tilePane;
    @FXML
    private Button refreshButton;
    @FXML
    private TextField addBalanceTF;

    private final String SERVER_NAME = "localhost";
    private final int PORT = 8081;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    //list to store images recieved from server
    List<UploadedImage> imageList;

    //to store buyer balance
    int balance = 0;

    //this method is called when the buyer window starts
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //initailize connection to server and input and output stream
        try {
            socket = new Socket(SERVER_NAME, PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(BuyerController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //get images from the server
        getImagesFromServer();
        
        //adds all images retrieved to buyer screen
        imageList.forEach(c -> {
            addImageToTilePane(c);
        });
    }

    //method to add balance to the buyer account
    @FXML
    private void addBalanceButtonCLicked(ActionEvent event) {
        int add = 0;
        //check if entered price is in int format or not
        try {
            add = Integer.parseInt(addBalanceTF.getText());
        } catch (Exception e) {
            System.out.println("Buyer did not enter a numeric value");
            Alerts.display("ERROR", "Please enter a numeric value (Ex: 100)");
            return;
        }

        updateBalance(balance + add);
        addBalanceTF.setText("0");
    }

    //this method is used to refresh the images so that new Images on the server are added to the Tile imageList
    @FXML
    private void refreshButtonClicked(ActionEvent event) {
        getImagesFromServer();
        imageList.forEach(c -> {
            addImageToTilePane(c);
        });
    }

    //send some request to server
    public void sendRequest(String request) {
        try {
            objectOutputStream.writeObject(request);
        } catch (IOException ex) {
            Logger.getLogger(BuyerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //get response from the server
    public String getResponse() {
        String response = "";
        try {
            response = (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BuyerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    //this method is used to add image from UploadedImage object to Tile Pane
    public void addImageToTilePane(UploadedImage ui) {
        //create a vbox to hold image, title and buy button
        VBox vbox = new VBox();

        //convert string to byte array
        byte[] imageByte = Base64.getDecoder().decode(ui.getImageInString());

        //convert byte array to image and set to imageview
        Image image = new Image((new ByteArrayInputStream(imageByte)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        //set title 
        Label label = new Label(ui.getTitle());

        //create buy button
        Button button = new Button("Buy");

        //action to perform when buy button is clicked
        button.setOnAction(c -> {
            purchaseImage(ui);
        });

        //set vbox properties for alignment
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);

        //add image, title and button to vbox
        vbox.getChildren().addAll(imageView, label, button);

        //add vbox to tile pane
        tilePane.getChildren().add(vbox);
    }

    //method to get Images from the server
    public void getImagesFromServer() {

        //clear the tiles from tilepane
        tilePane.getChildren().clear();

        //reinitialize the image list
        imageList = new ArrayList<>();

        //get images from server as a single String
        sendRequest("getImages");

        //get the String from server containing all Images
        String imagesString = getResponse();

        //check if images are empty
        if (imagesString.equals("")) {
            return;
        }

        //create an Array by getting Images from the imageString
        String[] images = imagesString.split("\n");

        //create UploadedImage from images String
        for (int i = 0; i < images.length; i++) {
            /**
             * Each line represents UploadedImage object. Each line consist of 4
             * properties separated by "," {title, imageString, price, format}
             */
            String[] properties = images[i].split(",");
            UploadedImage ui = new UploadedImage();
            ui.setTitle(properties[0]);
            ui.setImageInString(properties[1]);
            ui.setPrice(properties[2]);
            ui.setFormat(properties[3]);

            imageList.add(ui);
        }
    }

    //method when buy button is called for any image
    public void purchaseImage(UploadedImage ui) {

        try {
            //convert price to int
            int price = Integer.parseInt(ui.getPrice());
            
            //check if current balance is insufficient to buy image
            if (balance < price) {
                System.out.println("Not enough balance to buy the Image.");
                Alerts.display("ATTENTION", "Insufficient funds, please deposit money");
                return;
            }
            
            //get destination where file is to be saved
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(addBalanceButton.getScene().getWindow());
            
            if (selectedDirectory == null) {
                System.out.println("User cancelled operation.");
                Alerts.display("ATTENTION", "Operation has been cancelled");
                return;
            }
            System.out.println("dir - "+selectedDirectory.getAbsolutePath());
            byte[] imageByte = Base64.getDecoder().decode(ui.getImageInString());
            
            FileOutputStream fos = new FileOutputStream(selectedDirectory+"\\"+ui.getTitle()+"." + ui.getFormat());
            fos.write(imageByte);
            fos.close();
            sendRequest("buyImage");
            sendRequest(ui.getPrice());
            
            updateBalance(balance - price);
            
            System.out.println("Image bought");
            Alerts.display("THANK YOU", "Purchase complete, find image here: \n\n" + selectedDirectory.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(BuyerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //method to update the buyer balance and show on screen
    public void updateBalance(int newBalance) {
        balance = newBalance;
        balanceLabel.setText(newBalance + "");
    }
}
