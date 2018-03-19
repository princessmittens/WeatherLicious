package com.example.achristians.asgn3;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    /**
     * Initial declarations for the text views/city name etc.
     */
    TextView chooseCity, degrees, minAndMax, forecast, humidity, clouds;
    Button weather;
    EditText weatherTxt;
    String city;
    String apiKey = "&units=metric&APPID=0533bae34d4a2010f556ce49f3137055";
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Assign ALL of the buttons
         */
        chooseCity = findViewById(R.id.chooseCity);
        degrees = findViewById(R.id.degrees);
        minAndMax = findViewById(R.id.minandmax);
        forecast = findViewById(R.id.forecast);
        humidity = findViewById(R.id.humidity);
        clouds = findViewById(R.id.clouds);
        weather = findViewById(R.id.weather);
        weatherTxt = findViewById(R.id.weatherTxt);
        weather.setOnClickListener(new View.OnClickListener() {

            /**
             * On the Weather button click, start the API request and get the data
             */
            @Override
            public void onClick(View view) {
                //Get the city name from the edit view
                city = weatherTxt.getText().toString();

                //Function to run the API
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getWeather();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error getting Weather",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                };

                // Assign a thread and then run the getWeather function
                Thread thread = new Thread(null, runnable, "background");
                thread.start();

                InputMethodManager inputManager = (InputMethodManager) getSystemService
                        (Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    private void getWeather() {
        /**
         * Append base url to city and api key
         */
        final String url = "http://api.openweathermap.org/data/2.5/weather?q=";
        String urlWithBase = url.concat(city + apiKey);

        /**
         *  Make a request to connect to the Open Weather API
         */
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlWithBase, null,
                new Response.Listener<JSONObject>() {
                    /**
                     * If successful, take that data and call it 'response'
                     * @param response - data from Open Weather API
                     */
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Getting data.", Toast.
                                LENGTH_LONG).show();
                        /**
                         * Parse the data and print out an error message
                         * if the user inputs a wrong city or the server is unavailable
                         */
                        try {
                            String deg, minT, maxT, humid, clouds;
                            String descriptionForecast = "";

//                              Ref: https://stackoverflow.com/questions/8939250/parsing-json-
//                                      ile-java?noredirect=1&lq=1

                            /**
                             * Get Objects from main
                             */
                            JSONObject main = response.getJSONObject("main");
                            deg = main.getString("temp");
                            humid = main.getString("humidity");
                            minT = main.getString("temp_min");
                            maxT = main.getString("temp_max");

                            JSONObject cloud = response.getJSONObject("clouds");
                            clouds = cloud.getString("all");

                            /**
                             * Read in JSON object array
                             */
                            JSONArray weather = response.getJSONArray("weather");
                            for (int i = 0; i < weather.length(); i++) {
                                JSONObject weatherArray = weather.getJSONObject(i);
                                descriptionForecast = weatherArray.getString
                                        ("description");
                            }

                            /**
                             * Display values on screen
                             */
                            setTemp(deg);
                            setMinAndMax(minT, maxT);
                            setHumidityAndClouds(humid, clouds);
                            setDescription(descriptionForecast);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing file",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Can't connect to the " +
                                "server, you probably misspelled the city name." +
                                "Try again.",
                        Toast.LENGTH_LONG).show();
            }
        }
        );
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    /**
     * Rounds the temperature to nearest int value and displays on the screen
     *
     * @param temp - string value from Json Object
     */
    public void setTemp(String temp) {
        int y = (int) Math.round(Double.parseDouble(temp));
        degrees.setText((Integer.toString(y)) + "°C");
    }

    /**
     * Sets the min and max temperatures to display on the screen
     *
     * @param minT - String value of minimum temperature from the Json Object
     * @param maxT - String value of the max temperature from the Json Object
     */
    public void setMinAndMax(String minT, String maxT) {
        minAndMax.setText("Min: " + minT + "°C Max: " + maxT + "°C");
    }

    /**
     * Sets the Humidity and Cloud percentage values to display on the screen
     *
     * @param humid - String value of the humidity percentage from the Json Object
     * @param cloud - String value of the cloud percentage from the Json Object
     */
    public void setHumidityAndClouds(String humid, String cloud) {
        humidity.setText("Humidity:\n" + humid + "%");
        clouds.setText("Clouds:\n" + cloud + "%");
    }

    /**
     * Sets the Forecast Description to display on the screen
     *
     * @param description - String value of the text description from the Json Object
     */
    public void setDescription(String description) {
        forecast.setText(description);
    }
}
