package Model;
import Controller.ContactManager;
import Storage.ProfilePhotoStorage;

import java.time.LocalDateTime;
import java.util.List;

public class User {
    private int userId;
    private String nickName;
    private String phoneNumber;
    private String profileDescription;
    private String localTimezone;
    private boolean onlineStatus;
    private LocalDateTime lastTimeOnline;
    private List<String> tags;
    private String profilePhotoPath;
    private ProfilePhotoStorage pps;
    private ContactManager contactManager;


    public User(int userId, String nickName) {
        this.userId = userId;
        this.nickName = nickName;
    }

    public User() {
    }



    public int getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public ProfilePhotoStorage getPps() {
        return pps;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public List<String> getTags() {
        return tags;
    }

    public LocalDateTime getLastTimeOnline() {
        return lastTimeOnline;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public String getLocalTimezone() {
        return localTimezone;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
