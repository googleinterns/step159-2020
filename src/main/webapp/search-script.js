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
  const units = countUnits("num-units");
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("num-units", units);
  url.searchParams.set("term", termName);
  url.searchParams.set("school-name", schoolName);
  const response = await fetch(url, { method: "POST" });
  return response;
}

function redirect(newSite) {
  location.href = new URL("/" + newSite, window.location.origin);
}

// Wrap every letter in a span
var textWrapper = document.querySelector(".ml11 .letters");
textWrapper.innerHTML = textWrapper.textContent.replace(
  /([^\x00-\x80]|\w)/g,
  "<span class='letter'>$&</span>"
);

anime
  .timeline({ loop: false })
  .add({
    targets: ".ml11 .line",
    scaleY: [0, 1],
    opacity: [0.5, 1],
    easing: "easeOutExpo",
    duration: 700,
  })
  .add({
    targets: ".ml11 .line",
    translateX: [
      0,
      document.querySelector(".ml11 .letters").getBoundingClientRect().width +
        10,
    ],
    easing: "easeOutExpo",
    duration: 700,
    delay: 100,
  })
  .add({
    targets: ".ml11 .letter",
    opacity: [0, 1],
    easing: "easeOutExpo",
    duration: 600,
    offset: "-=775",
    delay: (el, i) => 34 * (i + 1),
  })
  .add({
    targets: ".ml11",
    opacity: 0,
    duration: 1000,
    easing: "easeOutExpo",
    delay: 1000000,
  });
