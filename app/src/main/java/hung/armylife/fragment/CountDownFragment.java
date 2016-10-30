package hung.armylife.fragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hung.armylife.MyAdapter;
import hung.armylife.MySQLHelper;
import hung.armylife.MySharedPreferences;
import hung.armylife.ProfileActivity;
import hung.armylife.R;
import hung.armylife.SQLiteHelper;
import hung.armylife.useless.Standard;
import hung.armylife.widget.CircleImg;

public class CountDownFragment extends Fragment {
    private View mView;
    private TextView txt_CountDown, txt_EntryDate, txt_ExitDate, txt_PassDay, txt_Invite;
    private Standard standard = new Standard();
    private ImageButton imageButtonProfile;
    private Button btn_Invite;
    private String EntryDate;
    private SQLiteHelper sqLiteHelper;
    private String ExitDate;
    private String Period;
    private String Redeem;
    private String PassDay;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private MySharedPreferences profile;
    final String[] Item_Period = {"4個月", "4個月5天", "1年", "1年15天", "3年", "4年"};
    private RecyclerView recyclerView;
    private List<Map<String, Object>> mySQLData = new ArrayList<Map<String,Object>>();
    private List<Map<String, Object>> myFriendsData = new ArrayList<Map<String,Object>>();
    private ArrayList<Integer> mDataType = new ArrayList<Integer>();
    private MyAdapter myAdapter;
    private ArrayList<String> FriendList = new ArrayList<String>();
    private MySQLHelper mySQLHelper;
    // TODO: Rename and change types and number of parameters
    public static CountDownFragment newInstance() {
        CountDownFragment fragment = new CountDownFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile = new MySharedPreferences(CountDownFragment.this.getContext());
        mySQLHelper = new MySQLHelper(CountDownFragment.this.getContext());
        sqLiteHelper = new SQLiteHelper(CountDownFragment.this.getContext());
    }

