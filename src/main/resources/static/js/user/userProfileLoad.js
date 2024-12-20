(async function () {
    try{
        const response = await fetch('/api/user/profile',{
            method : "GET",
            headers : {
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
            }
        });

        if(!response.ok){
            const errorData = await response.json();
            throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
        }

        const data = await response.json();
        updateUserInfo(data); // UI업데이트
    } catch(error){
        console.error('회원 정보 로드 오류 : ' + error);
        profileLoadHandleError(error);
    }

    // 회원 정보 UI 업데이트 함수
    function updateUserInfo(data) {
        const userFirstName = document.querySelector('.mypage-user-first-name');
        const userName = document.querySelector('.mypage-user-name');
        const userEmail = document.querySelector('.mypage-user-email');
        const editProfileBtn = document.querySelector('.edit-profile');
        const nameInput = document.querySelector('#name');
        const emailInput = document.querySelector('#email');
        const passwordPopupBtn = document.querySelector('.password-popup-btn');

        const username = data.username;

        userFirstName.innerText = username.charAt(0); // 첫 글자
        userName.innerHTML = username;
        userEmail.innerText = data.email;

        nameInput.value = username;
        emailInput.value = data.email;

        // 회원명 수정 버튼 이벤트 추가
        editProfileBtn.addEventListener('click', loadUserProfile);

        // 비밀번호 수정 버튼 표시/숨기기
        if (data.provider) {
            passwordPopupBtn.style.display = 'none'; // Oauth2 회원인 경우 숨기기
        } else {
            passwordPopupBtn.style.display = 'block'; // 일반 회원인 경우 표시
            passwordPopupBtn.addEventListener('click', passwordChangePopup);
        }
    }
    // 회원 정보 수정 함수
    async function loadUserProfile() {
        try {
            const nameInput = document.querySelector('#name');
            const emailInput = document.querySelector('#email');

            const userUpdateData = {
                email: emailInput.value,
                username: nameInput.value
            };

            const response = await fetch('/api/user/userProfile/edit', {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userUpdateData)
            });

            if (!response.ok) {
                throw new Error('회원 정보 수정에 실패했습니다: ' + response.statusText);
            }

            const updatedData = await response.json();
            document.querySelector('.mypage-user-name').innerHTML = updatedData.username;
            alert("사용자명 변경 성공");
        } catch (error) {
            console.error(error);
            profileLoadHandleError(error);
        }
    }

    // 비밀번호 변경 팝업 열기
    function passwordChangePopup() {
        const width = 500;
        const height = 500;
        const left = (screen.width - width) / 2;
        const top = (screen.height - height) / 2;

        window.open("/user/passwordChange", '비밀번호 변경', `width=${width}, height=${height}, top=${top}, left=${left}`);
    }


    // 비밀번호 변경 팝업에서 메시지 수신
    window.addEventListener('message', async (event) => {
        if (event.origin !== window.location.origin) {
            return; // 다른 출처의 메시지는 무시
        }

        if (event.data === 'passwordChangeSuccess') {
            try {
                const response = await fetch('/logout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    localStorage.removeItem('accessToken'); // 로컬 스토리지에서 액세스 토큰 제거
                    window.location.href = '/'; // 메인 페이지로 리디렉션
                } else {
                    alert('로그아웃에 실패했습니다. 다시 시도해주세요.');
                }
            } catch (error) {
                console.error(error);
                profileLoadHandleError(error);
            }
        }
    });

    function profileLoadHandleError(error) {
        if (error.status) {
            switch (error.status) {
                case 400:
                    alert(`잘못된 요청입니다: ${error.message}`);
                    break;
                case 404:
                    alert(`회원 정보를 찾을을 수 없습니다 : ${error.message}`);
                    break;
                case 409 :
                    alert(`요청이 현재 상태와 충돌합니다. : ${error.message}`);
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

})();