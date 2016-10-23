package hung.armylife.useless;

/**
 * Created by Hung on 2016/10/23.
 */

public class User {
    private String name;
    private String mood;
    private String entrydate;
    private String exitdate;
    private String friends_id;
    private String facebook_id;
    private int countdown;
    public User() {
    }
    public User(int countdown, String entrydate, String exitdate, String friends_id, String mood, String name, String facebook_id) {
        this.name = name;
        this.mood = mood;
        this.entrydate = entrydate;
        this.exitdate = exitdate;
        this.countdown = countdown;
        this.friends_id = friends_id;
        this.facebook_id = facebook_id;
    }
    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }
    public void setFriends_id(String friends_id) {
        this.friends_id = friends_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMood(String mood) {
        this.mood = mood;
    }
    public void setEntrydate(String entrydate) {
        this.entrydate = entrydate;
    }
    public void setExitdate(String exitdate) {
        this.exitdate = exitdate;
    }
    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }
    public String getFacebook_id() {
        return facebook_id;
    }
    public String getFriends_id() {
        return friends_id;
    }
    public String getName() {
        return name;
    }
    public String getMood() {
        return mood;
    }
    public String getEntrydate() {
        return entrydate;
    }
    public String getExitdate() {
        return exitdate;
    }
    public int getCountdown() {
        return countdown;
    }
}
