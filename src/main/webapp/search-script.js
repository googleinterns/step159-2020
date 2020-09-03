function countUnits(min, max) {
  const minUnits = Number(min);
  const maxUnits = Number(max);
  const units = [];
  for (let i = minUnits; i <= maxUnits; i++) {
    units.push(i);
  }
  return units;
}

async function showCourses() {
  const courseResults = document.getElementById("search-results");
  const courseName = document.getElementById("search-course").value;
  const profName = document.getElementById("search-prof").value;
  const termName = document.getElementById("search-term").value;
  const searchMessage = document.getElementById("search-message");
  const school = getUserSchool();
  courseResults.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  if (!document.getElementById("no-units").checked) {
    const units = countUnits(document.getElementById("min-units").value, document.getElementById("max-units").value);
    url.searchParams.set("units", units);
  } else {
    url.searchParams.set("units", []);
  }
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
  link.setAttribute("target", "_blank"); // Open in new window.
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

// Wrap every letter in a span
// Source: https://tobiasahlin.com/moving-letters/
const textWrapper = document.querySelector(".ml11 .letters");
textWrapper.innerHTML = textWrapper.textContent.replace(
  /([^\x00-\x80]|\w)/g,
  "<span class='letter'>$&</span>"
);

anime
  .timeline({ loop: true })
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
    delay: 10000,
  });

$(function () {
  $("#units-slider").slider({
    range: true,
    min: 0,
    max: 25,
    values: [5, 20],
    slide: function (event, ui) {
      $("#min-units").val(ui.values[0]);
      $("#max-units").val(ui.values[1]);
    },
  });
  $("#min-units").val($("#units-slider").slider("values", 0));
  $("#max-units").val($("#units-slider").slider("values", 1));
});

$("#no-units").change(function () {
  const ckb_status = $("#no-units").prop("checked");
  if (ckb_status) {
    $("#units-slider").slider("disable");
    $("#min-label").addClass("faded");
    $("#max-label").addClass("faded");
    $("#min-units").val('');
    $("#max-units").val('');
  } else {
    $("#units-slider").slider("enable");
    $("#min-label").removeClass("faded");
    $("#max-label").removeClass("faded");
    $("#min-units").val($("#units-slider").slider("values", 0));
    $("#max-units").val($("#units-slider").slider("values", 1));

  }
});