package com.example.finalsproject.dashboard_user_activities;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class class_history_list extends Application {
    private static class_history_list singleton;

    private static List<Location> myLocations;

    public static List<Location> getMyLocations(){
        return myLocations;
    }
    public void setMyLocations(List<Location>myLocations){
        class_history_list.myLocations = myLocations;
    }
    public class_history_list getInstance(){
        return singleton;
    }
    public void onCreate(){
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
}
