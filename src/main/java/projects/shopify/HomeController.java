package projects.shopify;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {

    @FXML
    private Button sellerButton;
    @FXML
    private Button buyerButton;
    
    //called when seller butotn is clicked
    @FXML
    private void sellerButtonClicked(ActionEvent event) throws IOException {
        App.setRoot("seller");
    }

    //called when buyer butotn is clicked
    @FXML
    private void buyerButtonClicked(ActionEvent event) throws IOException {
        App.setRoot("buyer");
    }
}
