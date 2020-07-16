//Displays the posts from a tab 
async function displayTab() {
    
    var rep = localStorage.getItem("rep");
    var urlParams = new URLSearchParams(window.location.search);

    //Pull values from the url
    var repName = decodeURI(urlParams.get('name')); 
    if (rep.trim() == "true") {
        localStorage.setItem("nickname", repName);
    }
    var tabName = decodeURI(urlParams.get('tab'));

    //fetch the representative entity corresponding to repName
    var response = await fetch(`/feed?repName=${repName}`);
    var representative = await response.json();

    //fetch the tabEntity corresponding to tabName
    var tabResponse = await fetch(`tab_entity?tabName=${tabName}`);
    var tabEntity = await tabResponse.json();

    //Set values for tabname, title, image, and platform
    document.getElementById("tabName").innerText = tabName.replace(repName.replace(/\s/g, ''), "");
    document.getElementById("tabNameDiv").style.margin = "0px 16px 16px";
    document.getElementById("repTitle").innerText = representative.title;
    document.getElementById("repProfilePic").src = representative.blobKeyUrl;
    document.getElementById("platform").innerText = tabEntity.propertyMap.Platform;

    //Add a button to return to feed
    var feedElement = document.getElementById("feed");
    var feedButton = document.createElement("button");
    feedButton.onclick = function() {window.location.href = `/feed.html?name=${repName}`};
    feedButton.className = "w3-button w3-block w3-theme-l1 w3-left-align";
    feedButton.innerText = "Back to Feed";
    feedElement.appendChild(feedButton);

    //Pull the posts under a particular tag
    var tabPostsResponse = await fetch(`/tab_posts?repName=${repName}&tab=${tabName}`);
    var posts = await tabPostsResponse.json();

    if (rep.trim() != "true") {
        createQuestionForm(repName, [{"name" : tabName}], false);
    }
    else if (posts.length == 0) {
        var emptyFeed = document.createElement("p");
        emptyFeed.innerText = "There are currently no questions under this tab."
        emptyFeed.className = "col-sm-10";
        document.getElementById("posts").appendChild(emptyFeed);
    }
    
    posts.forEach((post) => {
        displayPost(post, false);
        var question = document.getElementById(post.id);

        //answer button
        if (rep.trim() == "true") {
            createAnswerButton(post.id, repName, question);
        }
        else {
            createReplyButton(post.id, repName, question);
        }

        displayRepAnswer(post, repName);
        displayReplyList(post);
    })
}

//display the feed
async function displayFeed() {
    var rep = localStorage.getItem("rep");
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    if (rep.trim() == "true") {
        localStorage.setItem("nickname", repName);
    }
    document.getElementById("repName").innerText = repName;

    //Fetches the representative entity associated with name
    var response = await fetch(`/feed?repName=${repName}`);
    var representative = await response.json();
    document.getElementById("repTitle").innerText = representative.title;
    document.getElementById("repProfilePic").src = representative.blobKeyUrl;

    //Fetches the list of tabs for a particular rep
    var response = await fetch(`rep_tabs?repName=${repName}`);
    var tabList = await response.json();
    var tabs = document.getElementById("tabs");
    tabList.forEach((tab) => {
        addTabButton(tab.name, tabs, repName);
    });

    postList = representative.posts;
    if (rep.trim() != "true") {
        createQuestionForm(repName, tabList, true);
    }
    else if (postList.length == 0) {
        var emptyFeed = document.createElement("p");
        emptyFeed.innerText = "There are currently no questions on your feed."
        emptyFeed.className = "col-sm-10";
        document.getElementById("posts").appendChild(emptyFeed);
    }
    postList.forEach((post) => {
        var firstPost = (rep.trim() == "true" && post == postList[0]) ? true : false;
        displayPost(post, firstPost);
        var question = document.getElementById(post.id);
        //answer button
        if (rep.trim() == "true") {
            createAnswerButton(post.id, repName, question);
        }
        else {
            createReplyButton(post.id, repName, question);
        }
        displayRepAnswer(post, repName);
        displayReplyList(post);

        //show reaction buttons 
        var reactionDiv = document.createElement("div");
        displayReaction(post, repName, reactionDiv, "ANGRY"); 
        displayReaction(post, repName, reactionDiv, "CRYING"); 
        displayReaction(post, repName, reactionDiv, "LAUGHING"); 
        displayReaction(post, repName, reactionDiv, "HEART"); 
        displayReaction(post, repName, reactionDiv, "THUMBS_UP"); 
        displayReaction(post, repName, reactionDiv, "THUMBS_DOWN"); 
        document.getElementById(post.id).appendChild(reactionDiv);
    })
}

