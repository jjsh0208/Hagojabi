function removeUnnecessaryAssets() {
    // 특정 CSS 파일 제외하고 제거
    document.querySelectorAll("link[rel='stylesheet']").forEach(link => {
        if (!link.href.endsWith('/css/index.css')) {
            link.remove();
        }
    });

    // 특정 JS 파일 제외하고 제거
    document.querySelectorAll("script").forEach(script => {
        if (!script.src.endsWith('/js/navigation.js') && !script.src.endsWith('/js/assetsManager.js')) {
            script.remove();
        }
    });
}

function loadAssetsForUrl(targetUrl) {
    // 기존 자산 제거
    removeUnnecessaryAssets();

    // URL과 자산 매핑
    const assetMapping = {
        '^/user/joinForm$': {
            css: ['/css/user/joinForm.css'],
            js: ['/js/user/registrationForm.js']
        },
        '^/user/loginForm$': {
            css: ['/css/user/loginForm.css'],
            js: ['/js/user/loginForm.js', '/js/user/oauth2Login.js']
        },
        '^/user/myPage$' : {
            css : ['/css/user/myPage.css'],
            js : ['/js/user/myPage.js']
        },
        '^/projectStudyPost/new$': {
            css: [
                'https://cdn.quilljs.com/1.3.6/quill.snow.css',
                '/css/ProjectStudyPost/ProjectStudyPostForm.css'
            ],
            js: [
                'https://cdn.quilljs.com/1.3.6/quill.min.js'
            ]
        },
        '^/projectStudyPost$': {
            css: ['/css/ProjectStudyPost/projectStudyPost.css'],
            js: ['/js/ProjectStudyPost/projectStudyPostPaging.js']
        },
        '^/projectStudyPost/\\d+$': {
            css: ['/css/ProjectStudyPost/ProjectStudyPostDetail.css'],
            js: ['/js/ProjectStudyPost/projectStudyPostDetail.js']
        },
        '^/projectStudyPost/edit/\\d+$': {
            css: [
                'https://cdn.quilljs.com/1.3.6/quill.snow.css',
                '/css/ProjectStudyPost/ProjectStudyPostForm.css'
            ],
            js: [
                'https://cdn.quilljs.com/1.3.6/quill.min.js'
            ]
        }

    };

    // URL에 매핑된 자산 추가
    let assetsAdded = false;
    for (const [urlPattern, assets] of Object.entries(assetMapping)) {
        if (new RegExp(urlPattern).test(targetUrl)) {
            assetsAdded = true;

            // CSS 파일 추가
            assets.css?.forEach(cssFile => appendAsset('link', cssFile, 'stylesheet'));

            // JS 파일 추가
            if (assets.js && Array.isArray(assets.js)) {
                assets.js.forEach(jsFile => {
                    if (jsFile.includes('quill.min.js')) {
                        loadQuillAndDependencies(targetUrl);
                    } else {
                        appendAsset('script', jsFile);
                    }
                });
            }
            break;
        }
    }

    if (!assetsAdded) {
        console.warn(`No assets mapped for the URL: ${targetUrl}`);
    }
}

function appendAsset(type, src, rel = null) {
    const element = document.createElement(type);
    if (type === 'link') {
        element.rel = rel;
        element.href = src;
    } else if (type === 'script') {
        element.src = src;
    } else if (type === 'module'){
        element.type = 'module';
        element.src = src;
    }
    document.head.appendChild(element);
}

function loadQuillAndDependencies(targetUrl) {
    const quillScript = document.createElement('script');
    quillScript.src = 'https://cdn.quilljs.com/1.3.6/quill.min.js';
    quillScript.onload = () => {
        appendAsset('script', '/js/ProjectStudyPost/selectBox.js');

        if (/^\/projectStudyPost\/edit\/\d+$/.test(targetUrl)) {
            appendAsset('script', '/js/ProjectStudyPost/ProjectStudyPostEdit.js');
        } else if (targetUrl === '/projectStudyPost/new') {
            appendAsset('script', '/js/ProjectStudyPost/ProjectStudyPostForm.js');
        }
    };
    document.body.appendChild(quillScript);
}
