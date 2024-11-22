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
            // Prevent reloading selectBox.js or any other required scripts
            if (!script.src.endsWith('/js/navigation.js')&& !script.src.endsWith('/js/assetsManager.js')) {
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
        'ProjectStudyPost' :{
        css: ['/css/ProjectStudyPost/projectStudyPost.css']
        },

        // 모든 숫자 ID를 처리하는 정규식 적용
        '/ProjectStudyPost/\\d+': {
            css: ['/css/ProjectStudyPost/ProjectStudyPostDetail.css'],
            js : ['/js/ProjectStudyPost/projectStudyDetail.js']
        }

    };

    // assets가 존재하는지 확인
    for (const [urlPattern, assets] of Object.entries(assetMapping)) {
        const regex = new RegExp(urlPattern);
        if (regex.test(targetUrl)) {
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
                        projectScript1.src = '/js/ProjectStudyPost/selectBox.js';
                        document.body.appendChild(projectScript1);

                        const projectScript2 = document.createElement('script');
                        projectScript2.src = '/js/ProjectStudyPost/ProjectStudyPostForm.js';
                        document.body.appendChild(projectScript2);
                    };
                    document.body.appendChild(quillScript);
                } else {
                    // 일반적인 JS 파일 추가
                    assets.js.forEach(jsFile => {
                        // Check if the script is already added
                        if (!document.querySelector(`script[src='${jsFile}']`)) {
                            const script = document.createElement('script');
                            script.src = jsFile;
                            document.body.appendChild(script);
                        }
                    });
                }
            }
        }
    }
}

