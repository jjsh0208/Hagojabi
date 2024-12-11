(function () {
    function passwordChangePopup(event){
        event.preventDefault(); // 기본 동작 방지 (새로고침 방지)

        const newPassword = document.getElementById('password');
        const confirmNewPassword = document.getElementById('confirmPassword');

        if ( (newPassword.value !== confirmNewPassword.value && showError(confirmPassword, "비밀번호가 일치하지 않습니다."))){
            return;
        }

        // JSON 생성
        const newPasswordData = {
            password: newPassword.value
        };

        fetch('/api/user/userProfile/passwordChange',{
            method : "POST",
            headers : {
                'Content-Type': 'application/json',  // JSON 데이터 형식 지정
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
            },
            body: JSON.stringify(newPasswordData)
        })
            .then(response =>{
                if(!response.ok) throw new Error("비밀번호 수정에 실패했습니다.");

                return response.text();
            })
            .then(message => {
                alert(message);

                // 메인 창에 로그인 성공 메시지 전송
                window.opener.postMessage('passwordChangeSuccess', window.location.origin);

                window.close();
            })
            .catch(error =>{
                console.error(error);
                alert(error);
            })
    }
    document.getElementById('registration-form').addEventListener('submit',passwordChangePopup);
})();