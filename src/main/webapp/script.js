function loadBody() {
  verifyLogin();
}

/** Contacts Login servlet to verify if user is logged in. */
function verifyLogin() {
  fetch("/login")
    .then((response) => response.json())
    .then((userDetails) => {
      const loginElement = document.getElementById("login-box");
      const schoolName = document.getElementById("school-name");
      if (!userDetails.loggedIn) {
        schoolName.style.display = "none";
        loginElement.innerHTML += `<p><a href= ${userDetails.loginURL} > Click here to log in! </a></p>`;
      } else {
        console.log(userDetails);
        schoolName.style.display = "block";
        if (userDetails.hasOwnProperty("schoolName")) {
          schoolName.innerHTML += userDetails.schoolName;
        }
        loginElement.innerHTML += `<p><a href= ${userDetails.logoutURL} + > Click here to log out! </a></p>`;
      }
    });
}

function showClasses() {
  const classes = document.getElementById("search-results");
  const profName = document.getElementById("search-prof").value;
  classes.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("profName", profName);
  fetch(url)
    .then((response) => response.json())
    .then((response) => {
      return JSON.parse(response.classNames);
    })
    .then((classNames) => {
      classNames.forEach((name) =>
        classes.appendChild(createListElement(name))
      );
    });
}

/* Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement("li");
  liElement.innerText = text;
  return liElement;
}

// This is a temporary function, once I connect with Nina will get
// better way to create/access class ids.
function getClassID() {
  var classId = Math.floor(Math.random() * 101);
  document.getElementById("class-id").innerHTML = classId;
}
