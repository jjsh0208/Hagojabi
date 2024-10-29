// Handle form submission for email login
const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", function (event) {
    event.preventDefault(); // Prevent default form submission

    // Get input data
    const email = document.querySelector('input[name="email"]').value;
    const password = document.querySelector('input[name="password"]').value;

    // Create JSON data
    const loginData = {
        email : email,
        password : password
    };

    // Send fetch request for email login
    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            const accessToken = response.headers.get('Authorization')?.split(' ')[1];
            if (accessToken) {
                localStorage.setItem('accessToken', accessToken);
                // Fetch the /home content after successful login
                return fetch('/', {
                    method: 'GET',
                    headers: {
                        'Authorization': 'Bearer ' + accessToken
                    }
                });
            } else {
                throw new Error("No Access Token Received");
            }
        })
        .then(response => response.text())
        .then(html => {
            // Load the content into the content area
            document.open();
            document.write(html);
            document.close();

        })
        .catch(error => {
            console.error('로그인 실패:', error);
            alert('로그인에 실패했습니다. 다시 시도해주세요.');
        });
});