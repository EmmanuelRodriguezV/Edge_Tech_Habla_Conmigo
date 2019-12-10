package Proyect.Classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class  WebCam {

    static public ArrayList getWebCamImage(int CAMERA_NUMBER)
    {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"camara\"\r\n\r\n0\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000")
                .post(body)
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "PostmanRuntime/7.19.0")
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "a8d3fa5c-0e16-46e9-93bf-683b4386f23d,5a64175a-15ed-4a20-bd8e-278f9dc4a589")
                .addHeader("Host", "127.0.0.1:5000")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Content-Length", "162")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();
        BufferedImage image ;
        try{
            Response response = client.newCall(request).execute();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.body().string());
            //System.out.println(json.toString());
            JSONArray color_actual;
            JSONArray vector_images = (JSONArray) json.get("bitmap_array");
            JSONArray vector = (JSONArray) json.get("bitmap");
            ArrayList imagenes_reales = new ArrayList();
            for(int m = 0  ; m < vector_images.size(); m++)
            {
                vector = (JSONArray) vector_images.get(m);
                int vector_size = vector.size();
                int ancho = Math.round(Float.parseFloat(json.get("ancho").toString()));
                int alto = Math.round(Float.parseFloat(json.get("alto").toString()));
                image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
                int indice_width = 0;
                int indice_height = 0;

                JSONArray RGB ;
                Color color;
                for (int i =0 ; i < vector_size; i++)
                {
                    color_actual = (JSONArray)vector.get(i);
                    for(int j = 0 ;j < color_actual.size(); j++)
                    {
                        RGB = ((JSONArray)color_actual.get(j));
                        color = new Color(Math.toIntExact((Long) RGB.get(0)),Math.toIntExact((Long) RGB.get(1)),Math.toIntExact((Long) RGB.get(2)));
                        image.setRGB(indice_width,indice_height,color.getRGB());
                        indice_width+= 1;
                        if(indice_width > ancho-1){
                            indice_width = 0;
                            indice_height += 1;
                        }
                    }
                }
                imagenes_reales.add(image);
            }

            return  imagenes_reales;

        }
        catch (IOException | ParseException exception)
        {
            System.out.println(exception.getMessage());
            return  null;
        }



    }
    static public ObservableList<String>getCameras()
    {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        RequestBody body = RequestBody.create(mediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"camara\"\r\n\r\n0\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000/get_cameras")
                .get()
                .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "PostmanRuntime/7.19.0")
                .addHeader("Accept", "*/*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "daf90073-6a4d-4050-9b6e-346820a4c491,d4324103-9aa9-4889-b9f8-6dad7aac36ff")
                .addHeader("Host", "127.0.0.1:5000")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Content-Length", "162")
                .addHeader("Connection", "keep-alive")
                .addHeader("cache-control", "no-cache")
                .build();
        ObservableList<String> puertos = FXCollections.observableArrayList();
        try
        {
            Response response = client.newCall(request).execute();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response.body().string());
            System.out.println(json.toString());
            JSONArray msg = (JSONArray) json.get("numero_camaras");
            System.out.println(msg.size());

            puertos.add("Escoge Webcam");
            for (int i = 0 ; i < msg.size(); i++)
                puertos.add(i+1 +"");
        }
        catch (IOException | ParseException execption)
        {
            System.out.println(execption.getMessage());
        }
        return puertos;
    }
}
