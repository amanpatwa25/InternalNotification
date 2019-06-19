package patwa.aman.com.internalnotification;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText password;
    AutoCompleteTextView email;
    Button btn,register;

    String msg;
//    DatabaseReference notiData;
//    FirebaseRecyclerOptions<NotificationModel> options;
//    FirebaseRecyclerAdapter<NotificationModel,NotificationAdapter> adapter;

    String  tag_string_req = "string_req";

    String url = "https://ops.coutloot.com/internalApp/login";

    ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        btn = (Button)findViewById(R.id.email_sign_in_button);
        register = (Button)findViewById(R.id.email_register_button);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }
                        if (task.getResult() != null){
                            msg = task.getResult().getToken();

                        }else {
                            Log.v("MainToken", "result is null");
                        }

                        btn.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View view) {
                                pDialog = new ProgressDialog(MainActivity.this);
                                pDialog.setMessage("Loading...");
                                pDialog.show();

                                final String e = email.getText().toString();
                                final String p = password.getText().toString();

                                Log.v("email",e+"");
                                Log.v("pass",p+"");

                                if(TextUtils.isEmpty(e) || TextUtils.isEmpty(p)){
                                    Toast.makeText(MainActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                                    Log.v("Response",response.toString());
                                                    try {
                                                        JSONObject jsonObject=new JSONObject(response);
                                                        int s=jsonObject.getInt("success");
                                                        Log.v("success",s+"");
                                                        if(s==1){
                                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                                            SharedPreferences.Editor editor = pref.edit();

                                                            editor.putInt("success",s);
                                                            editor.putString("email",e);
                                                            editor.putString("deviceToken",msg);
                                                            editor.commit();
                                                            pDialog.dismiss();
                                                            Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
                                                            startActivity(startIntent);
                                                            finish();
                                                        }


                                                        else{
                                                            Toast.makeText(MainActivity.this, "Please enter correct credentials", Toast.LENGTH_LONG).show();
                                                        }

                                                    } catch (JSONException e1) {
                                                        e1.printStackTrace();
                                                    }

                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                                                    pDialog.dismiss();
                                                }
                                            }){
                                        @Override
                                        protected Map<String,String> getParams(){
                                            Map<String,String> params = new HashMap<String, String>();
//                                            params.put(KEY_USERNAME,username);
                                            params.put("password",p);
                                            params.put("email", e);
                                            params.put("deviceToken",msg);
                                            return params;
                                        }

                                    };

                                    AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);
                                }
                            }
                        });


//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });


//        }
    }

}
