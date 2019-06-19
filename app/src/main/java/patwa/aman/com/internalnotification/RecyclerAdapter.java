package patwa.aman.com.internalnotification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.ContextCompat.startActivity;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    String[] mobile,message,id;
    String  tag_string_req = "string_req";
    String url = "https://ops.coutloot.com/internalApp/status";
    StartActivity Api;


    public RecyclerAdapter(String[] mobile, String[] message, String[] id, StartActivity listener) {
        this.mobile = mobile;
        this.message = message;
        this.id = id;
        Api = listener;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notificationlayout,viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.mobile.setText(mobile[i]);
        viewHolder.body.setText(message[i]);

        viewHolder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "91" + mobile[i];
                String body = message[i];


                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                Log.v("Response",response.toString());
                                try {
                                    JSONObject jsonObject =new JSONObject(response);
                                    int success = jsonObject.getInt("success");

                                    if(success == 1){
                                        Api.getApi();
                                    }
                                    else{
                                        Toast.makeText(Api, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(viewHolder.itemView.getContext(),error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
//                                            params.put(KEY_USERNAME,username);
                        params.put("_id",id[i]);
                        params.put("status","SENT");
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);

                Api.getApi();

                Uri uri = Uri.parse("https://wa.me/<" + title + ">/?text=" + body);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.whatsapp");
                startActivity(viewHolder.itemView.getContext(),Intent.createChooser(intent,"Share with"),null);
            }
        });

        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                Log.v("Response",response.toString());

                                try {
                                    JSONObject jsonObject =new JSONObject(response);
                                    int success = jsonObject.getInt("success");

                                    if(success == 1){
                                        Api.getApi();
                                    }
                                    else{
                                        Toast.makeText(Api, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(viewHolder.itemView.getContext(),error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
//                                            params.put(KEY_USERNAME,username);
                        params.put("_id",id[i]);
                        params.put("status","REMOVE");
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mobile.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mobile,body;
        TextView send,remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mobile = itemView.findViewById(R.id.mobile_no);
            body = itemView.findViewById(R.id.mess);
            send = itemView.findViewById(R.id.btn_send);
            remove = itemView.findViewById(R.id.btn_remove);


        }
    }
}
