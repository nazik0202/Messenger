package Securit;

public interface Database {
    byte[] read(byte[] data);
    void edit(byte[] data, byte[] newData);
    void edit(byte[] data, byte[] newData, byte[] publicKey);
    void write(byte[] data, byte password, byte[] publicKey);
    void delete(byte[] data);
}
