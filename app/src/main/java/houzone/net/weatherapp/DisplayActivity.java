package houzone.net.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DisplayActivity extends ActionBarActivity {

    public static final String TAG = DisplayActivity.class.getSimpleName();

    private CurrentData mCurrentData;

    private String mSelectedCity;
    private double[] mLocationArray;

    @InjectView(R.id.locationId) TextView mLocation;
    @InjectView(R.id.timeId) TextView mTimeLabel;
    @InjectView(R.id.temperatureId) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValueId) TextView mHumidityLabel;
    @InjectView(R.id.precipitationValueId) TextView mPrecipLabel;
    @InjectView(R.id.summaryId) TextView mSummary;
    @InjectView(R.id.iconImageId) ImageView mImageView;
    @InjectView(R.id.refreshImageId) ImageView mRefreshImage;

    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.displayViewId) RelativeLayout mDisplayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mSelectedCity = intent.getStringExtra(getString(R.string.cityNameKey));
        mLocationArray = intent.getDoubleArrayExtra(getString(R.string.locationKey));

        mLocation.setText(mSelectedCity); // update displayed city name
        final double latitude = mLocationArray[0];
        final double longitude = mLocationArray[1];

        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRefreshedData(latitude, longitude);

            }
        });

        getRefreshedData(latitude, longitude);
    }

    private void getRefreshedData(double latitude, double longitude) {
        mTemperatureLabel = (TextView) findViewById(R.id.temperatureId);
        String apiKey = "bfa8d298ae3b29a2cdd3c127a0d60b42";
        String fcUrl = "https://api.forecast.io/forecast/"
                + apiKey + "/" + latitude + "," + longitude;

        if (isSignalAvailable()) {
            toggleVisibility();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(fcUrl).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() { // add call to a queue, asynchrous thread
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleVisibility();
                        }
                    });
                    alertUserAboutErrors(); // no response

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleVisibility();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentData = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateData();
                                }
                            });
                        } else {
                            alertUserAboutErrors(); // if response is corrupt
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e){
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }else {
            Toast.makeText(this, getString(R.string.toast_network_unavailable),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void toggleVisibility() { // of refresh icon
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImage.setVisibility(View.INVISIBLE);
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImage.setVisibility(View.VISIBLE);
        }
    }

    private void updateData() {
        mTemperatureLabel.setText(mCurrentData.getTemperature() + "");

        // convert time
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone(mCurrentData.getTimeZone()));
        String formattedDate = sdf.format(mCurrentData.getTime()*1000);
        mTimeLabel.setText("At " + formattedDate);

        mHumidityLabel.setText(mCurrentData.getHumidity()+"");
        mPrecipLabel.setText((mCurrentData.getPrecChance())+"%");
        mSummary.setText((mCurrentData.getSummary()));

        Drawable drawable = getResources().getDrawable(mCurrentData.getIconID());
        mImageView.setImageDrawable(drawable);

        mDisplayView.setBackgroundColor(-200000*mCurrentData.getTemperature());

    }

    private CurrentData getCurrentDetails(String jsonData) throws JSONException{    // called method throws the exception
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentData currentData = new CurrentData();
        currentData.setHumidity(currently.getDouble("humidity"));
        currentData.setTime(currently.getLong("time"));
        currentData.setIcon(currently.getString("icon"));
        currentData.setPrecChance(currently.getDouble("precipProbability"));
        currentData.setSummary(currently.getString("summary"));
        int tempInC = convertFtoC(currently.getInt("temperature"));
        currentData.setTemperature(tempInC); // convert temperature
        currentData.setTimeZone(timeZone);

        Log.d(TAG, currentData.getFormattedTime());

        return currentData;
    }

    private boolean isSignalAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean available = false;
        if (info != null && info.isConnected()){
            available = true;
        }
        return available;
    }

    private void alertUserAboutErrors() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog"); // show error popup
    }

    public int convertFtoC(int f)
    {
        double c = (f-32)*(5/9.0);
        return (int) c;
    }
}