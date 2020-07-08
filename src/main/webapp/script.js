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

//Displays the posts from a tab 
function displayTab(){
    var rep = localStorage.getItem("rep");
    var urlParams = new URLSearchParams(window.location.search);
    var tab = decodeURI(urlParams.get('tab'));
    var repName = decodeURI(urlParams.get('name')); 
    if (rep.trim() == "true"){
        localStorage.setItem("nickname", repName);
    }
    fetch(`/tab?repName=${repName}&tab=${tab}`).then(response => response.json())
    .then((posts)=>{
        var feed = document.getElementById("mid_col");

        //display rep name 
        var displayRepName = document.createElement("div");
        displayRepName.innerText = repName;
        feed.appendChild(displayRepName);
        if (rep.trim() != "true"){
            createQuestionForm(repName);
        }
        else{
            if (posts.length == 0){
                var emptyFeed = document.createElement("p");
                emptyFeed.innerText = "There are currently no questions on your feed."
                feed.appendChild(emptyFeed);
            }
        }
        posts.forEach((post) => {
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

            var returnHome = document.createElement("a");
            returnHome.href = "index.html";
            returnHome.innerText = "Return to Login";
            feed.appendChild(returnHome);
        })
        
    });
    
}

//Displays the feed for a particular rep
function displayFeed(){
    var rep = localStorage.getItem("rep");
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
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
        else{
            if (postList.length == 0){
                var emptyFeed = document.createElement("p");
                emptyFeed.innerText = "There are currently no questions on your feed."
                feed.appendChild(emptyFeed);
            }
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

            var returnHome = document.createElement("a");
            returnHome.href = "index.html";
            returnHome.innerText = "Return to Login";
            feed.appendChild(returnHome);
        })
    });
};

//Navigate to a particular tab
function getTab(tab){
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    window.location.href = `tab.html?name=${repName}&tab=${tab}`;
}

//Creates a reply form
function createReplyForm(questionID, repName){
    var question = document.getElementById(questionID);
    var nickname = localStorage.getItem("nickname");

    var replyForm = document.createElement("form");
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

//Creates an answer form 
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

//Displays the list of replies for a particular post
function displayReplyList(post){
    replyList = post.replies;
    replyList.forEach((reply)=>{
        var postElement = document.getElementById(post.id);
        var newReply = document.createElement("div");
        newReply.innerText = reply.name + ": " + reply.comment;
        postElement.appendChild(newReply);
    })
}

//Displays the representative's answer to a particular post
function displayRepAnswer(post, repName){
    var answer = post.answer;
    if (answer != undefined){    
        var postElement = document.getElementById(post.id);
        var repAnswer = document.createElement("div");
        repAnswer.innerText = repName + ": " + answer.comment;
        postElement.appendChild(repAnswer);
    }
}

//Creates form for user to ask a new question on rep's feed
function createQuestionForm(repName){
    var feed = document.getElementsByClassName("newComment")[0];
    var nickname = localStorage.getItem("nickname");
    
    var newQuestionForm = document.createElement("form");
    newQuestionForm.setAttribute("action", `/new_post?name=${nickname}&repName=${repName}`);
    newQuestionForm.setAttribute("method", "post");

    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    inputForm.setAttribute("name", "comment");
    newQuestionForm.appendChild(inputForm);

    var tabLabel = document.createElement("label");
    tabLabel.setAttribute("for", "tab");
    tabLabel.innerText = "Choose a label: ";
    newQuestionForm.appendChild(tabLabel);

    var tabDropdown = document.createElement("select");
    tabDropdown.name = "tab";
    tabDropdown.id = "tab";

    var educationTab = document.createElement("option");
    educationTab.value = "Education";
    educationTab.innerText = "Education";
    tabDropdown.appendChild(educationTab);

    var otherTab = document.createElement("option");
    otherTab.value = "Other";
    otherTab.innerText = "Other";
    tabDropdown.appendChild(otherTab);
    newQuestionForm.appendChild(tabDropdown);

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

//Stores boolean property "rep" in localStorage when rep enters zipcode and redirects to create account html
function storeRepBooleanAndSubmit(){
    event.preventDefault();
    localStorage.setItem("rep", true);
    document.getElementById("loginRep").submit();
}

//Stores boolean property "rep" in localStorage when rep enters zipcode and redirects to create account html 
function storeRepBooleanAndRedirect(){ 
    event.preventDefault(); 
    localStorage.setItem("rep", true);
    window.location.href = "/repList.html";
}

//Makes fetch to repListSerlvet and pulls list of reps, makes calls to displayRepList to render html elements with rep names
async function getRepList(){
    var rep = localStorage.getItem("rep");
    var displayFunction = (rep.trim() == "true") ? displayRepListLogin : displayRepListUser;
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

//Adds list element for each rep with link to rep's feed if account created
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

//Displays rep list and adds href linking to create account html if rep not already in datastore
function displayRepListLogin(text, title, name, inDatastore) {
    const listElement = document.createElement('li')
    const anchorElement = document.createElement('a');
    if (inDatastore == false){
        anchorElement.href = `repUsernamePassword.html?name=${name}&title=${title}`;
    }
    anchorElement.innerText = text;
    listElement.appendChild(anchorElement);
    return listElement;
}

//Displays the rep name and title from Civic Info API while the rep enters username/password for new account 
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

//Sends rep info as query parameters to the insertRepDatastoreServlet and 
//redirects depending on if rep's username was already taken 
function insertRepDatastore(){
    event.preventDefault();
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var repName = document.getElementById("repName").value;
    var title = document.getElementById("title").value;
    fetch(`/insert_rep_datastore?username=${username}&password=
    ${password}&repName=${repName}&title=${title}`).then(response => response.text())
    .then((usernameTaken) => {
        window.location.href = (usernameTaken.trim() == "true") ?  
        "usernameTaken.html" : "loginRep.html"
    });
}

//Go to previous page
function goBack() {
    window.history.back();
}

//Add another topic to the representative questionnaire
function addTopic(){
    var additionalTopics = document.getElementById("additionalTopics");

    var paragraphTopic = document.createElement("p");
    paragraphTopic.innerText = "Topic: ";

    var input = document.createElement("input");
    input.type = "text";
    input.class = "topic";
    input.value = "Additional Topic";

    var paragraphPlatform = document.createElement("p");
    paragraphPlatform.innerText = "Platform: "

    var inputPlatform = document.createElement("input");
    inputPlatform.type = "text";
    inputPlatform.class = "platform";

    additionalTopics.appendChild(paragraphTopic);
    additionalTopics.appendChild(input);
    additionalTopics.appendChild(paragraphPlatform);
    additionalTopics.appendChild(inputPlatform);
}

//Grab tags from questionnaire
function grabTags(){
    var topics = document.getElementsByClassName("topic");
    var listOfTopics = [];
    for (var i = 0; i < topics.length; i++) {
        listOfTopics.push(topics[i].value);
    }
}
