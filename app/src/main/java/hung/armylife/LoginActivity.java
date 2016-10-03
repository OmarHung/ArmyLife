package hung.armylife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private TextView TXT_Skip;
    private SharedPreferences profile;
    private GraphRequest graphRequest;
    private String id;
    private String data="Date", SP_Feeling="Feeling", SP_EntryDate="EntryDate", SP_Period="Period", SP_Redeem="Redeem", SP_SoldierCategory="SoldierCategory", SP_Sequence="Sequence", SP_Unit="Unit", SP_First="FIRST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        profile = getSharedPreferences(data, 0);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        TXT_Skip = (TextView) findViewById(R.id.txt_Skip);
        TXT_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int first = profile.getInt(SP_First,0);
                if(first==0) {
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    Toast.makeText(LoginActivity.this,"請先設定個人資訊",Toast.LENGTH_SHORT).show();
                }
                else
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });
        loginButton.setReadPermissions(Arrays.asList(new String[]{"public_profile","user_friends"}));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //登入成功
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名 ID FB個人頁面連結
                                Log.d("FB",object.toString());
                                Log.d("FB_name",object.optString("name"));
                                Log.d("FB_link",object.optString("link"));
                                Log.d("FB_id",object.optString("id"));
                                try {
                                    Log.d("FB_picture", object.getJSONObject("picture").getJSONObject("data").optString("url"));
                                }catch (JSONException e) {

                                }
                            }
                        });
                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,picture");
                request.setParameters(parameters);
                request.executeAsync();

                Bundle param = new Bundle();
                param.putString("fields", "friendlist ,members");
                graphRequest.setParameters(param);
                graphRequest.newMyFriendsRequest(
                        accessToken,
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                // Application code for users friends
                                try {
                                    //JSONObject rawName = response.getJSONObject();
                                    Log.d("FB_Friends", jsonArray.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).executeAsync();

                /*
                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        accessToken,
                        //AccessToken.getCurrentAccessToken(),
                        "/{"+id+"}/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONObject rawName = response.getJSONObject();
                                    Log.d("FB_Friends", rawName.toString());
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();
                */
                /*
                GraphRequestBatch batch = new GraphRequestBatch(
                        GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                                        // Application code for user
                                        Log.d("FB", jsonObject.toString());
                                        Log.d("FB_name", jsonObject.optString("name"));
                                        Log.d("FB_link", jsonObject.optString("link"));
                                        Log.d("FB_id", jsonObject.optString("id"));
                                    }
                                }),
                        GraphRequest.newMyFriendsRequest(
                                accessToken,
                                new GraphRequest.GraphJSONArrayCallback() {
                                    @Override
                                    public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                                        // Application code for users friends
                                        Log.d("FB_Friends", jsonArray.toString());
                                    }
                                })
                );
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                    }
                });
                batch.executeAsync();
                //accessToken之後或許還會用到 先存起來
                accessToken = loginResult.getAccessToken();
                Log.d("FB", "access token got.");
                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
*/
                //Bundle param = new Bundle();
                //param.putString("fields", "friendlist ,members");
                //graphRequest.setParameters(param);
                //graphRequest.executeAsync();
            }
            //登入取消
            @Override
            public void onCancel() {
                // App code
                Log.d("FB", "CANCEL");
            }
            //登入失敗
            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FB", exception.toString());
            }
        });
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
            }
        };

        //send request and call graph api
        graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //當RESPONSE回來的時候
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                //讀出姓名 ID FB個人頁面連結
                Log.d("FB", object.toString());
                Log.d("FB", object.optString("name"));
                Log.d("FB", object.optString("link"));
                Log.d("FB", object.optString("id"));
                Log.d("FB", object.optString("friends"));
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
