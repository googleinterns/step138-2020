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
async function displayTab() {
    var rep = localStorage.getItem("rep");
    var urlParams = new URLSearchParams(window.location.search);

    //Pull values from the url
    var repName = decodeURI(urlParams.get('name')); 
    var tabName = decodeURI(urlParams.get('tab'));
    document.getElementById("tabName").innerText = tabName.replace(repName.replace(/\s/g, ''), "");

    //fetch the tabEntity corresponding to tabName
    var tabResponse = await fetch(`tab_entity?tabName=${tabName}`);
    var tabEntity = await tabResponse.json();
    document.getElementById("platform").innerText = tabEntity.propertyMap.Platform;

    if (rep.trim() == "true") {
        localStorage.setItem("nickname", repName);
    }

    //Pull the posts under a particular tag
    var tabPostsResponse = await fetch(`/tab_posts?repName=${repName}&tab=${tabName}`);
    var posts = await tabPostsResponse.json();
    var feed = document.getElementById("mid_col");

    //display rep name 
    var displayRepName = document.createElement("div");
    displayRepName.innerText = repName;
    feed.appendChild(displayRepName);

    if (rep.trim() != "true") {
        createQuestionForm(repName, [{"name" : tabName}], false);
    }
    else if(posts.length == 0) {
        var emptyTab = document.createElement("p");
        emptyTab.innerText = "There are currently no questions associated with this tab."
        feed.appendChild(emptyTab);
    }
    returnHomeAnchor(feed);
    returnToFeed(repName, feed);
    returnPoliticianPageAnchor(feed, repName);
    posts.forEach((post) => {
        displayPost(post, feed);
        var question = document.getElementById(post.id);

        //reply button
        createReplyButton(post.id, repName, question);

        //answer button
        if (rep.trim() == "true") {
            createAnswerButton(post.id, repName, question);
        }

        displayRepAnswer(post, repName);
        displayReplyList(post);
    })
}

//Displays the feed for a particular rep
async function displayFeed() {
    var rep = localStorage.getItem("rep");
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    if (rep.trim() == "true") {
        localStorage.setItem("nickname", repName);
    }

    //Fetches the list of tabs for a particular rep
    var response = await fetch(`rep_tabs?repName=${repName}`);
    var tabList = await response.json();

    //Displays button on side bar for each tab linking to tab feed
    var leftCol = document.getElementById("left_col");
    tabList.forEach((tab) => {
        addTabButton(tab.name, leftCol, repName);
    });

    //Fetches the representative entity associated with name
    var response = await fetch(`/feed?repName=${repName}`);
    var representative = await response.json();
    postList = representative.posts;
    repName = representative.name;

    var feed = document.getElementById("mid_col");

    //display rep name 
    var displayRepName = document.createElement("div");
    displayRepName.innerText = repName;
    feed.appendChild(displayRepName);

    if (rep.trim() != "true") {
        createQuestionForm(repName, tabList, true);
    }
    else if (postList.length == 0) {
        var emptyFeed = document.createElement("p");
        emptyFeed.innerText = "There are currently no questions on your feed."
        feed.appendChild(emptyFeed);
    }
    returnHomeAnchor(feed);
    returnPoliticianPageAnchor(feed, repName);
    postList.forEach((post) => {
        displayPost(post, feed);
        var question = document.getElementById(post.id);

        //reply button
        createReplyButton(post.id, repName, question);

        //answer button
        if (rep.trim() == "true") {
            createAnswerButton(post.id, repName, question);
        }

        displayRepAnswer(post, repName);
        displayReplyList(post);
    })
};

//Displays the question for a post
function displayPost(post, feed) {
    var newQuestion = document.createElement("div");
    newQuestion.setAttribute("class", "newComment");
    newQuestion.setAttribute("id", post.id);
    var qText = document.createElement("p");
    qText.innerText = post.question.name + ": " + post.question.comment;
    newQuestion.appendChild(qText);
    feed.appendChild(newQuestion);
}

//Creates an anchor tag for returning home
function returnHomeAnchor(feed) {
    var returnHome = document.createElement("a");
    returnHome.href = "index.html";
    returnHome.innerText = "Return to Login";
    linebreak = document.createElement("br");
    returnHome.appendChild(linebreak);
    feed.appendChild(returnHome);
}

//Creates anchor tag that links back to representative's feed
function returnToFeed(repName, feed) {
    var returnToFeed = document.createElement("a");
    returnToFeed.href = "feed.html?name=" + encodeURI(repName);    
    returnToFeed.innerText = "Return to Feed";
    linebreak = document.createElement("br");
    returnToFeed.appendChild(linebreak);
    feed.appendChild(returnToFeed);
}

