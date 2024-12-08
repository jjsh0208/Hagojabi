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


            const username = data.username;

            user_first_name.innerText = username.charAt(0);
            user_name.innerHTML = username;
            user_email.innerText = data.email;

            name_input.value = username;
            email_input.value = data.email;

            edit_profile_btn.addEventListener('click',()=>{
                 loadUserProfile();
            })

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
})();