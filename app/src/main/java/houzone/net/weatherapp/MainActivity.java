package houzone.net.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

/**
 * Created by houssein on 01/09/15.
 */
public class MainActivity extends Activity{

    // using Butterknife, instead of defining and findViewById
    @InjectView(R.id.city1Name) TextView mCityView1;
    @InjectView(R.id.city2Name) TextView mCityView2;
    @InjectView(R.id.city3Name) TextView mCityView3;

    City city1 = new City("Berlin", 52.52437, 13.41053);
    City city2 = new City("Tokyo", 35.68950, 139.69171);
    City city3 = new City("Dubai", 25.25817, 55.30472);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mCityView1.setText(city1.getCityName());
        mCityView2.setText(city2.getCityName());
        mCityView3.setText(city3.getCityName());

        mCityView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = mCityView1.getText().toString();
                goToDisplay(city1);
            }
        });

        mCityView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = mCityView2.getText().toString();
                goToDisplay(city2);
            }
        });

        mCityView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDisplay(city3);
            }
        });
    }

    private void goToDisplay(City city){
        String cityName = city.getCityName();
        double[] location = {city.getLatitude(), city.getLongitude()};

        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra(getString(R.string.cityNameKey), cityName);
        intent.putExtra(getString(R.string.locationKey), location);
        startActivity(intent);
    }
}