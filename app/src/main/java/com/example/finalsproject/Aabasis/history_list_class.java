package com.example.finalsproject.Aabasis;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class history_list_class extends Application {
    private static history_list_class singleton;

    private static List<Location> myLocations;

    public static List<Location> getMyLocations(){
        return myLocations;
    }
    public void setMyLocations(List<Location>myLocations){
        history_list_class.myLocations = myLocations;
    }
    public history_list_class getInstance(){
        return singleton;
    }
    public void onCreate(){
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
