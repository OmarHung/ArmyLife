package hung.armylife.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hung.armylife.R;

public class NewsFragment extends Fragment {
    //private RecyclerView NewsList=null;
    private ListView NewsList=null;
    private TextView txt_Disconnect;
    private ImageButton img_Button_Retry;
    private int nowPage=1;
    private int totalPage;
    private boolean isLoadingMore;
    private List<Map<String, Object>> myDataset = new ArrayList<Map<String, Object>>();
    private MyAdapter myAdapter;
    private String WebURL = "http://www.mmstop.net/bbs/news_list.asp?action=more&c_id=4&s_id=5&page=";
    private String HomeURL =  "http://www.mmstop.net/bbs/";
    private int TIME_OUT=20000;
    private View view;
    private Handler mHandler;
    private ProgressDialog PDialog = null;
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        // = (RecyclerView) view.findViewById(R.id.rv_newslist);
        txt_Disconnect = (TextView) view.findViewById(R.id.txt_disconnect);
        img_Button_Retry = (ImageButton) view.findViewById(R.id.imgButton_retry);
        img_Button_Retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsList.setVisibility(View.VISIBLE);
                txt_Disconnect.setVisibility(View.INVISIBLE);
                img_Button_Retry.setVisibility(View.INVISIBLE);
                iniPage();
            }
        });
        NewsList = (ListView) view.findViewById(R.id.rv_newslist);
        myAdapter = new MyAdapter(getContext(), myDataset);
        NewsList.setAdapter(myAdapter);
        //NewsList.setDividerHeight(10);
        NewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goWeb(position);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //NewsList.setLayoutManager(layoutManager);
        NewsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == view.getCount()-1 && nowPage<totalPage) {
                        loadPage();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        /*
        NewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4個item自動載入，各位自由選擇
                // dy>0 表示向下滑動
                if (lastVisibleItem >= totalItemCount-4 && dy > 0 && nowPage<totalPage) {
                    if(!isLoadingMore){
                        loadPage();//這裡多線程也要手動控制isLoadingMore
                    }
                }
            }
        });
        */
        iniPage();
        onHandler();
        return view;
    }
    private void goWeb(int position) {
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri CONTENT_URI_BROWSERS = Uri.parse(HomeURL+myDataset.get(position).get("href").toString());
        intent.setData(CONTENT_URI_BROWSERS);
        //intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        startActivity(intent);
    }
    private void iniPage() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(WebURL + nowPage);
                    Document Doc = Jsoup.parse(url, TIME_OUT);
                    Elements title = Doc.select("a.xst");
                    Elements href = Doc.select("a.xst");
                    Elements tims = Doc.select("a.lastposttime");
                    Elements view = Doc.select("td.num");
                    Elements page = Doc.select("a.page");
                    totalPage = page.size() + 1;
                    //Log.d("Page","Page = "+totalPage);
                    //myDataset = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < title.size(); i++) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("title", title.get(i).attr("title") + " (" + view.get(i + 1).text() + ")");
                        item.put("time", tims.get(i).text());
                        item.put("href", href.get(i).attr("href"));
                        //Log.e("GetData", tims.get(i).text());
                        //Log.e("GetData", title.get(i).attr("title") + " " + tims.get(i).text() + " " + href.get(i).attr("href"));
                        myDataset.add(item);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (SocketTimeoutException e) {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                    //e.printStackTrace();
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                } finally {
                }
            }
        };
        PDialog = ProgressDialog.show(NewsFragment.this.getContext(), "載入新聞中...", "請稍後...", true);
        new Thread(runnable).start();
        /*
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1) {
                    NewsList.setAdapter(myAdapter);
                    PDialog.dismiss();
                }
                super.handleMessage(msg);
            }
        };
        */
    }
    private void onHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1) {
                    myAdapter.notifyDataSetChanged();
                    PDialog.dismiss();
                    Log.d("notifyDataSetChanged", "notifyDataSetChanged");
                }else {
                    myDataset.clear();
                    myAdapter.notifyDataSetChanged();
                    PDialog.dismiss();
                    NewsList.setVisibility(View.INVISIBLE);
                    txt_Disconnect.setVisibility(View.VISIBLE);
                    img_Button_Retry.setVisibility(View.VISIBLE);
                }
                /*
                else if(msg.what==2) {
                    Toast.makeText(getContext(), "連線逾時", Toast.LENGTH_SHORT).show();
                    Log.d("連線逾時", "連線逾時");
                    PDialog.dismiss();
                    //NewsList.setAdapter(myAdapter);
                }else if(msg.what==3) {
                    Toast.makeText(getContext(), "無網路", Toast.LENGTH_SHORT).show();
                    Log.d("無網路", "無網路");
                    PDialog.dismiss();
                    //NewsList.setAdapter(myAdapter);
                } */
            }
        };
    }
    private void loadPage() {
        Log.d("loadPage","loadPage"+ nowPage);
        Runnable mrunnable = new Runnable() {
            @Override
            public void run() {
                isLoadingMore=true;
                try {
                    URL url = new URL(WebURL+(nowPage+1));
                    Document Doc = Jsoup.parse(url, TIME_OUT);
                    Elements title = Doc.select("a.xst");
                    Elements href = Doc.select("a.xst");
                    Elements tims = Doc.select("a.lastposttime");
                    Elements view = Doc.select("td.num");
                    for (int i = 0; i < title.size(); i++) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("title", title.get(i).attr("title") + " (" + view.get(i + 1).text() + ")");
                        item.put("time", tims.get(i).text());
                        item.put("href", href.get(i).attr("href"));
                        //Log.e("GetData", title.get(i).attr("title") + " " + tims.get(i).text() + " " + href.get(i).attr("href"));
                        myDataset.add(item);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    isLoadingMore = false;
                    nowPage+=1;
                } catch (SocketTimeoutException e) {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                    //e.printStackTrace();
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                }
            }
        };
        PDialog = ProgressDialog.show(NewsFragment.this.getContext(), "載入新聞中...", "請稍後...", true);
        new Thread(mrunnable).start();
        /*
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1) {
                    myAdapter.notifyDataSetChanged();
                    Log.d("notifyDataSetChanged", "notifyDataSetChanged");
                }else if(msg.what==2) {
                    Toast.makeText(getContext(), "連線逾時", Toast.LENGTH_SHORT).show();
                    Log.d("連線逾時", "連線逾時");
                    //NewsList.setAdapter(myAdapter);
                }
                super.handleMessage(msg);
            }
        };
        */
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mLayInf;
        private List<Map<String, Object>> mData;
        public MyAdapter(Context context, List<Map<String, Object>> itemList)
        {
            mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = itemList;
        }

        @Override
        public int getCount()
        {
            //取得 ListView 列表 Item 的數量
            return mData.size();
        }

        @Override
        public Object getItem(int position)
        {
            //取得 ListView 列表於 position 位置上的 Item
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            //取得 ListView 列表於 position 位置上的 Item 的 ID
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            //設定與回傳 convertView 作為顯示在這個 position 位置的 Item 的 View。
            View v = mLayInf.inflate(R.layout.listview_news_layout, parent, false);

            TextView txtTitle = (TextView) v.findViewById(R.id.txt_title);
            TextView txtTime = (TextView) v.findViewById(R.id.txt_time);

            txtTitle.setText(mData.get(position).get("title").toString());
            txtTime.setText(mData.get(position).get("time").toString());

            return v;
        }
    }
    /*
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        private List<Map<String, Object>> mData;
        private int key=0;
        View v;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView txtTitle,txtTime;
            public ViewHolder(View v) {
                super(v);
                txtTitle = (TextView) v.findViewById(R.id.txt_title);
                txtTime = (TextView) v.findViewById(R.id.txt_time);
            }
        }
        public MyAdapter(List<Map<String, Object>> data) {
            mData = data;
        }
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_news_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri CONTENT_URI_BROWSERS = Uri.parse(HomeURL+v.getTag());
                    intent.setData(CONTENT_URI_BROWSERS);
                    //intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    startActivity(intent);
                }
            });
            key+=1;
            return vh;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.txtTitle.setText(position+" "+mData.get(position).get("title").toString());
            holder.txtTime.setText(mData.get(position).get("time").toString());
            v.setTag(mData.get(position).get("href").toString());
        }
        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
    */
}
