var developersId = [];
var developerUnits = [];
var developersName = [];
var preliminaryTasks = {};
var validationInfo;
var idCurrentTask;
var idCurrentSprint;
var sprintCompletionDate;
var sprintStartDate;

var minHh;

Date.prototype.yyyymmdd = function () {
    //Grab each of your components
    var yyyy = this.getFullYear().toString();
    var MM = (this.getMonth() + 1).toString();
    var dd = this.getDate().toString();

    return yyyy + '-' + (MM[1] ? MM : "0" + MM[0]) + '-' + (dd[1] ? dd : "0" + dd[0]);
};

Date.prototype.hh = function () {
    //Grab each of your components
    var hh = this.getHours().toString();

    return hh;
};

function applyFilter() {
    var department = document.getElementById('selectDepartment');
    var position = document.getElementById('developerPosition');
    var idDept = department.options[department.selectedIndex].id;
    var idPos = position.options[position.selectedIndex].id;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/applyFilter?idDept=" + idDept + "&idPos=" + idPos,
        timeout: 100000,
        success: function (data) {
            updateDeveloperList(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function addDevelopers() {
    $("input:checkbox[name=developer]:checked").each(function () {
        if (developersId.indexOf(parseInt($(this).val())) < 0) {
            developersId.push(parseInt($(this).val()));
            developersName.push($(this).next('label').text());
        }
    });
    updateSelectedDevelopers();
}

function updateSelectedDevelopers() {
    var selectedDevelopers = document.getElementById('selectedDevelopers');
    selectedDevelopers.innerHTML = "";
    for (var i = 0; i < developersName.length; i++) {
        var li = document.createElement('li');
        li.setAttribute('id', developersId[i]);
        li.setAttribute('class', 'list-group-item');
        li.setAttribute('value', developersId[i]);
        li.innerHTML = developersName[i] + '. Units: <input title="Units" id="unit' + developersId[i] + '" value="100" required type="number" min="0" max = "100"/>%  ' + ' <input class="btn-primary" type="button" value="Delete from list" onClick="deleteFromList('
            + developersId[i] + ')"/> ';
        selectedDevelopers.appendChild(li);
    }
    console.log(JSON.stringify(developersId));
}

function deleteFromList(id) {
    var index = developersId.indexOf(parseInt(id));
    if (index >= 0) {
        developersId.splice(index, 1);
        developersName.splice(index, 1);
    }
    updateSelectedDevelopers();
}

function updateDeveloperList(data) {
    var list = document.getElementById('developerList');
    list.innerHTML = "";
    var label = document.createElement('label');
    label.innerHTML = "Developer List";
    list.appendChild(label);
    if (typeof data === "undefined" || data == null) {
    } else {
        for (var i = 0; i < data.length; i++) {
            var div = document.createElement('div');
            div.setAttribute('class', 'checkbox');
            div.innerHTML = '<label>' +
                '<input name="developer" type="checkbox" value="' + data[i].employeeId + '">' +
                data[i].name + ' ' + data[i].surname +
                '</input><label style="display: none;">' +
                data[i].name + ' ' + data[i].surname +
                '</label>';
            list.appendChild(div);
        }
    }
}

function setPosition() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/positions",
        timeout: 100000,
        success: function (data) {
            updatePosition(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function setDepartments() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/departments",
        timeout: 100000,
        success: function (data) {
            updateDepartment(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function updatePosition(data) {
    var developerPosition = document.getElementById('developerPosition');
    for (var i = 0; i < data.length; i++) {
        var option = document.createElement('option');
        option.setAttribute('id', data[i].positionId);
        option.innerHTML = data[i].position;
        developerPosition.appendChild(option);
    }
}

function updateDepartment(data) {
    var departmentSelect = document.getElementById('selectDepartment');
    var option = document.createElement('option');
    option.innerHTML = 'No Department';
    option.setAttribute('id', null);

    departmentSelect.appendChild(option);

    for (var i = 0; i < data.length; i++) {
        option = document.createElement('option');
        option.setAttribute('id', data[i].departmentId);
        option.innerHTML = data[i].name;
        departmentSelect.appendChild(option);
    }
}

function setPreliminaryTask() {
    var idSprint = getParameterByName('sprintId');
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/getTaskBySprintId?idSprint=" + idSprint,
        timeout: 100000,
        success: function (data) {
            updatePreliminaryTask(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function setMinDate() {
    var startTime = document.getElementById('startTime');
    var preliminaryTaskId = document.getElementById('preliminaryTask').value;
    var startDate = document.getElementById('startDate');
    startDate.setAttribute('min', preliminaryTasks[preliminaryTaskId].yyyymmdd());
    startDate.value = (preliminaryTasks[preliminaryTaskId].yyyymmdd());

    minHh = preliminaryTasks[preliminaryTaskId].hh();
    console.log(minHh);
    startTime.value = minHh;
}

function updatePreliminaryTask(data) {
    var preliminaryTask = document.getElementById('preliminaryTask');
    if (typeof(data) !== "undefined" && data !== null) {
        for (var i = 0; i < data.length; i++) {
            if (idCurrentTask != data[i].taskId) {
                preliminaryTasks[data[i].taskId] = new Date(data[i].completionDate);

                var option = document.createElement('option');
                option.setAttribute('id', data[i].taskId);
                option.setAttribute('value', data[i].taskId);
                option.innerHTML = data[i].name + '. Completion: ' + data[i].completionDate;
                preliminaryTask.appendChild(option);
            }
        }
    }
}

function validateForm() {
    validationInfo = "";
    var error = document.getElementById('error');
    error.setAttribute("hidden", true);

    var form = document.getElementById('form');
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].value === '' && form.elements[i].hasAttribute('required')) {
            validationInfo = validationInfo + (form.elements[i].name + ' is required field!<br>');
        }
    }

    startDate = new Date(document.getElementById('startDate').value);
    completionDate = new Date(document.getElementById('completionDate').value);

    if (startDate > sprintCompletionDate) {
        validationInfo = validationInfo + "Start date after sprint completion date<br>";
    }

    if (startDate < sprintStartDate) {
        validationInfo = validationInfo + "Start date befor sprint start date<br>";
    }

    if (completionDate > sprintCompletionDate) {
        validationInfo = validationInfo + "Completion date after sprint completion date<br>";
    }

    if (validationInfo != "") {
        error.removeAttribute('hidden');
        error.innerHTML = validationInfo;
        return false;
    }

    return true;
}

function changeCompletionDate() {
    var startTime = document.getElementById('startTime');

    var estimate = document.getElementById('estimate').value;
    var completionDate = document.getElementById('completionDate');
    var completionTime = document.getElementById('completionTime');
    var startDate = document.getElementById('startDate');
    var selectedOprion = startTime.options[startTime.selectedIndex];

    console.log(startDate.getAttribute('min'));
    console.log(startDate.value);
    if (startDate.value == startDate.getAttribute('min')) {
        if (typeof(selectedOprion) !== "undefined" && selectedOprion !== null) {
            if (parseInt(selectedOprion.value) < parseInt(minHh)) {
                startTime.selectedIndex = minHh - 8;
            }
        }
        console.log(startTime.value)
    } else {
        startTime.removeAttribute('min', minHh);
    }
    var time;
    var days;
    if (typeof(selectedOprion) !== "undefined" && selectedOprion !== null) {
        var hours = (parseInt(estimate) + (parseInt(selectedOprion.value) - 8));
        days = parseInt(hours / 8);// 8 - work hours
        time = hours - days * 8;
        completionTime.selectedIndex = time;
    } else {
        var hours = (parseInt(estimate));
        days = parseInt(hours / 8);// 8 - work hours
        time = hours - days * 8;
        completionTime.selectedIndex = time;
    }
    var date = new Date(startDate.value);
    date.setDate(date.getDate() + days);
    completionDate.valueAsDate = date;
}

function Update(formData, url) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: formData,
        url: url,
        timeout: 100000,
        success: function (data) {
            if (data != null) {
                var error = document.getElementById('error');
                error.removeAttribute('hidden');
                error.innerHTML = data;
                return false;
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function sumbit() {
    if (validateForm()) {
        developerUnits = [];
        var devs = document.getElementById('employeeIds');
        var devUnits = document.getElementById('employeeUnits');
        devs.setAttribute('value', JSON.stringify(developersId));
        developersId.forEach(function (element) {
            developerUnits.push(parseInt(document.getElementById('unit' + element).value));
            console.log('Units' + document.getElementById('unit' + element).value);
        });
        devUnits.setAttribute('value', JSON.stringify(developerUnits));
        var idTask = document.getElementById('idTask');
        idTask.setAttribute('value', idCurrentTask);
        var idSprint = document.getElementById('idSprint');
        idSprint.setAttribute('value', idCurrentSprint);

        var formData = "{";
        var x = $("form").serializeArray();
        $.each(x, function (i, field) {
            formData = formData + '"' + field.name + '": "' + field.value + '" ,';
        });
        formData = formData.substr(0, formData.length - 2);
        formData = formData + "}";

        alert(formData);
        Update(formData, "/manager/edit");
    } else {

    }
}

function setTaskData(taskId) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/getTask?idTask=" + taskId,
        timeout: 100000,
        success: function (data) {
            if (data == null) {
                alert("No such task");
                window.location.href = "/manager/projects.html";
            }
            console.log(data);
            setTaskDataFromJson(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function setTaskDataFromJson(data) {
    var employees = data.employees;
    for (var i = 0; i < employees.length; i++) {
        developersId.push(employees[i].employeeId);
        developersName.push(employees[i].name + " " + employees[i].surname);
    }
    updateSelectedDevelopers();
    document.getElementById('description').value = data.description;
    document.getElementById('startDate').valueAsDate = new Date(data.startDate);
    document.getElementById('name').value = data.name;
    document.getElementById('completionDate').valueAsDate = new Date(data.completionDate);
    document.getElementById('estimate').value = data.estimate;
    if (typeof(data.previousTaskId) !== "undefined" && data.previousTaskId !== null) {
        document.getElementById('preliminaryTask').value = data.previousTaskId;
    }
}

function setDate() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/sprintInfo?idSprint=" + idCurrentSprint,
        timeout: 100000,
        success: function (data) {
            sprintStartDate = new Date(data.startDate);
            sprintCompletionDate = new Date(data.completionDate);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function onLoad() {
    idCurrentTask = parseInt(getParameterByName('idTask'));

    if (isNaN(idCurrentTask)) {
        alert("No such task");
        window.location.href = "/manager/projects.html";
    }

    idCurrentSprint = parseInt(getParameterByName('sprintId'));

    if (isNaN(idCurrentSprint)) {
        alert("No such sprint");
        window.location.href = "/manager/projects.html";
    }
    setDate();

    setDepartments();
    setPreliminaryTask();
    setPosition();
    setTaskData(idCurrentTask);
}

document.onload = onLoad();
