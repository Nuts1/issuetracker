package com.oleksandr.validator;

import com.oleksandr.dao.SprintDao;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Sprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import static com.oleksandr.validator.constant.SprintConstant.*;

/**
 * Created by nuts on 25.01.17.
 */
@Component
public class SprintValidator {
    private final SprintDao dao;

    @Autowired
    public SprintValidator(SprintDao dao) {
        this.dao = dao;
    }

    public void validateSave(SprintDto sprint, Errors errors) {
        validateLinkWithPreviousSprint(sprint, errors);

        if (sprint.getStartDate().compareTo(sprint.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "sprint.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "sprint.nameNull");
    }

    public void validateUpdate(SprintDto sprint, Errors errors) {
        validateLinkWithPreviousSprint(sprint, errors);

        if (sprint.getStartDate().compareTo(sprint.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "sprint.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmpty(errors, ID_SPRINT, "sprint.idNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "sprint.nameNull");
    }

    private void validateLinkWithPreviousSprint(SprintDto sprint, Errors errors) {
        if (sprint.getPreviousSprint() != null) {
            long id = Long.parseLong(sprint.getPreviousSprint());
            Sprint previous = dao.getById(id);
            if (previous.getPreviousSprint() != null &&
                    previous.getPreviousSprint() == sprint.getSprintId()) {
                errors.reject("sprint.circularDependency");
            }

            if (sprint.getStartDate().compareTo(previous.getCompletionDate()) < 0) {
                errors.rejectValue(START_DATE, "sprint.startDateBeforePreviousCompletionDate");
            }
        }
    }
}
