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

import hung.armylife.LoginActivity;
import hung.armylife.MySQLHelper;
import hung.armylife.MySharedPreferences;
import hung.armylife.ProfileActivity;
import hung.armylife.R;
import hung.armylife.useless.Standard;
import hung.armylife.widget.CircleImg;

public class CountDownFragment extends Fragment {
    private View mView;
    private TextView txt_CountDown, txt_EntryDate, txt_ExitDate, txt_PassDay, txt_Invite;
    private Standard standard = new Standard();
    private ImageButton imageButtonProfile;
    private Button btn_Invite;
    private String EntryDate;
    private String OriExitDate;
    private String NewExitDate;
    private String Period;
    private String Redeem;
    private String PassDay;
    private Date dateEntry;
    private Date dateOriExit;
    private Date dateNewExit;
    private Date dateNow;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    //private SharedPreferences profile;
    private MySharedPreferences profile;
    final String[] Item_Period = {"4個月", "4個月5天", "1年", "1年15天", "3年", "4年"};
    private ArrayList<Integer> mDataType = new ArrayList<Integer>();
    final Calendar c = Calendar.getInstance();
    private int mYear, mMonth, mDay;
    private String data="Date", SP_Feeling="Feeling", SP_EntryDate="EntryDate", SP_Period="Period", SP_Redeem="Redeem", SP_Sequence="Sequence", SP_First="FIRST";
    private RecyclerView recyclerView;
    private List<Map<String, Object>> myDataset;
    private MyAdapter myAdapter;
    private int overallYScroll = 0;
    private boolean Touch=false;
    private int ItemHigh, ItemAmount=10;
    private String facebook_id;
    private String Friend_id;
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
        //Log.d("onCreate","onCreate");
        //profile = getActivity().getSharedPreferences(data, 0);
        profile = new MySharedPreferences(CountDownFragment.this.getContext());
        mySQLHelper = new MySQLHelper(CountDownFragment.this.getContext());
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    public void onResume() {
        super.onResume();
        //Log.d("onResume","onResume");
        //overallYScroll = 0;
        getProfile();
        initView(mView);
    }
    public void getProfile() {
        EntryDate=profile.get_entryate();//.getString(SP_EntryDate, "");
        Redeem=profile.get_redeem();//.getString(SP_Redeem, "");  //折抵
        if (profile.get_peroid()>0) {//.getInt(SP_Period, -1) > 0) {
            Period = Item_Period[profile.get_peroid()];//.getInt(SP_Period, -1)]; //役期
        }
        if (EntryDate.length() > 0) {
            OriExitDate = EntryDate.replace(EntryDate.substring(0, 4), String.valueOf(Integer.valueOf(EntryDate.substring(0, 4)) + 1));
        }
        try {
            dateEntry = sdf.parse(EntryDate);
            dateOriExit = sdf.parse(OriExitDate);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dateOriExit.getTime()-(Integer.valueOf(Redeem)*(1000 * 3600 * 24)));
            NewExitDate = c.get(Calendar.YEAR)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.DAY_OF_MONTH);
            dateNewExit = sdf.parse(NewExitDate);
            dateNow = sdf.parse(mYear + "/" + (mMonth + 1) + "/" + mDay);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        facebook_id = profile.get_facebook_id();//.getString("facebook_id","未登入");
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
        if (getCountDownNum().length()>0) {
            txt_CountDown.setText(getCountDownNum());
            mySQLHelper.Update(profile.get_facebook_id(),"countdown",getCountDownNum());
            txt_PassDay.setText(PassDay);
            txt_EntryDate.setText(EntryDate);
            mySQLHelper.Update(profile.get_facebook_id(),"entrydate",EntryDate);
            txt_ExitDate.setText(NewExitDate);
            mySQLHelper.Update(profile.get_facebook_id(),"exitdate",NewExitDate);
            if (Integer.valueOf(getCountDownNum()) < 0) {
                txt_CountDown.setTextSize(100f);
                txt_CountDown.setText("0");
            }
        }
        imageButtonProfile = (ImageButton) view.findViewById(R.id.imageButton_Profile);
        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] title = {"1", "2", "3"};
                int[] view = {R.layout.setting_dialog_view,R.layout.setting_dialog_view,R.layout.setting_dialog_view};
                //FlowDialog fd=new FlowDialog(CountDownFragment.this.getActivity(), view, title);
                //fd.setCancelable(false);
                //fd.show();
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        myDataset = new ArrayList<Map<String,Object>>();
        myDataset.clear();
        mDataType.clear();
        setFriendList(profile.get_friends());
        String uriSelect="http://192.168.1.101/armyselect.php?facebook_id=";
        for(int i=0; i<FriendList.size(); i++){
            final StringRequest stringRequest = new StringRequest(uriSelect+FriendList.get(i),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Map<String, Object> item = new HashMap<String, Object>();
                            item.put("name", getFriens_Info(response,"name"));
                            item.put("feeling", getFriens_Info(response,"mood"));
                            item.put("cd", getFriens_Info(response,"countdown")+"天");
                            myDataset.add(item);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }
            });
            // Instantiate the RequestQueue.;
            RequestQueue queue = Volley.newRequestQueue(CountDownFragment.this.getContext());
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            if(i==FriendList.size()-1)
                mDataType.add(1);
            else
                mDataType.add(0);
        }
        myAdapter = new MyAdapter(myDataset,mDataType);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(myAdapter);
        /*
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //ItemHigh=recyclerView.computeVerticalScrollRange()/6;
                Log.d("ItemHigh","ItemHigh  = "+ItemHigh);
                Log.d("overallYScroll","overallYScroll  = "+overallYScroll);
                //Log.d("Touch","Touch0 "+ Touch);
                switch (newState) {
                    case 0:
                        Log.d("ScrollState","靜止狀態");
                        int Quotient=overallYScroll/ItemHigh;
                        int Remainder=overallYScroll%ItemHigh;
                        if(Touch) {
                            //Log.i("Height","Height  = "+ItemHigh);
                            //Log.i("overallYScroll","overallYScroll = "+overallYScroll);
                            //Log.d("Quotient","Quotient "+ Quotient);
                            //Log.d("Remainder","Remainder "+ Remainder);
                            Touch = false;
                            if(Remainder>ItemHigh/2)
                                recyclerView.smoothScrollBy(0, ItemHigh-Remainder);
                            else
                                recyclerView.smoothScrollBy(0, -Remainder);
                        }
                        break;
                    case 1:
                        //Log.d("ScrollState","手指在上面滑動");
                        Touch=true;
                        //Log.i("overallYScroll","overallYScroll = "+overallYScroll);
                        break;
                    case 2:
                        //Log.d("ScrollState","慣性滑動");
                        break;
                }
            }
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //recyclerView.smoothScrollBy();
                overallYScroll = overallYScroll + dy;
                //Log.i("check","overallYScroll = "+overallYScroll+" overall X  = " + dx+" overall Y  = " + dy);
            }
        });
        Touch=true;
        */
    }
    public String getFriens_Info(String mJSONText, String key) {
        String result="";
        try {
            Log.d("JSONText",mJSONText);
            JSONArray jsonArray = new JSONArray(mJSONText);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = ((JSONObject)jsonArray.get(i));
                result = jsonObject.getString(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    public void setFriendList(String Friend_id) {
        int count=0;
        int temp=0;
        for(int i=0;i<Friend_id.length();i++) {
            if(Friend_id.length()>0) {
                if (Friend_id.substring(i, i + 1).equals(";")) {
                    Log.d("setFriendList",Friend_id.substring(temp, i));
                    FriendList.add(Friend_id.substring(temp, i));
                    temp = i + 1;
                    count += 1;
                }else if(i==Friend_id.length()-1) {
                    Log.d("setFriendList",Friend_id.substring(temp, Friend_id.length()));
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
    public int getMonthSpace(String date1, String date2) throws ParseException {
        Date d = null;
        Date d1 = null;
        try {
            d =sdf.parse(date1);
            d1=sdf.parse(date2);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();

        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        c.setTime(d1);
        int year1 = c.get(Calendar.YEAR);
        int month1 = c.get(Calendar.MONTH);

        int result;
        if(year==year1) {
            result=month1-month;//两个日期相差几个月，即月份差
        }else {
            result=12*(year1-year)+month1-month;//两个日期相差几个月，即月份差
        }
        return result;
        /*
        int result = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(sdf.parse(date1));
        c2.setTime(sdf.parse(date2));

        result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);

        return result == 0 ? 1 : Math.abs(result);
        */

    }
    /*
    public int getAge() {
        int age=0;
        //傳入日期
        //try {
            if(BirthDay.length()>0) {
                //Date dateBirth = sdf.parse(BirthDay);
                //Date dateNow = sdf.parse(mYear + "/" + (mMonth + 1) + "/" + mDay);
                //long daterange = dateNow.getTime()-dateBirth.getTime();
                //return (int) (daterange / (1000 * 3600 * 24));
                return mYear-Integer.valueOf(BirthDay.substring(0,4));
            }
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //    Log.e("ParseException", e.toString());
        //}
        return age;
    }
    */
    public String getCountDownNum() {
        String strNum = "";
        //設定日期格式
        //傳入日期
        //try {
            if(EntryDate.length()>0 && OriExitDate.length()>0) {
                //Log.e("今日", mYear + "/" + (mMonth + 1) + "/" + mDay);
                long daterange = dateOriExit.getTime() - dateNow.getTime();
                //long alldate = dateEntry.getTime() - dateEntry.getTime();
                long passdate = dateNow.getTime() - dateEntry.getTime();
                //int alldateNum = (int) (alldate / (1000 * 3600 * 24) - Integer.valueOf(Redeem));
                int nowdateNum = (int) (daterange / (1000 * 3600 * 24) - Integer.valueOf(Redeem));
                int haspassdateNum = (int) (passdate / (1000 * 3600 * 24));
                PassDay=String.valueOf(haspassdateNum);
                //Log.e("已過天數", String.valueOf(haspassdateNum));
                strNum = String.valueOf(nowdateNum);
                //Log.e("相減", strNum);
            }
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //    Log.e("ParseException", e.toString());
        //}
        return strNum;
    }
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private List<Map<String, Object>> mData;
        private ArrayList<Integer> mDataSetTypes;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtName, txtFeeling, txtCountDown;
            public CircleImg circleImg;
            public ViewHolder(View v) {
                super(v);
                circleImg = (CircleImg) v.findViewById(R.id.circleImg);
                txtName = (TextView) v.findViewById(R.id.txt_name);
                txtFeeling = (TextView) v.findViewById(R.id.txt_feeling);
                txtCountDown = (TextView) v.findViewById(R.id.txt_countdown);
            }
        }
        public MyAdapter(List<Map<String, Object>> data, ArrayList<Integer> type) {
            mData = data;
            mDataSetTypes = type;
        }
        @Override
        public int getItemViewType(int position) {
            return mDataSetTypes.get(position);
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==1)
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_lastfriend_layout, parent, false));
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_friend_layout, parent, false));
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.txtName.setText(mData.get(position).get("name").toString());
            holder.txtFeeling.setText(mData.get(position).get("feeling").toString());
            holder.txtCountDown.setText(mData.get(position).get("cd").toString());
            Log.d("onBindViewHolder", "onBindViewHolder "+ position);
            //holder.circleImg.setImageDrawable(R.drawable.);
        }
        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
