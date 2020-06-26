//button styling 
mdc.ripple.MDCRipple.attachTo(document.querySelector('.foo-button'));

function display_feed(rep_name){
    fetch(`/feed?rep_name=${rep_name}`).then(response => response.json()).then((representative)=>{
        postList = representative.getPosts();
        postList.forEach((post) => {
        var feed = document.getElementById("mid_col");
        //new questions div
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
        repAnswer.addEventListener("click", createAnswerForm(post.getID()));
        repAnswer.setAttribute("class", "btn");
        var repAnswerIcon = document.createElement("i");
        repAnswerIcon.setAttribute("class", "fa fa-envelope-open");
        repAnswer.appendChild(repAnswerIcon);
        newQuestion.appendChild(replyBtn);
        newQuestion.appendChild(repAnswer);
        //whole question added to feed
        feed.appendChild(newQuestion);
        })

    });
}

function createReplyForm(questionID){
    var question = document.getElementById(questionID);
    console.log(questionID);
    var replyForm = document.createElement("form");
    replyForm.setAttribute("action", "/reply_to_post");
    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    replyForm.appendChild(inputForm);
    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    replyForm.appendChild(submitBtn);
    question.appendChild(replyForm);
        
}

function createAnswerForm(questionID){
    var question = document.getElementById(questionID);
    var ansForm = document.createElement("form");
    ansForm.setAttribute("action", "/rep_answer");
    var inputForm = document.createElement("input");
    inputForm.setAttribute("type", "text");
    ansForm.appendChild(inputForm);
    var submitBtn = document.createElement("button");
    submitBtn.setAttribute("class", "btn submit-btn");
    ansForm.appendChild(submitBtn);
    question.appendChild(ansForm);
}
