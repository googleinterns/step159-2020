// This is a temporary function, once I connect with Nina will get 
// better way to create/access class ids.
function getClassID() {
  var classId = Math.floor(Math.random() * 101);
  document.getElementById("class-id").innerHTML = classId;
}
