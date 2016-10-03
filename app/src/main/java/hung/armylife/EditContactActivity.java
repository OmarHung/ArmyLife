package hung.armylife;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class EditContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editName, editPhone, editMessage;
    private int groupPosition;
    private long _id;
    private int Type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
        }
        setContentView(R.layout.activity_edit_contact);
        editName = (EditText) findViewById(R.id.edit_name);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        editMessage = (EditText) findViewById(R.id.edit_message);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_done:
                        String strName=editName.getText().toString(), strPhone=editPhone.getText().toString(), strMessage=editMessage.getText().toString();
                        Intent intent = getIntent();
                        Bundle bundle = new Bundle();
                        bundle.putString("name",strName);
                        bundle.putString("phone",strPhone);
                        bundle.putString("message",strMessage);
                        if(Type==0) {
                            intent.putExtras(bundle);
                            setResult(0, intent); //requestCode需跟A.class的一樣
                        }else if(Type==1) {
                            bundle.putInt("groupPosition",groupPosition);
                            bundle.putLong("_id",_id);
                            intent.putExtras(bundle);
                            setResult(1, intent); //requestCode需跟A.class的一樣
                        }
                        finish();
                        break;
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(-1, intent);
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        Type = bundle.getInt("Type");
        if(Type==0) {
            Log.d("TAG", "Empty");
        }else {
            groupPosition = bundle.getInt("groupPosition");
            _id = bundle.getLong("_id");
            String strName = bundle.getString("name");
            String strPhone = bundle.getString("phone");
            String strMessage = bundle.getString("message");

            editName.setText(strName);
            editPhone.setText(strPhone);
            editMessage.setText(strMessage);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            Intent intent = getIntent();
            setResult(-1, intent);
            finish();
        }
        return true;
    }
}
