$(document).ready(function() {
    const $statusElem = $('.progress-box .status');
    const $progressElem = $('.progress-box .progress');
    const $dataList = $('#dataList');

    // 🔵 Step 1: 사이트 접속
    $("#start-crawl-btn").on('click', function() {
        const siteUrl = $('#url-input').val();
        if (!siteUrl) {
            alert("사이트 URL을 입력해주세요.");
            return false;
        }
        $statusElem.text('상태: 사이트 접속 중...');
        $progressElem.css('width', '30%');

        $.post('/crawl/start', { siteUrl: siteUrl }, function(response) {
            if (response.message === "OK") {
                $statusElem.text('상태: 추가 URL 입력 대기 중');
                $progressElem.css('width', '50%');
                $('#step-1').addClass('active');
                $('#step-2').addClass('active');
                $('#tab-url-section').slideDown();  // 🟢 Step 2 표시
            }
        });
    });

    // 🟢 Step 2: 크롤링 시작
    $("#continue-crawl-btn").on('click', function() {
        const tabUrl = $('#tab-url-input').val();
        if (!tabUrl) {
            alert("탭 URL을 입력해주세요.");
            return false;
        }
        $statusElem.text('상태: 크롤링 진행 중...');
        $progressElem.css('width', '75%');
        $dataList.empty();

        $.post('/crawl/continue', { tabUrl: tabUrl }, function(response) {
            if (response.message === "OK") {
                $progressElem.css('width', '100%');
                $statusElem.text('상태: 완료');
                let html = '';
                loadFileList();
                //response.data.result.forEach((item, index) => {
					//미리보기이므로..제한걸어두기 
					//if(index > 10) {
					//	return false;
					//}
                    //html += `<tr><td>${index + 1}</td><td>${item}</td></tr>`;
                //});
                //$dataList.html(html);
            }
        });
    });
    
    //저장된 txt파일 다운로드 
  	$(document).on("click", ".download-btn", function() {
        const fileName = $(this).data("file");
        window.location.href = `/files/download?fileName=${encodeURIComponent(fileName)}`;
	});
	
	loadFileList();
});

function loadFileList() {
	const $dataList = $("#dataList");
	$.get("/files/list", function(files) {
	    let html = "";
	    files.forEach(file => {
	        html += `<tr>
	            <td>${file}</td>
	            <td><button class="download-btn" data-file="${file}">다운로드</button></td>
	        </tr>`;
	    });
	    $dataList.html(html);
	});
}
