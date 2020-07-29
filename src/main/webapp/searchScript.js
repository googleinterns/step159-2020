function countUnits(){
  const inputElems = document.getElementsByName("search-units");
  const arr = []
  for (let i = 0; i < inputElems.length; i++) {       
    if (inputElems[i].checked){
      arr.push(inputElems[i].value);
    }
  }
  return arr;
}

function showCourses() {
  const courseResults = document.getElementById("search-results");
  const courseName = document.getElementById("search-course").value;
  const profName = document.getElementById("search-prof").value;
  const termName = document.getElementById("search-term").value;
  const units = countUnits();
  courseResults.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("courseName", courseName);
  url.searchParams.set("profName", profName);
  url.searchParams.set("units", units);
  url.searchParams.set("term", termName);
  fetch(url)
  .then(response => response.json())
  .then((courses) => {
    courses.forEach(course => courseResults.appendChild(createListElement(course.name)));
  });
}

/* Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}