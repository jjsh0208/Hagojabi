// 메인 창에서 메시지 리스너 추가
window.addEventListener('message', function(event) {
    // 메시지를 보낸 출처가 현재 창의 출처와 다르면 무시
    if (event.origin !== window.location.origin) {
        return; // 다른 출처의 메시지는 무시
    }

    // 로그인 성공 메시지가 수신되면
    if (event.data === 'loginSuccess') {
        // 로그인 성공 시 메인 페이지로 리다이렉트
        redirectToHome();
    }
});

// 팝업 열기 함수
function openPopup(url) {
    const width = 600;  // 팝업 너비
    const height = 600; // 팝업 높이
    const left = (screen.width - width) / 2;  // 중앙 정렬
    const top = (screen.height - height) / 2; // 중앙 정렬

    // 새로운 팝업 창을 열기
    window.open(url, '로그인', `width=${width}, height=${height}, top=${top}, left=${left}`);
}

// 메인 페이지로 리다이렉트하는 함수
async function redirectToHome() {

    try{
        const accessToken = localStorage.getItem('accessToken'); // 로컬 스토리지에서 액세스 토큰 가져오기

        const response = await  fetch('/', {
            method: "GET",
            headers: {
                'Authorization': 'Bearer ' + accessToken // 액세스 토큰 추가
            }
        });

        if (!response.ok) {
            throw new Error('Network response was not ok'); // 응답 실패 시 오류 발생
        }
        const html = await response.text(); // HTML 텍스트 반환

        history.pushState({ url: '/' }, '', '/');
        document.open();
        document.write(html);
        document.close();
    }catch (error){
        oauth2LoginHandleError(error);
    }
}

function oauth2LoginHandleError(error) {
    if (error.status) {
        switch (error.status) {
            case 400:
                alert(`잘못된 요청입니다: ${error.message}`);
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