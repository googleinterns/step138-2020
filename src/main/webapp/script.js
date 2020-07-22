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
    if (tabName.includes("Other") && rep.trim() == "true") {
        var addTab = document.getElementById("addTab");
        addTab.style.display = "block";
        addTab.onclick = function() {
            window.location.href = window.location.href + "&addTab=true"
        }
    }

    //Add tabs the sidebar
    var response = await fetch(`rep_tabs?repName=${encodeURI(repName)}`);
    var tabList = await response.json();
    var tabs = document.getElementById("tabs");
    tabList.forEach((tab) => {
        if (tab.name != tabName) {
            addTabButton(tab.name, tabs, repName);
        }
    });

    //fetch the representative entity corresponding to repName
    var response = await fetch(`/feed?repName=${encodeURI(repName)}`);
    var representative = await response.json();

    //fetch the tabEntity corresponding to tabName
    var tabResponse = await fetch(`tab_entity?tabName=${encodeURI(tabName)}`);
    var tabEntity = await tabResponse.json();

    //Set values for tabname, title, image, and platform
    document.getElementById("tabName").innerText = tabName.replace(repName.replace(/\s/g, ''), "");
    document.getElementById("tabNameDiv").style.margin = "0px 16px 16px";
    document.getElementById("repTitle").innerText = representative.title;
    document.getElementById("repProfilePic").src = representative.blobKeyUrl;
    document.getElementById("platform").innerText = tabEntity.propertyMap.Platform;
    document.getElementById("repName").innerText = repName;
    document.getElementById("repContainer").onclick = function() {
        window.location.href = `/politicianPage.html?name=${encodeURI(repName)}`};

    //Add a button to return to feed
    var feedButton = document.getElementById("feed");
    feedButton.onclick = function() {window.location.href = `/feed.html?name=${encodeURI(repName)}`};

    //Pull the posts under a particular tag
    var tabPostsResponse = await fetch(`/tab_posts?repName=${encodeURI(repName)}&tab=${tabName}`);
    var posts = await tabPostsResponse.json();

    if (rep.trim() != "true") {
        createQuestionForm(repName, [{"name" : tabName}], false);
        turnOnRepListIcon();
    }
    else if (posts.length == 0) {
        var emptyFeed = document.createElement("p");
        emptyFeed.innerText = "There are currently no questions under this tab."
        emptyFeed.className = "col-sm-10";
        document.getElementById("posts").appendChild(emptyFeed);
    }
    
    var addTab = decodeURI(urlParams.get("addTab"));
    if (addTab == "true") {
        document.getElementById("postsForm").style.display = "block";
    }
    posts.forEach((post) => {
        if (addTab == "true") {
            addPostToNewTabForm(post)
        } else { 
            displayPostWithoutNewTab(post, false);
        }
        var question = document.getElementById(post.id);

        //answer button
        if (rep.trim() == "true" && addTab != "true") {
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
    var response = await fetch(`/feed?repName=${encodeURI(repName)}`);
    var representative = await response.json();
    document.getElementById("repTitle").innerText = representative.title;
    document.getElementById("repProfilePic").src = representative.blobKeyUrl;
    document.getElementById("repContainer").onclick = function() {
        window.location.href = `/politicianPage.html?name=${encodeURI(repName)}`};

    //Fetches the list of tabs for a particular rep
    var response = await fetch(`rep_tabs?repName=${encodeURI(repName)}`);
    var tabList = await response.json();
    var tabs = document.getElementById("tabs");
    tabList.forEach((tab) => {
        addTabButton(tab.name, tabs, repName);
    });

    postList = representative.posts;
    if (rep.trim() != "true") {
        createQuestionForm(repName, tabList, true);
        turnOnRepListIcon();
    }
    else if (postList.length == 0) {
        var emptyFeed = document.createElement("p");
        emptyFeed.innerText = "There are currently no questions on your feed."
        emptyFeed.className = "col-sm-10";
        document.getElementById("posts").appendChild(emptyFeed);
    }
    postList.forEach((post) => {
        var firstPost = (rep.trim() == "true" && post == postList[0]) ? true : false;
        displayPostWithoutNewTab(post, firstPost);
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

//Add a link for returning to rep list
function turnOnRepListIcon() {
    document.getElementById("repListIcon").style.display = "block";
}

//Adds a tab button
function addTabButton(tabName, container, repName) {
    var tab = document.createElement("button");
    tab.onclick = function() {return getTab(tabName);} 
    tab.className = "w3-button w3-block w3-theme-l1 w3-left-align";
    tab.innerText = tabName.replace(repName.replace(/\s/g, ''), "");
    container.appendChild(tab);
}

//Displays specified reaction button in post 
function displayReaction(post, repName, reactionDiv, reaction) {
    var btn = document.createElement("button");
    var reactionCount = parseInt(post.reactions[reaction]);
    localStorage.setItem(post.id + reaction, reactionCount);
    if (localStorage.getItem(post.id) === reaction) {
        reactedBtn = btn; 
        setReactionButtonContent(btn, reaction, reactionCount, "selected");
    }
    else {
        setReactionButtonContent(btn, reaction, reactionCount, "notselected");
    }

    btn.onclick = function() {reactToPost(btn, reaction, post.id, repName);}; 
    reactionDiv.appendChild(btn); 
}

//Sets content inside the reaction button 
function setReactionButtonContent(btn, reaction, reactionCount, selected) {
    if (btn === null) {
        return; 
    } 
    var imageSrc = "reaction_icons/" + reaction.toLowerCase() + ".jpg";
    btn.innerHTML = '<img src="'+ imageSrc +'" width="20px" height="20px">';
    btn.innerHTML += reactionCount; 
    btn.setAttribute("class", selected);
}

//On click function for clicking on reaction button 
async function reactToPost(btn, reaction, postId, repName) {  
    var oldReaction = localStorage.getItem(postId);
    var reactionCount = parseInt(localStorage.getItem(postId + reaction)); 
    if (oldReaction === null || oldReaction === "null") { 
        await fetch(`/react_to_post?repName=${encodeURI(repName)}&postId=
            ${postId}&reaction=${reaction}`);
        reactedBtn = btn; 
        localStorage.setItem(postId, reaction);
        localStorage.setItem(postId + reaction, reactionCount + 1); 
        setReactionButtonContent(btn, reaction, reactionCount + 1, "selected"); 
        return; 
    }

    var oldReactionCount = parseInt(localStorage.getItem(postId + oldReaction));

    if (oldReaction === reaction) {
        if (reactionCount > 0) {
            await fetch(`/unreact_to_post?repName=${encodeURI(repName)}&postId=
                ${postId}&reaction=${reaction}`);
            localStorage.setItem(postId, null);
            reactedBtn = null; 
            localStorage.setItem(postId + reaction, reactionCount - 1); 
            setReactionButtonContent(btn, reaction, reactionCount - 1, "notselected"); 
        }
    }
    else {
        if (oldReactionCount > 0) {
            await fetch(`/unreact_to_post?repName=${encodeURI(repName)}&postId=
                ${postId}&reaction=${oldReaction}`);
            localStorage.setItem(postId + oldReaction, oldReactionCount - 1); 
            setReactionButtonContent(reactedBtn, oldReaction, oldReactionCount - 1, "notselected"); 
        }
        await fetch(`/react_to_post?repName=${encodeURI(repName)}&postId=
            ${postId}&reaction=${reaction}`);
        localStorage.setItem(postId + reaction, reactionCount + 1); 
        setReactionButtonContent(btn, reaction, reactionCount + 1, "selected"); 
        localStorage.setItem(postId, reaction);
        reactedBtn = btn; 
    }
}

//Displays the question for a post
function displayPostWithoutNewTab(post, firstPost) {
    var posts = document.getElementById("posts");
    var postElement = displayPost(post, firstPost);
    posts.appendChild(postElement);
}

//Displays posts with checkboxes for selecting particular ones to
//move to another tab
function addPostToNewTabForm(post) {
    var div = document.getElementById("post");
    var wrapper = document.createElement("div");
    wrapper.style.display = "flex";
    wrapper.style.alignItems = "center";
    var checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.name = "checkbox";
    checkbox.value = post.id;
    var postElement = displayPost(post, false);
    postElement.style.flexGrow = 1;
    wrapper.appendChild(checkbox);
    wrapper.appendChild(postElement);
    div.appendChild(wrapper);
}

//Adds a new tab and migrates posts accordingly
async function addNewTab() {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    var tabName = document.getElementById("newTabName").value;
    var platform = document.getElementById("newTabPlatform").value;
    var checkboxes= document.getElementsByName("checkbox");
    var checked = [];
    for (check of checkboxes) {
        if (check.checked == true) {
            checked.push(check.value);
        }
    }
    var response = await fetch(`/add_new_tab?tabName=${tabName}&platform=${platform}&posts=${checked}&repName=${repName}`);
    window.location.href = "/feed.html?name=" + encodeURI(repName);
}

//Abstract out display of Post
function displayPost(post, firstPost) {
    var newQuestion = document.createElement("div");
    newQuestion.className = "w3-container w3-card w3-white w3-round";
    newQuestion.style.margin = (firstPost) ? "0px 16px 16px" : "16px";
    if (firstPost) {
        newQuestion.style.marginTop = 0;
    }
    newQuestion.setAttribute("id", post.id);
    var postId = document.createElement("span");
    postId.className = "w3-right w3-opacity";
    postId.innerText = post.id;
    var name = document.createElement("h4");
    name.innerText = post.question.name;
    var hrElement = document.createElement("hr");
    hrElement.className = "w3-clear";
    var question = document.createElement("p");
    question.innerText = post.question.comment;
    linebreak = document.createElement("br");
    hrElement.appendChild(linebreak);

    newQuestion.appendChild(linebreak);
    newQuestion.appendChild(postId);
    newQuestion.appendChild(name);
    newQuestion.appendChild(hrElement);
    newQuestion.appendChild(question);
    return newQuestion
}

//Creates anchor tag that links back to representative's feed
function returnToFeed(repName, feed) {
    feed.href = "feed.html?name=" + encodeURI(repName);    
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
    tabAnchor.href = `tab.html?name=${encodeURI(repName)}&tab=${encodeURI(tabName)}`;
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
    window.location.href = `tab.html?name=${encodeURI(repName)}&tab=${tab}`;
}

//Creates a reply form
function createReplyForm(questionID, repName) {
    var formDiv = document.getElementById("replyForm" + questionID);
    var nickname = localStorage.getItem("nickname");

    var replyForm = document.createElement("form");
    replyForm.setAttribute("action", `/reply_to_post?postId=${questionID}&name=${nickname}&repName=${encodeURI(repName)}`);
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
    ansForm.setAttribute("action", `/rep_answer?postId=${questionID}&repName=${encodeURI(repName)}`);
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
        displayCommentWithLinking(postElement, newReply, reply);
    })
}

//Displays the representative's answer to a particular post
function displayRepAnswer(post, repName) {
    var answer = post.answer;
    if (answer != undefined) {    
        var postElement = document.getElementById(post.id);
        var repAnswer = document.createElement("p");
        displayCommentWithLinking(postElement, repAnswer, answer);
    }
}

//Displays comment and links to other posts if applicable
function displayCommentWithLinking(postElement, container, commentObject) {
    container.innerHTML = commentObject.name + ": ";
    var wordsInComment = commentObject.comment.split(" ");
    //Captures links that start with "@" and are followed by the 16 digits corresponding to postId
    var regex = /@\d{16}/g;
    var linksToPosts = commentObject.comment.match(regex);
    for (word of wordsInComment) {
        if (linksToPosts) {
            if (linksToPosts.includes(word)) {
            var anchor = `<a href="${window.location.href + "#" + word.substring(1, word.length)}">${word + " "}</a>`;
            container.innerHTML += anchor;
            }
        }
        else {
            container.innerHTML += (word + " ");
        }
    }
    postElement.appendChild(container);
}

//Creates form for user to ask a new question on rep's feed
function createQuestionForm(repName, tabList, feedBool) {
    document.getElementById("feedContainer").style.display = "block";
    var nickname = localStorage.getItem("nickname");

    var newQuestionForm = document.getElementById("newQuestionForm");
    newQuestionForm.setAttribute("action", `/new_post?name=${nickname}&repName=${encodeURI(repName)}&feed=${feedBool}`);
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
    if (stripTabName == "Other") {
        tabElement.selected = true;
    }
    tabElement.value = stripTabName;
    tabElement.innerText = stripTabName;
    tabDropdown.appendChild(tabElement);
}

//When user logins in, stores their zipcode and name in local storage and redirects to repList.html
function storeZipCodeAndNickname() {
    event.preventDefault();
    var nickname = document.getElementById("nickname").value;
    nickname = (nickname == "") ? "Anonymous" : nickname;
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
    //Change navbar based on rep v user 
    var backToLogin = document.getElementById("backToLogin");
    backToLogin.style.display = rep.trim() == "true" ? "block" : "none";
    
    var logout = document.getElementById("logout");
    logout.style.display = rep.trim() == "true" ? "none" : "block";
    
    var zipcode = localStorage.getItem("zipcode");
    var response = await fetch(`/rep_list?zipcode=${zipcode}`)
    var representatives = await response.json();
    representatives = JSON.parse(representatives);
    if (representatives["error"]) {
        window.location.href = `/errors/zipcodeNotFound.html?rep=${rep.trim()}`;
        return;
    }
    document.getElementById("repListTitle").innerText = "Representative List";
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

        listElement.onclick = function() {window.location.href = `feed.html?name=${encodeURI(name)}`};
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
        listElement.onclick = function() {window.location.href = `repUsernamePassword.html?name=${encodeURI(name)}&title=${title}`};
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
    var response = await fetch(`/rep_in_datastore?repName=${encodeURI(repName)}`)
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
    ${password}&repName=${encodeURI(repName)}&title=${title}`).then(response => response.text());
    window.location.href = (usernameTaken.trim() == "true") ?  
    "/errors/usernameTaken.html" : `repQuestionnaire.html?name=${encodeURI(repName)}`;
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
    //Remove * on the end of the last platform string if it exists
    if (listOfPlatforms != []) {
        var lastPlatform = listOfPlatforms[listOfPlatforms.length - 1];
        lastPlatform = (lastPlatform[lastPlatform.length - 1] == "*") ? 
        lastPlatform.substring(0, lastPlatform.length - 1): lastPlatform;
        listOfPlatforms[listOfPlatforms.length - 1] = lastPlatform;
    }
    var response = await fetch(`rep_submit_questionnaire?topicList=${listOfTopics}&platformList=
        ${listOfPlatforms}&intro=${intro}&repName=${encodeURI(repName)}`);
    if (document.getElementById("imageUpload") != null) {
        return true;
    }
    else {
        event.preventDefault();
        window.href.location = `/feed.html?name=${repName}`;
        return false;
    }
}

//Set action of repQuestionnaire to make request to blobstore
function fetchBlobstoreUrlAndShowForm() {
    var urlParams = new URLSearchParams(window.location.search);
    var repName = decodeURI(urlParams.get('name')); 
    fetch(`/blobstore-upload-url?repName=${encodeURI(repName)}`)
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
    var representativeResponse = await fetch(`/feed?repName=${encodeURI(repName)}`)
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

//Logs out a user or rep
function resetLocalStorage() {
    localStorage.setItem("nickname", "Anonymous");
    localStorage.setItem("rep", false);
}

//Picks a particular return link for the zipcode error page
function zipcodelink() {
    var urlParams = new URLSearchParams(window.location.search);
    var isRep = decodeURI(urlParams.get('rep')); 
    console.log(isRep);
    var backToLogin = document.getElementById("backToUserLogin");
    backToLogin.style.display = isRep.trim() == "true" ? "none" : "block";

    var zipcode = document.getElementById("backToRepZipcode");
    zipcode.style.display = isRep.trim() == "true" ? "block" : "none"; 
}
