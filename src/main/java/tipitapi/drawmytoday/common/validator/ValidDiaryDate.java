package tipitapi.drawmytoday.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiaryDateValidator.class)
@Documented
public @interface ValidDiaryDate {

    String message() default "내일보더 먼 날짜는 입력할 수 없습니다.";

    Class[] groups() default {};

    Class[] payload() default {};
}
