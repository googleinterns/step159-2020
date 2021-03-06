async function postRatingProperties(url, data = {}) {
  const response = await fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    cache: "no-cache",
    credentials: "same-origin", // Include, *same-origin, omit.
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data), // Body data type must match "Content-Type" header.
  });
  return response.json(); // Parses JSON response into native JavaScript objects and returns a Promise.
}

async function verify() {
  const auth2 = gapi.auth2.getAuthInstance();
  const googleUser = auth2.currentUser.get();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const response = await fetch(url, { method: "POST" });
  const userInfo = await response.json();
  return userInfo.userId;
}

async function getRatingPropertiesToStore() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);

  const ratingProperties = {
    courseKey: urlParams.get("course-key"),
    termKey: urlParams.get("term-key"),
    termInput: document.getElementById("term-input").value,
    profInput: document.getElementById("prof-input").value,
    ratingTerm: document.getElementById("rating-term").value,
    ratingProf: document.getElementById("rating-prof").value,
    hours: document.getElementById("hours").value,
    difficulty: document.getElementById("difficulty").value,
    grade: document.getElementById("grade").value,
    id: await verify(),
    translate: document.getElementById("translate").checked,
  };
  document.getElementById("term-form").reset();
  const url = addingAttributesToURL(ratingProperties);
  return [url, ratingProperties];
}

function addingAttributesToURL(ratingProperties) {
  const url = new URL("/data", window.location.origin);
  url.searchParams.set("course-key", ratingProperties.courseKey);
  url.searchParams.set("term-key", ratingProperties.termKey);
  url.searchParams.set("hour", ratingProperties.hours);
  url.searchParams.set("difficulty", ratingProperties.difficulty);
  url.searchParams.set("term-input", ratingProperties.termInput);
  url.searchParams.set("prof-input", ratingProperties.profInput);
  url.searchParams.set("rating-term", ratingProperties.ratingTerm);
  url.searchParams.set("rating-prof", ratingProperties.ratingProf);

  return url;
}

async function passRatingProperties() {
  if (isValidSubmission()) {
    document.getElementById("retrieve-last-rating-message").innerHTML = "";
    document.getElementById("submission-status-message").innerHTML =
      "Successful Submission!";
    const urlAndData = await getRatingPropertiesToStore();
    postRatingProperties(urlAndData[0], urlAndData[1]);
  } else {
    document.getElementById("submission-status-message").innerHTML =
      "Invalid Submission, check form fields";
  }
}

$(function () {
  $('[data-toggle="tooltip"]').tooltip();
});

async function getLatestRating() {
  let messageRetrievalElement = document.getElementById(
    "retrieve-last-rating-message"
  );

  messageRetrievalElement.innerHTML = "Fetching Rating...";
  const userId = await verify();
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const termKey = urlParams.get("term-key");

  const url = new URL("/latest-rating", window.location.origin);
  url.searchParams.set("reviewer-id", userId);
  url.searchParams.set("term-key", termKey);
  const response = await fetch(url);
  const formInfo = await response.json();

  if (Object.keys(formInfo).length == 0) {
    messageRetrievalElement.innerHTML =
      "You have not submitted a rating for this term";
  } else {
    document.getElementById("term-input").value = formInfo["termInput"];
    document.getElementById("prof-input").value = formInfo["profInput"];
    document.getElementById("rating-term").value = formInfo["ratingTerm"];
    document.getElementById("rating-prof").value = formInfo["ratingProf"];
    document.getElementById("hours").value = formInfo["hours"];
    document.getElementById("difficulty").value = formInfo["difficulty"];
    document.getElementById("grade").value = formInfo["grade"];
    document.getElementById("translate").value = formInfo["translation"];
    messageRetrievalElement.innerHTML = "Your form has been populated!";
  }
}

function isValidSubmission() {
  const allFormFields = [
    document.getElementById("term-input"),
    document.getElementById("prof-input"),
    document.getElementById("rating-term"),
    document.getElementById("rating-prof"),
    document.getElementById("hours"),
    document.getElementById("difficulty"),
    document.getElementById("grade"),
  ];

  for (let field of allFormFields) {
    // If any field is not field out properly.
    if (!field.checkValidity()) {
      return false;
    }
  }
  // If no field was submitted the wrong way.
  return true;
}

function clearForm() {
  document.getElementById("term-form").reset();
}
