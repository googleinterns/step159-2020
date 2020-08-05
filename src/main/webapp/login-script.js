function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  document.getElementById("class-info").classList.remove("hidden");
  document.getElementById("login-box").classList.add("hidden");
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
  document.getElementById("login-box").classList.remove("hidden");
  const url = new URL("/authentication", window.location.origin);
  url.searchParams.set("email", email);
  url.searchParams.set("status", "logged-out");
  fetch(url);
}