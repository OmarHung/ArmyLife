package hung.armylife;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hung.armylife.fragment.ContactFragment;
import hung.armylife.fragment.CountDownFragment;
import hung.armylife.fragment.FriendFragment;
import hung.armylife.fragment.NewsFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //private ViewPager mViewPager;
    private LinearLayout mTabHome;
    private LinearLayout mTabFriend;
    private LinearLayout mTabNews;
    private LinearLayout mTabKnowledge;
    private ImageButton mHomeImg;
    private ImageButton mFriendImg;
    private ImageButton mNewsImg;
    private ImageButton mKnowledgeImg;
    private TextView txtHome, txtFriend, txtNews, txtKnowledge;
    private CountDownFragment countDownFragment;
    private FriendFragment friendFragment;
    private NewsFragment newsFragment;
    private FragmentManager mFragmentMgr;
    private ArrayList<Fragment> fragmentArrayList;
    private Fragment mCurrentFrgment;
    private int currentIndex = 0;
    private String data="Date", SP_Feeling="Feeling", SP_EntryDate="EntryDate", SP_Period="Period", SP_Redeem="Redeem", SP_SoldierCategory="SoldierCategory", SP_Sequence="Sequence", SP_Unit="Unit", SP_First="FIRST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
        }
        setContentView(R.layout.activity_main);
        SharedPreferences profile;
        profile = getSharedPreferences(data,0);
        int first = profile.getInt(SP_First,0);
        if(first==0)
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        initView();
        //initViewPage();
        initEvent();
        initFragment();
    }
    private void initFragment() {
        fragmentArrayList = new ArrayList<Fragment>(3);
        fragmentArrayList.add(new CountDownFragment());
        fragmentArrayList.add(new ContactFragment());
        fragmentArrayList.add(new NewsFragment());
        fragmentArrayList.add(new FriendFragment());

        mTabHome.setSelected(true);
        changeTab(0);
        resetImg();
        mHomeImg.setImageResource(R.drawable.ic_event_black_48dp);
        txtHome.setTextColor(Color.BLACK);
    }
    private void changeTab(int index) {
        currentIndex = index;
        mTabHome.setSelected(index == 0);
        mTabFriend.setSelected(index == 1);
        mTabNews.setSelected(index == 2);
        mTabKnowledge.setSelected(index == 3);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFrgment) {
            ft.hide(mCurrentFrgment);
        }
        //先根据Tag从FragmentTransaction事物获取之前添加的Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentArrayList.get(currentIndex).getClass().getName());

        if (null == fragment) {
            //如fragment为空，则之前未添加此Fragment。便从集合中取出
            fragment = fragmentArrayList.get(index);
        }
        mCurrentFrgment = fragment;

        //判断此Fragment是否已经添加到FragmentTransaction事物中
        if (!fragment.isAdded()) {
            ft.add(R.id.frameLayout, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }
    private void initEvent() {
        mTabHome.setOnClickListener(this);
        mTabHome.setTag(0);
        mTabFriend.setOnClickListener(this);
        mTabFriend.setTag(1);
        mTabNews.setOnClickListener(this);
        mTabNews.setTag(2);
        mTabKnowledge.setOnClickListener(this);
        mTabKnowledge.setTag(3);
        /*
        int currentItem = mViewPager.getCurrentItem();
        switch (currentItem) {
            case 0:
                resetImg();
                mHomeImg.setImageResource(R.drawable.ic_event_black_48dp);
                break;
            case 1:
                resetImg();
                mFriendImg.setImageResource(R.drawable.ic_people_outline_black_48dp);
                break;
            case 2:
                resetImg();
                mNewsImg.setImageResource(R.drawable.ic_news_black_48dp);
                break;
            case 3:
                resetImg();
                mKnowledgeImg.setImageResource(R.drawable.ic_help_outline_black_48dp);
                break;
            default:
                break;
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        resetImg();
                        mHomeImg.setImageResource(R.drawable.ic_event_black_48dp);
                        break;
                    case 1:
                        resetImg();
                        mFriendImg.setImageResource(R.drawable.ic_people_outline_black_48dp);
                        break;
                    case 2:
                        resetImg();
                        mNewsImg.setImageResource(R.drawable.ic_news_black_48dp);
                        break;
                    case 3:
                        resetImg();
                        mKnowledgeImg.setImageResource(R.drawable.ic_help_outline_black_48dp);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        */
    }
    private void initView() {
        //mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mTabHome = (LinearLayout) findViewById(R.id.tab_Home);
        mTabFriend = (LinearLayout) findViewById(R.id.tab_Friend);
        mTabNews = (LinearLayout) findViewById(R.id.tab_News);
        mTabKnowledge = (LinearLayout) findViewById(R.id.tab_Knowledge);

        mHomeImg = (ImageButton) findViewById(R.id.imgButtom_Home);
        mFriendImg = (ImageButton) findViewById(R.id.imgButtom_Friend);
        mNewsImg = (ImageButton) findViewById(R.id.imgButtom_News);
        mKnowledgeImg = (ImageButton) findViewById(R.id.imgButtom_Knowledge);

        txtHome = (TextView) findViewById(R.id.txt_Home);
        txtFriend = (TextView) findViewById(R.id.txt_Friend);
        txtNews = (TextView) findViewById(R.id.txt_News);
        txtKnowledge = (TextView) findViewById(R.id.txt_Knowledge);
    }
    /*
    private void initViewPage() {
        List<Fragment> fragments = getFragments();
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(CountDownFragment.newInstance());
        fragments.add(FriendFragment.newInstance());
        fragments.add(NewsFragment.newInstance());
        fragments.add(FriendFragment.newInstance());
        return fragments;
    }
    class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mListViews;
        public PagerAdapter(FragmentManager fm, List<Fragment> mListViews) {
            super(fm);
            this.mListViews = mListViews;
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    fragment = new CountDownFragment();
                    break;
                case 1: // Fragment # 0 - This will show FirstFragment
                    fragment = new FriendFragment();
                    break;
                case 2: // Fragment # 0 - This will show FirstFragment
                    fragment = new NewsFragment();
                    break;
                case 3: // Fragment # 0 - This will show FirstFragment
                    fragment = new FriendFragment();
                    break;
            }
            return fragment;
        }
        @Override
        public int getCount() {
            return mListViews.size();
        }
    }
    */
    @Override
    public void onClick(View arg0) {
        changeTab((Integer) arg0.getTag());
        switch (arg0.getId()) {
            case R.id.tab_Home:
                //mViewPager.setCurrentItem(0);
                resetImg();
                mHomeImg.setImageResource(R.drawable.ic_event_black_48dp);
                txtHome.setTextColor(Color.BLACK);
                break;
            case R.id.tab_Friend:
                //mViewPager.setCurrentItem(1);
                resetImg();
                mFriendImg.setImageResource(R.drawable.ic_people_outline_black_48dp);
                txtFriend.setTextColor(Color.BLACK);
                break;
            case R.id.tab_News:
                //mViewPager.setCurrentItem(2);
                resetImg();
                mNewsImg.setImageResource(R.drawable.ic_news_black_48dp);
                txtNews.setTextColor(Color.BLACK);
                break;
            case R.id.tab_Knowledge:
                //mViewPager.setCurrentItem(3);
                resetImg();
                mKnowledgeImg.setImageResource(R.drawable.ic_help_outline_black_48dp);
                txtKnowledge.setTextColor(Color.BLACK);
                break;
            default:
                break;
        }
    }
    /**
     * 把所有图片变暗
     */
    private void resetImg() {
        mHomeImg.setImageResource(R.drawable.ic_event_gray_48dp);
        mFriendImg.setImageResource(R.drawable.ic_people_outline_gray_48dp);
        mNewsImg.setImageResource(R.drawable.ic_news_gray_48dp);
        mKnowledgeImg.setImageResource(R.drawable.ic_help_outline_gray_48dp);

        txtHome.setTextColor(Color.GRAY);
        txtFriend.setTextColor(Color.GRAY);
        txtNews.setTextColor(Color.GRAY);
        txtKnowledge.setTextColor(Color.GRAY);
        //mFoodImg.setImageResource(R.drawable.food_w);
        //mMapImg.setImageResource(R.drawable.map_w);
        //mFriendImg.setImageResource(R.drawable.friend_w);
        //mMoreImg.setImageResource(R.drawable.more_w);
    }
}
