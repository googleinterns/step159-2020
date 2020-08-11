async function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
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
    document.getElementById(
      "school-name"
    ).innerHTML = `Hi, ${profile.getName()}! Your email is ${profile.getEmail()}`;
  } else {
    document.getElementById("login-box").innerHTML =
      "Email not verified. Try again.";
  }
}

function signOut() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  auth2.signOut();
  document
    .getElementById("class-info")
    .classList.add("hidden");
  document
    .getElementById("login-box")
    .classList.remove("hidden");
}
