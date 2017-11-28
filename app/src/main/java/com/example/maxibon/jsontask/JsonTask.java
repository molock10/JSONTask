package com.example.maxibon.jsontask;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class JsonTask extends AppCompatActivity {

    private ArrayList<ColorElement> listOfColors;
    private TextView displayBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_task);

        listOfColors = new ArrayList<>();
        createStringDatabase();

        displayBox = findViewById(R.id.textdisplay);
        readJson();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    /**
     * Counts all colorElements that have a green value of 255
     *
     * @param view
     */
    public void count(View view){
        int counter = 0;

        for(ColorElement codeElement : listOfColors){
            if(codeElement.getGreen() == 255) {
                counter++;
            }
        }
        displayBox.setText(Integer.toString(counter));
    }

    public void list(View view){
       String builder = "";

       for(ColorElement colorElement : listOfColors){
           if(colorElement.getGreen() == 255){
               builder += colorElement.getColorTitle() + "\n";
           }
       }
       displayBox.setText(builder);
    }

    public void modify(View view){

        JSONArray jsonArray;
       try {
           jsonArray = load();
           JSONObject newColor = new JSONObject();

           newColor.put("color", "orange");
           newColor.put("category", "hue");

           JSONObject colorCode = new JSONObject();
           JSONArray rgba = new JSONArray();
           rgba.put(255);
           rgba.put(165);
           rgba.put(0);
           rgba.put(1);

           colorCode.put("rgba", rgba);
           colorCode.put("hex", "#FA0");
           newColor.put("code", colorCode);
           jsonArray.put(newColor);

           displayBox.setText(jsonArray.toString(2));

           //IF YOU WANT THE DISPLAY TO LOOK NICE
           //gets the element just added (the 7th number) and display in text
           // One must take into account that 0 is the 1st, so get(6) is actually 7
           /*String textToDisplay = "";

           JSONObject message = (JSONObject) jsonArray.get(6);

           textToDisplay += message.getString("color") + "\n";
           textToDisplay +=  message.getString("category") + "\n";

           JSONObject codeSegment = message.getJSONObject("code");
           JSONArray rgbaArray = codeSegment.getJSONArray("rgba");

           textToDisplay += rgbaArray.getInt(0) + ", ";
           textToDisplay += rgbaArray.getInt(1) + ", ";
           textToDisplay += rgbaArray.getInt(2) + ", ";
           textToDisplay += rgbaArray.getInt(3) + "\n";
           textToDisplay += codeSegment.getString("hex");
            */
           //displayBox.setText(textToDisplay);

       }catch (JSONException e){
           Toast.makeText(this, "couldn't add new color", Toast.LENGTH_SHORT).show();
       }
    }

    private void createStringDatabase() {
        String builder = "{\n" +
                "  \"colors\": [\n" +
                "    {\n" +
                "      \"color\": \"black\",\n" +
                "      \"category\": \"hue\",\n" +
                "      \"type\": \"primary\",\n" +
                "      \"code\": {\n" +
                "        \"rgba\": [255,255,255,1],\n" +
                "        \"hex\": \"#000\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"color\": \"white\",\n" +
                "      \"category\": \"value\",\n" +
                "      \"code\": {\n" +
                "        \"rgba\": [0,0,0,1],\n" +
                "        \"hex\": \"#FFF\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"color\": \"red\",\n" +
                "      \"category\": \"hue\",\n" +
                "      \"type\": \"primary\",\n" +
                "      \"code\": {\n" +
                "        \"rgba\": [255,0,0,1],\n" +
                "        \"hex\": \"#FF0\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"color\": \"blue\",\n" +
                "      \"category\": \"hue\",\n" +
                "      \"type\": \"primary\",\n" +
                "      \"code\": {\n" +
                "        \"rgba\": [0,0,255,1],\n" +
                "        \"hex\": \"#00F\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"color\": \"yellow\",\n" +
                "      \"category\": \"hue\",\n" +
                "      \"type\": \"primary\",\n" +
                "      \"code\": {\n" +
                "        \"rgba\": [255,255,0,1],\n" +
                "        \"hex\": \"#FF0\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"color\": \"green\",\n" +
                "      \"category\": \"hue\",\n" +
                "      \"type\": \"secondary\",\n" +
                "      \"code\": {\n" +
                "        \"rgba\": [0,255,0,1],\n" +
                "        \"hex\": \"#0F0\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        try {
            FileOutputStream fos = openFileOutput("database.json", Context.MODE_PRIVATE);
            fos.write(builder.getBytes());
            fos.close();
        }catch (FileNotFoundException e){
            Toast.makeText(this, "couldn't open the file", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(this, "couldn't write to file", Toast.LENGTH_SHORT).show();
        }
    }

    private void readJson(){

        try {
            JSONArray values = load();
            if(values == null){
                Toast.makeText(this,"Database is empty",Toast.LENGTH_LONG).show();
            }else
            {
                for (int i = 0; i < values.length(); i++) {
                    String colorTitle, category, hex, type;
                    ArrayList<Integer> rgba = new ArrayList<>();

                    try {
                        JSONObject message = (JSONObject) values.get(i);
                        colorTitle = message.getString("color");
                        category =  message.getString("category");

                        if(message.has("type")) {
                            type = message.getString("type");
                        }else type = null;

                        JSONObject codeSegment = message.getJSONObject("code");

                        JSONArray rgbaArray = codeSegment.getJSONArray("rgba");

                        rgba.add(rgbaArray.getInt(0));
                        rgba.add(rgbaArray.getInt(1));
                        rgba.add(rgbaArray.getInt(2));
                        rgba.add(rgbaArray.getInt(3));

                        hex = (String) codeSegment.getString("hex");

                        listOfColors.add(new ColorElement(colorTitle, category, type, rgba, hex));

                    } catch (JSONException e) {
                        Log.e("JSONReadException",e.getMessage());
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public JSONArray load() throws JSONException {

        JSONArray messages = null;
        try {
            FileInputStream stream = openFileInput("database.json");
            String content = readFullyAsString(stream, "UTF-8");
            JSONObject message = (JSONObject) new JSONTokener(content).nextValue();
            messages = message.getJSONArray("colors");

            stream.close();
        } catch (IOException e) {
            Log.e("JSONReadException",e.getMessage());
        }
        return messages;
    }

    public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {

        return readFully(inputStream).toString(encoding);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream)  throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }
}

class ColorElement{

    private String colorTitle;
    private String category;
    private ArrayList<Integer> rgba;
    private String hexadecimalCode;


    ColorElement(String color, String category, String type, ArrayList<Integer> rgba, String hex){
        this.colorTitle = color;
        this.hexadecimalCode = hex;
        this.rgba = rgba;
        this.hexadecimalCode = hex;
    }


    public String getColorTitle() {
        return colorTitle;
    }

    public String getCategory() {
        return category;
    }

    public int getRed(){
        return rgba.get(0);
    }
    public int getGreen(){
        return rgba.get(1);
    }
    public int getBlue(){
        return rgba.get(2);
    }
    public int getAlpha(){
        return rgba.get(3);
    }


    public String getHexadecimalCode() {
        return hexadecimalCode;
    }
}

