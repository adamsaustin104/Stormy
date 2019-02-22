package com.junipersys.stormy.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.junipersys.stormy.R;
import com.junipersys.stormy.databinding.ActivityMainBinding;
import com.junipersys.stormy.weather.Current;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Current current;
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getForecast(37.8267,-122.4233 );
    }

    private void getForecast(double lati, double longi) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);

        TextView darkSky = findViewById(R.id.darkSkyAttribute);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        iconImageView = findViewById(R.id.iconImageView);

        String apiKey = "0e8b416e4334f61219fd1d3497c03c0b";
        double latitude = lati;
        double longitude = longi;

        String forcastURL = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if(isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forcastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            current = getCurrentDetails(jsonData);

                            final Current displayWeather = new Current(
                                    current.getLocationLabel(),
                                    current.getIcon(),
                                    current.getTime(),
                                    current.getTemperature(),
                                    current.getHumidity(),
                                    current.getPrecipChance(),
                                    current.getSummary(),
                                    current.getTimeZone());

                            binding.setWeather(displayWeather);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON caught: ", e);
                    }
                }
            });
        }
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        String timeZone =forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();

        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setLocationLabel("Alcatraz Island, CA");
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timeZone);

        return current;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected())
            isAvailable = true;
        else
            Toast.makeText(this, R.string.network_unavailable_toast, Toast.LENGTH_LONG).show();

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_dialog");
    }

    public void refreshOnClick(View view){
        getForecast(37.8267,-122.4233 );
        Toast.makeText(this, "Refreshing Data..", Toast.LENGTH_LONG);
    }
}
