package tipitapi.drawmytoday.common.utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
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
        LocalDate lastDay = YearMonth.of(year, month).atEndOfMonth();

        return LocalDateTime.of(lastDay, LocalTime.of(23, 59, 59));
    }

    public static LocalDate getDate(int year, int month, int day) {
        validateDate(year, month, day);
        return LocalDate.of(year, month, day);
    }

    private static void validateMonth(int month) {
        if (month > 12 || month < 1) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public static void validateDate(int year, int month, int day) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            int lastDayOfMonth = yearMonth.lengthOfMonth();
            if (day < 1 || day > lastDayOfMonth) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
        } catch (DateTimeException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, e);
        }
    }
}
