package patwa.aman.com.internalnotification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText password,username;
    AutoCompleteTextView email;
    Button register;

    String  tag_string_req = "string_req";

    String url = "https://ops.coutloot.com/internalApp/register";
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        password = (EditText)findViewById(R.id.register_password);
        email = (AutoCompleteTextView)findViewById(R.id.register_email);
        register = (Button)findViewById(R.id.email_register_button);
        username = (EditText)findViewById(R.id.register_username);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String p = password.getText().toString();
                final String e = email.getText().toString();
                final String user = username.getText().toString();

                if(TextUtils.isEmpty(p) || TextUtils.isEmpty(e) || TextUtils.isEmpty(user))
                {
                    Toast.makeText(RegisterActivity.this, "Please enter all the details", Toast.LENGTH_LONG).show();
                }
                else{

                    pDialog = new ProgressDialog(RegisterActivity.this);
                    pDialog.setMessage("Loading...");
                    pDialog.show();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                    Log.v("Response",response.toString());

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        int s = jsonObject.getInt("success");

                                        if(s==1){
                                            Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }

                                        else{
                                            Toast.makeText(RegisterActivity.this, "Enter correct credentials.  Username should be more than 5 letters", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    pDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(RegisterActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                }
                            }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
//                                            params.put(KEY_USERNAME,username);
                            params.put("password",p);
                            params.put("email", e);
                            params.put("username", user);
                            return params;
                        }

                    };

                    AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);
                }
            }
        });

    }
}
