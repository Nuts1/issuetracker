/**
 * Created by nuts on 26.01.17.
 */
var monthNames = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
];
var weekday = new Array(7);
weekday[0] = "Sun";
weekday[1] = "Mon";
weekday[2] = "Tue";
weekday[3] = "Wed";
weekday[4] = "Thu";
weekday[5] = "Fri";
weekday[6] = "Sat";

var selected;

function onChangeFilter() {
    var idPr;
    if (selected === 'resourceList') {
        idPr = document.getElementById('projectList').value;
        var idEmpl = document.getElementById('employeeList').value;
        history.pushState(null, null, '/customer/resourceList.html?idProject=' + idPr + '&idEmployee=' + idEmpl);
        updateResourceList();
    } else if (selected === 'projectStatistic') {
        idPr = document.getElementById('projectList').value;
        history.pushState(null, null, '/customer/resourceList.html?idProject=' + idPr);
        updateProjectStatistic();
    } else if (selected === 'sprintStatistic') {
        idPr = document.getElementById('projectList').value;
        history.pushState(null, null, '/customer/resourceList.html?idSprint=' + idPr);
        updateSprintStatistic();
    }
}

Date.prototype.yyyymmdd = function () {
    //Grab each of your components
    var yyyy = this.getFullYear().toString();
    var MM = (this.getMonth() + 1).toString();
    var dd = this.getDate().toString();

    return yyyy + '-' + (MM[1] ? MM : "0" + MM[0]) + '-' + (dd[1] ? dd : "0" + dd[0]);
};

Date.prototype.daysInMonth = function () {
    return 33 - new Date(this.getFullYear(), this.getMonth(), 33).getDate();
};

function getDayNumber(date) {
    return date.getDate();
}

var resourceListData;

function minDate(date1, date2) {
    if (date1 < date2)
        return date1;
    else
        return date2;
}

function maxDate(date1, date2) {
    if (date1 < date2)
        return date2;
    else
        return date1;
}

function writeResourceTable(data) {
    var content = document.getElementById('content');
    content.innerHTML =
        '<div class="alert alert-warning">' +
        '  <strong></strong> Not planned days of work on a task. (Delay)' +
        '</div>' +

        '<div class="alert alert-danger">' +
        ' <strong>Danger!</strong> Overtime.' +
        '</div>';

    var divTableName = document.createElement('div');
    divTableName.setAttribute('class', "col-sm-6");
    divTableName.setAttribute('style', "width: 300px;");

    var divTableHours = document.createElement('div');
    divTableHours.setAttribute('class', "col-sm-8");

    var divTableHoursSlider = document.createElement('div');
    divTableHoursSlider.setAttribute('class', "container-fluid");
    divTableHoursSlider.setAttribute('style', "overflow-x:scroll;");

    var tableName = document.createElement('table');
    tableName.setAttribute('class', "table table-bordered");
    tableName.innerHTML = "";
    var tableHours = document.createElement('table');
    tableHours.setAttribute('class', "table table-bordered");

    tableHours.innerHTML = "";
    var tbodyName = document.createElement('tbody');
    var tbodyHours = document.createElement('tbody');

    var startDate = new Date(data.interval[0]);
    var completionDate = new Date(data.interval[1]);

    var startDateForm = new Date(document.getElementById('startDate').value);
    var completionDateForm = new Date(document.getElementById('completionDate').value);

    if (startDateForm != null && !isNaN(startDateForm.getTime())) {
        startDate = maxDate(startDate, startDateForm);
    }

    if (completionDateForm != null && !isNaN(completionDateForm.getTime())) {
        completionDate = minDate(completionDate, completionDateForm);
    }

    createEmployeeNameTableHead(tableName);
    createEmployeeTaskTAbleHead(tableHours, startDate, completionDate);
    for (var i = 0; i < data.employees.length; i++) {
        createEmployeeNameRow(tbodyName, tbodyHours, data.employees[i], startDate, completionDate);
        createEmployeeProjectRow(tbodyName, tbodyHours, data.employees[i], startDate, completionDate);
    }
    tableName.appendChild(tbodyName);
    tableHours.appendChild(tbodyHours);
    divTableName.appendChild(tableName);

    divTableHoursSlider.appendChild(tableHours);
    divTableHours.appendChild(divTableHoursSlider);

    content.appendChild(divTableName);
    content.appendChild(divTableHours);
}

function createEmployeeNameTableHead(tableName) {
    var tableHead = document.createElement('thead');

    tableHead.innerHTML = ' <tr><td>Year/Month</td></tr><tr>' +
        '<th>Name/Day</th>' +
        '</tr>';
    tableName.appendChild(tableHead);
}

