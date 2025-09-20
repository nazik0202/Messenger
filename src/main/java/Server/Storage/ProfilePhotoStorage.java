package Server.Storage;

import java.io.IOException;

public interface ProfilePhotoStorage {
    void savePhoto(int userId, byte[] photoData) throws IOException;
    void deletePhoto(int userID) throws IOException;
    byte[] loadPhoto(int userID) throws IOException;

}