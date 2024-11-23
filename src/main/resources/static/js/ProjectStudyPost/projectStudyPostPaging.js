(function () {
    const fetchPosts = async (page = 0, size = 10) => {
        try {
            const response = await fetch(`/api/ProjectStudyPost?page=${page}&size=${size}`);
            if (!response.ok) {
                throw new Error('Failed to fetch posts');
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.log('Error:', error);
        }
    };

    // Fetch posts and render them
    fetchPosts(0, 10).then(data => {
        if (data) {
            const cardContainer = document.querySelector('.board-card-grid');
            cardContainer.innerHTML = '';  // Clear any existing content

            const posts = data.content;  // Assuming 'data.content' contains posts

            posts.forEach(post => {
                // Create the main card container
                const postElement = document.createElement('div');
                postElement.classList.add('board-card');  // Class for the card

                // Create the card header
                const cardHeader = document.createElement('div');
                cardHeader.classList.add('board-card-header');

                const cardTitle = document.createElement('h2');
                cardTitle.classList.add('board-card-title');
                cardTitle.innerText = post.title;  // Set the post title

                const cardAuthor = document.createElement('p');
                cardAuthor.classList.add('board-card-author');
                cardAuthor.innerText = post.user ? post.user.name : 'Anonymous';  // Assuming `post.user.name` exists

                cardHeader.appendChild(cardTitle);
                cardHeader.appendChild(cardAuthor);

                // Create the card content
                const cardContent = document.createElement('div');
                cardContent.classList.add('board-card-content');

                const cardDescription = document.createElement('p');
                cardDescription.innerText = post.description || 'No description available';

                cardContent.appendChild(cardDescription);

                // Create the card footer
                const cardFooter = document.createElement('div');
                cardFooter.classList.add('board-card-footer');

                const cardMeta = document.createElement('div');
                cardMeta.classList.add('board-card-meta');

                const cardDate = document.createElement('span');
                cardDate.classList.add('board-card-date');
                cardDate.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="16" y1="2" x2="16" y2="6"></line>
                        <line x1="8" y1="2" x2="8" y2="6"></line>
                        <line x1="3" y1="10" x2="21" y2="10"></line>
                    </svg>
                    ${post.create_at ? post.create_at.substring(0, 10) : 'No date'}  <!-- Format date as YYYY-MM-DD -->
                `;

                const cardTechStack = document.createElement('div');
                cardTechStack.classList.add('board-tech-stack');

                post.techStack && post.techStack.forEach(tech => {
                    const techBadge = document.createElement('span');
                    techBadge.classList.add('board-tech-badge', 'primary');
                    techBadge.innerText = tech;  // Assuming `post.techStack` is an array
                    cardTechStack.appendChild(techBadge);
                });

                cardMeta.appendChild(cardDate);
                cardMeta.appendChild(cardTechStack);

                // Create the stats section (views and comments)
                const cardStats = document.createElement('div');
                cardStats.classList.add('board-card-stats');

                const cardViews = document.createElement('span');
                cardViews.classList.add('board-card-views');
                cardViews.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                        <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                    ${post.viewCount || 0}  <!-- Default view count to 0 if undefined -->
                `;

                const cardComments = document.createElement('span');
                cardComments.classList.add('board-card-comments');
                cardComments.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                    </svg>
                    ${post.commentCount || 0}  <!-- Default comment count to 0 if undefined -->
                `;

                cardStats.appendChild(cardViews);
                cardStats.appendChild(cardComments);

                // Append everything together
                cardFooter.appendChild(cardMeta);
                cardFooter.appendChild(cardStats);

                postElement.appendChild(cardHeader);
                postElement.appendChild(cardContent);
                postElement.appendChild(cardFooter);

                // Finally, append the post element to the card container
                cardContainer.appendChild(postElement);
            });
        }
    });
})();
