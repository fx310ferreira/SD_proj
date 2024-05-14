var stompClient = null;

function connect() {
    var socket = new SockJS('/my-websocket');
    stompClient = Stomp.over(socket);
    console.log(stompClient);
    stompClient.connect({}, function (frame) {
        console.log('connecting to websocket');
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            const result = JSON.parse(JSON.parse(message.body).content);
            displayTop(result.topSearches);
            console.log(result);
        });
    });
}

connect();

function displayTop(topSearches) {
    const topSearchesElement = document.querySelector('.top-searches');
    topSearchesElement.innerHTML = '';
    topSearches.forEach(item => {
        const faqItem = createFaqItem(item);
        topSearchesElement.appendChild(faqItem);
    });
}

function createFaqItem(data) {
    const container = document.createElement('div');
    container.classList.add('container-q');

    const question = document.createElement('div');
    question.classList.add('question');
    question.textContent = data;

    container.appendChild(question);

    return container;
}

//document.getElementById('send').onclick = function () {
//    stompClient.send("/app/message", {}, JSON.stringify({content: "puta"}));
//}