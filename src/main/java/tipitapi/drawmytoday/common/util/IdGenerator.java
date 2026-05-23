package tipitapi.drawmytoday.common.util;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {}

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
