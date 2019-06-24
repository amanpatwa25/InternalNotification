package patwa.aman.com.internalnotification;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StartActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {
    private int STORAGE_PERMISSION_CODE = 1;
    String  tag_string_req = "string_req";
    RecyclerView rv;
    JSONArray jsonArray;
    String url = "http://ops.coutloot.com:4321/internalApp/getAllNotifications";
    String email,deviceToken;
    ImageView iv;
    static Context context;
    ArrayList<Notification> notifications = new ArrayList<Notification>();

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
        } else {
            message = "Sorry! Not connected to internet";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }

    public Context getContext(){
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = this;

        rv = (RecyclerView)findViewById(R.id.rv);
        iv = (ImageView)findViewById(R.id.iv);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        int success = pref.getInt("success", 9);
        email = pref.getString("email",null);
        deviceToken = pref.getString("deviceToken",null);


        System.out.println("email"+email);
        System.out.println("deviceToken:"+deviceToken);

        System.out.println("success:"+success);

//        System.out.println("wapp:"+ hasWhatsapp("9322425286"));

        if(success != 1){
            Intent i= new Intent(StartActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
            email = pref.getString("email",null);
            deviceToken = pref.getString("deviceToken",null);

            System.out.println("email"+email);
            System.out.println("deviceToken:"+deviceToken);

            rv.setLayoutManager(new LinearLayoutManager(StartActivity.this));
            rv.setHasFixedSize(true);

            getApi();

        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.headerlayout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.log_out_btn){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("success");
            editor.remove("email");
            editor.remove("deviceToken");

            editor.commit();

            Intent loginIntent = new Intent(StartActivity.this,MainActivity.class);
            startActivity(loginIntent);
            finish();
        }
//        else if(item.getItemId() == R.id.remove_all_btn){
//            jsonArray = new JSONArray();
//
//        }

        return false;
    }

    void getApi(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
            int j;
                    @Override
                    public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                        Log.v("Response",response.toString());



                        try {

                            JSONObject jsonObject1 = new JSONObject(response);
                            int success = jsonObject1.getInt("success");

                            jsonArray = jsonObject1.getJSONArray("notification");


                            if (jsonArray.length() == 0 && success == 0) {
                                Toast.makeText(StartActivity.this, "No notification for you", Toast.LENGTH_LONG).show();
                                rv.setVisibility(View.INVISIBLE);
                            } else{
                                rv.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                    String mobile1 = jsonObject.getString("mobile");
                                    String message1 = jsonObject.getString("message");
                                    String id1 = jsonObject.getString("_id");
                                    JSONArray imageUrl = jsonObject.getJSONArray("imageUrl");
                                    ArrayList<String> image = new ArrayList<>();
                                    for(j=0;j<imageUrl.length();j++){

                                        image.add(imageUrl.get(j).toString());
                                    }

                                    System.out.println("jsonArray"+imageUrl);
                                    Log.v("MainImageUrl",imageUrl+"");

                                    Notification newNoti = new Notification(message1,mobile1,id1,image);

                                    notifications.add(newNoti);

                                }

                            RecyclerAdapter adapter = new RecyclerAdapter(StartActivity.this, notifications, StartActivity.this);
                            adapter.notifyDataSetChanged();
                            rv.setAdapter(adapter);

                        }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StartActivity.this,error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
//                                            params.put(KEY_USERNAME,username);
//                    params.put("deviceToken",deviceToken);
                params.put("email", email);
                params.put("status","NOT_SEND");
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed for storage")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(StartActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
