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
  const units = countUnits("search-units");
  const school = getUserSchool();
  courseResults.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("courseName", courseName);
  url.searchParams.set("profName", profName);
  url.searchParams.set("units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("schoolName", school);
  const response = await fetch(url);
  const searchResults = await response.json();
  if (searchResults.hasOwnProperty("message")) {
    // TODO: Make a div for this message so specific styling is easier.
    courseResults.innerHTML += searchResults.message + "<br />";
  }
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
  const units = countUnits("num-units");
  const schoolName = getUserSchool();
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("num-units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("school-name", schoolName);
  const response = await fetch(url, { method: "POST" });
  return response;
}

async function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const response = await fetch(url, { method: "POST" });
  const id = await response.json();
  if (id.verified) {
    // Successful sign-in.
    document
      .getElementById("class-info")
      .classList.remove("hidden");
    document
      .getElementById("login-box")
      .classList.add("hidden");
    document.getElementById(
      "school-name"
    ).innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
  } else {
    document.getElementById("login-box").innerHTML =
      "Email not verified. Try again.";
  }
}

/* Returns a Promise for the backend-generated ID of a user. */
async function verify() {
  const auth2 = gapi.auth2.getAuthInstance();
  const googleUser = auth2.currentUser.get();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const response = await fetch(url, { method: "POST" });
  const userInfo = await response.json();
  return userInfo.id;
}

function signOut() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  auth2.signOut();
  document
    .getElementById("class-info")
    .classList.add("hidden");
  document
    .getElementById("login-box")
    .classList.remove("hidden");
}
