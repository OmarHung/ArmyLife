package hung.armylife;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hung.armylife.widget.CircleImg;

/**
 * Created by Hung on 2016/10/30.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Map<String, Object>> mData;
    private ArrayList<Integer> mDataSetTypes;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtMood, txtCountDown;
        public ProfilePictureView mProfilePictureView;
        public ViewHolder(View v) {
            super(v);
            mProfilePictureView = (ProfilePictureView) v.findViewById(R.id.circleImg);
            txtName = (TextView) v.findViewById(R.id.txt_name);
            txtMood = (TextView) v.findViewById(R.id.txt_feeling);
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
        holder.txtMood.setText(mData.get(position).get("mood").toString());
        holder.txtCountDown.setText(mData.get(position).get("cd").toString());
        holder.mProfilePictureView.setProfileId(mData.get(position).get("facebook_id").toString());
        Log.d("onBindViewHolder", "onBindViewHolder "+ position);
        //holder.circleImg.setImageDrawable(R.drawable.);
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
