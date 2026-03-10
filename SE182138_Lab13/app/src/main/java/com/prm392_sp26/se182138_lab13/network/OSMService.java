package com.prm392_sp26.se182138_lab13.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OSMService {

    public static String search(String query){

        try{

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String url =
                    "https://nominatim.openstreetmap.org/search?q="
                            + encodedQuery +
                            "&format=json";

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent","Android")
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();

        }catch(Exception e){

            e.printStackTrace();
        }

        return null;
    }

    public static String route(
            double startLat,
            double startLon,
            double endLat,
            double endLon){

        try{

            String url =
                    "https://router.project-osrm.org/route/v1/driving/"
                            + startLon + "," + startLat + ";"
                            + endLon + "," + endLat
                            + "?overview=full&geometries=geojson";

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent","Android")
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();

        }catch(Exception e){

            e.printStackTrace();
        }

        return null;
    }
}
