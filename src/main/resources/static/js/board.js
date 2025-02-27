document.addEventListener("DOMContentLoaded",function(){	
	loadMore();
});

function goToDetail(id) {
    window.location.href = '/boardDetail?no=' + id;
}

let currentPage = 1;


const loadMore = async () => {
    currentPage++;
    const formData = {
        //page: currentPage,
        title: document.querySelector('[name="title"]').value,
        author: document.querySelector('[name="author"]').value
    };

    try {
        const response = await common.post('/api/board/list', formData);
        if (response.success) {
			console.log(response.data);
			response.data.forEach(item => {
			    const cardHTML = `
			        <div class="board-card" onclick="goToDetail(${item.no})">
			            <div class="card-header">
			                <span class="title">${item.title}</span>
			                <span class="date">${item.regDate.split(' ')[0]}</span>
			            </div>
			            <div class="card-footer">
			                <span class="author">${item.id}</span>
			            </div>
			        </div>
			    `;
			    document.getElementById('boardList').insertAdjacentHTML('beforeend', cardHTML);
			});

        } else {
            console.log(`loadMore 실: ${response.message}`);
        }
    } catch (error) {
        console.error('loadMore', error);
        alert('게시판 조회 문제가 발생했습니다.');
    }
};
