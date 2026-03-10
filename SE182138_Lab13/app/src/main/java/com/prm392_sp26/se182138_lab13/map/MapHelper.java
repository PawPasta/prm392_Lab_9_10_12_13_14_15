package com.prm392_sp26.se182138_lab13.map;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import android.graphics.Color;

import java.util.List;
public class MapHelper {

    public static void showLocation(MapView map,double lat,double lon,String title){

        GeoPoint point = new GeoPoint(lat,lon);

        map.getOverlays().clear();

        map.getController().setZoom(15.0);
        map.getController().setCenter(point);

        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle(title);

        map.getOverlays().add(marker);
        map.invalidate();
    }

    public static void showRoute(
            MapView map,
            GeoPoint start,
            GeoPoint end,
            List<GeoPoint> path,
            String startTitle,
            String endTitle){

        map.getOverlays().clear();

        Polyline line = new Polyline();
        line.setPoints(path);
        line.setColor(Color.BLUE);
        line.setWidth(8f);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(start);
        startMarker.setTitle(startTitle);

        Marker endMarker = new Marker(map);
        endMarker.setPosition(end);
        endMarker.setTitle(endTitle);

        map.getOverlays().add(line);
        map.getOverlays().add(startMarker);
        map.getOverlays().add(endMarker);

        map.getController().setZoom(13.0);
        map.getController().setCenter(new GeoPoint(
                (start.getLatitude() + end.getLatitude()) / 2.0,
                (start.getLongitude() + end.getLongitude()) / 2.0
        ));
        map.invalidate();
    }
}
