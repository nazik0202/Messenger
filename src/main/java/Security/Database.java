package Security;

public interface Database {
    byte[] read(byte[] data);
    void edit(byte[] data, byte[] newData);
    void write(byte[] data, byte[] dataForWrite);
    void delete(byte[] data);
}
