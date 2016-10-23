package hung.armylife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private AccessToken accessToken;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private TextView TXT_Skip, TXT_Name;
    private ProfilePictureView mProfilePictureView;
    //private SharedPreferences profile;
    private MySharedPreferences profile;
    private GraphRequest graphRequest;
    private String id;
    //private String data="Date", SP_Feeling="Feeling", SP_EntryDate="EntryDate", SP_Period="Period", SP_Redeem="Redeem", SP_SoldierCategory="SoldierCategory", SP_Sequence="Sequence", SP_Unit="Unit", SP_First="FIRST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        //profile = getSharedPreferences(data, 0);
        profile = new MySharedPreferences(this);
        callbackManager = CallbackManager.Factory.create();
        mProfilePictureView = (ProfilePictureView) findViewById(R.id.FB_Picture);
        TXT_Name = (TextView) findViewById(R.id.txt_FB_Name);
        TXT_Skip = (TextView) findViewById(R.id.txt_Skip);
        TXT_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int first = profile.get_first();//.getInt(SP_First,0);
                if(first==0) {
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    Toast.makeText(LoginActivity.this,"請先設定個人資訊",Toast.LENGTH_SHORT).show();
                }
                else
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        });
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
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
                            }
                        });
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
                //Log.d("FB", object.toString());
                //Log.d("FB", object.optString("name"));
                //Log.d("FB", object.optString("link"));
                //Log.d("FB", object.optString("id"));
                //Log.d("FB", object.optString("friends"));
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                Log.d("FB", "onCurrentAccessTokenChanged");
                getFacebookData();
                //mProfilePictureView.setVisibility(View.INVISIBLE);
                //TXT_Name.setVisibility(View.INVISIBLE);
                //TXT_Skip.setText("略過");
            }
        };
        getFacebookData();
    }
    public void getFacebookData() {
        if(AccessToken.getCurrentAccessToken()!=null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        //當RESPONSE回來的時候
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            mProfilePictureView.setVisibility(View.VISIBLE);
                            mProfilePictureView.setProfileId(object.optString("id"));
                            TXT_Name.setVisibility(View.VISIBLE);
                            TXT_Name.setText(object.optString("name"));
                            //讀出姓名 ID FB個人頁面連結
                            Log.d("FB_name", object.optString("name"));
                            Log.d("FB_link", object.optString("link"));
                            Log.d("FB_id", object.optString("id"));
                            TXT_Skip.setText("開始使用");
                            try {
                                Log.d("FB_picture", object.getJSONObject("picture").getJSONObject("data").optString("url"));
                            } catch (JSONException e) {

                            }
                            profile.set_facebook_id(object.optString("id"));//.edit().putString("facebook_id",object.optString("id")).commit();
                            profile.set_name(object.optString("name"));
                        }
                    });
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
                                String strFriends="";
                                for (int i = 0; i < jsonArray.length(); ++i) {
                                    JSONObject o = (JSONObject) jsonArray.get(i);
                                    if(i==jsonArray.length()-1)
                                        strFriends += o.getString("id");
                                    else
                                        strFriends += o.getString("id")+";";
                                    Log.d("FB_Friends", o.getString("id"));
                                }
                                profile.set_friends(strFriends);
                                MySQLHelper mySQLHelper = new MySQLHelper(LoginActivity.this);//.Update_Freinds_id(profile.getString("facebook_id","未登入"),strFriends);
                                mySQLHelper.Insert_All(profile.get_countdown(),
                                            profile.get_entryate(),
                                            profile.get_exitdate(),
                                            profile.get_friends(),
                                            profile.get_mood(),
                                            profile.get_name(),
                                            profile.get_facebook_id());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).executeAsync();

        }else {
            mProfilePictureView.setVisibility(View.INVISIBLE);
            TXT_Name.setVisibility(View.INVISIBLE);
            TXT_Skip.setText("略過");
        }
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
