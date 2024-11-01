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
function redirectToHome() {
    const accessToken = localStorage.getItem('accessToken'); // 로컬 스토리지에서 액세스 토큰 가져오기

    // 필요한 URL로 fetch 요청 (예: 메인 페이지)
    fetch('/', {
        method: "GET",
        headers: {
            'Authorization': 'Bearer ' + accessToken // 액세스 토큰 추가
        }
    }) // 메인 뷰를 요청하는 URL
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok'); // 응답 실패 시 오류 발생
            }
            return response.text(); // HTML 텍스트 반환
        })
        .then(html => {
            // 'content' div 업데이트
            document.open(); // 문서 열기
            document.write(html); // HTML 쓰기
            document.close(); // 문서 닫기
        })
        .catch(error => {
            console.error('Fetch error:', error); // 오류 로그 출력
        });
}
