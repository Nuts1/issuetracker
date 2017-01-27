var selectedTask; // id

var selectedTaskObj; // object
var previousTask;
var previousTaskActualComletionDate;
var info = document.getElementById('info');


function sumbitConfrimTheTask() {
    if (previousTaskActualComletionDate == null && previousTask != null) {
        alert("Previous task not complete. Task: " + previousTask.name);
        return;
    }
    if (selectedTaskObj.startDate > new Date()) {
        alert("Actual task not started yet.");
        return;
    }
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/employee/confirmTask?taskId=" + selectedTaskObj.taskId,
        timeout: 100000,
        success: function (data) {
            info.removeAttribute('hidden');
            info.innerHTML = data;
            document.getElementById('complete').disabled = false;
            setTimeout("info.setAttribute('hidden', true)", 2000);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function sumbitCompleteTheTask() {
    if (previousTaskActualComletionDate == null && previousTask != null) {
        alert("Previous task not complete. Task: " + previousTask.name);
        return;
    }
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/employee/completeTask?taskId=" + selectedTaskObj.taskId,
        timeout: 100000,
        success: function (data) {
            info.removeAttribute('hidden');
            info.innerHTML = data;
            setTimeout("info.setAttribute('hidden', true)", 2000);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function sumbitDelay() {
    var value = parseInt(document.getElementById('delayValue').value);
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/employee/setDelay?taskId=" + selectedTaskObj.taskId + "&delay=" + value,
        timeout: 100000,
        success: function (data) {
            info.removeAttribute('hidden');
            info.innerHTML = data;
            setTimeout("info.setAttribute('hidden', true)", 2000);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function writeTaskData(id) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/employee/getTask?taskId=" + id,
        timeout: 100000,
        success: function (data) {
            selectedTaskObj = data.task;
            writeTaskDataFromJson(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function writeTaskDataFromJson(data) {
    document.getElementById('confirm').disabled = true;
    document.getElementById('complete').disabled = true;
    document.getElementById('delayValue').disabled = true;
    document.getElementById('delaySumbit').disabled = true;

    if (data != null) {
        previousTask = data.task.previousTask;
        if (previousTask != null) {
            if (previousTask.actualCompletionDate != null) {
                previousTaskActualComletionDate = new Date(data.task.actualCompletionDate);
            } else {
                previousTaskActualComletionDate = null;
            }
        }

        document.getElementById('taskName').innerHTML = data.task.name;
        var taskInfo = document.getElementById('taskInfo');
        taskInfo.innerHTML = "Start date: " + data.task.startDate +
            "<br> Completion date: " + data.task.completionDate
            + "<br> Predicated completion date: " + data.task.predicatedCompletionDate
            + "<br> Units: " + data.load
            + "<br> Description: " + data.task.description;
        if (parseInt(data.confirm) == 0) {
            document.getElementById('confirm').disabled = false;
        } else {
            document.getElementById('confirm').setAttribute('title', 'Already confrim');
            if (data.task.actualCompletionDate == null) {
                document.getElementById('complete').disabled = false;
                document.getElementById('delayValue').disabled = false;
                document.getElementById('delaySumbit').disabled = false;
            } else {
                document.getElementById('complete').setAttribute('title', 'Already complete');
            }
        }
    } else {
        alert('No such employee');
    }
}

function getAllEmployeeTasks() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/employee/getTasks",
        timeout: 100000,
        success: function (data) {
            if (data != null) {
                var taskList = document.getElementById('taskList');
                taskList.innerHTML = '';
                for (var i = 0; i < data.length; i++) {
                    var li = document.createElement('li');
                    li.setAttribute('class', 'list-group-item');
                    li.setAttribute('style', 'margin: 5px;');
                    if (data[i].actualCompletionDate != null) {
                        li.setAttribute('style', 'margin: 5px; background: #c6ffc1;');
                    }
                    li.innerHTML = '<a onclick="writeTaskData(' + data[i].taskId + ')">' + data[i].name + "Start:" + data[i].startDate + '</a>';
                    //li.innerHTML = '<a href="/manager/project?projectId=' + data[0].projectId +  '">' + data[0].name + '</a>';
                    taskList.appendChild(li);
                }
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function onLoad() {
    getAllEmployeeTasks();

    selectedTask = getParameterByName('selectedTask');
    if (selectedTask != null && typeof selectedTask !== "undefined") {
        writeTaskData(selectedTask);
    }
}

window.onload = onLoad();
