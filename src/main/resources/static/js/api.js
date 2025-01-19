const baseUrl = 'http://localhost/api';

/** 
 * 서버에 GET 요청을 보내는 함수입니다.
 * 
 * @param {String} url 요청을 보낼 절대경로를 입력합니다.
 * @returns {Object} 요청에 따른 결과가 반환됩니다.
 */ 
export async function getRequest(url) {
  try {
      // 비동기 흐름 제어를 위한 async, await를 사용할 수 있도록 Promise 객체를 반환합니다.
      return await new Promise((resolve, reject) => {
          $.ajax({
              url: `${baseUrl}${url}`,
              method: 'GET',
              success: resolve, // 성공 시 데이터 반환
              error: reject // 실패 시 에러 반환
          });
      });
  } catch (e) {
      console.error(`GET 요청에서 예외가 발생했습니다:`);
      return e; // 예외 발생 시 해당 예외 반환
  }
}

/** 
 * 서버에 POST 요청을 보내는 함수입니다.
 * 
 * @param {String} url 요청을 보낼 절대경로를 입력합니다.
 * @param {Object} data POST 요청의 body로 전달할 객체를 입력합니다.
 * @returns {Object} 요청에 따른 결과가 반환됩니다.
 */ 
export async function postRequest(url, data = null) {
    try {
        if (typeof data === 'object') {
            data = JSON.stringify(data); // 객체를 JSON 데이터로 파싱
        }
        // 비동기 흐름 제어를 위한 async, await를 사용할 수 있도록 Promise 객체를 반환합니다.
        return await new Promise((resolve, reject) => {
            $.ajax({
                url: `${baseUrl}${url}`,
                method: 'POST',
                data: data,
                contentType: 'application/json', // POST 요청의 body를 JSON으로 전송
                success: resolve, // 성공 시 데이터 반환
                error: reject // 실패 시 에러 반환
            });
        });
    } catch (e) {
        console.error(`POST 요청에서 예외가 발생했습니다:`);
        return e; // 예외 발생 시 해당 예외 반환
    }
}

/** 
 * 서버에 DELETE 요청을 보내는 함수입니다.
 * 
 * @param {String} url 요청을 보낼 절대경로를 입력합니다.
 * @param {Object} data DELETE 요청의 body로 전달할 객체를 입력합니다.
 * @returns {Object} 요청에 따른 결과가 반환됩니다.
 */ 
export async function deleteRequest(url, data = null) {
    try {
        if (typeof data === 'object') {
            data = JSON.stringify(data); // 객체를 JSON 데이터로 파싱
        }
        // 비동기 흐름 제어를 위한 async, await를 사용할 수 있도록 Promise 객체를 반환합니다.
        return await new Promise((resolve, reject) => {
            $.ajax({
                url: `${baseUrl}${url}`,
                method: 'POST',
                data: data,
                contentType: 'application/json', // POST 요청의 body를 JSON으로 전송
                success: resolve, // 성공 시 데이터 반환
                error: reject // 실패 시 에러 반환
            });
        });
    } catch (e) {
        console.error(`DELETE 요청에서 예외가 발생했습니다:`);
        return e; // 예외 발생 시 해당 예외 반환
    }
}