//Creates an anchor tag for going to politician's page
function returnPoliticianPageAnchor(feed, repName) {
    var politicianPage = document.createElement("a");
    politicianPage.href = `politicianPage.html?name=${repName}`;
    politicianPage.innerText = "Beyond the Politician";
    linebreak = document.createElement("br");
    politicianPage.appendChild(linebreak);
    feed.appendChild(politicianPage);
}

//Creates an button for representative answer
function createAnswerButton(postId, repName, question) {
    var repAnswer = document.createElement("button");
    repAnswer.addEventListener("click", createAnswerForm(postId, repName));
    repAnswer.setAttribute("class", "btn");
    var repAnswerIcon = document.createElement("i");
    repAnswerIcon.setAttribute("class", "fa fa-envelope-open");
    repAnswer.appendChild(repAnswerIcon);
    question.appendChild(repAnswer);
}

//Creates a button for users or the representative to add a reply
function createReplyButton(postId, repName, question) {
    var replyBtn = document.createElement("button");
    replyBtn.addEventListener("click", createReplyForm(postId, repName));
    replyBtn.setAttribute("class", "btn");
    var replyIcon = document.createElement("i");
    replyIcon.setAttribute("class", "fa fa-comments");
    replyBtn.appendChild(replyIcon);
    question.appendChild(replyBtn);
}

//Adds a tab button
function addTabButton(tabName, leftCol, repName) {
    var inputElement = document.createElement("input");
    inputElement.type = "button";
    inputElement.value = tabName.replace(repName.replace(/\s/g, ''), "");
    inputElement.onclick = function() {return getTab(tabName);} 
    leftCol.appendChild(inputElement);
}

//Navigate to a particular tab
function getTab(tab) {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    window.location.href = `tab.html?name=${repName}&tab=${tab}`;
}

