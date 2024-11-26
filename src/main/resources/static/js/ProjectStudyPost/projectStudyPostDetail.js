(function () {

const postId = document.querySelector('.ProjectStudyPost-content').dataset.postId;

fetch("/api/ProjectStudyPost/"+postId , {
    method : "GET",
    headers: {
        'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
    },
    }).then(handleResponse)
    .then(handleSuccess)


function handleResponse(response){
    if (!response.ok) {
        return response.text().then(text => {
            throw new Error(text || '게시글 가져오기 실패');
        });
    }
    return response.json();
}
function handleSuccess(data){
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

    // Assuming position is an array
    data.position.forEach(position => {
        const listItem = document.createElement('li');
        listItem.textContent = position; // Set the text to the current position
        positionList.appendChild(listItem); // Add the <li> to the <ul>
    });

}
function handleError(error){
    alert("게시글 가져오는 도중 오류 발생 " + error);
    console.error(error);
}
})();