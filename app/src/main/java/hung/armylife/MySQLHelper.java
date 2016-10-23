package hung.armylife;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hung on 2016/10/23.
 */

public class MySQLHelper {
    Context context;
    String uriQeury="http://192.168.1.101/armyqeury.php";
    String uriInsert="http://192.168.1.101/armyinsert.php";
    String uriUpdte="http://192.168.1.101/armyupdate.php";
    String uriCheckData="http://192.168.1.101/armycheckdata.php?facebook_id=";
    public MySQLHelper(Context context) {
        this.context = context;
    }
    public void Update(final String facebook_id, final String key,final String value) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, uriUpdte,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response , Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "failed to update", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sql", "UPDATE member  SET "+key+" = '"+value+"' WHERE facebook_id = "+facebook_id);
                Log.d("Update",key+" "+value);
                return params;
            }
        };
        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(postRequest);
    }
    public void Insert_All(final int countdown, final String entrydate, final String exitdate, final String friends, final String mood, final String name, final String facebook_id) {
        final StringRequest stringRequest = new StringRequest(uriCheckData+facebook_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("hasData",response);
                        Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                        if(Integer.valueOf(response)>0) {
                            Update(facebook_id,"friends",friends);
                        }else {
                            StringRequest postRequest = new StringRequest(Request.Method.POST, uriInsert,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("Insert",response);
                                            Toast.makeText(context, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "failed to insert", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("name", name);
                                    params.put("countdown", String.valueOf(countdown));
                                    params.put("entrydate", entrydate);
                                    params.put("exitdate", exitdate);
                                    params.put("mood", mood);
                                    params.put("facebook_id", facebook_id);
                                    params.put("friends", friends);
                                    return params;
                                }
                            };
                            // Adding request to request queue
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            requestQueue.add(postRequest);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        // Instantiate the RequestQueue.;
        RequestQueue queue = Volley.newRequestQueue(context);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
