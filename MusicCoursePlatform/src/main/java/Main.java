import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application entry point for Music Course Platform.
 * JavaFX desktop application for connecting music teachers with learners.
 *
 * @author Sprint 2 Team
 * @version 1.0
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login screen FXML
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

            // Create scene
            Scene scene = new Scene(root);

            // Configure primary stage
            primaryStage.setTitle("Music Course Platform - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Fixed size window
            primaryStage.show();

            System.out.println("Application started successfully!");

        } catch (Exception e) {
            System.err.println("Error starting application:");
            e.printStackTrace();
        }
    }

    /**
     * Main method - launches the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
