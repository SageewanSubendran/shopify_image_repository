module com.mycompany.shopify_image_repository_desktopapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.shopify_image_repository_desktopapp to javafx.fxml;
    exports com.mycompany.shopify_image_repository_desktopapp;
}
