// DOM이 완전히 로드된 후 실행될 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function () {
    // 네비게이션 링크, 로그인 버튼, 로그아웃 버튼, 헤더 링크 선택
    const navLinks = document.querySelectorAll('.nav-link');
    const loginButton = document.querySelector('.login-button');
    const logoutButton = document.querySelector('.logout-button');
    const headerLink = document.querySelector('.header-link');

    // 네비게이션 처리 함수
    function handleNavigation(event, targetUrl) {
        event.preventDefault(); // 기본 링크 클릭 동작 방지

        const accessToken = localStorage.getItem('accessToken'); // 로컬 스토리지에서 액세스 토큰 가져오기

        // 로그인 폼이 아닌 링크에 접근할 때 액세스 토큰이 없으면 로그인 경고
        if (!accessToken && targetUrl !== '/loginForm') {
            alert('로그인이 필요합니다.');
            window.location.href = '/loginForm'; // 로그인 폼으로 리디렉션
            return;
        }

        // 선택한 URL에 fetch 요청
        fetch('http://localhost:8080' + targetUrl, {
            method: 'GET',
            headers: {
                'Authorization': accessToken ? 'Bearer ' + accessToken : '', // 액세스 토큰 추가
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('네트워크 응답이 올바르지 않습니다'); // 응답 실패 시 오류 발생
                return response.text(); // HTML 텍스트 반환
            })
            .then(html => {
                document.querySelector('.content').innerHTML = html; // 콘텐츠 업데이트
                history.pushState({url: targetUrl}, '', targetUrl); // 현재 URL 상태에 저장
            })
            .catch(error => {
                console.error('오류:', error); // 오류 로그 출력
                alert('페이지 로드에 실패했습니다. 다시 시도해주세요.'); // 오류 경고
            });
    }

    // 로그인 폼 로드 함수
    function loadLoginForm() {
        fetch('/loginForm', { method: 'GET' }) // 로그인 폼 요청
            .then(response => {
                if (!response.ok) throw new Error('로그인 폼 로드에 실패했습니다'); // 응답 실패 시 오류 발생
                return response.text(); // HTML 텍스트 반환
            })
            .then(html => {
                // 기존 CSS 링크 제거
                let cssLink = document.querySelector('link[href="/css/user/loginForm.css"]');
                if (cssLink) cssLink.remove();

                // 새 CSS 링크 추가
                cssLink = document.createElement('link');
                cssLink.rel = 'stylesheet';
                cssLink.href = '/css/user/loginForm.css';
                document.head.appendChild(cssLink);

                // 로그인 폼 스크립트 추가
                const script = document.createElement('script');
                script.src = '/js/user/loginForm.js';
                document.body.appendChild(script);

                // OAuth2 로그인 스크립트 추가
                const script2 = document.createElement('script');
                script2.src = '/js/user/oauth2Login.js';
                document.body.appendChild(script2);

                document.querySelector('.content').innerHTML = html; // 콘텐츠 업데이트
            })
            .catch(error => {
                console.error('로그인 폼 로드 오류:', error); // 오류 로그 출력
                alert('로그인 페이지 로드에 실패했습니다. 다시 시도해주세요.'); // 오류 경고
            });
    }

    // 루트 콘텐츠 로드 함수
    function loadRootContent(event) {
        event.preventDefault(); // 기본 링크 클릭 동작 방지

        const accessToken = localStorage.getItem('accessToken'); // 로컬 스토리지에서 액세스 토큰 가져오기

        // 메인 페이지 요청
        fetch('http://localhost:8080/home', {
            method: 'GET',
            headers: {
                'Authorization': accessToken ? 'Bearer ' + accessToken : '', // 액세스 토큰 추가
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('루트 콘텐츠 로드에 실패했습니다'); // 응답 실패 시 오류 발생
                return response.text(); // HTML 텍스트 반환
            })
            .then(html => {
                document.querySelector('.content').innerHTML = html; // 콘텐츠 업데이트
                history.pushState({url: '/home'}, '', '/'); // 루트 URL 상태에 저장
            })
            .catch(error => {
                console.error('루트 콘텐츠 로드 오류:', error); // 오류 로그 출력
                alert('메인 페이지 로드에 실패했습니다. 다시 시도해주세요.'); // 오류 경고
            });
    }

    // 네비게이션 링크에 클릭 이벤트 리스너 추가
    navLinks.forEach(link => {
        link.addEventListener('click', event => handleNavigation(event, link.getAttribute('href')));
    });

    // 로그인 버튼 클릭 이벤트 리스너 추가
    if (loginButton) {
        loginButton.addEventListener('click', event => {
            event.preventDefault(); // 기본 버튼 클릭 동작 방지
            loadLoginForm(); // 로그인 폼 로드 호출
        });
    }

    // 로그아웃 버튼 클릭 이벤트 리스너 추가
    if (logoutButton) {
        logoutButton.addEventListener('click', event => {
            event.preventDefault(); // 기본 버튼 클릭 동작 방지

            // 로그아웃 요청
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
        });
    }

    // 헤더 링크 클릭 이벤트 리스너 추가
    if (headerLink) {
        headerLink.addEventListener('click', loadRootContent); // 루트 콘텐츠 로드 호출
    }

    // 뒤로가기 또는 앞으로 가기 버튼을 클릭 시 URL에 맞는 콘텐츠 로드
    window.addEventListener('popstate', function(event) {
        const url = event.state ? event.state.url : '/home';

        alert(url);
        fetch('http://localhost:8080' + url, {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : '', // 액세스 토큰 추가
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('페이지 로드에 실패했습니다'); // 응답 실패 시 오류 발생
                return response.text(); // HTML 텍스트 반환
            })
            .then(html => {
                document.querySelector('.content').innerHTML = html; // 콘텐츠 업데이트
            })
            .catch(error => {
                console.error('페이지 로드 오류:', error); // 오류 로그 출력
                alert('페이지 로드에 실패했습니다. 다시 시도해주세요.'); // 오류 경고
            });
    });
});
