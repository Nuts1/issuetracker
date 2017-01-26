package com.oleksandr.validator;

import com.oleksandr.dto.SprintDto;
import com.oleksandr.entity.Project;
import com.oleksandr.entity.Sprint;
import com.oleksandr.service.entity.ProjectService;
import com.oleksandr.service.entity.SprintService;
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
    private final SprintService sprintService;
    private final ProjectService projectService;

    @Autowired
    public SprintValidator(SprintService sprintService, ProjectService projectService) {
        this.sprintService = sprintService;
        this.projectService = projectService;
    }

    public void validateSave(SprintDto sprint, Errors errors) {
        validateLinkWithPreviousSprint(sprint, errors);
        validateLinkWithProject(sprint, errors);
        if (sprint.getStartDate().compareTo(sprint.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "sprint.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "sprint.nameNull");
    }

    public void validateUpdate(SprintDto sprint, Errors errors) {
        validateLinkWithPreviousSprint(sprint, errors);
        validateLinkWithProject(sprint, errors);

        if (sprint.getStartDate().compareTo(sprint.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "sprint.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmpty(errors, ID_SPRINT, "sprint.idNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "sprint.nameNull");
    }

    private void validateLinkWithPreviousSprint(SprintDto sprint, Errors errors) {
        if (sprint.getPreviousSprint() != null) {
            long id = Long.parseLong(sprint.getPreviousSprint());
            Sprint previous = sprintService.getById(id);
            if (previous.getPreviousSprint() != null &&
                    previous.getPreviousSprint() == sprint.getSprintId()) {
                errors.reject("sprint.circularDependency");
            }

            if (sprint.getStartDate().compareTo(previous.getCompletionDate()) < 0) {
                errors.rejectValue(START_DATE, "sprint.startDateBeforePreviousCompletionDate");
            }
        }
    }

    private void validateLinkWithProject(SprintDto sprint, Errors errors) {
        long id = sprint.getProjectId();
        Project project = projectService.getById(id);


        if (sprint.getStartDate().compareTo(project.getStartDate()) < 0) {
            errors.rejectValue(START_DATE, "sprint.sprintStartDateBeforeProjectStartDate");
        }

        if (sprint.getCompletionDate().compareTo(project.getCompletionDate()) > 0) {
            errors.rejectValue(COMPLETION_DATE, "sprint.sprintCompletionDateLaterProjectCompletionDate");
        }
    }
}
