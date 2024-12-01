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
    // 기존 자산 제거
    removeUnnecessaryAssets();

    // URL과 자산 매핑
    const assetMapping = {
        '^/joinForm$': {
            css: ['/css/user/joinForm.css'],
            js: ['/js/user/registrationForm.js']
        },
        '^/loginForm$': {
            css: ['/css/user/loginForm.css'],
            js: ['/js/user/loginForm.js', '/js/user/oauth2Login.js']
        },
        '^/projectStudyPost/new$': {
            css: [
                'https://cdn.quilljs.com/1.3.6/quill.snow.css',
                '/css/ProjectStudyPost/ProjectStudyPostForm.css'
            ],
            js: [
                'https://cdn.quilljs.com/1.3.6/quill.min.js'
            ] // Quill만 먼저 추가
        },
        '^/projectStudyPost$': {
            css: ['/css/ProjectStudyPost/projectStudyPost.css'],
            js: ['/js/ProjectStudyPost/projectStudyPostPaging.js']
        },
        '^/projectStudyPost/\\d+$': {
            css: ['/css/ProjectStudyPost/ProjectStudyPostDetail.css'],
            js: ['/js/ProjectStudyPost/projectStudyPostDetail.js']
        },
        '^/projectStudyPost/edit/\\d+$': { // /edit/{id}를 new와 동일하게 매핑
            css: [
                'https://cdn.quilljs.com/1.3.6/quill.snow.css',
                '/css/ProjectStudyPost/ProjectStudyPostForm.css'
            ],
            js: [
                'https://cdn.quilljs.com/1.3.6/quill.min.js',

            ]
        }
    };

    // 정확한 URL 매칭에 따라 자산 추가
    let assetsAdded = false;
    for (const [urlPattern, assets] of Object.entries(assetMapping)) {
        const regex = new RegExp(urlPattern);
        if (regex.test(targetUrl)) {
            assetsAdded = true;

            // CSS 파일 추가
            if (assets.css && Array.isArray(assets.css)) {
                assets.css.forEach(cssFile => {
                    const cssLink = document.createElement('link');
                    cssLink.rel = 'stylesheet';
                    cssLink.href = cssFile;
                    document.head.appendChild(cssLink);
                });
            }

            // JS 파일 추가
            if (assets.js && Array.isArray(assets.js)) {
                if (
                    targetUrl === '/projectStudyPost/new' ||
                    new RegExp('^/projectStudyPost/edit/\\d+$').test(targetUrl)
                ) {
                    // /ProjectStudyPost/new와 /edit/{id} 전용 로직
                    const quillScript = document.createElement('script');
                    quillScript.src = assets.js[0];
                    quillScript.onload = () => {
                        // Quill이 로드된 후 필요한 스크립트 추가
                        const selectBoxScript = document.createElement('script');
                        selectBoxScript.src = '/js/ProjectStudyPost/selectBox.js';
                        document.body.appendChild(selectBoxScript);

                        const projectScript = document.createElement('script');
                        projectScript.src = '/js/ProjectStudyPost/ProjectStudyPostForm.js';
                        document.body.appendChild(projectScript);
                    };
                    document.body.appendChild(quillScript);
                } else {
                    // 일반적인 JS 파일 추가
                    assets.js.forEach(jsFile => {
                        if (!document.querySelector(`script[src='${jsFile}']`)) {
                            const script = document.createElement('script');
                            script.src = jsFile;
                            document.body.appendChild(script);
                        }
                    });
                }
            }
            break; // 일치하는 URL 패턴을 찾았으면 루프 종료
        }
    }

    // URL이 매핑되지 않은 경우의 처리를 추가할 수 있음
    if (!assetsAdded) {
        console.warn(`No assets mapped for the URL: ${targetUrl}`);
    }
}
