package hung.armylife;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;

import hung.armylife.widget.CircleImg;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private ImageButton imageButton;
    private CircleImg circleImg;
    private LinearLayout linearLayout_EntryDate, linearLayout_Period, linearLayout_Redeem, linearLayout_Sequence;
    private TextView txtFeeling, txtEntryDate, txtPeriod, txtRedeem, txtSequence;
    private static final int REQUESTCODE_PICK = 0;
    private static final int REQUESTCODE_TAKE = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private static final String IMAGE_FILE_NAME = "Image.jpg";
    private int mYear, mMonth, mDay;
    final String[] Item_Period = {"4個月", "4個月5天", "1年", "1年15天", "3年", "4年"};
    AlertDialog dialog;
    private SharedPreferences profile;
    private String data="Date", SP_Feeling="Feeling", SP_EntryDate="EntryDate", SP_Period="Period", SP_Redeem="Redeem", SP_Sequence="Sequence", SP_First="FIRST", SP_Photo="Photo";
    final Calendar c = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
        }
        setContentView(R.layout.activity_profile);
        profile = getSharedPreferences(data, 0);
        int first = profile.getInt(SP_First, 0);
        if(first==0)
            profile.edit().putInt(SP_First, 1).commit();
        // 設定初始日期
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        InitWidget();
        readData();
    }
    private void InitWidget() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(this);
        circleImg = (CircleImg) findViewById(R.id.circleImg);
        circleImg.setOnClickListener(this);
        linearLayout_EntryDate = (LinearLayout) findViewById(R.id.linearLayout_EntryDate);
        linearLayout_EntryDate.setOnClickListener(this);
        linearLayout_Period = (LinearLayout) findViewById(R.id.linearLayout_Period);
        linearLayout_Period.setOnClickListener(this);
        linearLayout_Redeem = (LinearLayout) findViewById(R.id.linearLayout_Redeem);
        linearLayout_Redeem.setOnClickListener(this);
        linearLayout_Sequence = (LinearLayout) findViewById(R.id.linearLayout_Sequence);
        linearLayout_Sequence.setOnClickListener(this);
        txtFeeling = (TextView) findViewById(R.id.txt_Feeling);
        txtEntryDate  = (TextView) findViewById(R.id.txt_EntryDate);
        txtPeriod = (TextView) findViewById(R.id.txt_Period);
        txtRedeem = (TextView) findViewById(R.id.txt_Redeem);
        txtSequence = (TextView) findViewById(R.id.txt_Sequence);
    }
    @Override
    public void onClick(View v) {
        final View item_Text = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_edit_text, null);
        final View item_Num = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_edit_num, null);
        switch (v.getId()) {
            case R.id.imageButton:
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("輸入你的心情")
                        .setView(item_Text)
                        .setPositiveButton("發佈", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item_Text.findViewById(R.id.editText);
                                String str = editText.getText().toString();
                                txtFeeling.setText(str);
                                profile.edit().putString(SP_Feeling, str).commit();
                            }
                        })
                        .show();
                break;
            case R.id.circleImg:
                final String[] Items = {"拍照","從相簿選擇"};
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("編輯頭像")
                        .setItems(Items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                                        break;
                                    case 1:
                                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                        break;
                                }
                            }
                        })
                        .show();
                break;
            case R.id.linearLayout_EntryDate:
                // 跳出日期選擇器
                new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // 完成選擇，顯示日期
                                txtEntryDate.setText(year + "/" + (monthOfYear + 1) + "/"+ dayOfMonth);
                                profile.edit().putString(SP_EntryDate, year + "/" + (monthOfYear + 1) + "/"+ dayOfMonth).commit();
                            }
                        }, mYear, mMonth, mDay).show();
                break;
            case R.id.linearLayout_Period:
                dialog = new AlertDialog.Builder(this)
                        .setTitle("役期")
                        .setSingleChoiceItems(Item_Period, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                txtPeriod.setText(Item_Period[which]);
                                profile.edit().putInt(SP_Period, which).commit();
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.linearLayout_Redeem:
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("折抵天數")
                        .setView(item_Num)
                        .setPositiveButton("送出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item_Num.findViewById(R.id.editText);
                                String str = editText.getText().toString();
                                txtRedeem.setText(str);
                                profile.edit().putString(SP_Redeem, str).commit();
                            }
                        }).show();
                break;
            case R.id.linearLayout_Sequence:
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("梯次")
                        .setView(item_Num)
                        .setPositiveButton("送出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item_Num.findViewById(R.id.editText);
                                String str = editText.getText().toString();
                                profile.edit().putString(SP_Sequence, str).commit();
                                txtSequence.setText(str);
                            }
                        }).show();
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case REQUESTCODE_TAKE:
                File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            circleImg.setImageDrawable(drawable);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream );

            byte bytes[] = stream.toByteArray();
            // Android 2.2以上才有內建Base64，其他要自已找Libary或是用Blob存入SQLite
            // 把byte變成base64
            String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
            profile.edit().putString(SP_Photo,base64).commit();
        }
    }
    public void readData(){
        String photo = profile.getString(SP_Photo,"");
        //Log.d("Photo",photo);
        if(!photo.equals("")) {
            byte bytes[] = Base64.decode(photo, Base64.DEFAULT);
            Drawable drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            circleImg.setImageDrawable(drawable);
        }
        txtFeeling.setText(profile.getString(SP_Feeling,""));
        //入伍日期
        txtEntryDate.setText(profile.getString(SP_EntryDate, "點擊編輯"));
        //役期
        if(profile.getInt(SP_Period,-1)==-1)
            txtPeriod.setText("點擊編輯");
        else
            txtPeriod.setText(Item_Period[profile.getInt(SP_Period, -1)]);
        //折抵天數
        txtRedeem.setText(profile.getString(SP_Redeem, "點擊編輯"));
        //梯次
        txtSequence.setText(profile.getString(SP_Sequence,"點擊編輯"));
    }
}
