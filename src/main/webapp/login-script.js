async function signIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  url.searchParams.set("private", "false");
  const response = await fetch(url, { method: "POST" });
  const id = await response.json();
  if (id.verified) {
    // Successful sign-in.
    document
      .getElementById("class-info")
      .classList.remove("hidden");
    document
      .getElementById("login-box")
      .classList.add("hidden");
    document
      .getElementById("form-signin")
      .classList.add("hidden");
    document
      .getElementById("body")
      .classList.remove("body");
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
  document
    .getElementById("class-info")
    .classList.add("hidden");
  document
    .getElementById("login-box")
    .classList.remove("hidden");
  document
    .getElementById("form-signin")
    .classList.remove("hidden");
  document
    .getElementById("body")
    .classList.add("body");
}

function signOutPrivate() {
  const auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut();
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
