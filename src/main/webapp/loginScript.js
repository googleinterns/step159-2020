function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  document.getElementById("class-info").classList.remove("hidden");
  document.getElementById("login-button").classList.add("hidden");
  document.getElementById("school-name").innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
  const url = new URL("/authentication", window.location.origin);
  url.searchParams.set("name", profile.getName()); //TODO: Make this information more secure - not in URL.
  url.searchParams.set("email", profile.getEmail());
  url.searchParams.set("id", profile.getId());
  url.searchParams.set("status", "logged-in");
  fetch(url);
}

function signOut() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  auth2.signOut();
  const email = profile.getEmail();
  document.getElementById("class-info").classList.add("hidden");
  document.getElementById("login-button").classList.remove("hidden");
  const url = new URL("/authentication", window.location.origin);
  url.searchParams.set("email", email);
  url.searchParams.set("status", "logged-out");
  fetch(url);
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