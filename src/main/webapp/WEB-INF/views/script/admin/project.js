var selectedProject;

var ROLE_MANAGER = 2;
var ROLE_CUSTOMER = 4;

function onLoad() {
    getByRole(ROLE_MANAGER);
    getByRole(ROLE_CUSTOMER);
    getProjectsAdmin();
    selectedProject = getParameterByName('selectedProject');
    if (selectedProject != null && typeof selectedProject !== "undefined") {
        selectProject(selectedProject);
    }
}


function getByRole(role) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin/employeeByRole?role=" + role,
        timeout: 100000,
        success: function (data) {
            var select = null;
            if (role == ROLE_MANAGER) {
                select = document.getElementById('selectManager');
            } else if (role == ROLE_CUSTOMER) {
                select = document.getElementById('selectCustomer');
            }
            if (select != null && data != null) {
                var option = document.createElement('option');
                for (var i = 0; i < data.length; i++) {
                    option = document.createElement('option');
                    option.setAttribute('value', data[i].employeeId);
                    option.setAttribute('name', data[i].employeeId);
                    option.setAttribute('id', data[i].employeeId);
                    option.innerHTML = data[i].name + " " + data[i].surname;
                    select.appendChild(option);
                }
                option.selected = true;
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}


function deleteProject() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin/deleteProject?projectId=" + selectedProject,
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


function writeProjectData(data) {
    document.getElementById('name').value = data.name;
    document.getElementById('startDate').valueAsDate = new Date(data.startDate);
    document.getElementById('completionDate').valueAsDate = new Date(data.completionDate);
    document.getElementById('' + data.manager.employeeId).selected = true;
    document.getElementById('' + data.customer.employeeId).selected = true;
}

function buttonAddNewProject() {
    history.pushState(null, null, '/admin/project.html');
    document.getElementById('name').value = '';
    document.getElementById('startDate').valueAsDate = new Date();
    document.getElementById('projectName').innerHTML = "";
}

function selectProject(id) {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin/getProject?projectId=" + id,
        timeout: 100000,
        success: function (data) {
            if (data != null) {
                selectedProject = getParameterByName('selectedProject');
                if (typeof selectedProject === "undefined" || selectedProject == null) {
                    history.pushState(null, null, '/admin/project.html?selectedProject=' + id);
                }
                writeProjectData(data);
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function getData() {
    var formData = "{";
    var x = $("#form").serializeArray();
    $.each(x, function (i, field) {
        formData = formData + '"' + field.name + '": "' + field.value + '" ,';
    });
    formData = formData.substr(0, formData.length - 2);
    formData = formData + "}";
    alert(formData);
    return formData;
}

function updateProject() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin/updateProject",
        data: getData(),
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


function addProject() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/admin/addProject",
        data: getData(),
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
        selectedProject = getParameterByName('selectedProject');
        if (typeof selectedProject === "undefined" || selectedProject == null) {
            addProject();
        } else {
            document.getElementById('projectId').value = selectedProject;
            updateProject();
        }
    }
}

function changeCompletionDateMin() {
    var startDate = document.getElementById('startDate').value;
    document.getElementById('completionDate').setAttribute('min', startDate);
}

function validateForm() {
    var validationInfo = "";
    var error = document.getElementById('error');
    error.setAttribute("hidden", true);

    var form = document.getElementById('form');
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

    return true;
}

window.onload = onLoad();
