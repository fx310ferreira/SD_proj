// document.addEventListener("DOMContentLoaded", function() {

const landingPage = document.querySelector('.landing-page');
const searchesPage = document.querySelector('.searches-page');

const searchBar = document.querySelectorAll('.search-bar');
const auroraGradient = document.querySelector('.aurora-gradient');

const hamburger = document.querySelector(".hamburger");
const sidebar = document.querySelector(".sidebar");

const leftArrow = document.getElementById('left-arrow');
const rightArrow = document.getElementById('right-arrow');

const colors = ['#FF6347', '#DD335C', '#A020F0', '#1E67C6', '#13FFAA'];
let currentIndex = 0;

function updateGradient() {
    const currentColor = colors[currentIndex];
    const nextColor = colors[(currentIndex + 1) % colors.length];

    searchBar.forEach(searchBar => {
        searchBar.style.border = `2px solid ${currentColor}`;
        searchBar.style.boxShadow = `0 0 20px ${currentColor}`;
    });

    /* if (document.querySelector('.searches-page').classList.contains('active')) {
        // Reduce the opacity of the aurora gradient
        auroraGradient.style.background = `radial-gradient(125% 125% at 50% 0%, #020617 50%, ${currentColor}), radial-gradient(125% 125% at 50% 0%, #020617 50%, ${nextColor})`;
    } else {
        // Keep the original gradient without opacity adjustment
        auroraGradient.style.background = `radial-gradient(125% 125% at 50% 0%, #020617 50%, ${currentColor}), radial-gradient(125% 125% at 50% 0%, #020617 50%, ${nextColor})`;
    } */

    auroraGradient.style.background = `radial-gradient(125% 125% at 50% 0%, #020617 50%, ${currentColor}), radial-gradient(125% 125% at 50% 0%, #020617 50%, ${nextColor})`;
    // auroraGradient.style.transition = 'background 2.5s ease-in-out';

    // sidebar.style.border = `2px solid ${currentColor}`;
    currentIndex = (currentIndex + 1) % colors.length;
}

updateGradient();

setInterval(updateGradient, 2500);

// ------------------------------------------------------

hamburger.addEventListener("click", function () {
    this.classList.toggle("active");
    sidebar.classList.toggle("active");
    landingPage.classList.toggle("shift");
    searchesPage.classList.toggle("shift");

    if (this.classList.contains("active")) {
        leftArrow.style.visibility = 'hidden';
        rightArrow.style.visibility = 'hidden';
    } else {
        setTimeout(() => {
            leftArrow.style.visibility = 'visible';
            rightArrow.style.visibility = 'visible';
        }, 600);
    }
});

// ------------------------------------------------------

const faqData = [
    { question: 'Google', answer: 'https://www.google.com' },
    { question: 'Youtube', answer: 'https://www.youtube.com' },
    { question: 'Facebook', answer: 'https://www.facebook.com' },
    { question: 'Instagram', answer: 'https://www.instagram.com' },
    { question: 'Tik Tok', answer: 'https://www.tiktok.com' },
    { question: 'Amazon', answer: 'https://www.amazon.com' }
    // { question: 'Wikipedia', answer: 'https://www.wikipedia.org' },
    // { question: '-hub', answer: "You don't wanna know" },
    // { question: 'Open AI', answer: 'https://www.openai.com' },
    // { question: 'Reddit', answer: 'https://www.reddit.com' }
];

function createFaqItem(data) {
    const container = document.createElement('div');
    container.classList.add('container-q');

    const question = document.createElement('div');
    question.classList.add('question');
    question.textContent = data.question;

    const answer = document.createElement('div');
    answer.classList.add('answer');
    answer.textContent = data.answer;

    container.appendChild(question);
    container.appendChild(answer);

    return container;
}

function renderFaqResults() {
    const topSearchesElement = document.querySelector('.top-searches');

    faqData.forEach(item => {
        const faqItem = createFaqItem(item);
        topSearchesElement.appendChild(faqItem);
    });
}

