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

    function getSelectedTags() {
        const selectedTags = {
            peopleCount: "",
            projectMode: "",
            duration: "",
            position: [],
            techStack: [],
            recruitmentType: "",
        };
        document.querySelectorAll('.select-box').forEach(selectBox => {
            const id = selectBox.id;
            const selectedItems = new Set();
            selectBox.querySelectorAll('.tag').forEach(tag => {
                // Exclude the 'delete-btn' from the tag's textContent
                const tagText = tag.textContent.trim(); // This includes both the tag text and the 'x' button
                const deleteBtnText = tag.querySelector('.delete-btn') ? tag.querySelector('.delete-btn').textContent.trim() : '';
                // Remove the 'delete-btn' part from the tag's textContent
                const tagTextWithoutDeleteBtn = tagText.replace(deleteBtnText, '').trim();
                // Add the cleaned-up tag text (excluding 'x' button) to the selected items
                selectedItems.add(tagTextWithoutDeleteBtn);
            });
            // Add the selected items to the corresponding field in selectedTags
            if (id === 'selectBoxPeople') selectedTags.peopleCount = [...selectedItems].join(',');
            if (id === 'selectBoxProjectMode') selectedTags.projectMode = [...selectedItems].join(',');
            if (id === 'selectBoxDuration') selectedTags.duration = [...selectedItems].join(',');
            if (id === 'selectBoxPosition') selectedTags.position = [...selectedItems];
            if (id === 'selectBoxTechStack') selectedTags.techStack = [...selectedItems];
            if (id === 'selectBoxRecruitmentType') selectedTags.recruitmentType = [...selectedItems].join(',');
        });

        // 폼 제출 처리
        return selectedTags;
    }


    // Form submission event listener
    document.getElementById("ProjectStudyPostForm").addEventListener("submit", async function (event) {
        event.preventDefault(); // Prevent the default form submission

        // Get form data
        const title = document.getElementById("title").value;
        const description = editor.root.innerHTML; // Get content from the Quill editor
        const recruitmentDeadline = document.getElementById("recruitmentDeadline").value;
        const contactEmail = document.getElementById("contactEmail").value;

        // Collect selected tags
        const selectedTags = getSelectedTags();


        const today = new Date();
        const deadlineDate = new Date(recruitmentDeadline);

        // Perform input validation
        if (!title || title.trim() === '') {
            alert("제목을 입력해주세요.");
            return; // Stop the form submission
        }

        if (!description || description.trim() === '') {
            alert("내용을 입력해주세요.");
            return; // Stop the form submission
        }

        if (!selectedTags.peopleCount || selectedTags.peopleCount.trim() === '') {
            alert("인원 수를 선택해주세요.");
            return; // Stop the form submission
        }

        if (!selectedTags.projectMode || selectedTags.projectMode.trim() === '') {
            alert("프로젝트 모드를 선택해주세요.");
            return; // Stop the form submission
        }

        if (!selectedTags.duration || selectedTags.duration.trim() === '') {
            alert("기간을 선택해주세요.");
            return; // Stop the form submission
        }

        if (deadlineDate < today) {
            alert("모집 마감일은 오늘 이후 날짜로 설정해주세요.");
            return; // Stop the form submission
        }

        if (!selectedTags.position || selectedTags.position.length === 0) {
            alert("직무를 선택해주세요.");
            return; // Stop the form submission
        }

        if (!recruitmentDeadline || recruitmentDeadline.trim() === '') {
            alert("모집 마감일을 선택해주세요.");
            return; // Stop the form submission
        }

        if (!selectedTags.techStack || selectedTags.techStack.length === 0) {
            alert("기술 스택을 선택해주세요.");
            return; // Stop the form submission
        }

        if (!selectedTags.recruitmentType || selectedTags.recruitmentType.length === 0) {
            alert("모집 유형을 선택해주세요.");
            return; // Stop the form submission
        }

        if (!contactEmail || contactEmail.trim() === '') {
            alert("연락처 이메일을 선택해주세요.");
            return; // Stop the form submission
        }

        // Prepare the data to send
        const contentData = {
            title: title,
            description: description,
            contactEmail: contactEmail,
            recruitmentDeadline : recruitmentDeadline,
            ...selectedTags // Include the selected tags in the request body
        };

        // Log contentData to the console for debugging
        console.log("Content data to send:", contentData);
        try {
            const response = await fetch('/api/projectStudyPost/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
                },
                body: JSON.stringify(contentData)
            })

            if (!response.ok) {
                const errorData = await response.json();
                throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
            }
            const data = await response.json(); // Return the server's response message

            postFromHandleSuccess(data);
        } catch(error){
            console.error(error);
            postFormHandleError(error);
        }

    });


    // Success handler
    async function postFromHandleSuccess(data) {
        console.log('게시글 작성 성공', data.message);

        // Construct the target URL
        const targetUrl = '/projectStudyPost/' + data.id;


        try {
            const response = await fetch(targetUrl, {
                method: 'GET',
                headers: {
                    'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
                }
            })

            if (!response.ok) {
                const errorData = await response.json();
                throw { status: response.status, message: errorData.message || 'Unexpected error occurred' };
            }

            const html = await response.text();

            const contentElement = document.querySelector('.content');

            contentElement.innerHTML = html;

            loadAssetsForUrl(targetUrl);

            // Update the browser history
            history.pushState({url: targetUrl}, '', targetUrl);

            // Notify the user
            alert(data.message);
        } catch(error) {
            console.error('오류:', error);
            postFormHandleError(error);
        }
    }


    function postFormHandleError(error) {
        if (error.status) {
            switch (error.status) {
                case 400:
                    alert(error.message);
                    break;
                case 404:
                    alert(error.message);
                    break;
                case 500:
                    alert(error.message);
                    break;
                default:
                    alert(error.message);
            }
        } else {
            alert(`네트워크 오류 또는 알 수 없는 오류: ${error.message || 'Unexpected error'}`);
        }
    }

})();
