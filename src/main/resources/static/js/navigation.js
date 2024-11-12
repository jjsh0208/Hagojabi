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
                loadAssetsForUrl(targetUrl);
                history.pushState({url: targetUrl}, '', targetUrl); // 현재 URL 상태에 저장
            })
            .catch(error => {
                console.error('오류:', error); // 오류 로그 출력
                alert(error);
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

                loadAssetsForUrl('/loginForm'); // Load assets based on URL
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
                removeUnnecessaryAssets();
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
                        window.location.href = '/loginForm'; // 메인 페이지로 리디렉션
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


function removeUnnecessaryAssets() {
    // Remove existing links except for /css/index.css
    const existingLinks = document.querySelectorAll("link[rel='stylesheet']");
    if (existingLinks.length > 0) {
        existingLinks.forEach(link => {
            if (!link.href.endsWith('/css/index.css')) {
                link.remove();
            }
        });
    }
    // Remove existing scripts except for /js/navigation.js
    const existingScripts = document.querySelectorAll("script");
    if (existingScripts.length > 0) {
        existingScripts.forEach(script => {
            if (!script.src.endsWith('/js/navigation.js')) {
                script.remove();
            }
        });
    }

}

function loadAssetsForUrl(targetUrl) {
    // 기존의 자산 제거
    removeUnnecessaryAssets();

    const assetMapping = {
        '/joinForm': {
            css: ['/css/user/joinForm.css'],
            js: ['/js/user/registrationForm.js']
        },
        '/loginForm': {
            css: ['/css/user/loginForm.css'],
            js: ['/js/user/loginForm.js', '/js/user/oauth2Login.js']
        },
        '/ProjectStudyPost/new': {
            css: [
                'https://cdn.quilljs.com/1.3.6/quill.snow.css',
                '/css/ProjectStudyPost/ProjectStudyPostForm.css'
            ],
            js: [
                'https://cdn.quilljs.com/1.3.6/quill.min.js'
            ] // Quill만 먼저 추가
        },
        '/qqq':{
            css : ['/css/ProjectStudyPost/ProjectStudyPostDetail.css']
        }
    };

    const assets = assetMapping[targetUrl];

    // assets가 존재하는지 확인
    if (assets) {
        // CSS 파일이 배열로 존재하는지 확인 후 추가
        if (assets.css && Array.isArray(assets.css)) {
            assets.css.forEach(cssFile => {
                const cssLink = document.createElement('link');
                cssLink.rel = 'stylesheet';
                cssLink.href = cssFile;
                document.head.appendChild(cssLink);
            });
        }
        // JS 파일이 배열로 존재하는지 확인 후 추가
        if (assets.js && Array.isArray(assets.js)) {
            // Quill.js가 필요한 경우 먼저 Quill.js를 로드한 후 ProjectStudyPostForm.js 추가
            if (targetUrl === '/ProjectStudyPost/new') {
                const quillScript = document.createElement('script');
                quillScript.src = assets.js[0];
                quillScript.onload = () => {
                    // Quill이 로드된 후 ProjectStudyPostForm.js 추가

                    const projectScript1 = document.createElement('script');
                    projectScript1.src = '/js/ProjectStudyPost/ProjectStudyPostForm.js';
                    document.body.appendChild(projectScript1);

                    const projectScript2 = document.createElement('script');
                    projectScript2.src = '/js/ProjectStudyPost/selectBox.js';
                    document.body.appendChild(projectScript2);
                };
                document.body.appendChild(quillScript);
            } else {
                // 일반적인 JS 파일 추가
                assets.js.forEach(jsFile => {
                    const script = document.createElement('script');
                    script.src = jsFile;
                    document.body.appendChild(script);
                });
            }
        }
    }
}