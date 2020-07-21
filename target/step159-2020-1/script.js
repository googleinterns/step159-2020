function loadBody() {
  verifyLogin();
}

/** Contacts Login servlet to verify if user is logged in. */
function verifyLogin() {
  console.log("Fetching login status...");
  fetch('/login').then(response => response.json()).then((userDetails) => {
    const loginElement = document.getElementById("login-box");
    if (userDetails[0] == "false") {
      document.getElementById("school-name").style.display = "none";
    } else {
      const schoolName = document.getElementById("school-name");
      schoolName.style.display = "block";
      const start = userDetails[1].indexOf('@');
      const end = userDetails[1].indexOf('.');
      if (start == -1 || end == -1) {
          schoolName.innerHTML += "Sorry, you have entered an invalid email."
      } else {
      const school = userDetails[1].slice(start + 1, end);
      schoolName.innerHTML += 'You go to the following school:';
      schoolName.innerHTML +=  school;
      }
    }
    loginElement.innerHTML += userDetails[1];
  });
}