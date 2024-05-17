document.querySelectorAll('.shorten-btn').forEach(btn => {
    console.log(btn.previousElementSibling);
    btn.addEventListener('click', function () {

        const bigUrl = btn.previousElementSibling.getAttribute("href");
        const url = 'https://url-shortener-service.p.rapidapi.com/shorten';
        const options = {
            method: 'POST',
            headers: {
                'content-type': 'application/x-www-form-urlencoded',
                'X-RapidAPI-Key': '26675a2eb4msh980b6e0d590bdadp1c8880jsnd15125c97e34',
                'X-RapidAPI-Host': 'url-shortener-service.p.rapidapi.com'
            },
            body: new URLSearchParams({
                url: bigUrl
            })
        };
        btn.textContent = "Loading...";
        fetch(url, options).then(response => response.json()).then(data => {
            navigator.clipboard.writeText(data.result_url);
            btn.textContent = "Copied";
        }).catch(error => {
            btn.textContent = "Failed";
        });
    });
});