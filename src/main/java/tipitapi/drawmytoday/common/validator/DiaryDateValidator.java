package tipitapi.drawmytoday.common.validator;

import java.time.LocalDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiaryDateValidator implements ConstraintValidator<ValidDiaryDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDate serverDate = LocalDate.now().plusDays(1);
        return !value.isAfter(serverDate);
    }

}