//Adds a tab button
function addTabButton(tabName, container, repName) {
    var tab = document.createElement("button");
    tab.onclick = function() {return getTab(tabName);} 
    tab.className = "w3-button w3-block w3-theme-l1 w3-left-align";
    tab.innerText = tabName.replace(repName.replace(/\s/g, ''), "");
    container.appendChild(tab);
}

function displayReaction(post, repName, reactionDiv, reaction) {
    var btn = document.createElement("button");
    btn.setAttribute("class", "btn"); 
    var imageSrc = "reaction_icons/" + reaction.toLowerCase() + ".jpg"
    if (localStorage.getItem(post.id + reaction) === "reacted") {
        btn.innerHTML = '<img src="'+ imageSrc +'" width="20px" height="20px" border="1">';
    }
    else {
        btn.innerHTML = '<img src="'+ imageSrc +'" width="20px" height="20px">';
    }
    btn.innerHTML += post.reactions[reaction]; 
    btn.onclick = function() {reactToPost(reaction, post.id, repName);} 
    reactionDiv.appendChild(btn); 
}

async function reactToPost(reaction, postId, repName) {
    var reactionState = localStorage.getItem(postId + reaction); 
    if (reactionState === null) {
        localStorage.setItem(postId + reaction, "unreacted");
    }
    if (reactionState === "unreacted") {
        await fetch(`/react_to_post?repName=${repName}&postId=
            ${postId}&reaction=${reaction}`);
        localStorage.setItem(postId + reaction, "reacted");
    }
    else {
        await fetch(`/unreact_to_post?repName=${repName}&postId=
            ${postId}&reaction=${reaction}`);
        localStorage.setItem(postId + reaction, "unreacted");
    }
    window.location.reload(false); 
}

