/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyect.Controllers;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author lilyg
 */
public class LoadingController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    ImageView image_view_gif;

    @FXML
    private AnchorPane rootPane;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Random random = new Random();
        image_view_gif.setImage(new Image("Proyect/gifs/HC"+(random.nextInt(5)+1+".gif")));
        new SplashScreen().start();
    }


    class SplashScreen extends Thread{
        @Override
        public void run()
        {
            try {
                Thread.sleep(3000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Parent root= null;
                        try{
                            root = FXMLLoader.load(getClass().getResource("../Views/Active.fxml"));
                            String command = "python ../scripts/python/ejemplo_json.py";
                            Process p = Runtime.getRuntime().exec(command);
                            System.out.println( p.isAlive());
                        }
                        catch(IOException ex)
                        {
                            Logger.getLogger(LoadingController.class.getName()).log(Level.SEVERE,null,ex);
                        }
                         Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.show();
                        rootPane.getScene().getWindow().hide();
                      //  System.out.println();
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(LoadingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
