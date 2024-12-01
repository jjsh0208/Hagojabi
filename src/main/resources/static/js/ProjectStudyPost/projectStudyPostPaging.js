(function () {
    // 게시글을 비동기적으로 가져오는 함수 (페이지네이션 지원)
    const fetchPosts = async (page = 0, size = 10) => {
        try {
            // 지정된 페이지와 크기로 게시글을 가져오는 GET 요청을 보냄
            const response = await fetch(`/api/projectStudyPost?page=${page}&size=${size}`);
            // 응답이 정상적이지 않으면 에러를 발생시킴
            if (!response.ok) {
                throw new Error('Failed to fetch posts');
            }
            // JSON 형식으로 응답 데이터를 파싱
            const data = await response.json();
            return data;
        } catch (error) {
            // 에러 발생 시 콘솔에 에러 메시지를 출력
            console.log('Error:', error);
        }
    };

    // 게시글 목록을 화면에 렌더링하는 함수
    const renderPosts = (posts) => {
        const cardContainer = document.querySelector('.board-card-grid');
        if(cardContainer){
            cardContainer.innerHTML = ''; // 기존 콘텐츠를 비움
        }

        // 각 게시글에 대해 반복하며 카드 요소를 생성
        posts.forEach(post => {
            console.log(JSON.stringify(post, null, 2));

            // 게시글 카드를 담을 div 요소 생성
            const postElement = document.createElement('div');
            postElement.classList.add('board-card');

            // 카드 헤더 생성 (제목과 작성자)
            const cardHeader = document.createElement('div');
            cardHeader.classList.add('board-card-header');

            // 게시글 제목을 카드 헤더에 추가
            const cardTitle = document.createElement('h2');
            cardTitle.classList.add('board-card-title');
            cardTitle.innerText = post.title;

            // 게시글 작성자를 카드 헤더에 추가 (작성자가 없으면 'Anonymous'로 표시)
            const cardAuthor = document.createElement('p');
            cardAuthor.classList.add('board-card-author');
            cardAuthor.innerText = post.author ? post.author.name : 'Anonymous';

            // 제목과 작성자를 카드 헤더에 추가
            cardHeader.appendChild(cardTitle);
            cardHeader.appendChild(cardAuthor);

            // 카드 내용 부분 생성 (설명)
            const cardContent = document.createElement('div');
            cardContent.classList.add('board-card-content');

            // 게시글 설명을 카드 내용에 추가 (설명이 없으면 'No description available' 표시)
            const cardDescription = document.createElement('p');
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = post.description || 'No description available';
            cardDescription.textContent = tempDiv.textContent || tempDiv.innerText;

            cardContent.appendChild(cardDescription);

            // 카드 풋터 부분 생성 (날짜와 기술 스택)
            const cardFooter = document.createElement('div');
            cardFooter.classList.add('board-card-footer');

            // 메타데이터 부분 생성 (날짜와 기술 스택)
            const cardMeta = document.createElement('div');
            cardMeta.classList.add('board-card-meta');

            // 게시글 생성일을 메타데이터에 추가
            const cardDate = document.createElement('span');
            cardDate.classList.add('board-card-date');
            cardDate.innerHTML = `                 
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">                     
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>                     
                    <line x1="16" y1="2" x2="16" y2="6"></line>                     
                    <line x1="8" y1="2" x2="8" y2="6"></line>                     
                    <line x1="3" y1="10" x2="21" y2="10"></line>                 
                </svg>                 
                ${post.create_at ? post.create_at.substring(0, 10) : 'No date'}             
            `;

            // 기술 스택을 메타데이터에 추가
            const cardTechStack = document.createElement('div');
            cardTechStack.classList.add('board-tech-stack');

            // 기술 스택이 있을 경우 각각의 기술을 배지로 표시
            post.techStack && post.techStack.forEach(tech => {
                const techBadge = document.createElement('span');
                techBadge.classList.add('board-tech-badge', 'primary');
                techBadge.innerText = tech;
                cardTechStack.appendChild(techBadge);
            });

            cardMeta.appendChild(cardDate);
            cardMeta.appendChild(cardTechStack);

            // 조회수와 댓글 수를 나타내는 통계 부분 생성
            const cardStats = document.createElement('div');
            cardStats.classList.add('board-card-stats');

            // 조회수 표시
            const cardViews = document.createElement('span');
            cardViews.classList.add('board-card-views');
            cardViews.innerHTML = `                 
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">                     
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>                     
                    <circle cx="12" cy="12" r="3"></circle>                 
                </svg>                 
                ${post.viewCount || 0}             
            `;

            // 댓글 수 표시
            const cardComments = document.createElement('span');
            cardComments.classList.add('board-card-comments');
            cardComments.innerHTML = `                 
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">                     
                    <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>                 
                </svg>                 
                ${post.commentCount || 0}             
            `;

            cardStats.appendChild(cardViews);
            cardStats.appendChild(cardComments);

            // 메타데이터와 통계 정보를 카드 풋터에 추가
            cardFooter.appendChild(cardMeta);
            cardFooter.appendChild(cardStats);

            // 헤더, 내용, 풋터를 카드 요소에 추가
            postElement.appendChild(cardHeader);
            postElement.appendChild(cardContent);
            postElement.appendChild(cardFooter);

            postElement.addEventListener('click', async =>{
                const postId = post.id;
                fetchPostDetails(postId);
            })

            // 최종적으로 카드를 카드 컨테이너에 추가
            cardContainer.appendChild(postElement);
        });
    };

    // 페이지네이션을 렌더링하는 함수
    const renderPagination = (currentPage, totalPages) => {
        const paginationContainer = document.querySelector('.pagination');
        paginationContainer.innerHTML = ''; // 기존 페이지네이션 내용 비움

        // 페이지 블록 범위 계산
        const pagesPerBlock = 10;
        const blockStart = Math.floor(currentPage / pagesPerBlock) * pagesPerBlock; // 현재 블록의 시작 페이지
        const blockEnd = Math.min(blockStart + pagesPerBlock, totalPages); // 현재 블록의 끝 페이지

        // 이전 페이지 버튼 생성
        const prevButton = document.createElement('li');
        prevButton.classList.add('btn-prev');
        prevButton.innerHTML = `<a href="#" aria-label="Previous page">&laquo;</a>`;
        prevButton.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentPage > 0) fetchAndRenderPosts(currentPage - 1);
        });
        paginationContainer.appendChild(prevButton);

        // 페이지 번호 버튼 생성
        for (let i = blockStart; i < blockEnd; i++) {
            const pageButton = document.createElement('li');
            const link = document.createElement('a');
            link.href = '#';
            link.innerText = i + 1; // 페이지 번호는 1부터 시작하도록 표시

            // 현재 페이지는 활성화 상태로 표시
            if (i === currentPage) {
                link.classList.add('active');
            }

            // 페이지 버튼 클릭 시 해당 페이지의 게시글을 가져와서 렌더링
            link.addEventListener('click', (e) => {
                e.preventDefault();
                fetchAndRenderPosts(i); // 클릭한 페이지의 게시글을 가져와 렌더링
            });

            // 페이지 버튼을 페이지네이션에 추가
            pageButton.appendChild(link);
            paginationContainer.appendChild(pageButton);
        }

        // 다음 페이지 버튼 생성
        const nextButton = document.createElement('li');
        nextButton.classList.add('btn-next');
        nextButton.innerHTML = `<a href="#" aria-label="Next page">&raquo;</a>`;
        nextButton.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentPage < totalPages - 1) fetchAndRenderPosts(currentPage + 1);
        });
        paginationContainer.appendChild(nextButton);
    };



    function fetchPostDetails(id){

        const targetUrl = '/projectStudyPost/'+ id;

        fetch(targetUrl, {
            method: 'GET',
            headers : {
                'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' +localStorage.getItem('accessToken') : '', // 액세스 토큰 추가
            }
        })
            .then(response =>{
                if(!response.ok) throw new Error("게시글을 가져오지못했습니다.");
                return response.text(); // HTML 텍스트 반환

            })
            .then(html =>{
                document.querySelector('.content').innerHTML = html; // 콘텐츠 업데이트
                loadAssetsForUrl(targetUrl);
                history.pushState({url: targetUrl}, '', targetUrl); // 현재 URL 상태에 저장
            })
            .catch(error =>{
                console.error('오류:',error);
                alert("페이지 로드에 실패했습니다. 다시 시도해주세요.");
            })

    }


    // 페이지를 가져와서 게시글과 페이지네이션을 렌더링하는 함수
    const fetchAndRenderPosts = async (page = 0, size = 12) => {
        const data = await fetchPosts(page, size);
        if (data) {
            renderPosts(data.content); // 게시글 렌더링
            renderPagination(data.number, data.totalPages); // 페이지네이션 렌더링
        }
    };

    // 처음 게시글을 가져와서 렌더링 (기본 페이지는 0, 게시글 개수는 12로 설정)
    fetchAndRenderPosts();
})();