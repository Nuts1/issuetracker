package com.oleksandr.validator;

import com.oleksandr.dto.ProjectDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import static com.oleksandr.validator.constant.ProjectConstant.*;

/**
 * Created by nuts on 25.01.17.
 */
@Component
public class ProjectValidator {

    public void validateSave(ProjectDto projectDto, Errors errors) {
        if (projectDto.getStartDate().compareTo(projectDto.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "project.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmpty(errors, CUSTOMER_ID, "project.idCustomerNull");
        ValidationUtils.rejectIfEmpty(errors, MANAGER_ID, "project.idManagerNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "project.nameNull");
    }

    public void validateUpdate(ProjectDto projectDto, Errors errors) {
        if (projectDto.getStartDate().compareTo(projectDto.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "project.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmpty(errors, PROJECT_ID, "project.idNull");
        ValidationUtils.rejectIfEmpty(errors, MANAGER_ID, "project.idManagerNull");
        ValidationUtils.rejectIfEmpty(errors, CUSTOMER_ID, "project.idCustomerNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "project.nameNull");
    }
}
