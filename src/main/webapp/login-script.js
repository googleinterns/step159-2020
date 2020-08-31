async function signIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  url.searchParams.set("private", "false");
  const loginResponse = await fetch(url, { method: "POST" });
  const id = await loginResponse.json();
  if (id.verified) {
    // Successful sign-in.
    hideLandingElements();
    const termList = await getTermList();
    const selectElement = document.getElementById("search-term");
    const optionElement = document.createElement("option");
    optionElement.appendChild(document.createTextNode("Select term..."));
    optionElement.value = "";
    selectElement.appendChild(optionElement);
    for (let term of termList) {
      selectElement.appendChild(createOptionElement(term));
    }
    document.getElementById(
      "school-name"
    ).innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
  } else {
    document.getElementById("login-message").innerHTML =
      "Email not verified. Try again.";
    signOut();
  }
}

async function signInPrivate(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  url.searchParams.set("private", "true");
  const response = await fetch(url, { method: "POST" });
  const id = await response.json();
  if (id.verified) {
    // Successful sign-in.
    hideLandingElements();
    document.getElementById(
      "private-school-name"
    ).innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
  } else {
    document.getElementById("private-login-message").innerHTML =
      "Email not verified. Try again.";
    signOutPrivate();
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
  auth2.signOut();
  showLandingElements();
}

function signOutPrivate() {
  const auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut();
  showLandingElements();
}

function hideLandingElements() {
  document
    .getElementById("private-class-info")
    .classList.remove("hidden");
  document
    .getElementById("private-login-box")
    .classList.add("hidden");
  document
    .getElementById("form-signin")
    .classList.add("hidden");
  document
    .getElementById("body")
    .classList.remove("body");
  removeAllTerms(); // Clear term list.
}

function showLandingElements() {
  document
    .getElementById("private-class-info")
    .classList.add("hidden");
  document
    .getElementById("private-login-box")
    .classList.remove("hidden");
  document
    .getElementById("form-signin")
    .classList.remove("hidden");
  document
    .getElementById("body")
    .classList.add("body");
}

async function getTermList() {
  const school = getUserSchool();
  const url = new URL("/term", window.location.origin);
  url.searchParams.set("school-name", school);
  const response = await fetch(url);
  const json = await response.json();
  return json.terms;
}

function getUserSchool() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  const email = profile.getEmail();
  const start = email.indexOf("@");
  const end = email.lastIndexOf(".");
  return email.substring(start + 1, end);
}

function createOptionElement(optionValue) {
  const optionElement = document.createElement("option");
  optionElement.appendChild(document.createTextNode(optionValue));
  optionElement.value = optionValue;
  return optionElement;
}

function removeAllTerms() {
  parent = document.getElementById("search-term");
  while (parent.firstChild) {
    parent.removeChild(parent.firstChild);
  }
}
