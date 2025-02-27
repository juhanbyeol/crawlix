document.getElementById('loginBtn').addEventListener('click', async () => {

    const userId = document.getElementById("userId").value;
    const password = document.getElementById("password").value;

    // 공통 API 호출 함수 사용
    try {
        const response = await common.post("/api/user/login", { id: userId, password: password });
		console.log(response);
        if (response && response.success) {
            //스토리지에 저장
           	common.setLocalStorage("loginInfo", response.data);
            window.location.href = "/"; // 로그인 성공 시 홈으로 이동
        } else {
            alert(response.message || "Login failed. Please try again.");
        }
    } catch (error) {
        console.error("Error during login:", error);
        alert("An error occurred. Please try again.");
    }
});
