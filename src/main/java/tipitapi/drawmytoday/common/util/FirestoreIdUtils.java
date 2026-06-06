package tipitapi.drawmytoday.common.util;

import java.util.Comparator;

public final class FirestoreIdUtils {

    private FirestoreIdUtils() {}

    /**
     * Stores entity IDs as String to match the application schema (UUID-based, see
     * draw-my-today-db-dev). Used for user / diary / image / prompt / ticket ids.
     * NOTE: emotion ids stay numeric — use {@link #toNumericStorageType(String)} for those.
     */
    public static Object toStorageType(String id) {
        return id;
    }

    /**
     * Stores emotion IDs as Long. Emotions are seeded master data with small numeric ids
     * and are never queried by value, so they remain numeric to match the dev schema.
     */
    public static Object toNumericStorageType(String id) {
        if (id == null) return null;
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return id;
        }
    }

    /**
     * Converts a Firestore field value (Number or String) back to a String domain ID.
     * Handles legacy Long-encoded IDs and new UUID String IDs uniformly.
     */
    public static String toDomainId(Object value, String fallback) {
        if (value instanceof Number) {
            return String.valueOf(((Number) value).longValue());
        }
        if (value instanceof String) {
            return (String) value;
        }
        return fallback;
    }

    /**
     * Comparator that preserves legacy Long ordering for numeric IDs while still working with UUIDs.
     * Numeric IDs sort before non-numeric IDs to keep legacy items in their original positions.
     * Nulls sort last.
     */
    public static final Comparator<String> ID_COMPARATOR = (left, right) -> {
        if (left == null && right == null) return 0;
        if (left == null) return 1;
        if (right == null) return -1;
        Long leftNum = tryParseLong(left);
        Long rightNum = tryParseLong(right);
        if (leftNum != null && rightNum != null) return Long.compare(leftNum, rightNum);
        if (leftNum != null) return -1;
        if (rightNum != null) return 1;
        return left.compareTo(right);
    };

    private static Long tryParseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
