package tipitapi.drawmytoday.common.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

    public static LocalDateTime getStartDate(ZoneId timezone, int year, int month) {
        validateMonth(month);
        Instant startDate = LocalDateTime.of(year, month, 1, 0, 0, 0)
            .toInstant(ZoneOffset.UTC);
        return LocalDateTime.ofInstant(startDate, timezone);
    }

    public static LocalDateTime getEndDate(int year, int month) {
        validateMonth(month);
        return LocalDateTime.of(year, month, getMaxDay(month), 23, 59);
    }

    public static LocalDateTime getEndDate(ZoneId timezone, int year, int month) {
        validateMonth(month);
        Instant endDate = LocalDateTime.of(year, month, getMaxDay(month), 23, 59, 59)
            .toInstant(ZoneOffset.UTC);
        return LocalDateTime.ofInstant(endDate, timezone);
    }

    public static Date getDate(int year, int month, int day) {
        validateDate(month, day);
        return Date.valueOf(LocalDate.of(year, month, day));
    }

    private static void validateMonth(int month) {
        if (month > 12 || month < 1) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private static void validateDate(int month, int day) {
        validateMonth(month);
        if (day > getMaxDay(month) || day < 1) {
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