function createEmployeeTaskTAbleHead(tableHours, startDate, completionDate) {
    var tableHead = document.createElement('thead');
    var date = new Date(startDate);
    date.setHours(0, 0, 0, 0)
    var tr2 = document.createElement('tr');
    var tr1 = document.createElement('tr');
    var td1 = document.createElement('td');
    tr2.setAttribute('class', 'success');
    var currentYear = date.getFullYear();
    var currentMonth = date.getMonth();
    var currentDay = date.getDate();
    var columnForMerge = date.daysInMonth() - currentDay + 1;
    td1.setAttribute('colspan', columnForMerge);
    td1.setAttribute('style', "min-width:150px");
    td1.innerHTML = date.getFullYear() + " " + monthNames[currentMonth];
    tr1.appendChild(td1);
    for (; date <= completionDate; date.setDate(date.getDate() + 1)) {
        if ((date.getFullYear() != currentYear || date.getMonth() != currentMonth) &&
            date.getDate() == 1) {
            currentYear = date.getFullYear();
            currentMonth = date.getMonth();
            columnForMerge = date.daysInMonth();

            var tdTemp = document.createElement('td');
            tdTemp.setAttribute('colspan', columnForMerge);
            tdTemp.setAttribute('style', "min-width:150px");
            tdTemp.innerHTML = date.getFullYear() + " " + monthNames[date.getMonth()];
            tr1.appendChild(tdTemp);
        }
        var td2 = document.createElement('td');
        td2.setAttribute('style', "min-width:70px");
        td2.innerHTML = date.getDate() + " " + weekday[date.getDay()];
        tr2.appendChild(td2);
    }
    tableHead.appendChild(tr1);
    tableHead.appendChild(tr2);
    tableHours.appendChild(tableHead);
}


function createEmployeeNameRow(tbodyName, tbodyHours, employee, startDate, completionDate) {
    var tr = document.createElement('tr');
    var td = document.createElement('td');
    tr.setAttribute('class', 'success');
    td.innerHTML = employee.name + ". Works: " + employee.totalWorks + " hours";
    tr.appendChild(td);
    tbodyName.appendChild(tr);

    var date = new Date(startDate);
    date.setHours(0, 0, 0, 0);
    var tr2 = document.createElement('tr');
    tr2.setAttribute('class', 'success');
    for (; date <= completionDate; date.setDate(date.getDate() + 1)) {
        var td2 = document.createElement('td');
        td2.innerHTML = "_";
        for (var j = 0; j < employee.totalDayWorks.length; j++) {
            var date2 = new Date(employee.totalDayWorks[j].date);
            date2.setHours(0, 0, 0, 0);
            if (date.valueOf() == date2.valueOf()) {
                var works = employee.totalDayWorks[j].works;
                if (works > 8) {
                    td2.setAttribute('class', 'danger');
                    td2.setAttribute('title', 'Overtime');
                }
                td2.innerHTML = works + " h";
                break;
            }
        }
        tr2.appendChild(td2);
    }
    tbodyHours.appendChild(tr2);
}

function createEmployeeProjectRow(tbodyName, tbodyHours, employee, startDate, completionDate) {
    for (var i = 0; i < employee.projectListItems.length; i++) {
        var tr = document.createElement('tr');
        tr.setAttribute('style', 'background:  #b2b6ba;');
        var td = document.createElement('td');
        td.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;Project: " + employee.projectListItems[i].nameProject;
        tr.appendChild(td);
        tbodyName.appendChild(tr);

        var date = new Date(startDate);
        date.setHours(0, 0, 0, 0);

        var tr2 = document.createElement('tr');
        tr2.setAttribute('style', 'background:  #b2b6ba;');
        for (; date <= completionDate; date.setDate(date.getDate() + 1)) {
            var td2 = document.createElement('td');
            td2.innerHTML = "_";
            for (var k = 0; employee.projectListItems[i].totalDayWorks != null &&
            k < employee.projectListItems[i].totalDayWorks.length; k++) {
                var date2 = new Date(employee.projectListItems[i].totalDayWorks[k].date);
                date2.setHours(0, 0, 0, 0);
                if (date.valueOf() == date2.valueOf()) {
                    var works = employee.projectListItems[i].totalDayWorks[k].works;
                    if (works > 8) {
                        td2.setAttribute('class', 'danger');
                        td2.setAttribute('title', 'Overtime');
                    }
                    td2.innerHTML = works + " h";
                    break;
                }
            }
            tr2.appendChild(td2);
        }
        tbodyHours.appendChild(tr2);
        createEmployeeSprintsRow(tbodyName, tbodyHours, employee.projectListItems[i].sprintListItems, startDate, completionDate)
    }
}

