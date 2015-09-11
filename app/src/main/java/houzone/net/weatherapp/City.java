package houzone.net.weatherapp;

/**
 * Created by houssein on 01/09/15.
 */
public class City {

    private String mCityName;
    private double mLatitude;
    private double mLongitude;

    public City(String name, double latitude, double longitude){
        mCityName = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

}