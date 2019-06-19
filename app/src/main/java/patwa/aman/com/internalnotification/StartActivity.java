package patwa.aman.com.internalnotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class StartActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {

    String  tag_string_req = "string_req";
    RecyclerView rv;
    JSONArray jsonArray;
    String url = "https://ops.coutloot.com/internalApp/getAllNotifications";
    String email,deviceToken;
    ImageView iv;
    static Context context;

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


//        Uri imgUri = Uri.parse("http://unsplash.com/photos/1HZcJjdtc9g");
//        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
//        whatsappIntent.setType("text/plain");
//        whatsappIntent.setPackage("com.whatsapp");
//        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share");
//        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
//        whatsappIntent.setType("image/jpeg");
//        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        try {
//            startActivity(whatsappIntent);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "Whatsapp not install", Toast.LENGTH_SHORT).show();
//        }


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
                    @Override
                    public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                        Log.v("Response",response.toString());



                        try {

                            JSONObject jsonObject1 = new JSONObject(response);
                            int success = jsonObject1.getInt("success");

                            jsonArray = jsonObject1.getJSONArray("notification");
                            String[] message = new String[jsonArray.length()];
                            String[] mobile = new String[jsonArray.length()];
                            String[] id = new String[jsonArray.length()];

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

                                    mobile[i] = mobile1;
                                    message[i] = message1;
                                    id[i] = id1;

//                                    try {
//                                        URL url = new URL(image);
//                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                                        connection.setDoInput(true);
//                                        connection.connect();
//                                        InputStream input = connection.getInputStream();
//                                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                                        Log.v("Bitmap", String.valueOf(myBitmap));
//
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
                                }

                            Log.v("Mobile:", String.valueOf(mobile));
                            Log.v("Message:", message + "");
                            Log.v("id", id + "");

                            RecyclerAdapter adapter = new RecyclerAdapter(mobile, message, id, StartActivity.this);
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

//    public String hasWhatsapp(String contactID) {
//        String rowContactId = null;
//        boolean hasWhatsApp;
//
//        String[] projection = new String[]{ContactsContract.RawContacts._ID};
//        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
//        String[] selectionArgs = new String[]{contactID, "com.whatsapp"};
//        Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
//        if (cursor != null) {
//            hasWhatsApp = cursor.moveToNext();
//            if (hasWhatsApp) {
//                rowContactId = cursor.getString(0);
//            }
//            cursor.close();
//        }
//        return rowContactId;
//    }
}
