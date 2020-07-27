function countUnits(){
  const inputElems = document.getElementsByName("search-units");
  const arr = []
  for (let i=0; i<inputElems.length; i++) {       
    if (inputElems[i].checked){
      arr.push(inputElems[i].value);
    }
  }
  return arr;
}

function showClasses() {
  const classes = document.getElementById("search-results");
  const className = document.getElementById("search-class").value;
  const profName = document.getElementById("search-prof").value;
  const units = countUnits();
  classes.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("className", className);
  url.searchParams.set("profName", profName);
  url.searchParams.set("units", units);
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