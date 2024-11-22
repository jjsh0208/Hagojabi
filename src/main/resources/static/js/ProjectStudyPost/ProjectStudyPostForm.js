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

    // Get selected tags from the dropdowns
    function getSelectedTags() {
        const selectedTags = {
            peopleCount: "",
            projectMode: "",
            duration: "",
            position: [],
            recruitmentDeadline: document.getElementById("recruitmentDeadline").value,
            techStack: [],
            recruitmentType: "",
            contactEmail: document.getElementById("contactEmail").value
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

        return selectedTags;
    }

    // Form submission event listener
    document.getElementById("ProjectStudyPostForm").addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission

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
            ...selectedTags // Include the selected tags in the request body
        };

        // Log contentData to the console for debugging
        console.log("Content data to send:", contentData);

        // Make a POST request to the server
        fetch('/api/ProjectStudyPost/create', {
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
    });

    // Handle server response
    function handleResponse(response) {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || '게시글 작성 실패');
            });
        }
        return response.json(); // Return the server's response message
    }

    // Success handler
    function handleSuccess(response) {
        console.log('게시글 작성 성공', response.message);

        // Construct the target URL
        const targetUrl = '/ProjectStudyPost/' + response.id;

        // Fetch the content for the newly created post
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
                // Ensure the content element exists
                const contentElement = document.querySelector('.content');
                if (!contentElement) {
                    throw new Error("Content element not found in the DOM.");
                }

                // Update the content dynamically
                contentElement.innerHTML = html;

                // Load additional assets if needed (optional)
                if (typeof loadAssetsForUrl === 'function') {
                    loadAssetsForUrl(targetUrl);
                }

                // Update the browser history
                history.pushState({ url: targetUrl }, '', targetUrl);

                // Notify the user
                alert("게시글 작성이 성공적으로 완료되었습니다.");
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
