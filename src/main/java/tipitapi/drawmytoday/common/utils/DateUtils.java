package tipitapi.drawmytoday.common.utils;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static LocalDateTime getStartDate(int year, int month) {
        validateMonth(month);
        return LocalDateTime.of(year, month, 1, 0, 0, 0);
    }

    public static LocalDateTime getEndDate(int year, int month) {
        validateMonth(month);
        return LocalDateTime.of(year, month, getMaxDay(month), 23, 59);
    }

    private static void validateMonth(int month) {
        if (month > 12 || month < 1) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private static int getMaxDay(int month) {
        if (month == 2) {
            return 28;
        }
        if (Stream.of(1, 3, 5, 7, 8, 10, 12)
            .anyMatch(m -> m == month)) {
            return 31;
        } else {
            return 30;
        }
    }
}
