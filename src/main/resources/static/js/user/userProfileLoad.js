(function () {
    fetch('/api/user/profile',{
        method : "GET",
        headers : {
            'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
        }
    })
        .then(response =>{
            if(!response) throw new Error("회원 정보를 가져오는데 실패했습니다.");

            return response.json();
        })
        .then(data => {
            const user_first_name = document.querySelector('.mypage-user-first-name');
            const user_name = document.querySelector('.mypage-user-name');
            const user_email = document.querySelector('.mypage-user-email');

            const edit_profile_btn = document.querySelector('.edit-profile');
            const name_input = document.querySelector('#name'); // 이름 input
            const email_input = document.querySelector('#email'); // 이메일 input

            const password_popup_btn = document.querySelector('.password-popup-btn');

            const username = data.username;

            user_first_name.innerText = username.charAt(0);
            user_name.innerHTML = username;
            user_email.innerText = data.email;

            name_input.value = username;
            email_input.value = data.email;

            // 회원명 수정 버튼
            edit_profile_btn.addEventListener('click',()=>{
                 loadUserProfile();
            })
            
            // password-popup 버튼 표시 여부 설정
            // Oauth2 회원이 아닌 일반 회원만 비밀번호 수정 가능
            if (data.provider) {
                // provider 값이 있으면 버튼 숨기기
                password_popup_btn.style.display = 'none';
            } else {
                // provider 값이 없으면 버튼 표시
                password_popup_btn.style.display = 'block';
                // password-popup 버튼 이벤트
                password_popup_btn.addEventListener('click', () => {
                    passwordChangePopup();
                });
            }


        }).catch(error =>{
            alert("회원 정보를 가져오는데 실패 했습니다.");
            console.error(error);
    })

    function loadUserProfile(){
        const username = document.querySelector('#name'); // 이름 input
        const email = document.querySelector('#email'); // 이메일 input

        const userUpdateData = {
            email  : email.value,
            username : username.value
        }
        const targetUrl = '/api/user/userProfile/edit';
        fetch(targetUrl,{
            method : "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userUpdateData)
        }).then(response =>{
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json(); // 성공적인 경우 텍스트 반환
        })
            .then(data => {
                const user_name = document.querySelector('.mypage-user-name');

                user_name.innerHTML = data.username;
                alert("사용자명 변경 성공");
            }).catch(error =>{
            console.error(error);
            alert(error);
        })
    }

    // 비밀번호 변경 팝업 생성
    function passwordChangePopup(){
        const width = 500;  // 팝업 너비
        const height = 500; // 팝업 높이
        const left = (screen.width - width) / 2;  // 중앙 정렬
        const top = (screen.height - height) / 2; // 중앙 정렬

        // 새로운 팝업 창을 열기
        window.open("/user/passwordChange" , '로그인', `width=${width}, height=${height}, top=${top}, left=${left}`);
    }
    
    
    // 비밀번호 변경 팝업에서 메시지를 받음
    window.addEventListener('message', function(event) {
        // 메시지를 보낸 출처가 현재 창의 출처와 다르면 무시
        if (event.origin !== window.location.origin) {
            return; // 다른 출처의 메시지는 무시
        }

        // 로그인 성공 메시지가 수신되면
        if (event.data === 'passwordChangeSuccess') {
            fetch('/logout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (response.ok) {
                        localStorage.removeItem('accessToken'); // 로컬 스토리지에서 액세스 토큰 제거
                        window.location.href = '/'; // 메인 페이지로 리디렉션
                    } else {
                        alert('로그아웃에 실패했습니다. 다시 시도해주세요.'); // 오류 경고
                    }
                })
                .catch(error => {
                    console.error('로그아웃 중 오류:', error); // 오류 로그 출력
                    alert('로그아웃 처리 중 문제가 발생했습니다.'); // 오류 경고
                });
        }
    });
})();