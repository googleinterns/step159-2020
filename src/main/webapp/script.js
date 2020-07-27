function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  document.getElementById("logged-in").classList.remove("hidden");
  document.getElementById("login-button").classList.add("hidden");
  document.getElementById("school-name").innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
}

function signOut() {
  const auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut();
  document.getElementById("logged-in").classList.add("hidden");
  document.getElementById("login-button").classList.remove("hidden");
}

function showClasses() {
  const classes = document.getElementById("search-results");
  const profName = document.getElementById("search-prof").value;
  classes.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("profName", profName);
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