    public void onResume() {
        super.onResume();
        getProfile();
        initView(mView);
    }
    public void getProfile() {
        EntryDate=profile.get_entryate();//.getString(SP_EntryDate, "");
        //Log.d("EntryDate",EntryDate);
        Redeem=profile.get_redeem();//.getString(SP_Redeem, "");  //折抵
        if (profile.get_peroid()>0) {//.getInt(SP_Period, -1) > 0) {
            Period = Item_Period[profile.get_peroid()];//.getInt(SP_Period, -1)]; //役期
        }
        if (EntryDate.length() > 0) {
            Log.d("EntryDate",EntryDate);
            String oriExitDate = EntryDate.replace(EntryDate.substring(0, 4), String.valueOf(Integer.valueOf(EntryDate.substring(0, 4)) + 1));
            Date dateOriExit=null;
            try {
                dateOriExit = sdf.parse(oriExitDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dateOriExit.getTime()-(Integer.valueOf(Redeem)*(1000 * 3600 * 24)));
            ExitDate = c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH);
            Log.d("ExitDate",ExitDate);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d("onCreateView","onCreateView");
        View view = inflater.inflate(R.layout.fragment_count_down, container, false);
        mView=view;
        return mView;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initView(View view) {
        txt_Invite = (TextView) view.findViewById(R.id.txt_invite);
        btn_Invite = (Button) view.findViewById(R.id.btn_invite);
        btn_Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appLinkUrl, previewImageUrl;
                appLinkUrl = "https://www.mydomain.com/myapplink";
                previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";
                if (AppInviteDialog.canShow()) {
                    AppInviteContent content = new AppInviteContent.Builder()
                            .setApplinkUrl(appLinkUrl)
                            .setPreviewImageUrl(previewImageUrl)
                            .build();
                    AppInviteDialog.show(CountDownFragment.this, content);
                }
            }
        });
        txt_EntryDate = (TextView) view.findViewById(R.id.txt_EntryDate);
        txt_ExitDate = (TextView) view.findViewById(R.id.txt_ExitDate);
        txt_CountDown = (TextView) view.findViewById(R.id.txt_CountDownNum);
        txt_PassDay = (TextView) view.findViewById(R.id.txt_PassDay);
        if (getCountDown(ExitDate).length()>0) {
            txt_CountDown.setText(getCountDown(ExitDate));
            mySQLHelper.Update(profile.get_facebook_id(),"countdown",getCountDown(ExitDate));
            txt_PassDay.setText(getPassDay(profile.get_entryate()));
            txt_EntryDate.setText(EntryDate);
            mySQLHelper.Update(profile.get_facebook_id(),"entrydate",EntryDate);
            txt_ExitDate.setText(ExitDate);
            mySQLHelper.Update(profile.get_facebook_id(),"exitdate",ExitDate);
            /*
            if (Integer.valueOf(getCountDown(ExitDate)) < 0) {
                txt_CountDown.setTextSize(100f);
                txt_CountDown.setText("0");
            }*/
        }
        imageButtonProfile = (ImageButton) view.findViewById(R.id.imageButton_Profile);
        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] title = {"1", "2", "3"};
                int[] view = {R.layout.setting_dialog_view,R.layout.setting_dialog_view,R.layout.setting_dialog_view};
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        setFriendList(profile.get_friends());
        mySQLHelper.SelectFriends(FriendList, new MySQLHelper.VolleyCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                Log.d("onSuccess","onSuccess");
                if(result.size()==FriendList.size()) {
                    mySQLData.clear();
                    mDataType.clear();
                    mySQLData = result;
                    for(int i=0; i<mySQLData.size(); i++) {
                        if(i==mySQLData.size()-1)
                            mDataType.add(1);
                        else
                            mDataType.add(0);
                    }
                    sqLiteHelper.saveToSQLite(mySQLData);
                    myFriendsData = sqLiteHelper.getFriendsData();
                    myAdapter = new MyAdapter(myFriendsData,mDataType);
                    recyclerView.setAdapter(myAdapter);
                }
            }
            public void onError() {
                myFriendsData = sqLiteHelper.getFriendsData();
                myAdapter = new MyAdapter(myFriendsData,mDataType);
                recyclerView.setAdapter(myAdapter);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }
    public void setFriendList(String Friend_id) {
        FriendList.clear();
        int temp=0;
        for(int i=0;i<Friend_id.length();i++) {
            if(Friend_id.length()>0) {
                if (Friend_id.substring(i, i + 1).equals(";")) {
                    FriendList.add(Friend_id.substring(temp, i));
                    temp = i + 1;
                }else if(i==Friend_id.length()-1) {
                    FriendList.add(Friend_id.substring(temp, Friend_id.length()));
                }
            }
        }
        if(FriendList.size()>0) {
            recyclerView.setVisibility(View.VISIBLE);
            btn_Invite.setVisibility(View.INVISIBLE);
            txt_Invite.setVisibility(View.INVISIBLE);
        }else {
            recyclerView.setVisibility(View.INVISIBLE);
            btn_Invite.setVisibility(View.VISIBLE);
            txt_Invite.setVisibility(View.VISIBLE);
        }
    }
    public String getCountDown(String exitdate) {
        Log.d("getCountDown",exitdate);
        String strNum = "";
        if(exitdate.length()>0) {
            try {
                int mYear, mMonth, mDay;
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                Date dateExit = sdf.parse(exitdate);
                Date dateNow = sdf.parse(mYear + "/" + (mMonth + 1) + "/" + mDay);
                int countdown =  (int) ((dateExit.getTime()-dateNow.getTime()) / (1000 * 3600 * 24));
                Log.d("getCountDown",String.valueOf(countdown));
                strNum = String.valueOf(countdown);
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return strNum;
    }
    public String getPassDay(String entrydate) {
        String strNum = "";
        if(entrydate.length()>0) {
            try {
                int mYear, mMonth, mDay;
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                Date dateEntry = sdf.parse(entrydate);
                Date dateNow = sdf.parse(mYear + "/" + (mMonth + 1) + "/" + mDay);
                int passday =  (int) ((dateNow.getTime()-dateEntry.getTime()) / (1000 * 3600 * 24));
                strNum = String.valueOf(passday);
                Log.d("getPassDay",String.valueOf(passday));
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return strNum;
    }
}