//Creates a reply form
function createReplyForm(questionID, repName) {
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
function createAnswerForm(questionID, repName) {
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
function displayReplyList(post) {
    replyList = post.replies;
    replyList.forEach((reply)=> {
        var postElement = document.getElementById(post.id);
        var newReply = document.createElement("div");
        newReply.innerText = reply.name + ": " + reply.comment;
        postElement.appendChild(newReply);
    })
}

//Displays the representative's answer to a particular post
function displayRepAnswer(post, repName) {
    var answer = post.answer;
    if (answer != undefined) {    
        var postElement = document.getElementById(post.id);
        var repAnswer = document.createElement("div");
        repAnswer.innerText = repName + ": " + answer.comment;
        postElement.appendChild(repAnswer);
    }
}

//Creates form for user to ask a new question on rep's feed
function createQuestionForm(repName, tabList, feedBool) {
    var feed = document.getElementsByClassName("newComment")[0];
    var nickname = localStorage.getItem("nickname");
    var newQuestionForm = document.createElement("form");
    newQuestionForm.setAttribute("action", `/new_post?name=${nickname}&repName=${repName}&feed=${feedBool}`);
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

    tabList.forEach((tab) => {
        addTabDropdown(tabDropdown, tab.name, repName);
    });

    newQuestionForm.appendChild(tabDropdown);

    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    newQuestionForm.appendChild(submitBtn);

    feed.appendChild(newQuestionForm);
};

//Add an option in the tab dropdown menu
function addTabDropdown(tabDropdown, tabName, repName) {
    var stripTabName = tabName.replace(repName.replace(/\s/g, ''), "");
    var tabElement = document.createElement("option");
    tabElement.value = stripTabName;
    tabElement.innerText = stripTabName;
    tabDropdown.appendChild(tabElement);
}

//When user logins in, stores their zipcode and name in local storage and redirects to repList.html
function storeZipCodeAndNickname() {
    event.preventDefault();
    var nickname = document.getElementById("nickname").value;
    var zipcode = document.getElementById("zipcode").value;
    localStorage.setItem("nickname", nickname);
    localStorage.setItem("zipcode", zipcode);
    localStorage.setItem("rep", false);
    window.location.href = "/repList.html";
}

//Stores boolean property "rep" in localStorage when rep enters zipcode and redirects to create account html
function storeRepBooleanAndSubmit() {
    event.preventDefault();
    localStorage.setItem("rep", true);
    document.getElementById("loginRep").submit();
}

//Stores boolean property "rep" in localStorage when rep enters zipcode and redirects to create account html 
function storeRepBooleanAndZipcodeAndRedirect() { 
    event.preventDefault(); 
    var zipcode = document.getElementById("zipcode").value;
    localStorage.setItem("zipcode", zipcode);
    localStorage.setItem("rep", true);
    window.location.href = "/repList.html";
}

//Makes fetch to repListSerlvet and pulls list of reps, makes calls to displayRepList to render html elements with rep names
async function getRepList() {
    var rep = localStorage.getItem("rep");
    var displayFunction = (rep.trim() == "true") ? displayRepListLogin : displayRepListUser;
    var zipcode = localStorage.getItem("zipcode");
    var response = await fetch(`/rep_list?zipcode=${zipcode}`)
    var representatives = await response.json();
    representatives = JSON.parse(representatives);
    if (representatives["error"]) {
        window.location.href = "zipcodeNotFound.html";
        return;
    }
    var representativeList = document.getElementById("repList");
    console.log(representativeList);
    console.log(typeof representativeList)
    var offices = representatives.offices;
    var officials = representatives.officials;
    for (var i = 0; i < offices.length; i++) {
        for (number of offices[i]["officialIndices"]) {
            var bool = await checkIfRepInDatastore(officials[number]["name"]);
            var rep;
            if (bool) {
                var repResponse = await fetch(`/feed?repName=${officials[number]["name"]}`);
                rep = await repResponse.json();
            }
            representativeList.appendChild(displayFunction(offices[i]["name"], officials[number]["name"], bool, rep.blobKeyUrl));
        }
    }
}

//Adds list element for each rep with link to rep's feed if account created
function displayRepListUser(title, name, inDatastore, image) {
    var listElement = document.createElement('li')
    listElement.className = "w3-bar";
    
    var imageElement = document.createElement("img");
    imageElement.className = "w3-bar-item w3-circle w3-hide-small";
    imageElement.style = "width:85px";

    if (inDatastore) {
        listElement.onclick = function() {window.location.href = `feed.html?name=${name}`};
        imageElement.src = image;
    }
    else{
        imageElement.src = "/images/defaultProfilePicture.png";
    }

    return displayRepList(listElement, imageElement, title, name);
}

//Displays rep list and adds href linking to create account html if rep not already in datastore
function displayRepListLogin(title, name, inDatastore, image) {
    const listElement = document.createElement('li')
    listElement.className = "w3-bar";
    
    const imageElement = document.createElement("img");
    imageElement.className = "w3-bar-item w3-circle w3-hide-small";
    imageElement.style = "width:85px";

    if (inDatastore == false) {
        listElement.onclick = function() {window.location.href = `repUsernamePassword.html?name=${name}&title=${title}`};
        imageElement.src = "/images/defaultProfilePicture.png";
    }
    else{
        imageElement.src = image;
    }

    return displayRepList(listElement, imageElement, title, name);
}

//Abstracts out the common aspects of repList regardless of user or rep
function displayRepList(listElement, imageElement, title, name) {
    const divElement = document.createElement("div");
    divElement.className = "w3-bar-item";

    const titleSpanElement = document.createElement("span");
    titleSpanElement.innerText = title;
    
    const nameSpanElement = document.createElement("span");
    nameSpanElement.innerText = name;
    nameSpanElement.className = "w3-large";
    linebreak = document.createElement("br");
    nameSpanElement.appendChild(linebreak);

    divElement.appendChild(nameSpanElement);
    divElement.appendChild(titleSpanElement);
    listElement.appendChild(imageElement);
    listElement.appendChild(divElement);

    return listElement;
}

//Displays the rep name and title from Civic Info API while the rep enters username/password for new account 
function createRepAccount() {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 

    var repNameElement = document.getElementById("repName");
    repNameElement.value = repName;
    repNameElement.innerText = repName;

    var title = decodeURI(urlParams.get('title'));
    var titleElement = document.getElementById("title");
    titleElement.value = title;
}

//Makes call to repInDatastoreServlet to check if rep has made an account
async function checkIfRepInDatastore(repName) {
    var response = await fetch(`/rep_in_datastore?repName=${repName}`)
    var json = await response.json();
    return (json === true);
}

//Sends rep info as query parameters to the insertRepDatastoreServlet and 
//redirects depending on if rep's username was already taken 
async function insertRepDatastore() {
    event.preventDefault();
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var repName = document.getElementById("repName").value;
    var title = document.getElementById("title").value;
    var usernameTaken = await fetch(`/insert_rep_datastore?username=${username}&password=
    ${password}&repName=${repName}&title=${title}`).then(response => response.text());
    window.location.href = (usernameTaken.trim() == "true") ?  
    "usernameTaken.html" : `repQuestionnaire.html?name=${repName}`;
}

//Go to previous page
function goBack() {
    window.history.back();
}

//Add another topic to the representative questionnaire
function addTopicAndPlatform() {
    var additionalTopics = document.getElementById("additionalTopics");

    additionalTopics.appendChild(addTopic());
    additionalTopics.appendChild(addPlatform());
}

//Adds a topic element
function addTopic(){
    var topicDivContainer = questionnaireContainerDiv();
    var topicLabel = questionnaireLabel(true);
    var innerDivTopic = questionnaireInnerDiv();

    var topicInput = document.createElement("input");
    topicInput.className = "topic";
    topicInput.type = "text";
    topicInput.placeholder = "Additional Topic";

    innerDivTopic.appendChild(topicInput);
    topicDivContainer.appendChild(topicLabel);
    topicDivContainer.appendChild(innerDivTopic);
    return topicDivContainer;
}

//Adds an element for the platform
function addPlatform(){
    var platformDivContainer = questionnaireContainerDiv();
    var platformLabel = questionnaireLabel(false);
    var innerDiv = questionnaireInnerDiv();

    var platformText = document.createElement("textarea");
    platformText.className = "form-control platform";
    platformText.rows = 3;
    platformText.placeholder = "Platform on additional topic";

    innerDiv.appendChild(platformText);
    platformDivContainer.appendChild(platformLabel);
    platformDivContainer.appendChild(innerDiv);

    return platformDivContainer;
}

//Returns questionnaire form container div
function questionnaireContainerDiv(){
    var divContainer = document.createElement("div");
    divContainer.className = "form-group";
    return divContainer;
}

//Returns questionnaire label
function questionnaireLabel(topic){
    var label = document.createElement("label");
    label.className = "control-label col-sm-2";
    if (topic) {
        label.innerText = "Topic: "
    } else {
        label.innerText = "Platform: "
    }
    return label;
}

//Return questionnaire inner div
function questionnaireInnerDiv(){
    var innerDiv = document.createElement("div");
    innerDiv.className = "col-sm-10";
    return innerDiv;
}


//Grab tabs from questionnaire
async function submitRepQuestionnaire() {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name'));
    var topics = document.getElementsByClassName("topic");
    console.log("These are the topics: " + topics);
    var platforms = document.getElementsByClassName("platform");
    console.log("These are the platforms: " + platforms);
    var intro = document.getElementById("intro").value;
    var listOfTopics = [];
    var listOfPlatforms = [];
    for (var i = 0; i < topics.length; i++) {
        console.log(topics[i].value);
        if (topics[i].value != "") {
            listOfTopics.push(topics[i].value);
            listOfPlatforms.push(platforms[i].value + "*");
        }
    }
    var response = await fetch(`rep_submit_questionnaire?topicList=${listOfTopics}&platformList=
        ${listOfPlatforms}&intro=${intro}&repName=${repName}`);
    if (document.getElementById("imageUpload") != null) {
        return true;
    }
    else {
        event.preventDefault();
        window.href.location = "loginRep.html";
        return false;
    }
}

//Set action of repQuestionnaire to make request to blobstore
function fetchBlobstoreUrlAndShowForm() {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    fetch(`/blobstore-upload-url?repName=${repName}`)
        .then((response) => {
            return response.text();
        })
        .then((imageUploadUrl) => {
            const questionnaire = document.getElementById("repQuestionnaire");
            questionnaire.action = imageUploadUrl;
        });
}

//Populated beyond the politician page
async function displayPoliticianPage(imgUrl) {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    var representativeResponse = await fetch(`feed?repName=${repName}`)
    var repJson = await representativeResponse.json();
    var bodyElement = document.getElementById('body_main');
    const nameElement = document.createElement("p");
    nameElement.innerText = repName;
    const imgElement = document.createElement("img");
    imgElement.setAttribute("src", repJson.blobKeyUrl);
    imgElement.setAttribute("class", "floated");
    const introElement = document.createElement("p");
    introElement.innerText = repJson.intro;
    bodyElement.appendChild(nameElement);
    bodyElement.appendChild(imgElement);
    bodyElement.appendChild(introElement);
    repJson.tabs.forEach((tab) => {
        addTabButton(tab.name, bodyElement, repName);
    });
    returnToFeed(repName, bodyElement);
}
