package com.itschool.myweather;

public class Coordinates {

    public static double longitude = 30.7247644;
    public static double latitude =  46.5444569;
//Токио
//  public static double longitude = 139.4302008;
//  public static double latitude =  35.6730185;

    public static String getCoordinates() {
        return "[" + longitude + "," + latitude +"]";
    }


}
