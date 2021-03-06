/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Alerts;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Sageewan Subendran
 */

//class to construct all alert boxes 
public class Alerts {
    
    public static void display(String title, String message){
        //new screen for alert box
        Stage window = new Stage();
        
        window.getIcons().add(new Image("file:src/main/resources/projects/shopify/attention.png"));
        
        //forces user to take care of alert box before performing other tasks
        window.initModality(Modality.APPLICATION_MODAL);
        
        //window attributes
        window.setTitle(title);
        window.setMinWidth(350);
        
        //set the message for the respective alert
        Label label = new Label();
        label.setText(message);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTranslateY(35);
        
        Button closeButton = new Button("Close");
        closeButton.setTranslateY(-40);
        closeButton.setOnAction(e -> window.close());
        
        //set layout size, features, and position
        VBox layout = new VBox(100);
        
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        
        
        //set and select the scene in order to show in window
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