//Displays the question for a post
function displayPost(post, firstPost) {
    var posts = document.getElementById("posts");
    var newQuestion = document.createElement("div");
    newQuestion.className = "w3-container w3-card w3-white w3-round";
    newQuestion.style.margin = (firstPost) ? "0px 16px 16px" : "16px";
    if (firstPost) {
        newQuestion.style.marginTop = 0;
    }
    newQuestion.setAttribute("id", post.id);
    var name = document.createElement("h4");
    name.innerText = post.question.name;
    var hrElement = document.createElement("hr");
    hrElement.className = "w3-clear";
    var question = document.createElement("p");
    question.innerText = post.question.comment;
    linebreak = document.createElement("br");
    hrElement.appendChild(linebreak);

    newQuestion.appendChild(linebreak);
    newQuestion.appendChild(name);
    newQuestion.appendChild(hrElement);
    newQuestion.appendChild(question);
    posts.appendChild(newQuestion);
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

//Creates an button for representative answer
function createAnswerButton(postId, repName, question) {
    var repAnswer = document.createElement("button");
    repAnswer.onclick = function() {
        var answerForm = document.getElementById("answerForm" + postId);
        answerForm.style.display = (answerForm.style.display == "none") ? "block" : "none";
    };
    repAnswer.className = "w3-button w3-theme-d2 w3-margin-bottom";
    repAnswer.innerText = "Answer";
    var icon = document.createElement("i");
    icon.className = "fa fa-comment";
    repAnswer.appendChild(icon);
    var formDiv = document.createElement("div");
    formDiv.id = "answerForm" + postId;
    question.appendChild(repAnswer);
    question.appendChild(formDiv);
    createAnswerForm(postId, repName);
}

//Creates a button for users to respond to a question
function createReplyButton(postId, repName, question) {
    var replyBtn = document.createElement("button");
    replyBtn.onclick = function() {
        var replyForm = document.getElementById("replyForm" + postId);
        replyForm.style.display = (replyForm.style.display == "none") ? "block" : "none";
    };
    replyBtn.className = "w3-button w3-theme-d2 w3-margin-bottom";
    var replyIcon = document.createElement("i");
    replyIcon.setAttribute("class", "fa fa-comments");
    replyBtn.appendChild(replyIcon);
    var formDiv = document.createElement("div");
    formDiv.id = "replyForm" + postId;
    question.appendChild(replyBtn);
    question.appendChild(formDiv);
    createReplyForm(postId, repName);
}

//Adds tabs to politician page
function addPoliticianTab(tabName, repName){
    var tabsElement = document.getElementById("tabs");
    var div = document.createElement("div");
    div.setAttribute("class", "w3-quarter w3-section");
    var span = document.createElement("span");
    span.setAttribute("class", "w3-xlarge");
    var tabAnchor = document.createElement("a");
    tabAnchor.href = `tab.html?name=${repName}&tab=${tabName}`;
    tabAnchor.innerText = tabName.replace(repName.replace(/\s/g, ''), "");
    span.appendChild(tabAnchor);
    linebreak = document.createElement("br");
    span.appendChild(linebreak);
    div.appendChild(span);
    tabsElement.appendChild(div);
}

//Navigate to a particular tab
function getTab(tab) {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    window.location.href = `tab.html?name=${repName}&tab=${tab}`;
}

//Creates a reply form
function createReplyForm(questionID, repName) {
    var formDiv = document.getElementById("replyForm" + questionID);
    var nickname = localStorage.getItem("nickname");

    var replyForm = document.createElement("form");
    replyForm.setAttribute("action", `/reply_to_post?postId=${questionID}&name=${nickname}&repName=${repName}`);
    replyForm.setAttribute("method", "post");

    var inputElement = document.createElement("input");
    inputElement.setAttribute("type", "text");
    inputElement.setAttribute("name", "reply");
    replyForm.appendChild(inputElement);

    var submitBtn = document.createElement("input");
    submitBtn.type = "submit";

    replyForm.appendChild(submitBtn);
    formDiv.appendChild(replyForm);
    linebreak = document.createElement("br");
    formDiv.appendChild(linebreak);
    formDiv.style.display = "none";       
}

//Creates an answer form 
function createAnswerForm(questionID, repName) {
    var formDiv = document.getElementById("answerForm" + questionID);

    var ansForm = document.createElement("form");
    ansForm.setAttribute("action", `/rep_answer?postId=${questionID}&repName=${repName}`);
    ansForm.setAttribute("method", "post");

    var inputElement = document.createElement("input");
    inputElement.type = "text";
    inputElement.name = "answer";
    ansForm.appendChild(inputElement);

    var submitBtn = document.createElement("input");
    submitBtn.type = "submit";
    
    ansForm.appendChild(inputElement);
    ansForm.appendChild(submitBtn);
    formDiv.appendChild(ansForm);
    linebreak = document.createElement("br");
    formDiv.appendChild(linebreak);
    formDiv.style.display = "none";       
}

//Displays the list of replies for a particular post
function displayReplyList(post) {
    replyList = post.replies;
    replyList.forEach((reply)=> {
        var postElement = document.getElementById(post.id);
        var newReply = document.createElement("p");
        newReply.innerText = reply.name + ": " + reply.comment;
        postElement.appendChild(newReply);
    })
}

//Displays the representative's answer to a particular post
function displayRepAnswer(post, repName) {
    var answer = post.answer;
    if (answer != undefined) {    
        var postElement = document.getElementById(post.id);
        var repAnswer = document.createElement("p");
        repAnswer.innerText = repName + ": " + answer.comment;
        postElement.appendChild(repAnswer);
    }
}

//Creates form for user to ask a new question on rep's feed
function createQuestionForm(repName, tabList, feedBool) {
    document.getElementById("feedContainer").style.display = "block";
    var nickname = localStorage.getItem("nickname");

    var newQuestionForm = document.getElementById("newQuestionForm");
    newQuestionForm.setAttribute("action", `/new_post?name=${nickname}&repName=${repName}&feed=${feedBool}`);
    newQuestionForm.setAttribute("method", "post");

    var tabDropdown = document.getElementById("tabDropDown");

    tabList.forEach((tab) => {
        addTabDropdown(tabDropdown, tab.name, repName);
    });
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

//Makes fetch to repListServlet and pulls list of reps, makes calls to displayRepList to render html elements with rep names
async function getRepList() {
    var rep = localStorage.getItem("rep");
    var displayFunction = (rep.trim() == "true") ? displayRepListLogin : displayRepListUser;
    var zipcode = localStorage.getItem("zipcode");
    var response = await fetch(`/rep_list?zipcode=${zipcode}`)
    var representatives = await response.json();
    representatives = JSON.parse(representatives);
    if (representatives["error"]) {
        window.location.href = "/errors/zipcodeNotFound.html";
        return;
    }
    var representativeList = document.getElementById("repList");
    var offices = representatives.offices;
    var officials = representatives.officials;
    for (var i = 0; i < offices.length; i++) {
        for (number of offices[i]["officialIndices"]) {
            var repInDatastore = await checkIfRepInDatastore(officials[number]["name"]);
            var rep;
            if (repInDatastore) {
                var repResponse = await fetch(`/feed?repName=${officials[number]["name"]}`);
                rep = await repResponse.json();
            }
            representativeList.appendChild(displayFunction(offices[i]["name"], officials[number]["name"], repInDatastore, rep.blobKeyUrl));
        }
    }
}

//Adds list element for each rep with link to rep's feed if account created
function displayRepListUser(title, name, inDatastore, image) {
    if (inDatastore) {
        var listElement = document.createElement('li')
        listElement.className = "w3-bar";
        
        var imageElement = document.createElement("img");
        imageElement.className = "w3-bar-item w3-circle w3-hide-small ";
        imageElement.style = "width:85px";

        listElement.onclick = function() {window.location.href = `feed.html?name=${name}`};
        imageElement.src = image;
        return displayRepList(listElement, imageElement, title, name);
    }
    return document.createElement("emptyNode");
}

//Displays rep list and adds href linking to create account html if rep not already in datastore
function displayRepListLogin(title, name, inDatastore, image) {
    if (inDatastore == false) {
        const listElement = document.createElement('li')
        listElement.className = (inDatastore == false) ? "w3-bar w3-hoverable" : "w3-bar";
        
        const imageElement = document.createElement("img");
        imageElement.className = "w3-bar-item w3-circle w3-hide-small";
        imageElement.style = "width:85px";
        listElement.onclick = function() {window.location.href = `repUsernamePassword.html?name=${name}&title=${title}`};
        imageElement.src = "/images/defaultProfilePicture.png";

        return displayRepList(listElement, imageElement, title, name);
    }
    return document.createElement("emptyNode");
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
    "/errors/usernameTaken.html" : `repQuestionnaire.html?name=${repName}`;
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
    label.innerText = topic ? "Topic: " : "Platform: ";
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
    var platforms = document.getElementsByClassName("platform");
    var intro = document.getElementById("intro").value;
    var listOfTopics = [];
    var listOfPlatforms = [];
    for (var i = 0; i < topics.length; i++) {
        if (topics[i].value != "") {
            listOfTopics.push(topics[i].value);
            var platform = (platforms[i].value != "") ? platforms[i].value : 
                "This representative has not provided a platform";
            var platformString = (i == topics.length - 1) ? platform : platform + "*";
            listOfPlatforms.push(platformString);
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
    
    const titleElement = document.getElementById("title");
    titleElement.innerText = repJson.title;

    const nameElement = document.getElementById("repName");
    nameElement.innerText = repName;

    const imgElement = (document.getElementsByClassName("bgimg"))[0];
    imgElement.style.backgroundImage = `url(${repJson.blobKeyUrl})`;
    
    const introElement = document.getElementById("about");
    introElement.innerText = repJson.intro;

    repJson.tabs.forEach((tab) => {
        addPoliticianTab(tab.name, repName);
    });

    returnToFeed(repName, document.getElementById("feed"));
}
