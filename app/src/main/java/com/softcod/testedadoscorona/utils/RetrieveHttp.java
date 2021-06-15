package com.softcod.testedadoscorona.utils;

import static java.lang.System.out;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class RetrieveHttp extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... strings) {

        try{
            URL url = new URL(strings[0]);
            HttpURLConnection client = null;

            String urlParameters = strings[2];
            Log.d("URL", "AQUI>>>>>>" + strings[0]);
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod(strings[1]);
            client.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            //client.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            client.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(client.getOutputStream())) {
                wr.write(postData);
            }
            StringBuilder content;
            out.println(strings[0]+ "\n" + strings[1]+ "\n" + strings[2]);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
                client.disconnect();
            }
            Log.e("\n\n\nRetrive\n\n\n",
                    "Json=  \n\n\n" + content.toString() +"\n\n\n");
            return new JSONObject(content.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
