const baseUrl = 'http://localhost';

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
      console.error(`컨텐츠 데이터를 가져오는 도중 예외가 발생했습니다: \n${e}`);
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
      // 비동기 흐름 제어를 위한 async, await를 사용할 수 있도록 Promise 객체를 반환합니다.
      return await new Promise((resolve, reject) => {
          $.ajax({
              url: `${baseUrl}${url}`,
              method: 'POST',
              data: JSON.stringify(data), // 객체를 JSON 데이터로 파싱
              contentType: 'application/json', // POST 요청의 body를 JSON으로 전송
              success: resolve, // 성공 시 데이터 반환
              error: reject // 실패 시 에러 반환
          });
      });
  } catch (e) {
      console.error(`컨텐츠 데이터를 가져오는 도중 예외가 발생했습니다: \n${e}`);
      return e; // 예외 발생 시 해당 예외 반환
  }
}
