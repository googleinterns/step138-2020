//button styling 
mdc.ripple.MDCRipple.attachTo(document.querySelector('.foo-button'));

//this funciton will implement the fetch to grab the list of comments from the database and call to the newDiv()
//function to place the comments into the feed div.
window.onload = function display_feed(){
    
}

//creates a new div for each new comment
function newDiv(){
    //make this list by calling a fetch function 
    var test_comment = ["Hello mr.rep", "hi mr.townsman", "hi doctor"];
    test_comment.forEach((comment) => {
        var new_comment = document.createElement("div");
        new_comment.setAttribute("class", "new_comment");
        new_comment.innerText = comment;
        var feed = document.getElementById("mid_col");
        feed.appendChild(new_comment);
    })
        
}
