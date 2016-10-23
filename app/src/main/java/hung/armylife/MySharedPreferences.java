package hung.armylife;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hung on 2016/10/23.
 */

public class MySharedPreferences {
    private SharedPreferences profile;
    private String SP_Date="Date", SP_Name="Name", SP_Countdown="Countdow", SP_Frends="Friends",
            SP_Facebook_id="Facebook_id", SP_Mood="Mood", SP_EntryDate="EntryDate", SP_ExitDate="ExitDate",
            SP_Period="Period", SP_Redeem="Redeem", SP_Sequence="Sequence", SP_First="First";
    //public static String _countdown,_name,_entryate,_exitdate,_mood,_friends,_facebook_id;
    public MySharedPreferences(Context context) {
        profile = context.getSharedPreferences(SP_Date, 0);
    }
    public void set_first(int _first) {
        profile.edit().putInt(SP_First,_first).commit();
    }
    public void set_countdown(int _countdown) {
        profile.edit().putInt(SP_Countdown,_countdown).commit();
    }
    public void set_name(String _name) {
        profile.edit().putString(SP_Name,_name).commit();
    }
    public void set_entryate(String _entryate) {
        profile.edit().putString(SP_EntryDate,_entryate).commit();
    }
    public void set_exitdate(String _exitdate) {
        profile.edit().putString(SP_ExitDate,_exitdate).commit();
    }
    public void set_mood(String _mood) {
        profile.edit().putString(SP_Mood,_mood).commit();
    }
    public void set_friends(String _friends) {
        profile.edit().putString(SP_Frends,_friends).commit();
    }
    public void set_facebook_id(String _facebook_id) {
        profile.edit().putString(SP_Facebook_id,_facebook_id).commit();
    }
    public void set_peroid(int _peroid) {
        profile.edit().putInt(SP_Period,_peroid).commit();
    }
    public void set_redeem(String _redeem) {
        profile.edit().putString(SP_Redeem,_redeem).commit();
    }
    public void set_sequence(String _sequence) {
        profile.edit().putString(SP_Sequence,_sequence).commit();
    }

    public int get_first() {
        return profile.getInt(SP_First,0);
    }
    public int get_countdown() {
        return profile.getInt(SP_Countdown,0);
    }
    public String get_name() {
        return profile.getString(SP_Name,"未登入");
    }
    public String get_entryate() {
        return profile.getString(SP_EntryDate,"未設定");
    }
    public String get_exitdate() {
        return profile.getString(SP_ExitDate,"");
    }
    public String get_mood() {
        return profile.getString(SP_Mood,"");
    }
    public String get_friends() {
        return profile.getString(SP_Frends,"");
    }
    public String get_facebook_id() {
        return profile.getString(SP_Facebook_id,"未登入");
    }
    public int get_peroid() {
        return profile.getInt(SP_Period,-1);
    }
    public String get_redeem() {
        return profile.getString(SP_Redeem,"未設定");
    }
    public String get_sequence() {
        return profile.getString(SP_Sequence,"未設定");
    }
}
