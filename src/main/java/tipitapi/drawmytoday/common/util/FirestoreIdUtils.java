package tipitapi.drawmytoday.common.util;

public final class FirestoreIdUtils {

    private FirestoreIdUtils() {}

    /**
     * Converts a String ID to the appropriate Firestore storage type for backward compatibility.
     * Numeric-looking IDs (legacy Long-based) are stored as Long to match existing field values.
     * UUID strings are stored as String.
     */
    public static Object toStorageType(String id) {
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
}
