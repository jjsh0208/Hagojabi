function handleRegistration(event) {
    event.preventDefault();

    // form에서 입력된 데이터를 가져온다.
    const email = document.getElementById('email');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    // 유효성 검사
    if (!validateInput(email, "이메일을 입력해 주세요.") ||
        !validateInput(username, "사용자 이름을 입력해 주세요.") ||
        !validateInput(password, "비밀번호를 입력해 주세요.") ||
        (password.value !== confirmPassword.value && showError(confirmPassword, "비밀번호가 일치하지 않습니다."))
    ) {
        return;
    }

    // JSON 생성
    const registrationData = {
        email: email.value,
        username: username.value,
        password: password.value
    };

    // 회원가입 요청
    fetch('/api/user/join', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(registrationData)
    })
        .then(handleResponse)
        .then(handleSuccess)
        .catch(handleError);
}

// 유효성 검사 함수
function validateInput(inputField, errorMessage) {
    if (!inputField.value) {
        alert(errorMessage);
        inputField.focus();
        return false;
    }
    return true;
}

// 오류 메시지를 표시하는 함수
function showError(inputField, message) {
    alert(message);
    inputField.focus();
    return false;
}

// 서버 응답 처리
function handleResponse(response) {
    if (response.status === 409) { // 중복 이메일
        return response.text().then(text => {
            alert(text); // 경고창에 오류 메시지 표시
            document.getElementById('email').focus();
            throw new Error('Duplicate email');
        });
    }
    if (!response.ok) {
        throw new Error('Network response was not ok: ' + response.statusText);
    }
    return response.text(); // 성공적인 경우 텍스트 반환
}

// 회원가입 성공 처리
function handleSuccess(message) {
    console.log('회원가입 성공:', message);

    fetch('/user/loginForm')
        .then(response => {
            if (!response.ok) throw new Error('로그인 폼 로드에 실패했습니다');
            return response.text(); // HTML 텍스트 반환
        })
        .then(html =>{
            alert("회원가입에 성공했습니다.");
            loadAssetsForUrl('/user/loginForm'); // Load assets based on URL
            document.querySelector('.content').innerHTML = html;
        })
        .catch(error => {
            alert('메인화면 로드에 실패했습니다.');
        });
}

// 오류 처리
function handleError(error) {
    alert("회원가입에 실패했습니다. 다시 시도해주세요.");
    console.error('회원가입 실패:', error);
}
