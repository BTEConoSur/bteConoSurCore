package pizzaaxx.bteconosur.utils;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class SQLUtils {

    public static @NotNull UUID uuidFromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }

}
