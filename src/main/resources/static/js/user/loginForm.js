(function () {
    async function handleLogin(event) {
        event.preventDefault(); // 기본 동작 방지 (새로고침 방지)

        const loginForm = document.getElementById("loginForm");
        const email = loginForm.querySelector('input[name="email"]').value;
        const password = loginForm.querySelector('input[name="password"]').value;

        const loginData = { email, password };

        try {
            const response = await fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
            }

            const accessToken = response.headers.get('Authorization')?.split(' ')[1];
            if (!accessToken) throw new Error("No Access Token Received");

            localStorage.setItem('accessToken', accessToken);

            const homeResponse = await fetch('/', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + accessToken
                }
            });

            if (!homeResponse.ok) {
                throw new Error('Failed to fetch home content');
            }

            const html = await homeResponse.text();
            history.pushState({ url: '/' }, '', '/');
            document.open();
            document.write(html);
            document.close();
        } catch (error) {
            LoginHandleError(error);
        }
    }

    async function loadSignUpPage() {
        try {
            const response = await fetch('/user/joinForm', { method: "GET" });

            if (!response.ok) throw new Error('Failed to load sign-up page');

            const html = await response.text();
            loadAssetsForUrl('/user/joinForm'); // Load assets based on URL
            document.querySelector('.content').innerHTML = html;
        } catch (error) {
            console.error('Error loading sign-up page:', error.message);
        }
    }

    function LoginHandleError(error) {
        if (error.status) {
            switch (error.status) {
                case 400:
                    alert(`잘못된 요청입니다: ${error.message}`);
                    break;
                case 401:
                    alert(`로그인 실패 : ${error.message}`);
                    break;
                case 500:
                    alert(`서버 오류입니다: ${error.message}`);
                    break;
                default:
                    alert(`알 수 없는 오류가 발생했습니다: ${error.message}`);
            }
        } else {
            alert(`네트워크 오류 또는 알 수 없는 오류: ${error.message || 'Unexpected error'}`);
        }
    }

    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('signUpLink').addEventListener('click', loadSignUpPage);
})();
