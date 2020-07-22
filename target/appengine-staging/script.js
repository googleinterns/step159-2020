function loadBody() {
  console.log("Verifying login...");
  verifyLogin();
}

/** Contacts Login servlet to verify if user is logged in. */
function verifyLogin() {
  fetch('/login').then(response => response.json()).then((arrStrings) => {
    const loginElement = document.getElementById("login-box");
    if (arrStrings[0] == "false") {
      document.getElementById("school-name").style.display = "none";
    } else {
      const schoolName = document.getElementById("school-name");
      schoolName.style.display = "block";
      const start = arrStrings[1].indexOf('@');
      const end = arrStrings[1].indexOf('.');
      if (start == -1 || end == -1) {
          schoolName.innerHTML += "Sorry, you have entered an invalid email."
      } else {
      const school = arrStrings[1].slice(start + 1, end);
      schoolName.innerHTML += 'You go to the following school: ';
      schoolName.innerHTML +=  school;
      }
    }
    loginElement.innerHTML += arrStrings[1]; 
  });
}

function showClasses() {
  const classes = document.getElementById("show-classes");
  classes.innerHTML = "";
  fetch('/search')
  .then(response => response.json())
  .then((response) => {
    return JSON.parse(response.classNames); // Get comments as array.
  })
  .then((classNames) => {
    classNames.forEach(name => createListElement(name));
    // for (let i=0; i<num; i++) {
    // classes.appendChild(createListElement(comments[i]));
    // }
  });
}


/* Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}