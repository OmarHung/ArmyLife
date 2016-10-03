package hung.armylife.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hung.armylife.DBHelper;
import hung.armylife.EditContactActivity;
import hung.armylife.R;

/**
 * Created by Hung on 2016/6/17.
 */
public class ContactFragment extends Fragment {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private Cursor mCursor;
    public static final String TABLE_NAME = "Contact";
    private DBHelper mDBHelper=null;
    private SQLiteDatabase db =null;
    private FloatingActionButton fab;
    public ContactFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
        Log.d("onDestroy","onDestroy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        mDBHelper = new DBHelper(getContext());
        db = mDBHelper.getReadableDatabase();
        mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (getContext().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                getContext().checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(getContext())
                    .setTitle("取得相關權限")
                    .setMessage("您必須同意讓ArmyLife傳送簡訊與撥打電話才能順利使用回報功能")
                    .setPositiveButton("取得權限", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE,Manifest.permission.RECEIVE_SMS}, REQUEST);
                        }
                    }).show();
        }
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditContactActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Type",0);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        // 取得ExpandableListView
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(getContext(), mCursor);//, listDataHeader,listDataChild);// 將列表資料加入至展開列表單
        expListView.setAdapter(listAdapter);

        // 點選標題 展開 監聽事件
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            public void onGroupExpand(int groupPosition) {
                for(int i=0; i<listAdapter.getGroupCount(); i++) {
                    if(groupPosition!=i)
                        expListView.collapseGroup(i);
                }
                expListView.setSelectedGroup(groupPosition);
            }
        });
        return view;
    }
    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private Cursor cursor;
        private ImageButton imageButtonLine, imageButtonMessage;
        public ExpandableListAdapter(Context context, Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
        }
        /* -------------------- 內容 -------------------- */
        public Object getChild(int groupPosition, int childPosititon) {
            cursor.moveToPosition(groupPosition);
            return cursor.getString(3);
        }
        public int getChildrenCount(int groupPosition) {
            return 1;
        }
        public long getChildId(int groupPosition, int childPosition) {
            //Log.d("getGroupId","groupPosition = " + groupPosition);
            cursor.moveToPosition(groupPosition);
            return cursor.getLong(0);
        }

        /* 內容View */
        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Log.d("getChildView","groupPosition = " + groupPosition);
            String childText = getMessage(groupPosition); // 取得內容
            /* 設置內容layout */
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandlistview_child_layout, null);
            }
            /* 設置內容 */
            final EditText editText = (EditText) convertView.findViewById(R.id.edt_message);
            editText.setTag(getChildId(groupPosition,0));
            editText.setText(childText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    //Log.d("TAG","getTag = "+editText.getTag()+" Text = "+s.toString());
                    if(editText.getTag().equals(getChildId(groupPosition,0))) {
                        //Log.d("getChildView","groupPosition = " + groupPosition);
                        //Log.d("TAG","ID = "+getChildId(groupPosition,0) +" Name = "+getName(groupPosition)+ "  Phone = "+getPhone(groupPosition)+ "  Message = "+s.toString());
                        //mDBHelper.updateMessage(getChildId(groupPosition,0), s.toString());
                        SQLiteDatabase db = mDBHelper.getWritableDatabase();
                        boolean b = mDBHelper.update(db, getChildId(groupPosition, 0), getName(groupPosition), getPhone(groupPosition), s.toString());
                        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);


                        listAdapter.notifyDataSetChanged();
                        editText.requestFocus();
                        int pos = editText.getText().length();
                        editText.setSelection(pos);
                        //expListView.setAdapter(listAdapter);
                        //Log.d("TAG","boolean = "+b);
                    }

                }
            });
            return convertView;
        }

        /* -------------------- 標題 -------------------- */
        public Object getGroup(int groupPosition) {
            cursor.moveToPosition(groupPosition);
            return cursor.getString(1);
        }

        public int getGroupCount() {
            return cursor.getCount();
        }

        public long getGroupId(int groupPosition) {
            //Log.d("getGroupId","groupPosition = " + groupPosition);
            cursor.moveToPosition(groupPosition);
            return cursor.getLong(0);
        }

        public int getGroupViewCount() {
            return cursor.getCount();
        }

        public long getGroupViewId(int groupPosition) {
            return groupPosition;
        }

        /* 標題View */
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final String strName=getName(groupPosition), strPhone=getPhone(groupPosition), strMessage=getMessage(groupPosition);
            //Log.d("TAG","getGroupView "+groupPosition);
            //String headerTitle = (String) getGroup(groupPosition); // 取得標題
            /* 設置標題layout */
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandlistview_father_layout, null);
            }
            /* 設置標題 */
            TextView txtName = (TextView) convertView.findViewById(R.id.lblListHeader);
            txtName.setText(strName);

            imageButtonLine = (ImageButton) convertView.findViewById(R.id.img_button_line);
            imageButtonLine.setTag(getGroupId(groupPosition));
            imageButtonLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkLineInstalled()) {
                        listAdapter.notifyDataSetChanged();
                        //PO文字
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName(PACKAGE_NAME, CLASS_NAME);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, strMessage);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(),"在你的手機裡找不到Line",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imageButtonMessage = (ImageButton) convertView.findViewById(R.id.img_button_message);
            imageButtonMessage.setTag(getGroupId(groupPosition));
            imageButtonMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    listAdapter.notifyDataSetChanged();
                    new AlertDialog.Builder(getContext())
                            .setTitle("發送訊息給"+strName)
                            .setMessage(strMessage)
                            .setNegativeButton("直接發送", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        String SENT = "sent";
                                        String DELIVERED = "delivered";

                                        Intent sentIntent = new Intent(SENT);
                                        PendingIntent sentPI = PendingIntent.getBroadcast(getContext(), 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                        Intent deliveryIntent = new Intent(DELIVERED);
                                        PendingIntent deliverPI = PendingIntent.getBroadcast(getContext(), 0, deliveryIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        /* Register for SMS send action */
                                        getContext().registerReceiver(new BroadcastReceiver() {
                                            @Override
                                            public void onReceive(Context context, Intent intent) {
                                                String result = "傳送失敗！";
                                                switch (getResultCode()) {
                                                    case Activity.RESULT_OK:
                                                        result = "傳送中...";
                                                        break;
                                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                                        result = "Transmission failed";
                                                        break;
                                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                                        result = "Radio off";
                                                        break;
                                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                                        result = "No PDU defined";
                                                        break;
                                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                                        result = "沒有服務";
                                                        break;
                                                }
                                                Log.d("result","result = "+result+"getResultCode() = "+getResultCode());
                                                Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                                            }

                                        }, new IntentFilter(SENT));
                                        /* Register for Delivery event */
                                        getContext().registerReceiver(new BroadcastReceiver() {
                                            @Override
                                            public void onReceive(Context context, Intent intent) {
                                                Toast.makeText(getContext(), "發送成功！", Toast.LENGTH_LONG).show();
                                            }

                                        }, new IntentFilter(DELIVERED));


                                        SmsManager smsManager = SmsManager.getDefault();
                                        try {
                                            smsManager.sendTextMessage(
                                                    strPhone, //對方手機號碼
                                                    null, //自己手機號碼
                                                    strMessage,//訊息內容 listDataChild.get(listDataHeader.get((int)v.getTag())).get(0),
                                                    sentPI, //傳送是否成功的回應　
                                                    deliverPI); //接收是否成功的回應　
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        ;
                                    }catch (Exception ex) {
                                        Toast.makeText(getContext(),ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                                        ex.printStackTrace();
                                    }
                                }
                            })
                            .setNeutralButton("發送前編輯", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.setData(Uri.parse("smsto:"));
                                    smsIntent.setType("vnd.android-dir/mms-sms");
                                    smsIntent.putExtra("address"  , new String (strPhone));
                                    smsIntent.putExtra("sms_body"  , strMessage);
                                    try {
                                        startActivity(smsIntent);
                                        Log.i("Finished sending SMS...", "");
                                    } catch (android.content.ActivityNotFoundException ex) {
                                    }
                                    */
                                    Uri uri = Uri.parse("smsto:"+strPhone);
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                                    intent.putExtra("sms_body", strMessage);
                                    startActivity(intent);

                                }
                            })
                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "關閉", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });
            ImageButton imageButtonPhone = (ImageButton) convertView.findViewById(R.id.img_button_phone);
            imageButtonPhone.setTag(getGroupId(groupPosition));
            imageButtonPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listAdapter.notifyDataSetChanged();
                    final String CALL = "android.intent.action.CALL";
                    Intent call = new Intent(CALL, Uri.parse("tel:" + strPhone));
                    startActivity(call);
                }
            });
            final ImageButton imageButtonEdit = (ImageButton) convertView.findViewById(R.id.img_button_edit);
            imageButtonEdit.setTag(getGroupId(groupPosition));
            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listAdapter.notifyDataSetChanged();
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    Intent intent = new Intent(getActivity(), EditContactActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("Type",1);
                                    bundle.putInt("groupPosition",groupPosition);
                                    bundle.putLong("_id",getGroupId(groupPosition));
                                    bundle.putString("name",strName);
                                    bundle.putString("phone",strPhone);
                                    bundle.putString("message",strMessage);
                                    intent.putExtras(bundle);
                                    startActivityForResult(intent, 1);
                                    break;
                                case R.id.delete:
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("刪除")
                                            .setMessage("是否刪除此回報？")
                                            .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if(imageButtonEdit.getTag().equals(getGroupId(groupPosition))) {
                                                        SQLiteDatabase db = mDBHelper.getWritableDatabase();
                                                        boolean b = mDBHelper.delete(db, getGroupId(groupPosition));
                                                        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                                                        listAdapter.notifyDataSetChanged();
                                                        expListView.setAdapter(listAdapter);
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    //startActivity(new Intent(getActivity(), EditContactActivity.class));
                }
            });

            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv);
            if(getChildrenCount(groupPosition) == 0){//該組下沒有子項
                imageView.setVisibility(View.GONE);
            }else {
                if (isExpanded) {//展開狀態
                    imageView.setImageResource(R.drawable.fs_open);
                } else {//收起狀態
                    imageView.setImageResource(R.drawable.fs_close);
                }
            }
            return convertView;
        }
        public String getName(int position) {
            cursor.moveToPosition(position);
            return cursor.getString(1);
        }
        public String getPhone(int position) {
            cursor.moveToPosition(position);
            return cursor.getString(2);
        }
        public String getMessage(int position) {
            cursor.moveToPosition(position);
            return cursor.getString(3);
        }
        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    //判斷是否安裝了line
    final String PACKAGE_NAME = "jp.naver.line.android";
    final String CLASS_NAME = "jp.naver.line.android.activity.selectchat.SelectChatActivity";
    private boolean checkLineInstalled(){
        PackageManager pm = getContext().getPackageManager();
        List<ApplicationInfo> m_appList = pm.getInstalledApplications(0);
        boolean lineInstallFlag = false;
        for (ApplicationInfo ai : m_appList) {
            if(ai.packageName.equals(PACKAGE_NAME)){
                lineInstallFlag = true;
                break;
            }
        }
        return lineInstallFlag;
    }
    private static final int REQUEST = 0;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("permissions","permissions = "+permissions.length);
        switch (requestCode) {
            case REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 取得權限
                    Log.d("permissions","Get");
                } else {
                    // 未取得權限
                }
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode>=0) {
            String strName = data.getExtras().getString("name");
            String strPhone = data.getExtras().getString("phone");
            String strMessage = data.getExtras().getString("message");
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            switch(resultCode) {//resultCode是剛剛妳A切換到B時設的resultCode
                case 0://新增
                    mDBHelper.append(db, strName, strPhone, strMessage);
                    mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                    listAdapter = new ExpandableListAdapter(getContext(), mCursor);
                    listAdapter.notifyDataSetChanged();
                    expListView.setAdapter(listAdapter);
                    expListView.expandGroup(expListView.getCount() - 1);
                    break;
                case 1://編輯
                    int groupPosition = data.getExtras().getInt("groupPosition");
                    long _id = data.getExtras().getLong("_id");
                    boolean b = mDBHelper.update(db, _id, strName, strPhone, strMessage);
                    mCursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                    listAdapter = new ExpandableListAdapter(getContext(), mCursor);
                    listAdapter.notifyDataSetChanged();
                    expListView.setAdapter(listAdapter);
                    expListView.expandGroup(groupPosition);
                    break;
            }
        }
    }
}