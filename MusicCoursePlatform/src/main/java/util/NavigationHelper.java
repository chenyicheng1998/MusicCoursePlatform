package util;

import controller.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility for common JavaFX scene-navigation actions shared across controllers.
 *
 * <p>Eliminates the repeated {@code FXMLLoader + Stage} boilerplate that
 * previously appeared in every controller's {@code handleLogout} and
 * navigation methods.</p>
 */
public final class NavigationHelper {

    private static final Logger logger = LoggerFactory.getLogger(NavigationHelper.class);

    private NavigationHelper() {
    }

    /**
     * Load an FXML scene and replace the current window's scene.
     *
     * @param event       the {@link ActionEvent} that triggered the navigation
     * @param callerClass the controller class used to resolve the FXML resource
     * @param fxmlPath    absolute path to the FXML resource (e.g. {@code "/fxml/login.fxml"})
     * @param title       the window title for the new scene
     */
    public static void navigateTo(ActionEvent event, Class<?> callerClass,
                                   String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(callerClass.getResource(fxmlPath));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            logger.error("Failed to navigate to {}: {}", fxmlPath, e.getMessage(), e);
        }
    }

    /**
     * Log out the current user and navigate to the login screen.
     *
     * @param event       the {@link ActionEvent} that triggered the logout
     * @param callerClass the controller class used to resolve the FXML resource
     * @param loginTitle  the window title for the login scene
     */
    public static void logout(ActionEvent event, Class<?> callerClass, String loginTitle) {
        SessionManager.getInstance().logout();
        navigateTo(event, callerClass, "/fxml/login.fxml", loginTitle);
    }
}

