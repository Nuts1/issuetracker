var tasks = {};

function selectProject(str) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/customer/project?projectId=" + str,
        timeout: 100000,
        success: function (data) {
            history.pushState(null, null, '/customer/projects.html?selectedProject=' + str);
            google.charts.setOnLoadCallback(tableCreate(data));
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function getParameterByName(name, url) {
    if (!url) {
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function getProjects() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/customer/projectList",
        timeout: 100000,
        success: function (data) {
            projectsWrite(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}


function projectsWrite(data) {
    var projectList = document.getElementById('projectList');
    for (var i = 0; i < data.length; i++) {
        var li = document.createElement('li');
        li.setAttribute('class', 'list-group-item');
        li.innerHTML = '<a onclick="selectProject(' + data[i].projectId + ')">' + data[i].name + '</a>';
        projectList.appendChild(li);
    }
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
    if (dataTableActualTime.getNumberOfRows() > 0) {
        var ActualTimeDiv = document.getElementById('diagram');
        var chart = new google.visualization.Gantt(ActualTimeDiv);
        chart.draw(dataTableActualTime);
    }

    if (dataTableBaseTime.getNumberOfRows() > 0) {
        var BaseTimeDiv = document.getElementById('diagramBase');
        var chart = new google.visualization.Gantt(BaseTimeDiv);
        chart.draw(dataTableBaseTime);
    }
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

    var dataTableActualTime = new google.visualization.DataTable();

    dataTableActualTime.addColumn('string', 'Task ID');
    dataTableActualTime.addColumn('string', 'Task Name');
    dataTableActualTime.addColumn('string', 'Resource');
    dataTableActualTime.addColumn('date', 'Start Date');
    dataTableActualTime.addColumn('date', 'End Date');
    dataTableActualTime.addColumn('number', 'Duration');
    dataTableActualTime.addColumn('number', 'Percent Complete');
    dataTableActualTime.addColumn('string', 'Dependencies');

    var dataTableBaseTime = new google.visualization.DataTable();

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

        var sprintInfo = document.createElement('h4');
        sprintInfo.setAttribute('id', project.sprints[i].sprintId);
        sprintInfo.innerHTML = "<hr>Sprint: " + project.sprints[i].name +
            '<br><label>Start date: </label><label id="' + project.sprints[i].sprintId + 'SprintStartDate">' + project.sprints[i].startDate +
            '</label><br><label>Completion date: </label><label id="' + project.sprints[i].sprintId + 'SprintCompletionDate">' + project.sprints[i].completionDate + '</label>';
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
            tableBody.appendChild(tr);
        }
        table.appendChild(tableBody);
    }
    showChart(dataTableActualTime, dataTableBaseTime);

    $('[data-toggle="tooltip"]').tooltip();
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