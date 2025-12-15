package Client.Model;
import Client.Controller.ContactManager;
import Server.Storage.ProfilePhotoStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && onlineStatus == user.onlineStatus && Objects.equals(nickName, user.nickName) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(profileDescription, user.profileDescription) && Objects.equals(localTimezone, user.localTimezone) && Objects.equals(lastTimeOnline, user.lastTimeOnline) && Objects.equals(tags, user.tags) && Objects.equals(profilePhotoPath, user.profilePhotoPath) && Objects.equals(pps, user.pps) && Objects.equals(contactManager, user.contactManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, nickName, phoneNumber, profileDescription, localTimezone, onlineStatus, lastTimeOnline, tags, profilePhotoPath, pps, contactManager);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileDescription='" + profileDescription + '\'' +
                ", localTimezone='" + localTimezone + '\'' +
                ", onlineStatus=" + onlineStatus +
                ", lastTimeOnline=" + lastTimeOnline +
                ", tags=" + tags +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                ", pps=" + pps +
                ", contactManager=" + contactManager +
                '}';
    }
}
