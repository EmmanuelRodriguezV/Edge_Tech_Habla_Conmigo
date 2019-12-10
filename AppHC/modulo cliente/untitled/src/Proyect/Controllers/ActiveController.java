/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proyect.Controllers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import Proyect.Classes.WebCam;
import com.github.sarxos.webcam.Webcam;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.animation.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import okhttp3.*;
import org.json.simple.JSONArray;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;


/**
 * FXML Controller class
 *
 * @author lilyg
 */
public class ActiveController implements Initializable {


    String texto_final;
    int contador;
    String palabra_pasada;
    ArrayList<String> buffer_entrada;
    String  palabra_actual;
    //@FXML
   // Panel fondo_panel ;
    @FXML
    ImageView image_view_luna;
    @FXML
    Pane _panel_barra;
    @FXML
    AnchorPane _main_panel;
    @FXML
    ImageView imagen_camara;
    @FXML
    ChoiceBox choiceBox;
    @FXML
    Label labelHelp;
    @FXML
    Circle circleHelp;
    @FXML
    Label lbl_black;
    @FXML
    TextArea txtArea;
    @FXML
    Pane panel_camara;
    @FXML
     Pane Panel_Camaras;
    int camara_seleccionada;
    boolean MOSTRAR_CAMARA;
    boolean MODO_OSCURO;
    Stage s;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        buffer_entrada = new ArrayList<>();
        texto_final = "";
        MOSTRAR_CAMARA = true;
        choiceBox.setItems(WebCam.getCameras());
        MODO_OSCURO = false;
        choiceBox.setValue("Escoge Webcam");
        // s = ((Stage)panel_camara.getScene().getWindow());
    }
    public  void onClickExitCamBtn ()
    {
        System.out.println("presionado");
       panel_camara.setVisible(false);
        lbl_black.setVisible(false);
    }
public void OnClickOcultarPanel()
{
    if(imagen_camara.isVisible())
    {
        imagen_camara.setVisible(false);
        txtArea.setPrefSize(704, 413);
        txtArea.setLayoutX(236);
        txtArea.setLayoutY(170);
    }else{
        imagen_camara.setVisible(true);
        txtArea.setPrefSize(704, 159);
        txtArea.setLayoutX(234);
        txtArea.setLayoutY(604);
    }
}
public  void  onClickAyuda()
{
    Panel_Camaras.setVisible(true);
    labelHelp.setText("");
    circleHelp.setVisible(true);
    imagen_camara.setVisible(false);
    txtArea.setVisible(false);
    final IntegerProperty i = new SimpleIntegerProperty(0);
    Timeline timeline = new Timeline(
            new KeyFrame(
                    Duration.seconds(3),
                    eventh -> {
                        switch(i.get()){
                            case 0:
                                step(338, 0, false);
                                labelHelp.setText("Escuchar la conversación");
                                i.set(i.get() + 1);
                                break;
                            case 1:
                                step(513, 0, false);
                                labelHelp.setText("Visualizar la cámara");
                                i.set(i.get() + 1);
                                break;
                            case 2:
                                step(687, 0, false);
                                labelHelp.setText("Guardar la conversación");
                                i.set(i.get() + 1);
                                break;
                            case 3:
                                step(1071, 0, false);
                                labelHelp.setText("Cambiar a modo nocturno");
                                i.set(i.get() + 1);
                                break;
                            case 4:
                                step(500,300, true);
                                labelHelp.setText("Visualización de la camara");
                                i.set(i.get() + 1);
                                break;
                            case 5:
                                step(500, 600, true);
                                labelHelp.setText("Texto de la conversación");
                                i.set(i.get() + 1);
                                break;
                            default:
                                step(-38,-38,true);
                                finish();
                                break;
                        }
                    }
            )
    );
    timeline.setCycleCount(7);
    timeline.play();
}
    void step(double x, double y, boolean s){
        TranslateTransition th = new TranslateTransition(Duration.seconds(1),circleHelp);
        th.setToX(circleHelp.getLayoutX() + x);
        if(s){
            th.setToY(circleHelp.getLayoutY() + y);
        }
        th.play();
    }


    void finish(){
        panel_camara.setVisible(false);
        Panel_Camaras.setVisible(false);
        labelHelp.setVisible(false);
        circleHelp.setVisible(false);
        imagen_camara.setVisible(true);
        txtArea.setVisible(true);
    }
    public  void onClickGuardar()
    {
        System.out.println("si entro");
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(s);

        if (file != null) {
            SaveFile(txtArea.getText(), file);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Guardado Correcto");
            alert.setHeaderText("Conversacion Guardada");
            alert.setContentText("Tus avances se han guardado");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
        }
        Task t = new Task() {
            @Override
            protected Object call() throws Exception {
                return null;
            }

            @Override
            public void run() {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(s);

                if (file != null) {
                    SaveFile(txtArea.getText(), file);
                }
            }
        };
        new Thread(t).start();
    }
    public  void OnClickEscuchar()
    {
        Task t = new Task() {
            @Override
            protected Object call() throws Exception {
                return null;
            }

            @Override
            public void run() {

                String BASE_URL = "http://127.0.0.1:5000";
                String route = "/playsound";
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("text", txtArea.getText())
                        .build();
                Request request = new Request.Builder()
                        .url(BASE_URL + route)
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    //System.out.println();
                    String palabra = response.body().string();
                }
                catch (IOException Ex)
                {
                    System.out.println(Ex.getMessage());
                }
            }
        };
        new Thread(t).start();
    }

    private void SaveFile(String content, File file){
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
           // Logger.getLogger(JavaFX_Text.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public void onClickModoOscuro ()
    {

        System.out.println("el evento funciona");
        if(!MODO_OSCURO) {
            image_view_luna.getStyleClass().remove("btnMoon");
            image_view_luna.getStyleClass().add("btnMoonDark");
            MODO_OSCURO = !MODO_OSCURO;
            _panel_barra.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(202,62,71), CornerRadii.EMPTY,Insets.EMPTY)));
            _main_panel.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(82,82,82), CornerRadii.EMPTY,Insets.EMPTY)));
          //  txtArea.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(38,56,89), CornerRadii.EMPTY,Insets.EMPTY)));
            //_main_panel.setBackground(new Color(82, 82, 82));
        }
        else {
            MODO_OSCURO = !MODO_OSCURO;
            image_view_luna.getStyleClass().remove("btnMoonDark");
            image_view_luna.getStyleClass().add("btnMoon");
            _main_panel.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(253,255,253), CornerRadii.EMPTY,Insets.EMPTY)));
            _panel_barra.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(34,139,230), CornerRadii.EMPTY,Insets.EMPTY)));

        }


    }
