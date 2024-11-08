// Quill 에디터 초기화
const editor = new Quill("#editor", {
    theme: "snow",
});

// 폼 제출 처리
document.getElementById("projectForm").addEventListener("submit", function (event) {
    event.preventDefault(); // 폼 제출 기본 동작 방지

    // 에디터에서 텍스트 가져오기
    const title = document.getElementById("title").value;
    const description = editor.root.innerHTML; // 에디터의 내용을 HTML로 가져옴

    const contentData = {
        title: title,
        description: description
    };

    fetch('/projects/ProjectCreate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
        },
        body: JSON.stringify(contentData)
    })
        .then(handleResponse)
        .then(handleSuccess)
        .catch(handleError); // 에러 처리
});

// 서버 응답 처리
function handleResponse(response) {
    if (!response.ok) {
        return response.text().then(text => {
            throw new Error(text || '게시글 작성 실패');
        });
    }
    return response.text(); // 성공시 서버의 응답 메시지 반환
}

// 성공 처리
function handleSuccess(message) {
    console.log('게시글 작성 성공', message);
    alert("게시글 작성이 성공적으로 완료되었습니다.");
}

// 에러 처리
function handleError(error) {
    alert("게시글 작성에 실패했습니다. 다시 시도해주세요.");
    console.log('게시글 작성 실패', error);
}
