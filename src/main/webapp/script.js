// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

function storeZipCode(){
    event.preventDefault();
    var zipcode = document.getElementById("zipcode").value;
    localStorage.setItem("zipcode", zipcode);
    window.location.href = "/repList.html";
}

function getRepList(){
    var zipcode = localStorage.getItem("zipcode");
    fetch(`/rep_list?zipcode=${zipcode}`).then(response => response.json()).then((representatives) => {
        representatives = JSON.parse(representatives);
        var representativeList = document.getElementById("repList");
        var offices = representatives.offices;
        var officials = representatives.officials;
        for (var i = 0; i < offices.length; i++) {
            for (number of offices[i]["officialIndices"]){
                console.log("i: " + i + " number: " + number + " array: " 
                + offices[i]["officialIndices"]);
                representativeList.appendChild(createListElement(
                    offices[i]["name"] + ": " + officials[number]["name"]));
            }
        }
    });
}

function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
