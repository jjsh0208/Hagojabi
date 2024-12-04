(function () {

    fetch('/api/user/verify',{
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

            const username = data.username;

            user_first_name.innerText = username.charAt(0);

            user_name.innerHTML = username;

            user_email.innerText = data.email;
        }).catch(error =>{
            alert("회원 정보를 가져오는데 실패 했습니다.");
            console.error(error);
    })
})();