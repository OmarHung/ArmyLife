package hung.armylife.custom_dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hung.armylife.R;

/**
 * Created by Hung on 2016/7/13.
 */
public class FlowDialog extends Dialog{
    public Activity activity;
    //public Dialog dialog;
    public ViewPager viewPager;
    public Button left, right;
    public TextView title;
    public int[] viewID;
    public String[] strTitle;
    public int nowPosition;

    public FlowDialog(Activity activity, int[] viewID, String[] strTitle) {
        super(activity);
        this.activity = activity;
        this.viewID = viewID;
        this.strTitle = strTitle;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        viewPager = (ViewPager) findViewById(R.id.vp_content);
        final LayoutInflater mInflater = getLayoutInflater().from(activity);
        ArrayList<View> viewList = new ArrayList<View>();
        for(int i=0; i<viewID.length; i++) {
            View v = mInflater.inflate(viewID[i], null);
            viewList.add(v);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(viewList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG",String.valueOf(position));
                title.setText(strTitle[position]);
                nowPosition = position;
                right.setText("NEXT");
                if(nowPosition==viewID.length-1){
                    right.setText("DONE");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        title = (TextView) findViewById(R.id.txt_title);
        title.setText(strTitle[0]);
        left = (Button) findViewById(R.id.btn_left);
        right = (Button) findViewById(R.id.btn_right);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nowPosition>0) {
                    viewPager.setCurrentItem(nowPosition-1);
                    right.setText("NEXT");
                }
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(right.getText().toString().equals("DONE"))
                    dismiss();
                if(nowPosition<viewID.length-1) {
                    viewPager.setCurrentItem(nowPosition+1);
                }
                if(nowPosition==viewID.length-1){
                    right.setText("DONE");
                }
            }
        });
    }
    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mListViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
    }
}
