document.addEventListener("DOMContentLoaded",() => {	
	
	const data = {
    "no": 1,
    "id": "byeol",
    "title": "게시글 제목",
    "detail": "<p>내용입니다</p><p><strong>이 부분은 볼드체</strong></p>",
    "regDate": "2024-09-27 12:03:11",
    "upDate": null,
    "delDate": null,
    "delYn": "N",
    "modifierId": "admin"
};

	renderBoardDetail(data);
});

const renderBoardDetail = (data) => {
    const boardDetail = document.getElementById('boardDetail');

    boardDetail.innerHTML = '';
	
	let date = data.regDate.split(' ')[0];

    const detailHTML = `
        <div class="board-detail" data-id="${data.no}">
            <div class="detail-header">
                <h1 class="detail-title">${data.title}</h1>
                <div class="detail-meta">
                    <span class="author">${data.id}</span>
                    <span class="date">${date}</span>
                </div>
            </div>
            <div class="detail-body">
                ${sanitizeEditorContent(data.detail)}
            </div>
            <div class="detail-footer">
                <p>Last updated by <strong>${data.modifierId}</strong></p>
            </div>
        </div>
    `;

    // Append the generated HTML to the container
    boardDetail.insertAdjacentHTML('beforeend', detailHTML);
};

const sanitizeEditorContent = (editorHTML) => {
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = editorHTML;

    // Iterate through child nodes and wrap text nodes in <p> tags
    const sanitizedContent = Array.from(tempDiv.childNodes).map(node => {
        if (node.nodeType === Node.TEXT_NODE && node.textContent.trim() !== '') {
            return `<p>${node.textContent.trim()}</p>`;
        }
        return node.outerHTML || ''; // Return HTML for other elements
    });

    return sanitizedContent.join('');
};