document.getElementById('signupButton').addEventListener('click', async () => {
    const formData = {
        id: document.getElementById('id').value,
        password: document.getElementById('password').value,
        name: document.getElementById('name').value,
        mobile: document.getElementById('mobile').value,
        email: document.getElementById('email').value,
        auth_provider: document.getElementById('auth_provider').value,
        profile_picture: document.getElementById('profile_picture').value,
        login_method: 'LOCAL', // 기본값
        role: 'user' // 기본값
    };

    try {
        const response = await common.post('/api/user/signup', formData);
        if (response.success) {
            alert('회원가입 성공!');
            window.location.href = '/login'; // 로그인 페이지로 리다이렉트
        } else {
            alert(`회원가입 실패: ${response.message}`);
        }
    } catch (error) {
        console.error('회원가입 요청 중 오류 발생:', error);
        alert('회원가입 중 문제가 발생했습니다.');
    }
});