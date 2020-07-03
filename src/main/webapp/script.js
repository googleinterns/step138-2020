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
function displayFeed(){
    var rep = localStorage.getItem("rep");
    console.log(rep);
    var urlParams = new URLSearchParams(window.location.search);
    var repName = urlParams.get('name'); 
    if (rep.trim() == "true"){
        localStorage.setItem("nickname", repName);
    }
    fetch(`/feed?repName=${repName}`).then(response => response.json())
    .then((representative)=>{
        postList = representative.posts;
        repName = representative.name;
        var feed = document.getElementById("mid_col");

        //display rep name 
        var displayRepName = document.createElement("div");
        displayRepName.innerText = repName;
        feed.appendChild(displayRepName);
        if (rep.trim() != "true"){
            createQuestionForm(repName);
        }
        postList.forEach((post) => {
            //new questions div
            var newQuestion = document.createElement("div");
            newQuestion.setAttribute("class", "newComment");
            newQuestion.setAttribute("id", post.id);

            //question text added to div
            var qText = document.createElement("p");
            qText.innerText = post.question.name + ": " + post.question.comment;
            newQuestion.appendChild(qText);
            feed.appendChild(newQuestion);
            var question = document.getElementById(post.id);

            //reply button
            var replyBtn = document.createElement("button");
            replyBtn.addEventListener("click", createReplyForm(post.id, repName));
            replyBtn.setAttribute("class", "btn");
            var replyIcon = document.createElement("i");
            replyIcon.setAttribute("class", "fa fa-comments");
            replyBtn.appendChild(replyIcon);
            question.appendChild(replyBtn);

            //answer button
            if (rep.trim() == "true"){
                var repAnswer = document.createElement("button");
                repAnswer.addEventListener("click", createAnswerForm(post.id, repName));
                repAnswer.setAttribute("class", "btn");
                var repAnswerIcon = document.createElement("i");
                repAnswerIcon.setAttribute("class", "fa fa-envelope-open");
                repAnswer.appendChild(repAnswerIcon);
                question.appendChild(repAnswer);
            }

            displayRepAnswer(post, repName);
            displayReplyList(post);
        })

    });
};

function createReplyForm(questionID, repName){
    var question = document.getElementById(questionID);
    var replyForm = document.createElement("form");
    var nickname = localStorage.getItem("nickname");
    replyForm.setAttribute("action", `/reply_to_post?postId=${questionID}&name=${nickname}&repName=${repName}`);
    replyForm.setAttribute("method", "post");
    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    inputForm.setAttribute("name", "reply");
    replyForm.appendChild(inputForm);
    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    replyForm.appendChild(submitBtn);
    question.appendChild(replyForm);       
}

function createAnswerForm(questionID, repName){
    var question = document.getElementById(questionID);
    var ansForm = document.createElement("form");
    ansForm.setAttribute("action", `/rep_answer?postId=${questionID}&repName=${repName}`);
    ansForm.setAttribute("method", "post");
    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    inputForm.setAttribute("name", "answer");
    ansForm.appendChild(inputForm);
    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    ansForm.appendChild(submitBtn);
    question.appendChild(ansForm);
}

function displayReplyList(post){
    replyList = post.replies;
    replyList.forEach((reply)=>{
        var postElement = document.getElementById(post.id);
        var newReply = document.createElement("div");
        newReply.innerText = reply.name + ": " + reply.comment;
        postElement.appendChild(newReply);
    })
}

function displayRepAnswer(post, repName){
    var answer = post.answer;
    if (answer != undefined){    
        var postElement = document.getElementById(post.id);
        var repAnswer = document.createElement("div");
        repAnswer.innerText = repName + ": " + answer.comment;
        postElement.appendChild(repAnswer);
    }
}

