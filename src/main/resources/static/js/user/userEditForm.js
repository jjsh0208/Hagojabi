(function () {

    fetch("/api/user/userProfile",{
        method : "GET",
        headers : {
            'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
        }
    }).then(response =>{
        if (!response) throw new Error("회원 정보를 가져오는데 실패했습니다.");

        return response.json();
    })
        .then(data =>{
            const title = document.querySelector('.title');
            title.innerText = '';
            title.innerText = '회원정보변경'
            
            const subtitle = document.querySelector('.subtitle');
            subtitle.innerText = '';
            subtitle.innerText = '정보를 변경해주세요';

            const submit_button = document.querySelector('.submit-button');
            submit_button.innerText = '';
            submit_button.innerText = '회원정보변경';
        })





})();