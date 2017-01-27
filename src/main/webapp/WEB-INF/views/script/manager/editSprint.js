var currentProject;
var sprintId;
var sprintsCompletionDate = {};

var idProject;

function setSprints(idProject) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/sprintsByProjectId?projectId=" + idProject,
        async: false,
        timeout: 100000,
        success: function (data) {
            var sprints = document.getElementById('preliminarySprints');
            for (var i = 0; i < data.length; i++) {
                if (sprintId != parseInt(data[i].sprintId)) {
                    sprintsCompletionDate[data[i].sprintId] = data[i].completionDate;
                    var option = document.createElement('option');
                    option.setAttribute('value', data[i].sprintId);
                    option.setAttribute('id', data[i].sprintId);
                    option.innerHTML = data[i].name;
                    sprints.appendChild(option);
                }
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function startDateChanged() {
    var sprintStartDate = document.getElementById('sprintStartDate');
    var sprintCompletionDate = document.getElementById('sprintCompletionDate');
    sprintCompletionDate.setAttribute('min', sprintStartDate.value);
    sprintCompletionDate.setAttribute('value', sprintStartDate.value);
}

function validateSprintForm() {
    var validationInfo = "";
    var error = document.getElementById('errorSprint');
    error.setAttribute("hidden", true);

    var form = document.getElementById('sprintForm');
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].value === '' && form.elements[i].hasAttribute('required')) {
            validationInfo = validationInfo + (form.elements[i].name + ' is required field!<br>');
        }
    }

    var index = document.getElementById('preliminarySprints').value;
    var preliminarySprintsCompletionDate = new Date(sprintsCompletionDate[index]);

    var sprintStartDate = new Date(document.getElementById('sprintStartDate').value);
    if (sprintStartDate < preliminarySprintsCompletionDate) {
        validationInfo = validationInfo + "sprint start date < preliminary dprints completion date";
    }

    if (validationInfo != "") {
        error.removeAttribute('hidden');
        error.innerHTML = validationInfo;
        return false;
    }

    return true;
}

function save(formData) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        data: formData,
        url: "/manager/editSprint",
        timeout: 100000,
        success: function (data) {
            if (data != null) {
                var error = document.getElementById('errorSprint');
                error.removeAttribute('hidden');
                error.innerHTML = data;
            }
            selectProject(str);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}


function editSrint() {
    if (validateSprintForm()) {
        var data = "{";
        var x = $("#sprintForm").serializeArray();
        $.each(x, function (i, field) {
            data = data + '"' + field.name + '": "' + field.value + '" ,';
        });
        data = data.substr(0, data.length - 2);
        data = data + "}";
        alert(data);
        save(data);
    } else {

    }
}

function setSprintData() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/manager/getSprint",
        data: JSON.stringify(parseInt(sprintId)),
        timeout: 100000,
        success: function (data) {
            if (data == null) {
                alert("No such sprint");
                window.location.href = "/manager/projects.html";
            }
            console.log(data);
            setSprintDataFromJson(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function setSprintDataFromJson(data) {
    document.getElementById('sprintStartDate').valueAsDate = new Date(data.startDate);
    document.getElementById('name').value = data.name;
    document.getElementById('sprintCompletionDate').valueAsDate = new Date(data.completionDate);
    if (typeof(data.previousSprint) !== "undefined" && data.previousSprint !== null) {
        document.getElementById('preliminarySprints').value = data.previousSprint;
    }

}

function onLoad() {
    currentProject = parseInt(getParameterByName('idProject'));
    if (isNaN(currentProject)) {
        alert("No project selected");
    }

    sprintId = parseInt(getParameterByName('sprintId'));

    if (isNaN(sprintId)) {
        alert("No such sprint");
        window.location.href = "/manager/projects.html";
    }
    setSprints(currentProject);

    var projectId = document.getElementById('projectId');
    projectId.value = currentProject;
    var sprintIdInput = document.getElementById('sprintId');
    sprintIdInput.value = sprintId;
    setSprintData();
}

document.onload = onLoad();
