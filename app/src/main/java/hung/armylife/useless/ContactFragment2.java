package hung.armylife.useless;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hung.armylife.DBHelper;
import hung.armylife.EditContactActivity;
import hung.armylife.R;

/**
 * Created by Hung on 2016/6/17.
 */
public class ContactFragment2 extends Fragment {
    DBExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private Cursor mCursor;
    public static final String TABLE_NAME = "Contact";
    private DBHelper mDBHelper=null;
    private FloatingActionButton fab;
    List<String> listDataHeader; //標題
    //HashMap<String, List<String>> listDataChild; //內容
    List<String> itemDataPhone;
    List<String> itemDataMessege;
    public ContactFragment2() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ContactFragment2 newInstance() {
        ContactFragment2 fragment = new ContactFragment2();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        mDBHelper = new DBHelper(getContext());
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
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
                startActivity(new Intent(getActivity(), EditContactActivity.class));
            }
        });

        /*
        listDataHeader = new ArrayList<String>(); // 標題
        listDataChild = new HashMap<String, List<String>>(); // 內容

        listDataHeader.add("連長");
        listDataHeader.add("副連長");
        listDataHeader.add("輔導長");
        List<String> first = new ArrayList<String>();
        first.add("報告連長，我已到家");

        List<String> second = new ArrayList<String>();
        second.add("報告副連長，我已到家");

        List<String> end = new ArrayList<String>();
        end.add("報告輔導長，我已到家");

        listDataChild.put(listDataHeader.get(0), first); // 標題, 內容
        listDataChild.put(listDataHeader.get(1), second);
        listDataChild.put(listDataHeader.get(2), end);
        */
        listDataHeader = new ArrayList<String>(); // 標題
        //listDataChild = new HashMap<String, List<String>>(); // 內容
        itemDataPhone = new ArrayList<String>();
        itemDataMessege = new ArrayList<String>();

        listDataHeader.add("連長");
        listDataHeader.add("副連長");
        listDataHeader.add("輔導長");

        itemDataPhone.add("1");
        itemDataPhone.add("2");
        itemDataPhone.add("3");

        itemDataMessege.add("報告連長，我已到家");
        itemDataMessege.add("報告副連長，我已到家");
        itemDataMessege.add("報告連長，我已到家");

        // 取得ExpandableListView
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        listAdapter = new DBExpandableListAdapter(mCursor, getContext());//, listDataHeader,listDataChild);// 將列表資料加入至展開列表單
        //listAdapter = new ExpandableListAdapter(getContext());//, listDataHeader,listDataChild);// 將列表資料加入至展開列表單
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
    public class DBExpandableListAdapter extends CursorTreeAdapter {
        private Cursor mCursor;
        private Context mContext;
        private LayoutInflater mInflater;
        private ImageButton imageButtonLine, imageButtonMessage;
        public DBExpandableListAdapter(Cursor cursor, Context context) {
            super(cursor, context);
            mCursor = cursor;
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public String getName(int groupPosition) {
            mCursor.moveToPosition(groupPosition);
            return mCursor.getString(1);
        }
        public String getPhone(int groupPosition) {
            mCursor.moveToPosition(groupPosition);
            return mCursor.getString(2);
        }
        public String getMessage(int groupPosition) {
            mCursor.moveToPosition(groupPosition);
            return mCursor.getString(3);
        }
        /* 內容View */
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = getMessage(groupPosition); // 取得內容
            //Log.d("TAG","getChildView");
            /* 設置內容layout */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.expandlistview_child_layout, null);
            }
            /* 設置內容 */
            final EditText editText = (EditText) convertView.findViewById(R.id.edt_message);
            editText.setTag(groupPosition);
            editText.setText(childText);
            //Log.d("Text","groupPosition = "+groupPosition+"childText="+childText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemDataMessege.set((int)editText.getTag(),s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            return convertView;
        }
        /* 標題View */
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final String headerTitle = getName(groupPosition); // 取得標題
            final String messege = getMessage(groupPosition); // 取得標題
            /* 設置標題layout */
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.expandlistview_father_layout, null);
            }
            /* 設置標題 */
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            lblListHeader.setText(headerTitle);

            imageButtonLine = (ImageButton) convertView.findViewById(R.id.img_button_line);
            imageButtonLine.setTag(groupPosition);
            imageButtonLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkLineInstalled()) {
                        //PO文字
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName(PACKAGE_NAME, CLASS_NAME);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, messege);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(),"在你的手機裡找不到Line",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imageButtonMessage = (ImageButton) convertView.findViewById(R.id.img_button_message);
            imageButtonMessage.setTag(groupPosition);
            imageButtonMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("發送訊息給"+headerTitle)
                            .setMessage(messege)
                            .setPositiveButton("傳訊息", new DialogInterface.OnClickListener() {
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
                                                    "0930961318", //對方手機號碼
                                                    null, //自己手機號碼
                                                    messege,//訊息內容 listDataChild.get(listDataHeader.get((int)v.getTag())).get(0),
                                                    sentPI, //傳送是否成功的回應　
                                                    deliverPI); //接收是否成功的回應　
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        ;
                                    }catch (Exception ex) {
                                        Toast.makeText(getContext(),
                                                ex.getMessage().toString(), Toast.LENGTH_LONG)
                                                .show();
                                        ex.printStackTrace();
                                    }

                                    /*
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.setData(Uri.parse("smsto:"));
                                    smsIntent.setType("vnd.android-dir/mms-sms");
                                    smsIntent.putExtra("address"  , new String ("0930961318"));
                                    smsIntent.putExtra("sms_body"  , listDataChild.get(listDataHeader.get((int)imageButtonMessage.getTag())).get(0));
                                    try {
                                        startActivity(smsIntent);
                                        Log.i("Finished sending SMS...", "");
                                    } catch (android.content.ActivityNotFoundException ex) {
                                    }
                                    */

                                    //Toast.makeText(getContext(), "傳送中...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "關閉", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });
            ImageButton imageButtonPhone = (ImageButton) convertView.findViewById(R.id.img_button_phone);
            imageButtonPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String CALL = "android.intent.action.CALL";
                    Intent call = new Intent(CALL, Uri.parse("tel:" + "0930961318"));
                    startActivity(call);
                }
            });
            ImageButton imageButtonEdit = (ImageButton) convertView.findViewById(R.id.img_button_edit);
            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    startActivity(new Intent(getActivity(), EditContactActivity.class));
                                    break;
                                case R.id.delete:
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("刪除")
                                            .setMessage("是否刪除此回報？")
                                            .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
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
            if (isExpanded) {//展開狀態
                imageView.setImageResource(R.drawable.fs_open);
            } else {//收起狀態
                imageView.setImageResource(R.drawable.fs_close);
            }
            return convertView;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            return null;
        }
        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            return null;
        }
        @Override
        protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
            return null;
        }
        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {

        }
        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {

        }
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private ImageButton imageButtonLine, imageButtonMessage;
        //private List<String> listDataHeader; // 標題
        //private HashMap<String, List<String>> listDataChild; // 內容
        public ExpandableListAdapter(Context context) {//, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
            this.context = context;
            //this.listDataHeader = listDataHeader;
            //this.listDataChild = listChildData;
        }
        /* -------------------- 內容 -------------------- */
        public Object getChild(int groupPosition, int childPosititon) {
            //return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
            //return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosititon);
            return itemDataMessege.get(groupPosition);
        }

        public int getChildrenCount(int groupPosition) {
            //return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
            return 1;
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /* 內容View */
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = (String) getChild(groupPosition, childPosition); // 取得內容
            //Log.d("TAG","getChildView");
            /* 設置內容layout */
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandlistview_child_layout, null);
            }
            /* 設置內容 */
            final EditText editText = (EditText) convertView.findViewById(R.id.edt_message);
            editText.setTag(groupPosition);
            editText.setText(childText);
            //Log.d("Text","groupPosition = "+groupPosition+"childText="+childText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemDataMessege.set((int)editText.getTag(),s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            return convertView;
        }

        /* -------------------- 標題 -------------------- */
        public Object getGroup(int groupPosition) {
            //return this.listDataHeader.get(groupPosition);
            return listDataHeader.get(groupPosition);
        }

        public int getGroupCount() {
            //return this.listDataHeader.size();
            return listDataHeader.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

    /* -------------------- 標題圖示 -------------------- */
    /*
    public Object getGroupView(int groupPosition) {
        return Integer.toString(this.listIv[groupPosition]);
    }
    */

        public int getGroupViewCount() {
            //return this.listDataHeader.size();
            return listDataHeader.size();
        }

        public long getGroupViewId(int groupPosition) {
            return groupPosition;
        }

        /* 標題View */
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            //Log.d("TAG","getGroupView");
            //String headerIv = (String) getGroupView(groupPosition); // 取得圖示
            String headerTitle = (String) getGroup(groupPosition); // 取得標題
            /* 設置標題layout */
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandlistview_father_layout, null);
            }

            /* 設置圖示 */
            //ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
            //iv.setImageResource(Integer.parseInt(headerIv));
            /* 設置標題 */
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            lblListHeader.setText(headerTitle);

            imageButtonLine = (ImageButton) convertView.findViewById(R.id.img_button_line);
            imageButtonLine.setTag(groupPosition);
            imageButtonLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkLineInstalled()) {
                        //PO文字
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setClassName(PACKAGE_NAME, CLASS_NAME);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getMessage((int)v.getTag()));
                        startActivity(intent);
                    }else {
                        Toast.makeText(getContext(),"在你的手機裡找不到Line",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            imageButtonMessage = (ImageButton) convertView.findViewById(R.id.img_button_message);
            imageButtonMessage.setTag(groupPosition);
            imageButtonMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final String strMessage=itemDataMessege.get((int)v.getTag());
                    new AlertDialog.Builder(getContext())
                            .setTitle("發送訊息給"+listDataHeader.get((int)v.getTag()))
                            .setMessage(strMessage)
                            .setPositiveButton("傳訊息", new DialogInterface.OnClickListener() {
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
                                                    "0930961318", //對方手機號碼
                                                    null, //自己手機號碼
                                                    strMessage,//訊息內容 listDataChild.get(listDataHeader.get((int)v.getTag())).get(0),
                                                    sentPI, //傳送是否成功的回應　
                                                    deliverPI); //接收是否成功的回應　
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        ;
                                    }catch (Exception ex) {
                                            Toast.makeText(getContext(),
                                                    ex.getMessage().toString(), Toast.LENGTH_LONG)
                                                    .show();
                                            ex.printStackTrace();
                                        }

                                    /*
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.setData(Uri.parse("smsto:"));
                                    smsIntent.setType("vnd.android-dir/mms-sms");
                                    smsIntent.putExtra("address"  , new String ("0930961318"));
                                    smsIntent.putExtra("sms_body"  , listDataChild.get(listDataHeader.get((int)imageButtonMessage.getTag())).get(0));
                                    try {
                                        startActivity(smsIntent);
                                        Log.i("Finished sending SMS...", "");
                                    } catch (android.content.ActivityNotFoundException ex) {
                                    }
                                    */

                                    //Toast.makeText(getContext(), "傳送中...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getContext(), "關閉", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });
            ImageButton imageButtonPhone = (ImageButton) convertView.findViewById(R.id.img_button_phone);
            imageButtonPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String CALL = "android.intent.action.CALL";
                    Intent call = new Intent(CALL, Uri.parse("tel:" + "0930961318"));
                    startActivity(call);
                }
            });
            ImageButton imageButtonEdit = (ImageButton) convertView.findViewById(R.id.img_button_edit);
            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    startActivity(new Intent(getActivity(), EditContactActivity.class));
                                    break;
                                case R.id.delete:
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("刪除")
                                            .setMessage("是否刪除此回報？")
                                            .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
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
    public String getMessage(int groupPosition) {
        return itemDataMessege.get(groupPosition);
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
}