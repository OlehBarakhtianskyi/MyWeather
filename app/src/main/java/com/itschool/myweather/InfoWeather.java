package com.itschool.myweather;

public final class InfoWeather {
    int cloudness;
    int precipitations;
    private int temperature;
    int pressure;
    int humidity;
    int windSpeed;
    int windDegree;
    private boolean isDay;


    public int getCloudness() {
        return cloudness;
    }

    public void setCloudness(int cloudness) {
        this.cloudness = cloudness;
    }

    public int getPrecipitations() {
        return precipitations;
    }

    public void setPrecipitations(int precipitations) {
        this.precipitations = precipitations;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = (temperature > 100 || temperature < -100 ? -273 : temperature );
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(int windDegree) {
        this.windDegree = windDegree;
    }

    public String getisDay() {
        return isDay ? "Day" : "Night";
    }

    public void setDay(boolean day) {
        isDay = day;
    }
}
