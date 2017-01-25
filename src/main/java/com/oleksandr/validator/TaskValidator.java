package com.oleksandr.validator;

import com.oleksandr.dao.TaskDao;
import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import static com.oleksandr.validator.constant.TaskConstant.*;

@Component
public class TaskValidator {
    private final TaskDao dao;

    @Autowired
    public TaskValidator(TaskDao dao) {
        this.dao = dao;
    }

    public void validateSave(TaskDto task, Errors errors) {
        validateLinkWithPreviousTask(task, errors);

        if (task.getStartDate().compareTo(task.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "task.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "task.nameNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION, "task.descriptionNull");
    }

    public void validateUpdate(TaskDto task, Errors errors) {
        validateLinkWithPreviousTask(task, errors);

        if (task.getStartDate().compareTo(task.getCompletionDate()) > 0) {
            errors.rejectValue(START_DATE, "task.startDateLaterCompletionDate");
        }

        ValidationUtils.rejectIfEmpty(errors, ID_TASK, "task.idNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "task.nameNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION, "task.descriptionNull");
    }

    private void validateLinkWithPreviousTask(TaskDto task, Errors errors) {
        if (task.getPreviousTaskId() != null) {
            long id = Long.parseLong(task.getPreviousTaskId());
            Task previous = dao.getById(id);
            if (previous.getPreviousTask() != null &&
                    previous.getPreviousTask().getTaskId() == task.getIdTask()) {
                errors.reject("task.circularDependency");
            }

            if (task.getStartDate().compareTo(previous.getCompletionDate()) < 0) {
                errors.rejectValue(START_DATE, "task.startDateBeforePreviousCompletionDate");
            }
        }
    }
}