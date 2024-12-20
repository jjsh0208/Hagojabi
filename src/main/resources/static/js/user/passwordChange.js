(function () {
  async function passwordChangePopup(event){
        event.preventDefault(); // 기본 동작 방지 (새로고침 방지)

        const newPassword = document.getElementById('password');
        const confirmNewPassword = document.getElementById('confirmPassword');

        if ((newPassword.value !== confirmNewPassword.value && showError(confirmPassword, "비밀번호가 일치하지 않습니다."))){
            return;
        }

        // JSON 생성
        const newPasswordData = {
            password: newPassword.value
        };

        try{
            const response = await fetch('/api/user/userProfile/passwordChange',{
                method : "POST",
                headers : {
                    'Content-Type': 'application/json',  // JSON 데이터 형식 지정
                    'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
                    },
                body: JSON.stringify(newPasswordData)
            });

            if(!response.ok){
                const errorData = await response.json();
                throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
            }

            const data = await response.json();
            let message = data.message;
            alert(message);

            // 메인 창에 로그인 성공 메시지 전송
            window.opener.postMessage('passwordChangeSuccess', window.location.origin);

            window.close();
        } catch(error){
            console.error(error);
            passwordEditHandleError(error);
        }
    }

    function passwordEditHandleError(error) {
        if (error.status) {
            switch (error.status) {
                case 400:
                    alert(`잘못된 요청입니다: ${error.message}`);
                    break;
                case 404:
                    alert(`해당 유저를 찾을 수 없습니다 : ${error.message}`);
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
    document.getElementById('registration-form').addEventListener('submit',passwordChangePopup);
})();