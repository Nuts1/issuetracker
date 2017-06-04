var tasks = {};

var dataTableActualTime;
var dataTableBaseTime;
var charts = [];

function selectProject(str) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/manager/project?projectId=" + str,
        timeout: 100000,
        success: function (data) {
            history.pushState(null, null, '/manager/projects.html?selectedProject=' + str);
            setSprints(str);
            google.charts.setOnLoadCallback(tableCreate(data));
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function addRow(task, startDate, completionDate, dataTable, taskResources) {
    if (task.previousTask != null) {
        dataTable.addRow(['' + task.taskId,
            'Task : ' + task.name,
            taskResources,
            new Date(startDate),
            new Date(completionDate),
            null,
            0,
            '' + task.previousTask.taskId]);
    } else {
        dataTable.addRow(['' + task.taskId,
            'Task : ' + task.name,
            taskResources,
            new Date(startDate),
            new Date(completionDate),
            null,
            0,
            null]);
    }
}
function showChart(dataTableActualTime, dataTableBaseTime) {
    if(charts[0] === undefined || charts[0] === null) {
        var ActualTimeDiv = document.getElementById('diagram');
        var BaseTimeDiv = document.getElementById('diagramBase');

        charts[0] = new google.visualization.Gantt(ActualTimeDiv);
        charts[1] = new google.visualization.Gantt(BaseTimeDiv);
    } else {
        charts[0].clearChart();
        charts[1].clearChart();
    }

    charts[0].draw(dataTableActualTime);
    charts[1].draw(dataTableBaseTime);
}
function successCompleteTask(tr) {
    tr.setAttribute('class', 'alert alert-success');
    tr.setAttribute('title', 'Task completed on time');
    tr.setAttribute('data-toggle', 'tooltip');
}
function delayCompleteTask(tr) {
    tr.setAttribute('class', 'alert alert-danger');
    tr.setAttribute('title', 'Task completed with a delay');
    tr.setAttribute('data-toggle', 'tooltip');
}
function deleyStartTask(tr) {
    tr.setAttribute('class', 'alert alert-danger');
    tr.setAttribute('title', 'task started with a delay');
    tr.setAttribute('data-toggle', 'tooltip');
}
function tableCreate(project) {

    var projectName = document.getElementById('projectName');
    var projectInfo = document.getElementById('projectInfo');
    var sprints = document.getElementById('sprints');
    sprints.innerHTML = "";
    projectName.innerHTML = project.name;
    projectInfo.innerHTML = '<label>Start date: </label><label id="' + project.projectId + 'ProjectStartDate">' + project.startDate
        + '</label><br><label>Completion date: </label><label id="' + project.projectId + 'ProjectCompletionDate">' + project.completionDate + '</label>'
        + '<br> Predicated completion date: ' + project.predicatedCompletionDate
        + '<br> Customer: ' + project.customer.name + " " + project.customer.surname
        + '<br> Manager: ' + project.manager.name + ' ' + project.manager.surname;
    var sprintIdSelect = document.getElementById('sprintId');
    sprintIdSelect.innerHTML = "<option selected disabled>Select Sprint</option>";

    dataTableActualTime = new google.visualization.DataTable();
    dataTableActualTime.addColumn('string', 'Task ID');
    dataTableActualTime.addColumn('string', 'Task Name');
    dataTableActualTime.addColumn('string', 'Resource');
    dataTableActualTime.addColumn('date', 'Start Date');
    dataTableActualTime.addColumn('date', 'End Date');
    dataTableActualTime.addColumn('number', 'Duration');
    dataTableActualTime.addColumn('number', 'Percent Complete');
    dataTableActualTime.addColumn('string', 'Dependencies');

    dataTableBaseTime = new google.visualization.DataTable();

    dataTableBaseTime.addColumn('string', 'Task ID');
    dataTableBaseTime.addColumn('string', 'Task Name');
    dataTableBaseTime.addColumn('string', 'Resource');
    dataTableBaseTime.addColumn('date', 'Start Date');
    dataTableBaseTime.addColumn('date', 'End Date');
    dataTableBaseTime.addColumn('number', 'Duration');
    dataTableBaseTime.addColumn('number', 'Percent Complete');
    dataTableBaseTime.addColumn('string', 'Dependencies');

    for (var i = 0; i < project.sprints.length; i++) {
        var option = document.createElement('option');
        option.setAttribute('value', project.sprints[i].sprintId);
        option.innerHTML = project.sprints[i].name;
        sprintIdSelect.appendChild(option);

        var sprintInfo = document.createElement('h4');
        sprintInfo.setAttribute('id', project.sprints[i].sprintId);
        sprintInfo.innerHTML = "<hr>Sprint: " + project.sprints[i].name +
            '<br><label>Start date: </label><label id="' + project.sprints[i].sprintId + 'SprintStartDate">' + project.sprints[i].startDate +
            '</label><br><label>Completion date: </label><label id="' + project.sprints[i].sprintId + 'SprintCompletionDate">' + project.sprints[i].completionDate + '</label>' +
            '<a style="margin: 5px 5px 5px 5px;" class="btn btn-primary" href="/manager/editSprintPage.html?idProject=' + project.projectId + '&sprintId=' + project.sprints[i].sprintId + '">Edit</a>' +
            '<input class="btn btn-primary" onclick="deleteSprint(' + project.sprints[i].sprintId + ')" type="button" value="Delete">';
        sprints.appendChild(sprintInfo);

        var table = document.createElement('table');
        table.setAttribute("class", "table table-bordered");
        var tableBody = document.createElement('tbody');
        var tableHead = document.createElement('thead');

        tableHead.innerHTML = ' <tr>' +
            '<th>No.</th>' +
            '<th>Task</th>' +
            '<th style="width: 40%;">Description</th>' +
            '<th>Start date</th>' +
            '<th>Actual Start date</th>' +
            '<th>Estimate</th>' +
            '<th>Predicted Delay</th>' +
            '<th>Completion Time</th>' +
            '<th>Actual Completion Time</th>' +
            '<th>Pred.</th>' +
            '<th>Resource Names</th>' +
            '<th>Controls</th>' +
            '</tr>';
        sprints.appendChild(table);

        table.appendChild(tableHead);
        tasks = {};
        for (j = 0; j < project.sprints[i].tasks.length; j++) {
            var task = project.sprints[i].tasks[j];
            tasks[task.taskId] = j;
        }
        for (var j = 0; j < project.sprints[i].tasks.length; j++) {
            var tr = document.createElement('tr');
            var tdNo = document.createElement('td');
            var tdTask = document.createElement('td');
            var tdDescription = document.createElement('td');
            var tdStartDate = document.createElement('td');
            var tdEstimate = document.createElement('td');
            var tdPredicatedDelay = document.createElement('td');
            var tdCompletionTime = document.createElement('td');
            var tdPred = document.createElement('td');
            var tdResourceNames = document.createElement('td');
            var tdControls = document.createElement('td');
            var tdActualCompletionTime = document.createElement('td');
            var tdActualStartTime = document.createElement('td');

            task = project.sprints[i].tasks[j];
            var taskResources = task.employees.map(function (e) {
                return e.name + " " + e.surname;
            }).join(".<br>");

            tdNo.innerHTML = j;
            tdTask.innerHTML = task.name;
            tdDescription.innerHTML = task.description;
            tdStartDate.innerHTML = task.startDate;
            tdEstimate.innerHTML = task.estimate;
            tdPredicatedDelay.innerHTML = task.predictedDelay;

            if (task.actualStartDate != null && task.actualCompletionDate != null) {
                addRow(task, task.actualStartDate, task.actualCompletionDate, dataTableActualTime, taskResources);
            } else if (task, task.actualStartDate != null) {
                addRow(task, task.actualStartDate, task.completionDate, dataTableActualTime, taskResources);
            } else if (task, task.actualCompletionDate != null) {
                addRow(task, task.startDate, task.actualCompletionDate, dataTableActualTime, taskResources);
            } else {
                addRow(task, task.startDate, task.completionDate, dataTableActualTime, taskResources);
            }

            addRow(task, task.startDate, task.completionDate, dataTableBaseTime, taskResources);

            if (task.actualStartDate != null) {
                if (new Date(task.actualStartDate) > new Date(task.startDate)) {
                    deleyStartTask(tr);
                }
                tdActualStartTime.innerHTML = task.actualStartDate;
            }

            if (task.actualCompletionDate != null) {
                if (new Date(task.actualCompletionDate) > new Date(task.completionDate)) {
                    delayCompleteTask(tr);
                } else {
                    successCompleteTask(tr);
                }
                tdActualCompletionTime.innerHTML = task.actualCompletionDate;
            } else {
                tdControls.innerHTML =
                    '<td>' +
                    '<a style="margin: 5px 5px 5px 5px;" class="btn btn-primary" href="/manager/editTask.html?idTask=' + task.taskId + '&idProject=' + project.projectId + '&sprintId=' + project.sprints[i].sprintId + '">Edit</a>' +
                    '<input class="btn btn-primary" onclick="deleteTask(' + task.taskId + ')" type="button" value="Delete">' +
                    '</td>';
            }

            if (typeof task.previousTask !== "undefined" && task.previousTask != null) {
                tdPred.innerHTML = tasks[task.previousTask.taskId];
            }

            tdCompletionTime.innerHTML = task.completionDate;
            if (task.employees != null) {
                tdResourceNames.innerHTML = taskResources;
            }

            tr.appendChild(tdNo);
            tr.appendChild(tdTask);
            tr.appendChild(tdDescription);
            tr.appendChild(tdStartDate);
            tr.appendChild(tdActualStartTime);
            tr.appendChild(tdEstimate);
            tr.appendChild(tdPredicatedDelay);
            tr.appendChild(tdCompletionTime);
            tr.appendChild(tdActualCompletionTime);
            tr.appendChild(tdPred);
            tr.appendChild(tdResourceNames);
            tr.appendChild(tdControls);
            tableBody.appendChild(tr);
        }
        table.appendChild(tableBody);
    }
    showChart(dataTableActualTime, dataTableBaseTime);

    $('[data-toggle="tooltip"]').tooltip();
}

function deleteTask(id) {
    $.ajax({
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(id),
        url: "/manager/delete",
        timeout: 100000,
        success: function (data) {
            var selectedProject = getParameterByName('selectedProject');
            if (typeof selectedProject === "undefined" || selectedProject == null) {
            } else {
                selectProject(selectedProject);
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function deleteSprint(id) {
    $.ajax({
        type: "POST",
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(id),
        url: "/manager/deleteSprint",
        timeout: 100000,
        success: function (data) {
            var selectedProject = getParameterByName('selectedProject');
            if (typeof selectedProject === "undefined" || selectedProject == null) {
            } else {
                selectProject(selectedProject);
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function onLoad() {

    var selectedProject = getParameterByName('selectedProject');
    if (typeof selectedProject === "undefined" || selectedProject == null) {
    } else {
        selectProject(selectedProject);
    }
    getProjects();
}

window.onload = onLoad();