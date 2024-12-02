(function () {
    let editor;

    // Initialize Quill editor if it is not initialized
    if (!editor) {
        editor = new Quill("#editor", {
            theme: "snow",
        });
    } else {
        // If already initialized, reset the content
        editor.root.innerHTML = "";
    }

    window.projectStudyPostEditModul = {
        createTag :  function (container, label) {
            const tag = document.createElement('span');
            tag.className = 'tag'; // 'tag' 클래스 추가
            tag.textContent = label; // 현재 텍스트 설정

            const deleteBtn = document.createElement('div');
            deleteBtn.className = 'delete-btn';
            deleteBtn.textContent = 'x';

            let placeholder = container.querySelector('.placeholder');
            if (!placeholder) {  // placeholder가 없으면 새로 추가
                placeholder = document.createElement('span');
                placeholder.className = 'placeholder';
                placeholder.style.display = 'none';
                placeholder.textContent = '옵션을 선택하세요';
                container.appendChild(placeholder); // placeholder 추가
            }

            tag.appendChild(deleteBtn); // 삭제 버튼을 태그에 추가

            // 삭제 버튼 클릭 시 해당 태그를 제거
            deleteBtn.addEventListener('click', (e) => {
                e.stopPropagation(); // 이벤트 버블링 방지
                container.removeChild(tag); // 태그를 컨테이너에서 제거

                // 컨테이너에 태그가 하나도 없으면 placeholder를 다시 추가
                if (container.children.length === 1) {
                    container.appendChild(placeholder); // placeholder 추가
                }
            });

            container.appendChild(placeholder); // placeholder 추가
            container.appendChild(tag); // 태그 추가
        },


        populateDataFields :  function (data) {
            // Create tag for recruitmentType
            if (data.recruitmentType) {
                const recruitmentType = document.getElementById('selectBoxRecruitmentType');
                recruitmentType.innerHTML = ''; // 기존 내용 지우기
                this.createTag(recruitmentType, data.recruitmentType);
            }

            // Create tag for peopleCount
            if (data.peopleCount) {
                const peopleCount = document.getElementById('selectBoxPeople');
                peopleCount.innerHTML = ''; // 기존 내용 지우기
                this.createTag(peopleCount, data.peopleCount);
            }

            if (data.projectMode) {
                const projectMode = document.getElementById('selectBoxProjectMode');
                projectMode.innerHTML = ''; // 기존 내용 지우기
                this.createTag(projectMode, data.projectMode);
            }

            if (data.duration) {
                const duration = document.getElementById('selectBoxDuration');
                duration.innerHTML = ''; // 기존 내용 지우기
                this.createTag(duration, data.duration);
            }

            // Create tags for techStack (multiple items)
            if (data.techStack) {
                const techStack = document.getElementById('selectBoxTechStack');
                techStack.innerHTML = ''; // 기존 내용 지우기
                data.techStack.forEach(tech => {
                    this.createTag(techStack, tech); // Create a tag for each tech
                });
            }

            // Create tags for position (multiple items)
            if (data.position) {
                const position = document.getElementById('selectBoxPosition');
                position.innerHTML = ''; // 기존 내용 지우기
                data.position.forEach(pos => {
                    this.createTag(position, pos); // Create a tag for each position
                });
            }

            if (data.recruitmentDeadline) {
                const recruitmentDeadline = document.getElementById('recruitmentDeadline');
                recruitmentDeadline.value = data.recruitmentDeadline;
            }

            if (data.contactEmail) {
                const contactEmail = document.getElementById('contactEmail');
                contactEmail.value = data.contactEmail;
            }

            if (data.title) {
                const title = document.getElementById('title');
                title.value = data.title;
            }

            if (data.description) {
                alert(data.description);
                const interval = setInterval(() => {
                    const description = document.querySelector('.ql-editor');
                    if (description) {
                        description.innerHTML = data.description; // 데이터 삽입
                        clearInterval(interval); // 반복 중지
                    }
                }, 100); // 100ms 간격으로 요소 탐색
            }
        }
    }


    // Define the form submission method
    function handleSubmit(event) {
        event.preventDefault();

        // Get form data
        const title = document.getElementById("title").value;
        const description = editor.root.innerHTML; // Get content from the Quill editor
        const recruitmentDeadline = document.getElementById("recruitmentDeadline").value;
        const contactEmail = document.getElementById("contactEmail").value;

        // Collect selected tags
        const selectedTags = getSelectedTags();

        // Perform input validation
        if (!title || title.trim() === '') {
            alert("제목을 입력해주세요.");
            return;
        }

        if (!description || description.trim() === '') {
            alert("내용을 입력해주세요.");
            return;
        }

        if (!selectedTags.peopleCount || selectedTags.peopleCount.trim() === '') {
            alert("인원 수를 선택해주세요.");
            return;
        }

        if (!selectedTags.projectMode || selectedTags.projectMode.trim() === '') {
            alert("프로젝트 모드를 선택해주세요.");
            return;
        }

        if (!selectedTags.duration || selectedTags.duration.trim() === '') {
            alert("기간을 선택해주세요.");
            return;
        }

        if (!selectedTags.position || selectedTags.position.length === 0) {
            alert("직무를 선택해주세요.");
            return;
        }

        if (!recruitmentDeadline || recruitmentDeadline.trim() === '') {
            alert("모집 마감일을 선택해주세요.");
            return;
        }

        if (!selectedTags.techStack || selectedTags.techStack.length === 0) {
            alert("기술 스택을 선택해주세요.");
            return;
        }

        if (!selectedTags.recruitmentType || selectedTags.recruitmentType.length === 0) {
            alert("모집 유형을 선택해주세요.");
            return;
        }

        if (!contactEmail || contactEmail.trim() === '') {
            alert("연락처 이메일을 선택해주세요.");
            return;
        }

        // Prepare the data to send
        const contentData = {
            title: title,
            description: description,
            ...selectedTags // Include the selected tags in the request body
        };

        // Log contentData to the console for debugging
        console.log("Content data to send:", contentData);

        // Make a POST request to the server
        fetch('/api/projectStudyPost/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
            },
            body: JSON.stringify(contentData)
        })
            .then(handleResponse)
            .then(handleSuccess)
            .catch(handleError); // Error handling
    }

    // Attach the form submission event listener
    document.getElementById("ProjectStudyPostForm").addEventListener("submit", handleSubmit);

    // Handle server response
    function handleResponse(response) {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || '게시글 수정 실패');
            });
        }
        return response.json(); // Return the server's response message
    }

    // Success handler
    function handleSuccess(response) {
        console.log('게시글 수정 성공', response.message);

        const targetUrl = '/ProjectStudyPost/' + response.id;

        fetch(targetUrl, {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
            }
        })
            .then(response => {
                if (!response.ok) throw new Error('Failed to load the page content');
                return response.text(); // Return the HTML content
            })
            .then(html => {
                const contentElement = document.querySelector('.content');
                if (!contentElement) {
                    throw new Error("Content element not found in the DOM.");
                }

                contentElement.innerHTML = html;

                if (typeof loadAssetsForUrl === 'function') {
                    loadAssetsForUrl(targetUrl);
                }

                history.pushState({ url: targetUrl }, '', targetUrl);
                alert("게시글 작성이 성공적으로 수정되었습니다.");
            })
            .catch(error => {
                console.error('오류:', error);
                alert('페이지 로드에 실패했습니다. 다시 시도해주세요.');
            });
    }

    // Error handler
    function handleError(error) {
        alert("게시글 작성에 실패했습니다. 다시 시도해주세요.");
        console.log('게시글 작성 실패', error);
    }
})();