function createQuestionForm(repName){
    var feed = document.getElementsByClassName("newComment");
    feed = feed[0];
    var newQuestionForm = document.createElement("form");
    var nickname = localStorage.getItem("nickname");
    newQuestionForm.setAttribute("action", `/new_post?name=${nickname}&repName=${repName}`);
    newQuestionForm.setAttribute("method", "post");
    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    inputForm.setAttribute("name", "comment");
    newQuestionForm.appendChild(inputForm);
    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    newQuestionForm.appendChild(submitBtn);
    feed.appendChild(newQuestionForm);
};

//When user logins in, stores their zipcode and name in local storage and redirects to repList.html
function storeZipCodeAndNickname(){
    event.preventDefault();
    var nickname = document.getElementById("nickname").value;
    var zipcode = document.getElementById("zipcode").value;
    localStorage.setItem("nickname", nickname);
    localStorage.setItem("zipcode", zipcode);
    localStorage.setItem("rep", false);
    window.location.href = "/repList.html";
}

function storeRepBooleanAndRedirect(redirect){
    event.preventDefault();
    localStorage.setItem("rep", true);
    console.log("I Stored rep boolean");
    if (redirect){
        window.location.href = "/repListCreateAccount.html";
    }
    else{
        document.getElementById("loginRep").submit();
    }
}

//Makes fetch to repListSerlvet and pulls list of reps, makes calls to displayRepList to render html elements with rep names
async function getRepList(){
    var rep = localStorage.getItem("rep");
    var displayFunction = (rep.trim() == "true") ? displayRepListLogin : displayRepListUser;
    console.log(displayFunction);
    var zipcode = localStorage.getItem("zipcode");
    var response = await fetch(`/rep_list?zipcode=${zipcode}`)
    var representatives = await response.json();
    representatives = JSON.parse(representatives);
    if (representatives["error"]){
            window.location.href = "zipcodeNotFound.html";
            return;
        }
    var representativeList = document.getElementById("repList");
    var offices = representatives.offices;
    var officials = representatives.officials;
    for (var i = 0; i < offices.length; i++) {
        for (number of offices[i]["officialIndices"]){
            var bool = await checkIfRepInDatastore(officials[number]["name"]);
            representativeList.appendChild(displayFunction(offices[i]["name"] + ": " + 
            officials[number]["name"], offices[i]["name"], officials[number]["name"], bool));
        }
    }
}

//Adds list element for each rep, anchor tag nested inside which links to rep's feed if account created
function displayRepListUser(text, title, name, inDatastore) {
    const listElement = document.createElement('li')
    const anchorElement = document.createElement('a');
    if (inDatastore){
        anchorElement.href = `feed.html?name=${name}`;
    }
    anchorElement.innerText = text;
    listElement.appendChild(anchorElement);
    return listElement;
}

function displayRepListLogin(text, title, name, inDatastore) {
    const listElement = document.createElement('li')
    const anchorElement = document.createElement('a');
    if (inDatastore == false){
        anchorElement.href = `createRepAccount.html?name=${name}&title=${title}`;
    }
    anchorElement.innerText = text;
    listElement.appendChild(anchorElement);
    return listElement;
}

function createRepAccount(){
    var urlParams = new URLSearchParams(window.location.search);

    var repName = urlParams.get('name'); 
    var repNameElement = document.getElementById("repName");
    repNameElement.value = repName;
    repNameElement.innerText = repName;

    var title = urlParams.get('title');
    var titleElement = document.getElementById("title");
    titleElement.value = title;
}

//Makes call to repInDatastoreServlet to check if rep has made an account
async function checkIfRepInDatastore(repName){
    var response = await fetch(`/rep_in_datastore?repName=${repName}`)
    var json = await response.json();
    return (json === true);
}

function insertRepDatastore(){
    event.preventDefault();
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var repName = document.getElementById("repName").value;
    var title = document.getElementById("title").value;
    fetch(`/insert_rep_datastore?username=${username}&password=
    ${password}&repName=${repName}&title=${title}`).then(window.location.href = "loginRep.html");
}
