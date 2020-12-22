package projects.shopify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //creating home screen
        scene = new Scene(loadFXML("home"), 640, 480);
        stage.setScene(scene);
        //application icon
        stage.getIcons().add(new Image("file:src/main/resources/projects/shopify/shopifylogo.png"));
        stage.show();
    }

    //specify which screen to be loaded
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    //loads the fxml view passed as parameter
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}