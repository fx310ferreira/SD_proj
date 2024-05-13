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

document.getElementById('right-arrow').addEventListener('click', function () {
    currentPageNumber++;
    updateContent();
});

document.getElementById('left-arrow').addEventListener('click', function () {
    currentPageNumber--;
    updateContent();
});

function updateContent() {
    if (currentPageNumber > 1) {
        leftArrow.style.visibility = 'visible';
    } else {
        leftArrow.style.visibility = 'hidden';
    }
    rightArrow.style.display = 'block';
}
