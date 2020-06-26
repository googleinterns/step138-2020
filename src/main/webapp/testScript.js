//button styling 
mdc.ripple.MDCRipple.attachTo(document.querySelector('.foo-button'));

function displayFeed(repName){
    fetch(`/feed?rep_name=${repName}`).then(response => response.json()).then((representative)=>{
        postList = representative.getPosts();
        repName = representative.getName();
        postList.forEach((post) => {
            var feed = document.getElementById("mid_col");
            //new questions div
            var displayRepName = document.createElement("div");
            displayRepName.innerText = repName;
            feed.appendChild(displayRepName);
            var newQuestion = document.createElement("div");
            newQuestion.setAttribute("class", "newComment");
            newQuestion.setAttribute("id", post.getID());
            //question text added to div
            var qText = document.createElement("p");
            qText.innerText = post.getQuestion();
            newQuestion.appendChild(qText);
            //username added
            var username = document.createElement("p");
            username.setAttribute("class", "username");
            username.innerText = "John Smith";
            newQuestion.appendChild(username);
            //reply/answer buttons
            var replyBtn = document.createElement("button");
            replyBtn.addEventListener("click", createReplyForm(post.getID()));
            replyBtn.setAttribute("class", "btn");
            var replyIcon = document.createElement("i");
            replyIcon.setAttribute("class", "fa fa-comments");
            replyBtn.appendChild(replyIcon);
            var repAnswer = document.createElement("button");
            repAnswer.addEventListener("click", createAnswerForm(post.getID(), repName));
            repAnswer.setAttribute("class", "btn");
            var repAnswerIcon = document.createElement("i");
            repAnswerIcon.setAttribute("class", "fa fa-envelope-open");
            repAnswer.appendChild(repAnswerIcon);
            newQuestion.appendChild(replyBtn);
            newQuestion.appendChild(repAnswer);
            //whole question added to feed
            createReplyList(post);
            feed.appendChild(newQuestion);
        })

    });
}

function createReplyForm(questionID){
    var question = document.getElementById(questionID);
    console.log(questionID);
    var replyForm = document.createElement("form");
    localStorage.getItem("nickname");
    replyForm.setAttribute("action", `/reply_to_post?postId=${questionID}&name=${nickname}`);
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
    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    inputForm.setAttribute("name", "answer");
    ansForm.appendChild(inputForm);
    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    ansForm.appendChild(submitBtn);
    question.appendChild(ansForm);
}
//todo add function to build a mini-feed for each question
//maybe build separate html for each question to display or make a dropdown/drop_to_the_side functionality.
//post.getReplies()
//list<comments>
//Comment: getMessage(); getName();
//
// Work in progress.

// function createReplyList(post){
//     replyList = post.getReplies();//getReplies retuns a list of Comment objects -- getName(), getMessage()
//     replyList.forEach((reply)=>{
//         newReply = document.createElement("div");
//         //divs on a new html to display;
//         //paragaphs in a drop down display
//         //set a side a part of the screen - [post]---[replies:
//         //                                             nice question!]
//     })
// }