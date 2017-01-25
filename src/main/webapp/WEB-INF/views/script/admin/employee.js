var selectedEmployee;
function onLoad() {
  getRole();
  getPosition();
  getQualification();
  selectedEmployee = getParameterByName('selectedEmployee');
  if(selectedEmployee != null && typeof selectedEmployee !== "undefined") {
    writeEmployeeData(selectedEmployee);
  }
  check();
}

function getRole() {
  var roles = document.getElementById('roleSelect');
  var roles2 = document.getElementById('selecRole');
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/admin/getRoles",
    timeout : 100000,
    success : function(data) {
      if(data != null) {
        var option = document.createElement('option');
        var option2 = document.createElement('option');
        for(var i = 0; i < data.length; i++) {
          option.setAttribute('value', data[i].roleId);
          option.setAttribute('name', data[i].roleId);
          option.setAttribute('id', data[i].roleId);
          option.innerHTML = data[i].name;
          option2.setAttribute('value', data[i].roleId);
          option2.setAttribute('name', data[i].roleId);
          option2.setAttribute('id', data[i].roleId);
          option2.innerHTML = data[i].name;
          roles.appendChild(option);
          roles2.appendChild(option2);
          option = document.createElement('option');
          option2 = document.createElement('option');
        }
        option.selected = true;
      }
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function getPosition() {
  var selectType = document.getElementById('selectType');
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/admin/getPosition",
    timeout : 100000,
    success : function(data) {
      if(data != null) {
        var option = document.createElement('option');
        for(var i = 0; i < data.length; i++) {
          option.setAttribute('value', data[i].positionId);
          option.setAttribute('name', data[i].positionId);
          option.setAttribute('id', data[i].positionId);
          option.innerHTML = data[i].position;

          selectType.appendChild(option);
          option = document.createElement('option');
        }
        option.selected = true;
      }
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

var ROLE_EMPLOYEE = 3;
function check() {
  var role = document.getElementById('selecRole').value;
  var selectType = document.getElementById('selectType');
  var selectQualification = document.getElementById('selectQualification');

  if(parseInt(role) != ROLE_EMPLOYEE) {
    selectType.disabled  = true;
    selectQualification.disabled  = true;
  } else {
    selectType.disabled  = false;
    selectQualification.disabled  = false;
  }
}

function getQualification() {
  var selectQualification = document.getElementById('selectQualification');
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/admin/getQualification",
    timeout : 100000,
    success : function(data) {
      if(data != null) {
        var option = document.createElement('option');
        for(var i = 0; i < data.length; i++) {
          option.setAttribute('value', data[i].qualificationId);
          option.setAttribute('name', data[i].qualificationId);
          option.setAttribute('id', data[i].qualificationId);
          option.innerHTML = data[i].qualification;

          selectQualification.appendChild(option);
          option = document.createElement('option');
        }
        option.selected = true;
      }
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function getByRole() {
  var role = document.getElementById('roleSelect').value;
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/admin/employeeByRole?role=" + role,
    timeout : 100000,
    success : function(data) {
      if(data != null) {
        var employeeList = document.getElementById('employeeList');
        employeeList.innerHTML = '';
        for(var i = 0; i < data.length; i++) {
          var li = document.createElement('li');
          li.setAttribute('class', 'list-group-item');
          li.setAttribute('style', 'margin: 5px;');
          li.innerHTML = '<a onclick="writeEmployeeData('+ data[i].employeeId + ')">' + data[i].name + " " + data[i].surname + '</a>';
          employeeList.appendChild(li);
        }
      }
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function writeEmployeeData(id) {
  var employee;
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/admin/getEmployee?id="+id,
    timeout : 100000,
    success : function(employee) {
      if(employee != null) {
        document.getElementById('name').value = employee.name;
        document.getElementById('surname').value = employee.surname;
        document.getElementById('email').value = employee.email;
        document.getElementById('selecRole').value = employee.role.roleId;
        check();
        if(employee.position != null) {
          document.getElementById('selectType').value = employee.position.positionId;
        }
        if(employee.qualification != null) {
          document.getElementById('selectQualification').value = employee.qualification.qualificationId;
        }
        history.pushState(null, null, '/admin/employeeAdmin.html?selectedEmployee=' + employee.employeeId);
      } else {
        alert('No such employee');
      }
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function buttonAddNewEmployee() {
  history.pushState(null, null, '/admin/employeeAdmin.html');
  document.getElementById('name').value = '';
  document.getElementById('surname').value = '';
  document.getElementById('email').value = "";
  document.getElementById('selecRole').value = "";
  document.getElementById('selectType').value = "";
  document.getElementById('selectQualification').value = "";
}


function getData() {
  var formData = "{";
  var x = $("#form").serializeArray();
  $.each(x, function(i, field){
      formData = formData + '"' + field.name + '": "' + field.value + '" ,';
  });
  formData = formData.substr(0, formData.length - 2);
  formData = formData + "}";
  alert(formData);
  return formData;
}

function updateEmployee() {
  $.ajax({
    type : "POST",
    contentType : "application/json",
    url : "/admin/updateEmployee",
    data: getData(),
    timeout : 100000,
    success : function(data) {

    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function addEmployee() {
  $.ajax({
    type : "POST",
    contentType : "application/json",
    url : "/admin/addEmployee",
    data: getData(),
    timeout : 100000,
    success : function(data) {

    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function sumbit() {
  if(validateForm()) {
    selectedEmployee = getParameterByName('selectedEmployee');
    if(typeof selectedEmployee === "undefined" || selectedEmployee == null) {
      addEmployee();
    } else {
      document.getElementById('employeeId').value = selectedEmployee;
      updateEmployee();
    }
  }
}

function validateForm() {
  var validationInfo = "";
  var error = document.getElementById('error');
  error.setAttribute("hidden", true);

  var form = document.getElementById('form');
  for(var i=0; i < form.elements.length; i++){
    if(form.elements[i].value === '' && form.elements[i].hasAttribute('required')){
      validationInfo = validationInfo + (form.elements[i].name + ' is required field!<br>');
    }
  }

  if(validationInfo != "") {
    error.removeAttribute('hidden');
    error.innerHTML = validationInfo;
    return false;
  }

  return true;

}

window.onload = onLoad();