renderFaqResults();

let questions = document.querySelectorAll(".question");

questions.forEach(question => {
    question.addEventListener("click", event => {
        const active = document.querySelector(".question.active");

        if (active && active !== question) {
            active.classList.toggle("active");
            active.nextElementSibling.style.maxHeight = 0;
            active.nextElementSibling.style.padding = "0";
        }

        question.classList.toggle("active");
        const answer = question.nextElementSibling;

        if (question.classList.contains("active")) {
            answer.style.padding = "20px 0";
            // answer.style.maxHeight = answer.scrollHeight + "px";
        } else {
            answer.style.maxHeight = 0;
            answer.style.padding = "0";
        }
    })
})

// ------------------------------------------------------

document.querySelector('.bx-search').addEventListener('click', function () {
    const searchQuery = document.querySelector('.search-input').value;

    // Perform the search using the query (...)

    // Hide the landing page and show the search results page
    document.querySelector('.landing-page').classList.remove('active');
    document.querySelector('.searches-page').classList.add('active');
});

document.querySelector('.search-input').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        const searchQuery = document.querySelector('.search-input').value;

        // Perform the search using the query

        // Hide the landing page and show the search results page
        document.querySelector('.landing-page').classList.remove('active');
        document.querySelector('.searches-page').classList.add('active');
    }
});

// ------------------------------------------------------

const searchResultsData = [
    { title: 'Search Result 1', description: 'Search Result 1 Description' },
    { title: 'Search Result 2', description: 'Search Result 2 Description' },
    { title: 'Search Result 3', description: 'Search Result 3 Description' },
    { title: 'Search Result 4', description: 'Search Result 4 Description' },
    { title: 'Search Result 5', description: 'Search Result 5 Description' },
    { title: 'Search Result 6', description: 'Search Result 6 Description' },
    { title: 'Search Result 7', description: 'Search Result 7 Description' },
    { title: 'Search Result 8', description: 'Search Result 8 Description' },
    { title: 'Search Result 9', description: 'Search Result 9 Description' },
    { title: 'Search Result 10', description: 'Search Result 10 Description' }
];

function createSearchResultItem(result) {
    const li = document.createElement('li');
    li.classList.add('search-item');

    const h3 = document.createElement('h3');
    h3.textContent = result.title;

    const p = document.createElement('p');
    p.textContent = result.description;

    li.appendChild(h3);
    li.appendChild(p);

    return li;
}

function renderSearchResults() {
    const searchResultsElement = document.querySelector('.search-results');
    searchResultsElement.innerHTML = '';

    searchResultsData.forEach(result => {
        const item = createSearchResultItem(result);
        searchResultsElement.appendChild(item);
    });
}

renderSearchResults();

// ------------------------------------------------------

let currentPageNumber = 1;

updateContent();

document.getElementById('left-arrow').addEventListener('click', function() {
    currentPageNumber--;
    animateSearchResults('right');
    updateContent();
});

document.getElementById('right-arrow').addEventListener('click', function() {
    currentPageNumber++;
    animateSearchResults('left');
    updateContent();
});

function animateSearchResults(direction) {
    const searchItems = document.querySelectorAll('.search-item');
    const animationDuration = 0.6;
    let delayIncrement = animationDuration / searchItems.length;

    searchItems.forEach((item, index) => {
        const delay = direction === 'left' ? index * delayIncrement : (searchItems.length - index - 1) * delayIncrement;
        const translateValue = `${100 * (currentPageNumber - 1)}%`;
        item.style.transition = `transform ${animationDuration}s ${delay}s ease-in-out`;
        item.style.transform = `translateX(${translateValue})`;
    });
}

function updateContent() {
    if (currentPageNumber > 1) {
        leftArrow.style.visibility = 'visible';
    } else {
        leftArrow.style.visibility = 'hidden';
    }
    rightArrow.style.display = 'block';
}
