async function handleRegistration(event) {
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

    if (!validatePassword(password.value)){
        alert("비밀번호는 8~20자 사이로, 영문자, 숫자, 특수문자를 포함해야 합니다.");
        password.focus();
        return;
    }

    if(!await sendEmailVerificationCode()){
        alert("입력한 인증 코드가 일치하지 않습니다. 다시 확인해주세요.");
        return;
    }

    // JSON 생성
    const registrationData = {
        email: email.value,
        username: username.value,
        password: password.value
    };

    try{
          const response = await  fetch('/api/user/join', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registrationData)
        });

        if(!response.ok){
            const errorData = await response.json();
            throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
        }

        const message = await response.text(); // 성공적인 경우 텍스트 반환
        await handleSuccess(message); // 성공 처리

    }catch (error){
        registrationHandleError(error); // 오류 처리
    }
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

function validatePassword(password){
    // 영문자,숫자,특수문자 를 포함한 8~20 자
    const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[\W_])[A-Za-z\d\W_]{8,20}$/;
    return regex.test(password);
}


// 오류 메시지를 표시하는 함수
function showError(inputField, message) {
    alert(message.message);
    inputField.focus();
    return false;
}

// 회원가입 성공 처리
async function handleSuccess(message) {
    console.log('회원가입 성공:', message);

    try{
        const response = await fetch('/user/loginForm')

        if(!response.ok){
            const errorData = await response.json();
            throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
        }

        const html = await response.text();
        alert('회원가입에 성공했습니다.');
        loadAssetsForUrl('/user/loginForm');
        document.querySelector('.content').innerHTML = html;
    } catch(error){
        registrationHandleError(error);
    }
}
async function sendCode(){

    const email = document.getElementById('email');
    alert("인증 메일이 발송되었습니다.");

    if (!validateInput(email, "이메일을 입력해 주세요.")) return;

    const data = {
        email : email.value
    };

    const response = await  fetch('/emailSend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if(!response.ok){
        const errorData = await response.json();
        throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
    }
}

async function sendEmailVerificationCode() {

    const verificationCode = document.getElementById('EmailVerificationCode');

    if (!validateInput(verificationCode, "이메일 인증 코드를 입력해 주세요.")) return;

    const data = {
        code : verificationCode.value
    };

    const response = await  fetch('/emailVerify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if(!response.ok){
        const errorData = await response.json();
        throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
    }

    const result = await response.json(); // 성공적인 경우 텍스트 반환

    return result.result === true;
}

function registrationHandleError(error) {
    if (error.status) {
        switch (error.status) {
            case 400:
                alert(`잘못된 요청입니다: ${error.message}`);
                break;
            case 404:
                alert(error.message);
                break;
            case 409 :
                alert(`중복된 이메일입니다 : ${error.message}`);
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

// 이벤트 리스너 추가
document.getElementById('registration-form').addEventListener('submit', handleRegistration);
document.getElementById('send-code-button').addEventListener('click',sendCode);