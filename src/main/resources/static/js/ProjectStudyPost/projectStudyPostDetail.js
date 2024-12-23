(async function () {
    const postId = document.querySelector('.ProjectStudyPost-content').dataset.postId;

    try {
        const response = await fetch("/api/projectStudyPost/" + postId, {
            method: "GET",
            headers: {
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
            },
        })

        if (!response.ok) {
            const errorData = await response.json();
            throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
        }
        const data = await response.json();
        postDetailHandleSuccess(data);

    } catch (error){
        console.error(error);
        postDetailHandleError(error);
    }

    function postDetailHandleSuccess(data) {
        // 게시글 데이터 채우기
        document.querySelector('.card-title').textContent = data.title;
        document.querySelector('.card-body').innerHTML = data.description;
        document.querySelector('.recruitment-type').textContent = data.recruitmentType;
        document.querySelector('.recruitment-people').textContent = data.peopleCount;
        document.querySelector('.recruitment-deadline').textContent = data.recruitmentDeadline;
        document.querySelector('.project-mode').textContent = data.projectMode;
        document.querySelector('.project-duration').textContent = data.duration;

        const emailLink = document.querySelector('.user_email');
        emailLink.href = `mailto:${data.contactEmail}`; // href에 mailto 설정
        emailLink.textContent = data.contactEmail; // 이메일 텍스트 설정

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

            btn_edit.addEventListener('click', () => {
                loadPostEdit(postId, data);
            })

            btn_delete.addEventListener('click', () => {
                postDelete(postId);
            })


        } else {
            actionButtons.style.display = "none"; // 작성자가 아닐 경우 버튼 숨김
        }
    }

    async function loadPostEdit(id, data) {
        const targetUrl = '/projectStudyPost/edit/' + id;

        try {
            const response = await fetch(targetUrl, {
                method: "GET",
                headers: {
                    'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
                }
            });


             // HTML 반환
            document.querySelector('.content').innerHTML = await response.text(); // 콘텐츠 업데이트
            loadAssetsForUrl(targetUrl);
            history.pushState({url: targetUrl}, '', targetUrl); // 현재 URL 상태에 저장

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

            const interval2 = setInterval(() => {
                if (window.projectStudyPostEditModul && window.projectStudyPostEditModul.handleSubmit) {
                    clearInterval(interval2); // 로드 완료, 반복 중지
                    document.getElementById("ProjectStudyPostForm").addEventListener("submit", (event) => {
                        window.projectStudyPostEditModul.handleSubmit(event, postId);
                    });
                } else {
                    console.log('edit.js 로드 대기 중...');
                }
            }, 100); // 100ms 간격으로 확인

        } catch(error){
            console.error(error);
            postDetailHandleError(error);
        }
    }


    async function postDelete(postId) {
        const targetUrl = '/api/projectStudyPost/delete/' + postId;

        try {
            const response = await fetch(targetUrl, {
                method: "DELETE",
                headers: {
                    'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
                }
            })

            if (!response.ok) {
                const errorData = await response.json();
                throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
            }
            alert("게시글이 성공적으로 삭제되었습니다.");
            history.back();
        } catch(error){
            console.error(error);
            postDetailHandleError(error);
        }
    }

    function postDetailHandleError(error) {
        if (error.status) {
            switch (error.status) {
                case 400:
                    alert(`잘못된 요청입니다: ${error.message}`);
                    break;
                case 404:
                    alert(`해당 유저를 찾을 수 없습니다 : ${error.message}`);
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

})();