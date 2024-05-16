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
            displayBarrels(result.activeBarrels);
            displayTimes(result.responseTimes, result.activeBarrels);
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

function displayBarrels(barrels) {
    const barrelsElement = document.querySelector('.active-barrels');
    barrelsElement.innerHTML = '';
    barrels.forEach(item => {
        const faqItem = createBarrelItem(item);
        barrelsElement.appendChild(faqItem);
    });
}

function createBarrelItem(data) {
    const container = document.createElement('div');
    container.classList.add('barrel');
    container.textContent = data;
    return container;
}

function displayTimes(responseTimes, barrels) {
    const timesElement = document.querySelector('.response-times');
    timesElement.innerHTML = '';
    console.log(responseTimes);
    //todo fix times display
    barrels.forEach(item => {
        var sum = 0;
        responseTimes[item].forEach(time => {
            sum += time;
        });
        var avg = sum / responseTimes[item].length;
        avg = avg.toFixed(2);
        const faqItem = createTimes(avg);
        timesElement.appendChild(faqItem);
    });
}

function createTimes(data) {
    const container = document.createElement('div');
    container.classList.add('response-time');
    console.log(data);
    container.textContent = data;
    return container;
}