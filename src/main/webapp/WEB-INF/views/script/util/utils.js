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

function projectsWrite(data) {
  var projectList = document.getElementById('projectList');
  for(var i = 0; i < data.length; i++) {
    var li = document.createElement('li');
    li.setAttribute('class', 'list-group-item');
    li.innerHTML = '<a onclick="selectProject('+ data[i].projectId + ')">' + data[i].name + '</a>';
    //li.innerHTML = '<a href="/manager/project?projectId=' + data[0].projectId +  '">' + data[0].name + '</a>';
    projectList.appendChild(li);
  }
}

function getProjects() {
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/manager/projectList",
    timeout : 100000,
    success : function(data) {
      projectsWrite(data);
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}

function getProjectsAdmin() {
  $.ajax({
    type : "GET",
    contentType : "application/json",
    url : "/admin/projectList",
    timeout : 100000,
    success : function(data) {
      projectsWrite(data);
    },
    error : function(e) {
      console.log("ERROR: ", e);
    }
  });
}
