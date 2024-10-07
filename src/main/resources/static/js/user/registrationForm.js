document.addEventListener('DOMContentLoaded', function (){
    const form = document.getElementById('registration-form');
    form.addEventListener('submit', async function(event){
        event.preventDefault();

        // form에서 입력된 데이터를 가져온다.
        const email = document.getElementById('email');
        const username = document.getElementById('username');
        const password = document.getElementById('password');
        const confirmPassword  = document.getElementById('confirmPassword');
        
        // 비밀번호 확인 검증
        // 비밀번호 확인 검증
        if (password.value !== confirmPassword.value) {
            console.log(password.value);
            console.log(confirmPassword.value);
            alert("비밀번호가 일치하지 않습니다.");
            confirmPassword.focus();
            return;
        }

        // JSON 생성
        const registrationData = {
            email : email.value,
            username : username.value,
            password : password.value
        };

        fetch('http://localhost:8080/join',{
            method : 'POST',
            headers: {
                'Content-Type' : 'application/json'
            },
            body: JSON.stringify(registrationData)
        })
            .then(response => {
                console.log("test")
              if (response.status === 409){ // 기입한 이메일이 중복된 이메일인 경우

                  return response.text().then(text =>{
                      alert(text); // 경고창에 오류 메시지 표시
                      email.focus();
                      throw new Error('Duplicate email'); // Prevent further execution
                  })
              }
              if (!response.ok){
                  throw new Error('Network response was not ok ' + response.statusText);
              }
                return response.text(); // 성공적인 경우 텍스트 반환
            })
            .then(message => {
                // 성공적으로 회원가입이 이루어진 경우
                console.log('회원가입 성공:', message);
                // 성공 시 리다이렉트
                window.location.href = '/loginForm'; // 로그인 페이지로 리다이렉트
            })
            .catch(error => {
                // 회원가입 실패 시 처리
                console.error('회원가입 실패:', error);
                alert("회원가입에 실패했습니다. 다시 시도해주세요.");
            });
    })
})