function createEmployeeSprintsRow(tbodyName, tbodyHours, sprintListItems, startDate, completionDate) {
    for (var i = 0; i < sprintListItems.length; i++) {
        var tr = document.createElement('tr');
        tr.setAttribute('style', 'background: #d7dce0;');
        var td = document.createElement('td');
        td.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sprint: " + sprintListItems[i].name;
        tr.appendChild(td);
        tbodyName.appendChild(tr);

        var date = new Date(startDate);
        date.setHours(0, 0, 0, 0);

        var tr2 = document.createElement('tr');
        tr2.setAttribute('style', 'background:  #d7dce0;');
        for (; date <= completionDate; date.setDate(date.getDate() + 1)) {
            var td2 = document.createElement('td');
            td2.innerHTML = "_";
            for (var k = 0; sprintListItems[i].totalDayWorks != null &&
            k < sprintListItems[i].totalDayWorks.length; k++) {
                var date2 = new Date(sprintListItems[i].totalDayWorks[k].date);
                date2.setHours(0, 0, 0, 0);
                if (date.valueOf() == date2.valueOf()) {
                    var works = sprintListItems[i].totalDayWorks[k].works;
                    if (works > 8) {
                        td2.setAttribute('class', 'danger');
                        td2.setAttribute('title', 'Overtime');
                    }
                    td2.innerHTML = works + " h";
                    break;
                }
            }
            tr2.appendChild(td2);
        }
        tbodyHours.appendChild(tr2);
        createEmployeeTaskRow(tbodyName, tbodyHours, sprintListItems[i].taskItems, startDate, completionDate);
    }
}

function createEmployeeTaskRow(tbodyName, tbodyHours, taskItems, startDate, completionDate) {
    for (var i = 0; i < taskItems.length; i++) {
        var tr = document.createElement('tr');
        var td = document.createElement('td');
        var tabs = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        td.innerHTML = tabs
            + taskItems[i].name
            + " Units: " + taskItems[i].load + "%";
        tr.appendChild(td);
        tbodyName.appendChild(tr);

        var date = new Date(startDate);
        date.setHours(0, 0, 0, 0);

        var tr2 = document.createElement('tr');
        for (; date <= completionDate; date.setDate(date.getDate() + 1)) {
            var td2 = document.createElement('td');
            td2.innerHTML = "_";
            for (var k = 0; taskItems[i].dayWorks != null &&
            k < taskItems[i].dayWorks.length; k++) {
                var date2 = new Date(taskItems[i].dayWorks[k].date);
                date2.setHours(0, 0, 0, 0);
                if (date.valueOf() == date2.valueOf()) {
                    var works = taskItems[i].dayWorks[k].works;
                    if (works > 8) {
                        td2.setAttribute('class', 'danger');
                        td2.setAttribute('title', 'Overtime');
                    }
                    if (taskItems[i].dayWorks[k].isDelay == true) {
                        td2.setAttribute('class', 'alert alert-warning');
                        td2.setAttribute('title', 'Delaty');
                    }
                    td2.innerHTML = works + " h";
                    break;
                }
            }
            tr2.appendChild(td2);
        }
        tbodyHours.appendChild(tr2);
    }
}


function selectResourceList() {
    selected = 'resourceList';
    getProjects();
    updateResourceList();
    getEmployee();
    document.getElementById('projectListDiv').hidden = false;
    document.getElementById('employeeListDiv').hidden = false;
    document.getElementById('periodDiv').hidden = false;

    document.getElementById('startDate').min = new Date(resourceListData.interval[0]).yyyymmdd();
    document.getElementById('startDate').max = new Date(resourceListData.interval[1]).yyyymmdd();

    document.getElementById('completionDate').min = new Date(resourceListData.interval[0]).yyyymmdd();
    document.getElementById('completionDate').max = new Date(resourceListData.interval[1]).yyyymmdd();

}

function onChangeInterval() {
    writeResourceTable(resourceListData);
}


