document.addEventListener('DOMContentLoaded', function () {
    const navLinks = document.querySelectorAll('.nav-link');
    const access = localStorage.getItem('access'); // Get the token from localStorage

    navLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            const targetUrl = link.getAttribute('href');

            // Prevent default behavior for all links
            event.preventDefault();

            // Perform the fetch request to the target URL
            fetch('http://localhost:8080' + targetUrl, {
                method: 'GET',
                headers: {
                    'Authorization' : access,
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text(); // Get the response as text (HTML)
                })
                .then(html => {
                    // Replace the content of a specific element with the new HTML
                    document.body.innerHTML = html; // Adjust based on where you want to insert the content
                })
                .catch(error => {
                    console.error('Error:', error);
                    window.location.href = '/loginForm';
                    alert('페이지 로드에 실패했습니다. 다시 시도해주세요.');
                });
        });
    });
});