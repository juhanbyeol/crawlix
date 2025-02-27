const common = {
    /**
     * 서버에 POST 요청을 보냅니다.
     * @param {string} url 요청 URL
     * @param {object} data 요청 데이터
     * @returns {Promise<object>} 응답 데이터
     */
    post: async (url, data) => {
        const headers = {
            'Content-Type': 'application/json',
        };
        const response = await fetch(url, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
    },

    /**
     * 서버에 GET 요청을 보냅니다.
     * @param {string} url 요청 URL
     * @returns {Promise<object>} 응답 데이터
     */
    get: async (url) => {
        const headers = {
            'Content-Type': 'application/json',
        };
        const response = await fetch(url, {
            method: 'GET',
            headers: headers,
        });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
    },
    
    setLocalStorage :(name, data) => {
		window.localStorage.setItem(name,JSON.stringify(data));
	},
    getLocalStorage :(name) => {
		return JSON.parse(window.localStorage.getItem(name));
	},
	removeLolocalStorage : (name) => {
		window.localStorage.removeItem(name);
	}
};

