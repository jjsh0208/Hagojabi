(function () {


    const postId = document.querySelector('.ProjectStudyPost-content').dataset.postId;

    fetch("/api/projectStudyPost/" + postId, {
        method: "GET",
        headers: {
            'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
        },
    })
        .then(handleResponse)
        .then(handleSuccess)
        .catch(handleError);

    function handleResponse(response) {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || '게시글 가져오기 실패');
            });
        }
        return response.json();
    }

    function handleSuccess(data) {
        // 게시글 데이터 채우기
        document.querySelector('.card-title').textContent = data.title;
        document.querySelector('.card-body').innerHTML = data.description;
        document.querySelector('.recruitment-type').textContent = data.recruitmentType;
        document.querySelector('.recruitment-people').textContent = data.peopleCount;
        document.querySelector('.recruitment-deadline').textContent = data.recruitmentDeadline;
        document.querySelector('.project-mode').textContent = data.projectMode;
        document.querySelector('.project-duration').textContent = data.duration;

        const techStackContainer = document.querySelector('.tech-stack');
        techStackContainer.innerHTML = ''; // Clear existing badges
        data.techStack.forEach(tech => {
            const badge = document.createElement('span');
            badge.className = 'badge'; // Add the badge class
            badge.textContent = tech; // Set the text to the current tech
            techStackContainer.appendChild(badge); // Append to the container
        });

        const positionList = document.querySelector('.position-list');
        positionList.innerHTML = ''; // Clear existing items
        data.position.forEach(position => {
            const listItem = document.createElement('li');
            listItem.textContent = position; // Set the text to the current position
            positionList.appendChild(listItem); // Add the <li> to the <ul>
        });

        // 수정/삭제 버튼 표시 여부 제어
        const actionButtons = document.querySelector('.action-buttons');
        if (data.isAuthor) {
            actionButtons.style.display = "block"; // 작성자일 경우 버튼 표시
            const btn_edit = document.querySelector('.btn-edit');
            const btn_delete = document.querySelector('.btn-delete');

            btn_edit.addEventListener('click',()=>{
                loadPostEdit(postId ,data);
            })

            btn_delete.addEventListener('click',()=>{
                postDelete(postId);
            })


        } else {
            actionButtons.style.display = "none"; // 작성자가 아닐 경우 버튼 숨김
        }
    }

    function handleError(error) {
        alert("게시글 가져오는 도중 오류 발생 " + error);
        console.error(error);
    }

    function  loadPostEdit(id ,data){
        const targetUrl = '/projectStudyPost/edit/' + id;

        fetch(targetUrl, {
            method: "GET",
            headers: {
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
            }
        })
            .then(response => {
                if (!response.ok) throw new Error("게시글을 가져오지 못했습니다.");
                return response.text(); // HTML 반환
            })
            .then(html => {
                document.querySelector('.content').innerHTML = html; // 콘텐츠 업데이트
                loadAssetsForUrl(targetUrl);
                history.pushState({ url: targetUrl }, '', targetUrl); // 현재 URL 상태에 저장

                // 가져온 데이터를 기반으로 필드 채우기
                // **동적으로 모듈 로드**
                const interval = setInterval(() => {
                    if (window.projectStudyPostEditModul && window.projectStudyPostEditModul.populateDataFields) {
                        clearInterval(interval); // 로드 완료, 반복 중지
                        window.projectStudyPostEditModul.populateDataFields(data); // populate fields
                    } else {
                        console.log('edit.js 로드 대기 중...');
                    }
                }, 100); // 100ms 간격으로 확인

            })
            .catch(error => {
                console.error("오류 발생:", error);
                alert("게시글 로드 중 오류 발생: " + error.message);
            });
    }
})();