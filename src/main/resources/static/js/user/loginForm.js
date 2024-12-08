(function () {
function handleLogin(event) {
    event.preventDefault();

    const loginForm = document.getElementById("loginForm");
    const email = loginForm.querySelector('input[name="email"]').value;
    const password = loginForm.querySelector('input[name="password"]').value;

    const loginData = {
        email: email,
        password: password
    };

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            if (!response.ok) { // 로그인 실패 처리
                return response.text().then(text => {
                    throw new Error(text || '로그인 실패');
                });
            }
            const accessToken = response.headers.get('Authorization')?.split(' ')[1];
            if (accessToken) {
                localStorage.setItem('accessToken', accessToken);
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
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch home content');
                alert("test");
            }
            return response.text();
        })
        .then(html => {
            history.pushState({ url: '/' }, '', '/');
            document.open();
            document.write(html);
            document.close();
        })
        .catch(error => {
            console.error('로그인 실패:', error);
            alert(error.message); // 오류 메시지를 사용자에게 표시
        });
}

// 회원가입 링크 클릭 이벤트 설정
document.getElementById("signUpLink").addEventListener("click", function () {

    fetch('/user/joinForm', { method: "GET" })
        .then(response => {
            if (!response.ok) throw new Error('Failed to load sign-up page');
            return response.text();
        })
        .then(html => {

            loadAssetsForUrl('/user/joinForm'); // Load assets based on URL
            // 콘텐츠 업데이트
            document.querySelector('.content').innerHTML = html;
        })
        .catch(error => console.error('Error loading sign-up page:', error));
});


})();