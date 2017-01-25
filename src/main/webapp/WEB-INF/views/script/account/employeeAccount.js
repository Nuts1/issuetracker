function onLoad() {
  getEmployeeData();
}

function getEmployeeData() {
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/util/getEmployee",
    timeout : 100000,
    success : function(data) {
      if(data != null) {
        document.getElementById('name').value = data.name;
        document.getElementById('surname').value = data.surname;
        document.getElementById('email').value = data.email;
        document.getElementById('role').value = data.role.name;
        if(data.position != null) {
          document.getElementById('position').value = data.position.position;
        }
        if(data.qualification != null) {
          document.getElementById('qualification').value = data.qualification.qualification;
        }
      }
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function changePassword() {
  var info = document.getElementById('info');
  var infoStr = '';
  info.setAttribute("hidden", true);
  var old = document.getElementById('old').value;
  var new1 = document.getElementById('new1').value;
  var new2 = document.getElementById('new2').value;
  if(old == '') {
    infoStr = infoStr + "type old password<br>";
  }
  if(new1.length < 5) {
    infoStr = infoStr + "length < 5<br>";
  }
  if(new1 != new2) {
    infoStr = infoStr + "password not compare<br>";
  }
  if(infoStr != '') {
    info.innerHTML = infoStr;
    info.removeAttribute('hidden');
  } else {
    $.ajax({
      type : "GET",
      contentType : "application/json",
      url : "/util/changePassword",
      timeout : 100000,
      data: {
          oldPassword: old,
          newPassword: new2
      },
      success : function(data) {
        if(data != null) {
          info.innerHTML = data;
          info.removeAttribute('hidden');
        }
      },
      error : function(e) {
        console.log("ERROR: ", e);
      }
    });
  }
}

window.onload = onLoad();
