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

//Displays the feed for a particular rep
function displayFeed(repName){
    fetch(`/feed?repName=${repName}`).then(response => response.json()).then((representative)=>{
        postList = representative.getPosts();
        postList.forEach((post) => {
            var newQuestion = document.createElement("div");
            newQuestion.setAttribute("class", "newComment");
            newQuestion.innerText = post.getQuestion();
            var feed = document.getElementById("midCol");
            feed.appendChild(newQuestion);
        })
    });
}

//When user logins in, stores their zipcode and name in local storage and redirects to repList.html
function storeZipCodeAndNickname(){
    event.preventDefault();
    var nickname = document.getElementById("nickname").value;
    var zipcode = document.getElementById("zipcode").value;
    localStorage.setItem("nickname", nickname);
    localStorage.setItem("zipcode", zipcode);
    window.location.href = "/repList.html";
}

//Makes fetch to repListSerlvet and pulls list of reps, makes calls to displayRepList to render html elements with rep names
function getRepList(){
    var zipcode = localStorage.getItem("zipcode");
    fetch(`/rep_list?zipcode=${zipcode}`).then(response => response.json()).then((representatives) => {
        representatives = JSON.parse(representatives);
        console.log(representatives);
        var representativeList = document.getElementById("repList");
        var offices = representatives.offices;
        var officials = representatives.officials;
        for (var i = 0; i < offices.length; i++) {
            for (number of offices[i]["officialIndices"]){
                console.log("i: " + i + " number: " + number + " array: " + offices[i]["officialIndices"]);
                console.log("This is check: " + checkIfRepInDatastore(officials[number]["name"].trim()));
                console.log("Name: " + officials[number]["name"].trim());
                console.log("Type of: " + typeof(officials[number]["name"].trim()));
                representativeList.appendChild(displayRepList(offices[i]["name"] + ": " + 
                officials[number]["name"], officials[number]["name"], checkIfRepInDatastore(officials[number]["name"].trim())));
            }
        }
    });
}

//Adds list element for each rep, anchor tag nested inside which links to rep's feed if account created
function displayRepList(text, name, inDatastore) {
    const listElement = document.createElement('li')
    const anchorElement = document.createElement('a');
    console.log("This is boolean: " + inDatastore);
    if (inDatastore){
        // anchorElement.href = `javascript:displayFeed(${name})`;
        anchorElement.href="google.com";
    }
    anchorElement.innerText = text;
    // anchorElement.addEventListener("click", displayFeed(name)); 
    listElement.appendChild(anchorElement);
    return listElement;
}

//Makes call to repInDatastoreServlet to check if rep has made an account
function checkIfRepInDatastore(repName){
    console.log("repName: " +  repName);
    fetch(`/rep_in_datastore?repName=${repName}`).then(response => response.text()).then(resp => {
        console.log("check if in datastore response: " + resp + repName);
        console.log(typeof(resp));
        console.log("Bool Answer: " + (resp == true));
        console.log("String answer: " + (resp == "true"));
        return ("Resp typeof: " + resp === "true")});
}

function insertRepDatastore(){
    fetch("/insert_rep_datastore").then(c)
}
    