package iboot;

public final class Utils {
    private Utils() {
        // To prevent instantiation
    }

    public static long toLittleEndianLong(byte[] bytes, int offset, int size) {
        long result = 0;
        for (int i = 0; i < size; i++) {
            result += ((long)bytes[offset + i]) * (1l << (8 * i));
        }
        return result;
    }
}
