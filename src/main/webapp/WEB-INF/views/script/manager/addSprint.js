var selectedProject;
var sprintsCompletionDate = {};

function setSprints(idProject) {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/manager/sprintsByProjectId?projectId=" + idProject,
        timeout: 100000,
        success: function (data) {
            var sprints = document.getElementById('preliminarySprints');
            sprints.innerHTML = '';
            var option = document.createElement('option');
            option.value = '';
            option.innerHTML = "Have no preliminary sprints";
            sprints.appendChild(option);

            for (var i = 0; i < data.length; i++) {
                sprintsCompletionDate[data[i].sprintId] = data[i].completionDate;
                option = document.createElement('option');
                option.setAttribute('value', data[i].sprintId);
                option.innerHTML = data[i].name;
                sprints.appendChild(option);
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

function onLoad() {
    selectedProject = getParameterByName('selectedProject');
    setSprints(selectedProject);
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

    if (validationInfo != "") {
        error.removeAttribute('hidden');
        error.innerHTML = validationInfo;
        return false;
    }

    var index = document.getElementById('preliminarySprints').value;
    var preliminarySprintsCompletionDate = new Date(sprintsCompletionDate[index]);

    var projectCompletionDate = new Date(document.getElementById(selectedProject + 'ProjectCompletionDate').innerHTML);
    var projectStartDate = new Date(document.getElementById(selectedProject + 'ProjectStartDate').innerHTML);

    var sprintStartDate = new Date(document.getElementById('sprintStartDate').value);
    var sprintCompletionDate = new Date(document.getElementById('sprintCompletionDate').value);

    if (sprintStartDate < preliminarySprintsCompletionDate) {
        validationInfo = validationInfo + "sprint start date < preliminary sprints completion date<br>";
    }

    if(sprintStartDate.getDay() === 6 || startDate.getDay() === 0) {
        validationInfo = validationInfo + "Error you cannot select a weekend<br>";
    }

    if (projectStartDate > sprintStartDate) {
        validationInfo = validationInfo + "Start date befor project start date<br>";
    }

    if (projectCompletionDate < sprintCompletionDate) {
        validationInfo = validationInfo + "Completion date after project completion date<br>";
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
        url: "/manager/saveSprint",
        timeout: 100000,
        success: function (data) {
            if (data != null) {
                var error = document.getElementById('errorSprint');
                error.removeAttribute('hidden');
                error.innerHTML = data;
                return false;
            }
            selectProject(getParameterByName('selectedProject'));
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}


function sumbitSrint() {
    if (validateSprintForm()) {
        selectedProject = parseInt(getParameterByName('selectedProject'));
        if (isNaN(selectedProject)) {
            alert("No project selected");
        }
        var projectId = document.getElementById('projectId');
        projectId.setAttribute('value', selectedProject);

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

document.onload = onLoad();
