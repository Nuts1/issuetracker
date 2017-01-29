package com.oleksandr.service.reports;

import com.oleksandr.dao.*;
import com.oleksandr.dto.ProjectDto;
import com.oleksandr.dto.SprintDto;
import com.oleksandr.dto.TaskDto;
import com.oleksandr.entity.Task;
import com.oleksandr.entity.TaskEmployee;
import com.oleksandr.service.reports.data.resourselist.*;
import com.oleksandr.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

import static java.util.Calendar.*;


/**
 * Created by Nuts on 1/10/2017
 * 8:25 PM.
 */
@Service
public class ResourceListServiceImpl {
    private final EmployeeDao employeeDao;
    private final ProjectDao projectDao;
    private final SprintDao sprintDao;
    private final TaskEmployeeDao taskEmployeeDao;

    @Autowired
    public ResourceListServiceImpl(EmployeeDao employeeDao,
                                   ProjectDao projectDao,
                                   SprintDao sprintDao,
                                   TaskEmployeeDao taskEmployeeDao,
                                   TaskDao taskDao) {
        this.employeeDao = employeeDao;
        this.projectDao = projectDao;
        this.taskEmployeeDao = taskEmployeeDao;
        this.sprintDao = sprintDao;
    }

    public ResourceListDto getResourceList(Long idProject, Long idEmployee) {
        List<EmployeeListItem> employeeListItems = new ArrayList<>();
        List<Employee> employees;
        if(idProject != null && idEmployee == null) {
            employees = employeeDao.getByProjectId(idProject); // resource list for project
        } else if(idEmployee != null) {
            employees = new ArrayList<>();
            employees.add(employeeDao.getById(idEmployee));
        } else {
            employees = employeeDao.getAll(); // resource list for all employees
        }

        employees.forEach(employee -> {
            HashMap<Date, DayWork> totalDayWorks = new HashMap<>();
            EmployeeListItem employeeListItem = new EmployeeListItem();
            employeeListItem.setName(employee.getName());
            List<ProjectDto> projects;
            if(idProject == null) {
                projects = projectDao.getProjectDtos(employee.getEmployeeId());
            } else {
                projects = projectDao.getProjectDtos(employee.getEmployeeId(), idProject);
            }
            List<ProjectListItem> projectListItems = new ArrayList<>();
            projects.forEach(project -> {
                ProjectListItem projectListItem = new ProjectListItem();
                projectListItem.setNameProject(project.getName());
                List<SprintDto> sprints = sprintDao.getSprints(employee.getEmployeeId(), Long.parseLong(project.getProjectId()));
                List<SprintListItem> sprintListItems = new ArrayList<>();
                HashMap<Date, DayWork> totalDayWorksInProject = new HashMap<>();
                sprints.forEach(sprint -> {
                    HashMap<Date, DayWork> totalDayWorksInSprint = new HashMap<>();
                    SprintListItem sprintListItem = new SprintListItem();
                    sprintListItem.setName(sprint.getName());

                    List<TaskItem> taskItems = new ArrayList<>();
                    List<TaskEmployee> tasks = taskEmployeeDao.getEmployeeTasksByEmployeeAndSprint(employee, sprint.getSprintId());
                    tasks.forEach(task -> {
                        TaskItem taskItem = new TaskItem();
                        taskItem.setName(task.getTask().getName());
                        taskItem.setLoad(task.getLoad());
                        taskItem.setDayWorks(getDayWorks(task, totalDayWorksInSprint));
                        taskItems.add(taskItem);
                    });

                    sprintListItem.setTaskItems(taskItems);
                    List<DayWork> total = new ArrayList<>();
                    totalDayWorksInSprint.forEach((key, value) -> {
                        setTotalDayWorks(totalDayWorksInProject, toCalendar(key), value.getWorks());
                        total.add(value);
                    });
                    sprintListItem.setTotalDayWorks(total);
                    sprintListItems.add(sprintListItem);
                });
                projectListItem.setSprintListItems(sprintListItems);
                List<DayWork> total = new ArrayList<>();
                totalDayWorksInProject.forEach((key, value) -> {
                    setTotalDayWorks(totalDayWorks, toCalendar(key), value.getWorks());
                    total.add(value);
                });
                projectListItem.setTotalDayWorks(total);
                projectListItems.add(projectListItem);
            });
            List<DayWork> total = new ArrayList<>();
            totalDayWorks.forEach((key, value) -> total.add(value));

            employeeListItem.setProjectListItems(projectListItems);
            employeeListItem.setTotalDayWorks(total);
            employeeListItem.setTotalWorks(total.stream()
                    .mapToDouble(DayWork::getWorks)
                    .sum());
            employeeListItems.add(employeeListItem);
        });
        return new ResourceListDto(employeeListItems);
    }

