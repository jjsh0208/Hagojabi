(function () {
    function getProjectStudyPost(id){
        const targetUrl = '/ProjectStudyPost/'+ id;
        fetch(targetUrl,{
            method : "GET",
            'Authorization': localStorage.getItem('accessToken') ? 'Bearer ' + localStorage.getItem('accessToken') : ''
        })
            .then(response => {
                if(!response.ok) throw new Error("게시글을 가져오지못했습니다.");
                return response.text() //html 반환

            })
            .then(html =>{
                alert("test");
            })
    }

})();
