function generateRandomStars(screenWidth, color, density) {
    let stars = '';

    for (let i = 0; i < density; i++) {
        const hShadow = Math.floor(Math.random() * screenWidth);
        const vShadow = Math.floor(Math.random() * screenWidth);
        stars += `${hShadow}px ${vShadow}px ${color}, `;
    }

    return stars.slice(0, -2); // Remove the trailing comma and space
}

const color = '#FFFFFF';
const screenWidth = 1800;
const density = 200;
const speeds = [1, 1.1, 1.2, 1.3, 1.4, 1.5];
const delays = [0, 0.1, 0.2, 0.3, 0.4, 0.5];
const background = document.querySelector('.background');

for (let i = 0; i < 6; i++) {
    const starsGroup = document.createElement('div');
    starsGroup.classList.add('stars-group');
    starsGroup.style.boxShadow = generateRandomStars(screenWidth, color, density);
    starsGroup.style.animationDuration = `${speeds[i]}s`;
    starsGroup.style.animationDelay = `${delays[i]}s`;
    background.appendChild(starsGroup);
}