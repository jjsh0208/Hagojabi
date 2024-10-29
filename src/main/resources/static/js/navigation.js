document.addEventListener('DOMContentLoaded', function () {
    const navLinks = document.querySelectorAll('.nav-link');
    const loginButton = document.querySelector('.login-button');
    const logoutButton = document.querySelector('.logout-button');

    function handleNavigation(event, targetUrl) {
        event.preventDefault();

        const accessToken = localStorage.getItem('accessToken');

        // If no access token exists, redirect to login page
        if (!accessToken) {
            alert('로그인이 필요합니다.');
            window.location.href = '/loginForm';
            return;
        }

        // Perform the fetch request to the target URL
        fetch('http://localhost:8080' + targetUrl, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + accessToken,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(html => {
                // Replace only the content area with the response
                document.querySelector('.content').innerHTML = html;
            })
            .catch(error => {
                console.error('Error:', error);
                alert('페이지 로드에 실패했습니다. 다시 시도해주세요.');
            });
    }

    // Attach click listeners to nav links for dynamic navigation
    navLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            const targetUrl = link.getAttribute('href');
            handleNavigation(event, targetUrl);
        });
    });

    // Function to load login form content dynamically
    function loadLoginForm() {
        fetch('/loginForm', { method: 'GET' })
            .then(response => {
                if (!response.ok) throw new Error('Failed to load login form');
                return response.text();
            })
            .then(html => {
                // Check if loginForm CSS is already loaded
                let cssLink = document.querySelector('link[href="/css/user/loginForm.css"]');
                if (cssLink) {
                    // Temporarily remove it to force re-application
                    cssLink.remove();
                }

                // Re-add the CSS link to ensure it's applied
                cssLink = document.createElement('link');
                cssLink.rel = 'stylesheet';
                cssLink.href = '/css/user/loginForm.css';
                cssLink.onload = () => {
                    // Ensure CSS is applied after loading
                    document.querySelector('.content').style.display = 'block';
                };
                document.head.appendChild(cssLink);

                // Load the JS file for login form functionality
                const script = document.createElement('script');
                script.src = '/js/user/loginForm.js'; // 경로를 맞게 조정하세요
                document.body.appendChild(script);



                // Load only the login form content into the content area
                document.querySelector('.content').innerHTML = html;




            })
            .catch(error => {
                console.error('Error loading login form:', error);
                alert('로그인 페이지 로드에 실패했습니다. 다시 시도해주세요.');
            });
    }

    // Attach login button functionality
    if (loginButton) {
        loginButton.addEventListener('click', function (event) {
            event.preventDefault();
            loadLoginForm();
        });
    }

    // Attach logout button functionality
    if (logoutButton) {
        logoutButton.addEventListener('click', function (event) {
            event.preventDefault();
            // Clear access token and redirect to home
            localStorage.removeItem('accessToken');
            window.location.href = '/logout';
        });
    }
});
