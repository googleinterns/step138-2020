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

//button styling 
// mdc.ripple.MDCRipple.attachTo(document.querySelector('.foo-button'));

window.onload = function displayFeed(){
    var urlParams = new URLSearchParams(window.location.search);
    var repName = urlParams.get('name'); 
    fetch(`/feed?repName=${repName}`).then(response => response.json())
    .then((representative)=>{
        console.log(representative);
        postList = representative.posts;
        repName = representative.name;
        var feed = document.getElementById("mid_col");
        //display rep name 
        var displayRepName = document.createElement("div");
        displayRepName.innerText = repName;
        feed.appendChild(displayRepName);
        createQuestionForm(repName);
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
            //username added
            // var username = document.createElement("p");
            // username.setAttribute("class", "username");
            // username.innerText = "John Smith";
            // newQuestion.appendChild(username);
            var question = document.getElementById(post.id);
            //reply/answer buttons
            var replyBtn = document.createElement("button");
            replyBtn.addEventListener("click", createReplyForm(post.id, repName));
            replyBtn.setAttribute("class", "btn");
            var replyIcon = document.createElement("i");
            replyIcon.setAttribute("class", "fa fa-comments");
            replyBtn.appendChild(replyIcon);
            var repAnswer = document.createElement("button");
            repAnswer.addEventListener("click", createAnswerForm(post.id, repName));
            repAnswer.setAttribute("class", "btn");
            var repAnswerIcon = document.createElement("i");
            repAnswerIcon.setAttribute("class", "fa fa-envelope-open");
            repAnswer.appendChild(repAnswerIcon);
            question.appendChild(replyBtn);
            question.appendChild(repAnswer);
            //whole question added to feed
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
    replyList = post.replies;//getReplies retuns a list of Comment objects -- getName(), getMessage()
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
    console.log("This is feed: " + feed);
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
    window.location.href = "/repList.html";
}

//Makes fetch to repListSerlvet and pulls list of reps, makes calls to displayRepList to render html elements with rep names
async function getRepList(){
    var zipcode = localStorage.getItem("zipcode");
    var response = await fetch(`/rep_list?zipcode=${zipcode}`)
    var representatives = await response.json();
    representatives = JSON.parse(representatives);
    console.log(representatives);
    var representativeList = document.getElementById("repList");
    var offices = representatives.offices;
    var officials = representatives.officials;
    for (var i = 0; i < offices.length; i++) {
        for (number of offices[i]["officialIndices"]){
            var bool = await checkIfRepInDatastore(officials[number]["name"]);
            representativeList.appendChild(displayRepList(offices[i]["name"] + ": " + 
            officials[number]["name"], officials[number]["name"], bool));
        }
    }
}

//Adds list element for each rep, anchor tag nested inside which links to rep's feed if account created
function displayRepList(text, name, inDatastore) {
    const listElement = document.createElement('li')
    const anchorElement = document.createElement('a');
    if (inDatastore){
        anchorElement.href = `feed.html?name=${name}`;
    }
    anchorElement.innerText = text;
    listElement.appendChild(anchorElement);
    return listElement;
}

//Makes call to repInDatastoreServlet to check if rep has made an account
async function checkIfRepInDatastore(repName){
    console.log("repName: " +  repName);
    var response = await fetch(`/rep_in_datastore?repName=${repName}`)
    var json = await response.json();
    return (json === true);
}

function insertRepDatastore(){
    fetch("/insert_rep_datastore");
}
