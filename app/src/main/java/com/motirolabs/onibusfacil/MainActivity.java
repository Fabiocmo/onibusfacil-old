package com.motirolabs.onibusfacil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ActionBarActivity {

    //

    private static final String TAG = "MainActivity";

    //

    private static final boolean DBG = BuildConfig.DEBUG;

    //

    private static final String AD_UNIT_ID1 = "ca-app-pub-000000000000000000000000000"; // carlos
    private static final String AD_UNIT_ID2 = "ca-app-pub-000000000000000000000000000"; // geshner

    //

    private static String URL = "http://api.onibusfacil.com/busstops.php";

    //

    Context context;

    ExpandableListView expandableListView;

    ArrayList<Stop> stops;

    StopListAdapter stopListAdapter;

    boolean isRunning = false;

    private int versionCode;

    private AdView mAdView;

    private int lastExpandedPosition = -1;

    private String deviceID;

    //

    private LocationReceiver locationReceiver = new LocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location location, int counter) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            double distance = 5;

            if (!isRunning) {

                BusstopLoader busstopLoader = new BusstopLoader(latitude, longitude, distance);

                busstopLoader.execute();

            }

            if (DBG) Log.d(TAG, "locationReceiver : onLocationReceived");

        }
    };

    //

    public static final String md5(final String s) {

        try {

            // Create MD5 Hash

            MessageDigest digest = MessageDigest.getInstance("MD5");

            digest.update(s.getBytes());

            byte messageDigest[] = digest.digest();

            // Create Hex String

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < messageDigest.length; i++) {

                String h = Integer.toHexString(0xFF & messageDigest[i]);

                while (h.length() < 2) {
                    h = "0" + h;
                }

                hexString.append(h);

            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            //

        }

        return "";

    }

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        expandableListView = (ExpandableListView) findViewById(R.id.elvBusstops);

        expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            //

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {

                    expandableListView.collapseGroup(lastExpandedPosition);

                }

                lastExpandedPosition = groupPosition;

            }

        });

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;

        expandableListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));

        stops = new ArrayList<Stop>();

        stopListAdapter = new StopListAdapter(this, stops);

        expandableListView.setAdapter(stopListAdapter);

        //

        Calendar rightNow = Calendar.getInstance();

        double day1 = rightNow.get(Calendar.DAY_OF_YEAR);
        double day2 = Math.round(day1 / 2) * 2;

        String adUnit = day1 == day2 ? AD_UNIT_ID2 : AD_UNIT_ID1;

        //

        mAdView = new AdView(this);

        mAdView.setAdUnitId(adUnit);
        mAdView.setAdSize(AdSize.SMART_BANNER);

        //

        LinearLayout layout = (LinearLayout) findViewById(R.id.home_layout);

        layout.addView(mAdView);

        //

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("00000000000000000000000000000001")
                .build();

        mAdView.loadAd(adRequest);

        //

        context = this;

        //

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        deviceID = md5(android_id).toUpperCase();

        //

        versionCode = 0;

        try {

            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

        }

    }

    //

    @Override
    public void onPause() {

        mAdView.pause();

        super.onPause();

    }

    //

    @Override
    public void onResume() {

        super.onResume();

        mAdView.resume();

    }

    //

    @Override
    public void onDestroy() {

        mAdView.destroy();

        super.onDestroy();

    }

    //

    @Override
    protected void onStart() {

        super.onStart();

        startLocationManager();

        if (DBG) Log.d(TAG, "onStart");

    }

    //

    @Override
    protected void onStop() {

        stopLocationManager();

        super.onStop();

        if (DBG) Log.d(TAG, "onStop");

    }

    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }

    //

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_refresh:

                restartLocationManager();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }

    //

    private int getDipsFromPixel(float pixels) {

        final float scale = getResources().getDisplayMetrics().density;

        return (int) (pixels * scale + 0.5f);

    }

    //

    private void startLocationManager() {

        registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION));

        startService(new Intent(this, LocationService.class));

    }

    //

    private void stopLocationManager() {

        stopService(new Intent(this, LocationService.class));

        unregisterReceiver(locationReceiver);

    }

    //

    private void restartLocationManager() {

        stopLocationManager();

        startLocationManager();

    }

    //

    private class BusstopLoader extends AsyncTask<Void, Integer, Long> {

        ProgressDialog dialog;

        double latitude;

        double longitude;

        double distance;

        //

        public BusstopLoader(double latitude, double longitude, double distance) {

            super();

            this.latitude = latitude;
            this.longitude = longitude;
            this.distance = distance;

        }

        //

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            dialog = new ProgressDialog(context);

            dialog.setMessage("Carregando...");
            dialog.show();

        }

        //

        @Override
        protected void onPostExecute(Long result) {

            dialog.dismiss();

            stopListAdapter.notifyDataSetChanged();

            expandableListView.setSelection(0);

            super.onPostExecute(result);

        }

        //

        @Override
        protected Long doInBackground(Void... params) {

            isRunning = true;

            try {

                String u1 = "?latitude=" + latitude + "&longitude=" + longitude + "&distance=" + distance;
                String u2 = "&distance=1.5";
                String u3 = "&limit=100";
                String u4 = "&version=" + versionCode;
                String u5 = "&deviceID=" + deviceID;

                String url = URL + u1 + u2 + u3 + u4 + u5;

                if (DBG) Log.i(TAG, url);

                HttpParams httpParams = new BasicHttpParams();

                HttpProtocolParams.setContentCharset(httpParams, "utf-8");

                HttpClient httpClient = new DefaultHttpClient(httpParams);

                HttpGet httpGet = new HttpGet(url);

                // httpGet.addHeader("Accept-Encoding", "gzip");

                HttpResponse httpResponse = httpClient.execute(httpGet);

                StatusLine statusLine = httpResponse.getStatusLine();

                int statusCode = statusLine.getStatusCode();

                if (statusCode != 200) {
                    return null;
                }

                InputStream jsonStream = httpResponse.getEntity().getContent();

                // ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(jsonStream));

                // jsonStream = convertZipInputStreamToInputStream(zipInputStream);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jsonStream));

                //

                StringBuilder stringBuilder = new StringBuilder();

                String line;

                while ((line = bufferedReader.readLine()) != null) {

                    stringBuilder.append(line);

                }

                String jsonData = stringBuilder.toString();

                if (DBG) Log.i("JSON data ", jsonData);

                JSONObject joData = new JSONObject(jsonData);

                JSONArray jaItems1 = joData.getJSONArray("stops");

                if (DBG) Log.i("JSON size ", "" + jaItems1.length());

                stops.clear();

                for (int i1 = 0; i1 < jaItems1.length(); i1++) {

                    JSONObject jBusstop = jaItems1.getJSONObject(i1);

                    String jBStopName = jBusstop.getString("stopName");

                    double jBStopLatitude = jBusstop.getDouble("latitude");
                    double jBStopLongitude = jBusstop.getDouble("longitude");
                    double jBStopDistance = jBusstop.getDouble("distance");

                    Stop stops = new Stop(jBStopLatitude, jBStopLongitude, jBStopDistance, jBStopName);

                    JSONArray jRoutes = jBusstop.getJSONArray("routes");

                    Route route = null;

                    if (jRoutes.length() == 0) {

                        route = new Route(1, "SEM ITINERÁRIO");

                        stops.getRoutes().add(route);

                    } else {

                        for (int i2 = 0; i2 < jRoutes.length(); i2++) {

                            JSONObject jRoute = jRoutes.getJSONObject(i2);

                            int jType = jRoute.getInt("type");

                            if (DBG) Log.i("jType=", "" + jType);

                            if (jType == 1) {

                                String jDirection = jRoute.getString("direction");

                                if (DBG) Log.i("jDirection=", "" + jDirection);

                                route = new Route(jType, jDirection);

                            }

                            if (jType == 2) {

                                String jRouteCode = jRoute.getString("routeCode");
                                String jRouteName = jRoute.getString("routeName");
                                String jRouteTime = jRoute.getString("routeTime");

                                if (DBG) Log.i("jRoute=", "" + jRouteCode + "," + jRouteName);

                                route = new Route(jType, jRouteCode, jRouteName, jRouteTime);

                            }

                            stops.getRoutes().add(route);

                        }

                    }

                    MainActivity.this.stops.add(stops);

                }

                if (DBG) Log.d(TAG, "doInBackground");

            } catch (ClientProtocolException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (JSONException e) {

                e.printStackTrace();

            }

            isRunning = false;

            return null;
        }

        /*

        private InputStream convertZipInputStreamToInputStream(ZipInputStream in) throws IOException {

            final int BUFFER = 2048;

            int count = 0;

            byte data[] = new byte[BUFFER];

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            while ((count = in.read(data, 0, BUFFER)) != -1) {
                out.write(data);
            }

            InputStream is = new ByteArrayInputStream(out.toByteArray());

            return is;

        }

        */

    }

}
