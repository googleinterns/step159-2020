function countUnits(sectionName) {
  const unitElems = document.getElementsByName(sectionName);
  const units = [];
  for (let elem of unitElems) {
    if (elem.checked) {
      units.push(elem.value);
    }
  }
  return units;
}

async function showCourses() {
  const courseResults = document.getElementById("search-results");
  const courseName = document.getElementById("search-course").value;
  const profName = document.getElementById("search-prof").value;
  const termName = document.getElementById("search-term").value;
  const searchMessage = document.getElementById("search-message");
  const units = countUnits("search-units");
  const school = getUserSchool();
  courseResults.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("school-name", school);
  const response = await fetch(url);
  const searchResults = await response.json();
  searchMessage.innerHTML = searchResults.message + "<br />";
  const courses = JSON.parse(searchResults.courses);
  courses.forEach((course) =>
    courseResults.appendChild(createListElement(course))
  );
}

/* Creates an <li> element containing the course link and name. */
function createListElement(course) {
  const liElement = document.createElement("li");
  const link = document.createElement("a");
  const url = new URL("/course.html", window.location.origin);
  url.searchParams.set("term-key", course.termKey);
  url.searchParams.set("course-key", course.courseKey);
  link.setAttribute("href", url);
  link.innerText =
    course.name + " - " + course.professor + " (" + course.term + ")";
  liElement.appendChild(link);
  return liElement;
}

function getUserSchool() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  const email = profile.getEmail();
  const start = email.indexOf("@");
  const end = email.lastIndexOf(".");
  return email.substring(start + 1, end);
}

async function addCourse() {
  const courseName = document.getElementById("course-name").value;
  const profName = document.getElementById("prof-name").value;
  const termName = document.getElementById("term").value;
  const schoolName = document.getElementById("school-name").value;
  const units = document.getElementById("num-units").value;
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("num-units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("school-name", schoolName);
  url.searchParams.set("num-enrolled", "300");
  const response = await fetch(url, { method: "POST" });
  return response;
}

function redirect(newSite) {
  location.href = new URL("/" + newSite, window.location.origin);
}