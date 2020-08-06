function collectUnitsFromSearch() {
  const inputElems = document.getElementsByName("search-units");
  const addedUnits = []
  for (let elem of inputElems) {       
    if (elem.checked){
      addedUnits.push(elem.value);
    }
  }
  return addedUnits;
}

function collectUnitsFromInput() {
  const inputElems = document.getElementsByName("num-units");
  const addedUnits = []
  for (let elem of inputElems) {       
    if (elem.checked){
      addedUnits.push(elem.value);
    }
  }
  return addedUnits;
}

function showCourses() {
  const courseResults = document.getElementById("search-results");
  const courseName = document.getElementById("search-course").value;
  const profName = document.getElementById("search-prof").value;
  const termName = document.getElementById("search-term").value;
  const units = collectUnitsFromSearch();
  const school = getUserSchool();
  courseResults.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("courseName", courseName);
  url.searchParams.set("profName", profName);
  url.searchParams.set("units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("schoolName", school)
  fetch(url)
  .then(response => response.json())
  .then((courses) => {
    courses.forEach(course => courseResults.appendChild(createListElement(course)));
  });
}

/* Creates an <li> element containing the course link and name. */
function createListElement(course) {
  const liElement = document.createElement('li');
  const link = document.createElement('a');
  const url = new URL("/course.html", window.location.origin);
  url.searchParams.set("course-name", course.name);
  url.searchParams.set("prof-name", course.professor);
  url.searchParams.set("num-units", course.units);
  url.searchParams.set("term", course.term);
  const school = getUserSchool();
  url.searchParams.set("school-name", school);
  link.setAttribute('href', url);
  link.innerText = course.name;
  liElement.appendChild(link);
  return liElement;
}

function getUserSchool() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  const email = profile.getEmail();
  const start = email.indexOf('@');
  const end = email.lastIndexOf('.');
  const school = email.substring(start+1, end);
  return school;
}

function addCourse() {
  const courseName = document.getElementById("course-name").value;
  const profName = document.getElementById("prof-name").value;
  const termName = document.getElementById("term").value;
  const units = collectUnitsFromInput();
  const schoolName = getUserSchool();
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("num-units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("school-name", schoolName);
  fetch(url, { method: "POST" });
}