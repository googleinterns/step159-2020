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
    const termList = await getTermList();
    const selectElement = document.getElementById("search-term");
    const optionElement = document.createElement('option');
    optionElement.appendChild(document.createTextNode("Select term..."));
    optionElement.value = ""; 
    selectElement.appendChild(optionElement);
    for (let term of termList) {
        selectElement.appendChild(createOptionElement(term));
    }
    document.getElementById("class-info").classList.remove("hidden");
    document.getElementById("login-box").classList.add("hidden");
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
    document.getElementById("private-class-info").classList.remove("hidden");
    document.getElementById("private-login-box").classList.add("hidden");
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
  removeAllTerms(); // Clear term list.
  document.getElementById("class-info").classList.add("hidden");
  document.getElementById("login-box").classList.remove("hidden");
}

function signOutPrivate() {
  const auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut();
  document.getElementById("private-class-info").classList.add("hidden");
  document.getElementById("private-login-box").classList.remove("hidden");
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
  const optionElement = document.createElement('option');
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