    private List<DayWork> getDayWorks(TaskEmployee task, HashMap<Date, DayWork> totalDayWorks) {
        Calendar startDate = toCalendar(task.getTask().getStartDate());
        Calendar completionDate = toCalendar(task.getTask().getCompletionDate());
        Calendar actualCompletionDate = null;
        if(task.getTask().getActualCompletionDate() != null)
            actualCompletionDate = toCalendar(task.getTask().getActualCompletionDate());

        double load = (double) task.getLoad() / 100.0; // 100%

        Calendar maxDate = (actualCompletionDate != null &&
                actualCompletionDate.compareTo(completionDate) > 0) ? actualCompletionDate : completionDate;

        if(startDate.compareTo(maxDate) > 0) {
            Logger.getGlobal().warning("startDate.compareTo(maxDate) > 0");
            return null;
        }

        List<DayWork> dayToWorks = new ArrayList<>();

        // startDay
        int hourStartTask = startDate.get(HOUR_OF_DAY);
        DayWork dayWork = new DayWork();
        resetTime(startDate);
        dayWork.setDate(startDate.getTime());
        double hoursSpendOnTaskInDay = ((16 - hourStartTask) * load);
        dayWork.setWorks(hoursSpendOnTaskInDay); // 8 - hours in work day
        setTotalDayWorks(totalDayWorks, startDate, hoursSpendOnTaskInDay);
        dayToWorks.add(dayWork);

        startDate.add(Calendar.DATE, 1);

        // middle day (between start and and day)
        while (startDate.compareTo(maxDate) < 0 && (startDate.get(Calendar.YEAR) != maxDate.get(Calendar.YEAR) ||
                startDate.get(MONTH) != maxDate.get(MONTH) ||
                startDate.get(DAY_OF_MONTH) != maxDate.get(DAY_OF_MONTH))) {
            dayWork = new DayWork();
            resetTime(startDate);
            dayWork.setDate(startDate.getTime());

            if(startDate.compareTo(completionDate) > 0) {
                dayWork.setDelay(true);
            }

            hoursSpendOnTaskInDay = (8 * load); // 8 - hours in work day
            dayWork.setWorks(hoursSpendOnTaskInDay);

            setTotalDayWorks(totalDayWorks, startDate, hoursSpendOnTaskInDay);

            dayToWorks.add(dayWork);
            startDate.add(Calendar.DATE, 1);
        }
        // startDay and completionDate the same day
        int hourCompletionTask = completionDate.get(HOUR_OF_DAY);
        if (hourCompletionTask > 8) {
            dayWork = new DayWork();
            resetTime(startDate);
            dayWork.setDate(startDate.getTime());
            hoursSpendOnTaskInDay = ((hourCompletionTask - 8) * load);
            dayWork.setWorks(hoursSpendOnTaskInDay); // 8 - hours in work day
            setTotalDayWorks(totalDayWorks, startDate, hoursSpendOnTaskInDay);
            dayToWorks.add(dayWork);
        }
        return dayToWorks;
    }

    private void resetTime(Calendar startDate) {
        startDate.set(HOUR_OF_DAY, 0);
        startDate.set(MILLISECOND, 0);
        startDate.set(SECOND, 0);
        startDate.set(MINUTE, 0);
    }

    private void setTotalDayWorks(HashMap<Date, DayWork> dayToWorks, Calendar startDate, double hoursSpendOnTaskInDay) {
        if (dayToWorks.containsKey(startDate.getTime())) {
            DayWork temp = dayToWorks.get(startDate.getTime());
            temp.setWorks(temp.getWorks() + hoursSpendOnTaskInDay);
        } else {
            DayWork temp = new DayWork();
            temp.setDate(startDate.getTime());
            temp.setWorks(hoursSpendOnTaskInDay);
            dayToWorks.put(startDate.getTime(), temp);
        }
    }

    private static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

}