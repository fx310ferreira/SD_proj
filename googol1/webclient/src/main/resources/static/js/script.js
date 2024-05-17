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
    landingPage?.classList.toggle("shift");
    searchesPage?.classList.toggle("shift");
    console.log("Hamburger clicked");
    fetch("http://localhost:8080/index",
    {method: "POST",
     body:'https://github.com',})
      .then((response) => response.json())
      .then((json) => console.log(json));
//    if (this.classList.contains("active")) {
//        leftArrow.style.visibility = 'hidden';
//        rightArrow.style.visibility = 'hidden';
//    } else {
//        setTimeout(() => {
//            updateContent();
//        }, 600);
//    }
});

// ------------------------------------------------------

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
    if (searchQuery) {
        performHackerNewsSearch(searchQuery);
    }

    // Hide the landing page and show the search results page
    document.querySelector('.landing-page').classList.remove('active');
    document.querySelector('.searches-page').classList.add('active');
});

document.querySelector('.search-input').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        const searchQuery = document.querySelector('.search-input').value;
        if (searchQuery) {
            performHackerNewsSearch(searchQuery);
        }

        // Hide the landing page and show the search results page
        document.querySelector('.landing-page').classList.remove('active');
        document.querySelector('.searches-page').classList.add('active');
    }
});

function performHackerNewsSearch(query) {
    fetch('https://hacker-news.firebaseio.com/v0/topstories.json')
        .then(response => response.json())
        .then(ids => {
            let fetchPromises = ids.map(id => 
                fetch(`https://hacker-news.firebaseio.com/v0/item/${id}.json`)
                .then(response => response.json())
            );

            Promise.all(fetchPromises).then(results => {
                let indexCount = 0;
                let indexPromises = results.map(item => {
                    if ((item.title && item.title.includes(query)) || (item.text && item.text.includes(query))) {
                        return fetch('http://localhost:8080/index', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: item.url
                        })
                        .then(response => response.json())
                        .then(data => {
                            console.log(data);
                            indexCount++;
                        });
                    }
                });

                Promise.all(indexPromises).then(() => {
                    console.log(`${indexCount} link(s) indexado(s) do Hacker News`);
                    document.querySelector('.sidebar').innerHTML += `<p>${indexCount} link(s) indexado(s) do Hacker News</p>`;
                });
            });
        });
}

function getAstronomyPicture() {
    const apiKey = 'S2hU0xFxd7RpD4NdVXjzdAN0ogmYkCyidxCiYZAI';
    const apiUrl = `https://api.nasa.gov/planetary/apod?api_key=${apiKey}`;

    const popUpImage = document.querySelector('.popup-image');
    const popUpTitle = document.getElementById('.popup-title');

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            popUpImage.src = data.url;
            popUpImage.style.display = 'block';
            popUpTitle.textContent = data.title;
        })
        .catch(error => {
            console.error('Error fetching the image:', error);
            popUpImage.style.display = 'none';
            popUpTitle.textContent = 'Error loading the Astronomy Picture of the Day';
        });
};

