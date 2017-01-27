package com.oleksandr.validator;

import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Sprint;
import com.oleksandr.entity.Task;
import com.oleksandr.service.entity.SprintService;
import com.oleksandr.service.entity.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Date;

import static com.oleksandr.validator.constant.SprintConstant.COMPLETION_DATE;
import static com.oleksandr.validator.constant.SprintConstant.START_DATE;
import static com.oleksandr.validator.constant.TaskConstant.*;

@Component
public class TaskValidator {
    private final TaskService taskService;
    private final SprintService sprintService;

    @Autowired
    public TaskValidator(TaskService taskService, SprintService sprintService) {
        this.taskService = taskService;
        this.sprintService = sprintService;
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

        if (task.getActualCompletionDate() != null) {
            errors.reject("task.changeCompleted");
        }

        ValidationUtils.rejectIfEmpty(errors, ID_TASK, "task.idNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, "task.nameNull");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION, "task.descriptionNull");
    }

    private void validateLinkWithPreviousTask(TaskDto task, Errors errors) {
        if (task.getPreviousTaskId() != null && task.getPreviousTaskId().length() > 0 ) {
            long id = Long.parseLong(task.getPreviousTaskId());
            Task previous = taskService.getById(id);
            if (previous.getPreviousTask() != null && task.getIdTask() != null &&
                    previous.getPreviousTask().getTaskId() == task.getIdTask()) {
                errors.reject("task.circularDependency");
            }

            Date completionPreviousTask = (previous.getActualCompletionDate() != null) ?
                previous.getActualCompletionDate() : previous.getCompletionDate();

            if (task.getStartDate().compareTo(completionPreviousTask) < 0) {
                errors.rejectValue(START_DATE, "task.startDateBeforePreviousCompletionDate");
            }
        }
    }


    private void validateLinkWithProject(TaskDto taskDto, Errors errors) {
        long id = taskDto.getSprintId();
        Sprint sprint = sprintService.getById(id);

        if (taskDto.getStartDate().compareTo(sprint.getStartDate()) < 0) {
            errors.rejectValue(START_DATE, "task.taskStartDateBeforeSprintStartDate");
        }

        if (taskDto.getCompletionDate().compareTo(sprint.getCompletionDate()) > 0) {
            errors.rejectValue(COMPLETION_DATE, "task.taskCompletionDateLaterSprintCompletionDate");
        }
    }
}