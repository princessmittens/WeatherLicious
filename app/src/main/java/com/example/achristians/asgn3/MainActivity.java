package com.example.achristians.asgn3;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView chooseCity, degrees, minAndMax, forecast, humidity, clouds;
    Button weather;
    EditText weatherTxt;
    String city;
    String apiKey = "&APPID=0533bae34d4a2010f556ce49f3137055";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseCity = findViewById(R.id.chooseCity);
        degrees = findViewById(R.id.degrees);
        minAndMax = findViewById(R.id.minandmax);
        forecast = findViewById(R.id.forecast);
        humidity = findViewById(R.id.humidity);
        clouds = findViewById(R.id.humidity);
        weather = findViewById(R.id.weather);
        weatherTxt = findViewById(R.id.weatherTxt);

        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                city = weatherTxt.getText().toString();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getWeather();
                    }
                };
            }

        });
    }

    private void getWeather() {

            final String url = "https://api.openweathermap.org/data/2.5/weather?q=";
            String urlWithBase = url.concat(city+apiKey);

            JsonObjectRequest request = new JSONObjectRequest(
                    Request.Method.GET, urlWithBase, null,
                    new Response.Listener<JSONObject>()
                    {
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Here it comes!", Toast.LENGTH_SHORT).show();

                            try{
                                response = response.getJSONObject("main");
                                String humidity = response.humidity;

                            } catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener(){
                public void onErrorResponse(VolleyError e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error receiving data", Toast.LENGTH_SHORT).show();
                }
            }
            );
            RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
}
