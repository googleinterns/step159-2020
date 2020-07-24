function loadBody() {
  verifyLogin();
}

/** Contacts Login servlet to verify if user is logged in. */
function verifyLogin() {
  fetch('/login').then(response => response.json()).then((userDetails => {
    const loginElement = document.getElementById("login-box");
    const schoolName = document.getElementById("school-name");
    if (!userDetails.loggedIn) {
      schoolName.style.display = "none";
      loginElement.innerHTML += "<p><a href=\"" + userDetails.url + "\"> Click here to log in! </a></p>";
    } else {
      schoolName.style.display = "block";
      schoolName.innerHTML += userDetails.schoolName;
      loginElement.innerHTML += "<p><a href=\"" + userDetails.url + "\"> Click here to log out! </a></p>";
    }
  }));
}

function countUnits(){
  const inputElems = document.getElementsByName("search-units");
  const arr = []
  for (let i=0; i<inputElems.length; i++) {       
    if (inputElems[i].checked){
      arr.push(inputElems[i].value);
    }
  }
  return arr;
}

function showClasses() {
  const classes = document.getElementById("search-results");
  const className = document.getElementById("search-class").value;
  const profName = document.getElementById("search-prof").value;
  const units = countUnits();
  classes.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("className", className);
  url.searchParams.set("profName", profName);
  url.searchParams.set("units", units);
  fetch(url)
  .then(response => response.json())
  .then((response) => {
    return JSON.parse(response.classNames);
  })
  .then((classNames) => {
    classNames.forEach(name => classes.appendChild(createListElement(name)));
  });
}

/* Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}