function updateResourceList() {
    var projectId = getParameterByName('idProject');
    var employeeId = getParameterByName('idEmployee');
    var idPr = parseInt(projectId);
    var idEmpl = parseInt(employeeId);
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/customer/resources?idProject=" + idPr + "&idEmployee=" + idEmpl,
        timeout: 100000,
        async: false,
        success: function (data) {
            if (data == null) {
                alert("Error");
            } else {
                resourceListData = data;
                writeResourceTable(data);
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function projectsWrite(data) {
    if (data != null) {
        var projectList = document.getElementById('projectList');
        projectList.innerHTML = '';

        var option = document.createElement('option');
        option.setAttribute('value', '');
        option.setAttribute('selected', true);
        option.innerHTML = 'For all projects';
        projectList.appendChild(option);

        for (var i = 0; i < data.length; i++) {
            option = document.createElement('option');
            option.setAttribute('id', data[i].projectId);
            option.setAttribute('value', data[i].projectId);
            option.innerHTML = 'Project: ' + data[i].name;
            projectList.appendChild(option);
        }
    }
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

function getEmployee() {
    var projectId = getParameterByName('projectId');

    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/customer/employees?idProject=" + projectId,
        timeout: 100000,
        success: function (data) {
            if (data != null) {
                var employeeList = document.getElementById('employeeList');
                employeeList.innerHTML = '';
                option = document.createElement('option');
                option.setAttribute('value', '');
                option.setAttribute('selected', true);
                option.innerHTML = 'For all employee';
                employeeList.appendChild(option);

                for (var i = 0; i < data.length; i++) {
                    var option = document.createElement('option');
                    option.setAttribute('id', data[i].employeeId);
                    option.setAttribute('value', data[i].employeeId);
                    option.innerHTML = data[i].name + ' ' + data[i].surname;
                    employeeList.appendChild(option);
                }
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}


///-------------------------------------------------------------------------------------

function selectProjectStatistic() {
    document.getElementById('projectListDiv').hidden = false;
    document.getElementById('employeeListDiv').hidden = true;
    document.getElementById('periodDiv').hidden = true;
    getProjects();
    var projectList = document.getElementById('content');
    projectList.innerHTML = '';
    selected = 'projectStatistic';
}

function writeStatisticTable(data) {
    var content = document.getElementById('content');
    content.innerHTML = '';

    var table = document.createElement('table');
    table.setAttribute('class', "table table-bordered");
    table.setAttribute('style', "margin: 10px;");


    var tableBody = document.createElement('tbody');
    var tableHead = document.createElement('thead');


    tableBody.innerHTML = ' <tr>' +
        '<th>Name</th>' +
        '<th>Value</th>' +
        '</tr>';
    table.appendChild(tableBody);

    var obj = data.statistic;

    for (var key in obj) {
        var name = key;
        var value = obj[key];

        var tr = document.createElement('tr');
        var tdName = document.createElement('td');
        var tdValue = document.createElement('td');
        tdName.innerHTML = name;
        tdValue.innerHTML = value;


        tr.appendChild(tdName);
        tr.appendChild(tdValue);
        tableBody.appendChild(tr);
    }
    table.appendChild(tableBody);


    var tbodyName = document.createElement('tbody');
    content.appendChild(table);
}

function updateProjectStatistic() {
    var projectId = getParameterByName('idProject');
    var idPr = parseInt(projectId);
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/customer/projectStatistic?idProject=" + idPr,
        timeout: 100000,
        async: false,
        success: function (data) {
            if (data == null) {
                alert("Error");
            } else {
                writeStatisticTable(data);
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}


//--------------------------------------------------------------------------

function selectSprintStatistic() {
    document.getElementById('projectListDiv').hidden = false;
    document.getElementById('employeeListDiv').hidden = true;
    document.getElementById('periodDiv').hidden = true;
    getSprints();
    var content = document.getElementById('content');
    content.innerHTML = '';
    selected = 'sprintStatistic';
}

function updateSprintStatistic() {
    var sprintId = getParameterByName('idSprint');
    var idSpr = parseInt(sprintId);
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/customer/sprintStatistic?idSprint=" + idSpr,
        timeout: 100000,
        async: false,
        success: function (data) {
            if (data == null) {
                alert("Error");
            } else {
                writeStatisticTable(data);
            }
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

function getSprints() {
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/customer/sprints",
        timeout: 100000,
        success: function (data) {
            writeSprint(data);
        },
        error: function (e) {
            console.log("ERROR: ", e);
        }
    });
}

function writeSprint(data) {
    var sprintList = document.getElementById('projectList');
    sprintList.innerHTML = '';

    var option = document.createElement('option');
    option.setAttribute('value', '');
    option.setAttribute('selected', true);
    option.innerHTML = 'For all projects';
    sprintList.appendChild(option);

    for (var i = 0; i < data.length; i++) {
        option = document.createElement('option');
        option.setAttribute('id', data[i].sprintId);
        option.setAttribute('value', data[i].sprintId);
        option.innerHTML = 'Sprint:' + data[i].name;
        sprintList.appendChild(option);
    }
}
