package patwa.aman.com.internalnotification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.ContextCompat.startActivity;


class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    String[] mobile,message,id;
    String[] imageUrl;
    String  tag_string_req = "string_req";
    String url = "http://ops.coutloot.com:4321/internalApp/status";
    StartActivity Api;
    ProgressDialog p,p2;
    PackageManager pm;
    Context context;
    ArrayList<Notification> notifications;
    ArrayList<Uri> image = new ArrayList<>();
//    Bitmap[] bit = new Bitmap[10];
    int check = 0;
    int position;


    public RecyclerAdapter(StartActivity startActivity, ArrayList<Notification> notifications, StartActivity startActivity1) {
        Api = startActivity1;
        this.notifications = notifications;
        this.context = startActivity;

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
        viewHolder.mobile.setText(notifications.get(i).mobile);
        viewHolder.body.setText(notifications.get(i).message);
        System.out.println(notifications.get(i).images);

        viewHolder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "91" + notifications.get(i).mobile;
                String body = notifications.get(i).message;


                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://ops.coutloot.com:4321/internalApp/sendText",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                Log.v("Response",response.toString());
                                try {
                                    JSONObject jsonObject =new JSONObject(response);
                                    int success = jsonObject.getInt("status");

                                    if(success == 1){
                                        notifications.removeAll(notifications);
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
                        params.put("_id",notifications.get(i).id);
//                        params.put("status","SENT");
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);
//                notifications.removeAll(notifications);
//                Api.getApi();

                Uri uri = Uri.parse("https://wa.me/<" + title + ">/?text=" + body);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.whatsapp");
                startActivity(viewHolder.itemView.getContext(),Intent.createChooser(intent,"Share with"),null);
            }
        });

        viewHolder.sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("ImageUrl: ",imageUrl+"");

                p2 = new ProgressDialog(viewHolder.itemView.getContext());
                p2.setMessage("Please wait...It is loading");
//            p.setIndeterminate(false);
                p2.setCancelable(false);
                p2.show();

                position = i;
//
                downloadImage(notifications.get(i).images.get(check));

//                System.out.println("Image Array: "+image);


            }
        });

        viewHolder.sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://ops.coutloot.com:4321/internalApp/sendSms",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                                Log.v("Response",response.toString());
                                try {
                                    JSONObject jsonObject =new JSONObject(response);
                                    int success = jsonObject.getInt("status");

                                    if(success == 1){
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + notifications.get(i).mobile));
                                        intent.putExtra("sms_body", notifications.get(i).message);
                                        startActivity(context,intent,null);

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
                        params.put("_id",notifications.get(i).id);
//                        params.put("status","SENT");
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);









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
                                        notifications.removeAll(notifications);
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
                        params.put("_id",notifications.get(i).id);
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
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mobile,body;
        TextView send,remove,sendImg,sendSms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mobile = itemView.findViewById(R.id.mobile_no);
            body = itemView.findViewById(R.id.mess);
            send = itemView.findViewById(R.id.btn_send);
            remove = itemView.findViewById(R.id.btn_remove);
            sendImg = itemView.findViewById(R.id.btn_send_img);
            sendSms = itemView.findViewById(R.id.send_sms);
        }
    }




    public class ImageShare extends AsyncTask<Bitmap, Void, Uri> {


        @Override
        protected void onPreExecute() {
            p = new ProgressDialog(context);
            p.setMessage("Please wait...It is loading");
//            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected void onPostExecute(Uri uri) {
            image.add(uri);
            p.dismiss();
            System.out.println("Size: "+notifications.get(position).images.size());
            if(check == (notifications.get(position).images.size() - 1)){
                check = 0;

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

                                        Intent waIntent = new Intent(Intent.ACTION_SEND);
                                        waIntent.setType("image/*");
                                        waIntent.setPackage("com.whatsapp");
                                        waIntent.putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, image);
                                        context.startActivity(Intent.createChooser(waIntent, "Share with"));
                                        notifications.removeAll(notifications);
                                        image.removeAll(image);
                                        Api.getApi();
                                        return;

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
                                Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
//                                            params.put(KEY_USERNAME,username);
                        params.put("_id",notifications.get(position).id);
                        params.put("status","SENT");
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(stringRequest,tag_string_req);






            }
            else {
                check = check+1;
                downloadImage(notifications.get(position).images.get(check));
            }



        }



        @Override
        protected Uri doInBackground(Bitmap... bitmaps) {
            pm = context.getPackageManager();
            int i;
//            ArrayList<Uri> image = new ArrayList<>();
//            Uri[] imageUri = new Uri[bitmaps.length];
            try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                Toast.makeText(ImageActivity.this, "Inside try", Toast.LENGTH_SHORT).show();
                    bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmaps[0], "Title", null);
                    Uri imageUri = Uri.parse(path);
//                Toast.makeText(ImageActivity.this, "Inside pm", Toast.LENGTH_SHORT).show();
                    @SuppressWarnings("unused")
                    PackageInfo info = (PackageInfo) pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);

                return imageUri;

            } catch (Exception e) {

                Log.e("Error on sharing", e.getMessage() + " ");
//                Toast.makeText(ImageActivity.this, "App not Installed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                return null;
            }

        }


        }

        public void downloadImage(String url){

        if(url.equals(null)){
            Toast.makeText(context, "No images for this message", Toast.LENGTH_SHORT).show();
        }
        else {

            Log.v("Url", url + "");

            Picasso.with(context)
                    .load(url)
                    .into(new Target() {
                              @Override
                              public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                  p2.dismiss();
//                                          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                                  ImageShare imageShare = new ImageShare();
                                  imageShare.execute(bitmap);

                              }


                              @Override
                              public void onBitmapFailed(Drawable errorDrawable) {
//                                      p.dismiss();
                                  p2.dismiss();
                                  Toast.makeText(context, "Failed to load bitmap" + errorDrawable, Toast.LENGTH_SHORT).show();
//                                      if(check<imageUrl.length) {
//                                          check = check + 1;
//                                          downloadImage(imageUrl[check]);
//                                      }
                              }

                              @Override
                              public void onPrepareLoad(Drawable placeHolderDrawable) {
                              }
                          }
                    );
        }

        }

    }

