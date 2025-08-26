package Security.ConsoleTest;

import Security.Database;

import java.util.HashMap;
import java.util.Map;

public class SimpleDatabase implements Database {
    private final Map<byte[], byte[]> storage = new HashMap<>();
    @Override
    public byte[] read(byte[] data) {
        return storage.get(data);
    }

    @Override
    public void edit(byte[] data, byte[] newData) {
        storage.put(data, newData);
    }

    @Override
    public void write(byte[] data, byte[] dataForWrite) {
        storage.put(data, dataForWrite);
    }

    @Override
    public void delete(byte[] data) {
        storage.remove(data);
    }
}