public void onClickAccCamBtn()
    {
        if(!choiceBox.getValue().equals("Escoge Webcam") )
        {
            System.out.println("presionado");
            panel_camara.setVisible(false);
            lbl_black.setVisible(false);
            System.out.println(choiceBox.getValue());
            camara_seleccionada = Integer.parseInt((String) choiceBox.getValue());
           // imagen_camara.setImage( SwingFXUtils.toFXImage(WebCam.getWebCamImage(1),null));
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    return null;
                }
                @Override public void run() {
                    try {
                        Webcam webcam;
                        while (true) {
                             webcam = Webcam.getDefault();
                            webcam.open();
                            JSONArray jsonArray = new JSONArray();
                            JSONArray rgbarray = new JSONArray();
                            BufferedImage image = webcam.getImage();
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", output);
                            if (MOSTRAR_CAMARA)
                                imagen_camara.setImage(SwingFXUtils.toFXImage((BufferedImage) image, null));
                            String BASE_URL = "http://127.0.0.1:5000";
                            String route = "/ejemplo2";
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("BaseString", DatatypeConverter.printBase64Binary(output.toByteArray()))
                                    .build();
                            Request request = new Request.Builder()
                                    .url(BASE_URL + route)
                                    .post(requestBody)
                                    .build();
                            OkHttpClient client = new OkHttpClient();
                            Response response = client.newCall(request).execute();
                            //System.out.println();
                            String palabra = response.body().string();
                            System.out.println(palabra);
                            if(!palabra.equals("$"))
                            buffer_entrada.add(palabra);
                            if(buffer_entrada.size() > 3)
                            {
                                boolean agregar = true;
                                String primero = buffer_entrada.get(0);
                                for (String a : buffer_entrada)
                                    if(!primero.equals(a)) {
                                        agregar = false;
                                    }
                                if(agregar) {
                                    if(palabra.equals("space"))
                                        //texto_final += " ";
                                        palabra = " ";
                                    else
                                    if(palabra.equals("b nueva"))
                                        //texto_final += "b";
                                        palabra = "b";
                                    else
                                        if (palabra.equals("a nueva"))
                                            //texto_final+= "a";
                                            palabra = "a";
                                        else
                                            if (palabra.equals("c nueva"))
                                                //texto_final+="c";
                                                palabra = "c";
                                            else
                                                if (palabra.equals("d nueva"))
                                                    //texto_final+="d";
                                                    palabra = "d";
                                                else
                                                    texto_final += palabra;
                                   // txtArea.clear();
                                    txtArea.appendText(palabra);
                                }
                              buffer_entrada.clear();
                            }
/*

*/
                        }
                    //webcam.close();
                    }
                    catch (Exception exception)
                    {
                        System.out.println(exception.getMessage());
                    }

                }
            };
            new Thread(task).start();

        }
    }
}
