import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

        @Override
        public void start(Stage stage) throws Exception {
                // Load login screen
                FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/ui/views/login.fxml"));
                Scene scene = new Scene(loader.load(), 500, 450);

                stage.setTitle("Smart Waste Pickup System");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
        }

        public static void main(String[] args) {
                launch(args);
        }
}