function hideLandingSignIn(privateBool) {
  let classInfo = "";
  let loginBox = "";

  if (privateBool) {
    classInfo = "private-class-info";
    loginBox = "private-login-box";
  } else {
    classInfo = "class-info";
    loginBox = "login-box";
  }
  document.getElementById(classInfo).classList.remove("hidden");
  document.getElementById(loginBox).classList.add("hidden");
  document
    .getElementById("navbar")
    .classList.remove("hidden");
  document
    .getElementById("form-signin")
    .classList.add("hidden");
  document
    .getElementById("body")
    .classList.remove("body");
  document
    .getElementById("body")
    .classList.add("bg-light");
}

function revealLandingSignOut(privateBool) {
  let classInfo = "";
  let loginBox = "";

  if (privateBool) {
    classInfo = "private-class-info";
    loginBox = "private-login-box";
  } else {
    classInfo = "class-info";
    loginBox = "login-box";
  }

  document.getElementById(classInfo).classList.add("hidden");
  document.getElementById(loginBox).classList.remove("hidden");
  document
    .getElementById("navbar")
    .classList.add("hidden");
  document
    .getElementById("form-signin")
    .classList.remove("hidden");
  document
    .getElementById("body")
    .classList.add("body");
  document
    .getElementById("body")
    .classList.remove("bg-light");
}

async function signIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const loginResponse = await fetch(url, { method: "POST" });
  const id = await loginResponse.json();
  if (id.verified) {
    // Successful sign-in.
    hideLandingSignIn(false);
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
    ).innerHTML = `Hi, ${profile.getName()}!`;
    if (id.whitelist) {
      document
        .getElementById("redirect-button-container")
        .classList.remove("hidden");
    } else {
      document
        .getElementById("redirect-button-container")
        .classList.add("hidden");
    }
  } else {
    document.getElementById("login-message").innerHTML =
      "Email not verified. Try again.";
    signOut();
  }
}

async function privateSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const response = await fetch(url, { method: "POST" });
  const id = await response.json();
  if (id.whitelist) {
    // Successful sign-in.
    hideLandingSignIn(true);
    document.getElementById(
      "private-school-name"
    ).innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
  } else {
    document.getElementById("private-login-message").innerHTML =
      "Email not verified. Try again.";
    signOutPrivate();
  }
}

async function reportSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const response = await fetch(url, { method: "POST" });
  const id = await response.json();
  if (id.whitelist) {
    // Successful sign-in.
    document
      .getElementById("reports-container")
      .classList.remove("hidden");
    document
      .getElementById("reports-login-message")
      .classList.add("hidden");
  } else {
    document
      .getElementById("reports-container")
      .classList.add("hidden");
    document
      .getElementById("reports-login-message")
      .classList.remove("hidden");
    document.getElementById("reports-login-message").innerHTML =
      "You do not have access to view this page.";
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
  revealLandingSignOut(false);
}

function signOutPrivate() {
  const auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut();
  revealLandingSignOut(true);
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
