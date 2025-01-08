import { getRequest } from './api.js';

/********************
 *    Templates 
 *******************/

/**
 * dom 템플릿 객체입니다.
 */
const domTemplates = {
    contentDom: (content, startTime) => `
        <div class="d-flex border-bottom py-3">
            <div class="w-100 ms-3">
                <div class="d-flex">
                    <img class="rounded-circle flex-shrink-0" src="${content.contentIconLink}" alt="" style="width: 40px; height: 40px;">
                    <div id="content-${content.contentId}" class="text-start ms-3">
                        <h6 class="mb-0">${content.contentName}</h6>
                        <small class="remain-time">${startTime}</small>
                    </div>
                </div>
            </div>
        </div>
    `,
};

/**
 * modal 관리 객체입니다.
 */
const modalManager = {
    // 모달 내용 업데이트
    updateModalBody(bodyDom) {
        $('#remain-time-modal-body').html(bodyDom);
    },

    // 모달 열기
    openModal() {
        $('#staticBackdrop').modal('show');
    },

    // 모달 닫기
    async closeModal() {
        $('#staticBackdrop').modal('hide');
        await initializeContentContainer('.content-container', 'main');
    }
};

/********************
 *  Initialization 
 *******************/

// 페이지 초기화
$(() => {
    initFunction();
    $('.content-modal-link').on('click', handleModalClick);
    $('[data-bs-dismiss="modal"]').on('click', modalManager.closeModal);
});

// 페이지 로드 시 실행되는 초기화 함수
async function initFunction() {
    await initializeContentContainer('.content-container', 'main');
}

// modal 링크(전체) 클릭 시 실행되는 함수
async function handleModalClick(event) {
    event.preventDefault();

    await initializeContentContainer('#remain-time-modal-body', 'modal');

    modalManager.openModal();
}

/******************************
 *     Business functions
 ******************************/

// content 데이터를 가져오고 전처리하는 함수
async function fetchContentData(url) {
    try {
        const response = await getRequest(url);
        const contents = getValidStartTime(response); // 유효 시간 데이터 파싱
        return contents;
    } catch (e) {
        console.error('데이터를 가져오는 데 실패했습니다.', e);
        return [];
    }
}

/** 
 * 콘텐츠를 렌더링하고 타이머를 설정하는 함수
 * 
 * @param {string} selector - 렌더링할 컨테이너의 dom 선택자
 * @param {string} [timerName='main'] - 실행하는 타이머의 종류 (초기값: 'main')
 */
async function initializeContentContainer(selector, timerName = 'main') {
    const contents = await fetchContentData('/contents/start-time');

    // 컨테이너 렌더링
    const contentsDom = contents.map(content =>
        domTemplates.contentDom(content, 'loading...')
    ).join('');
    $(selector).html(contentsDom);

    // 유효한 데이터만 필터링
    const validContents = contents.filter(content => {
        if (!(content.contentStartTimes instanceof Date)) {
            updateContentTime(content.contentId, content.contentStartTimes, selector);
            return false;
        }
        return true;
    });

    // 타이머 시작
    startTimer(
        validContents,
        (id, formattedTime) => {
            updateContentTime(id, formattedTime, selector);
        },
        (id, finalTime) => {
            updateContentTime(id, finalTime, selector);
        },
        timerName
    );
}

/**********************
 *  Utility functions
**********************/

/**
 * 유효한 시간을 반환하는 함수
 * 
 * @param {Array} contents - content 배열
 * @returns {Array} 유효 시간이 분류된 contents 배열
*/ 
function getValidStartTime(contents) {
    return contents.map(content => {
        const validStartTimes = content.contentStartTimes
        .map(contentStartTime => new Date(contentStartTime.contentStartTime)) // Date 객체 변환
        .filter(contentStartTime => contentStartTime > new Date()) // 유효 시간 필터링
        
        return {
            ...content,
            contentStartTimes: getRemainingTime(validStartTimes),
        };
    });
}

/**
 * 남은 시간에 따른 데이터를 반환하는 함수
 * 
 * @param {Array<Date>} timeArray - 시간 배열
 * @returns {String | Date} '출현 대기 중...' || 유효 시간
 */
function getRemainingTime(timeArray) {
    // 1. 남은 일정이 없을 경우
    if (!timeArray || timeArray.length === 0) {
        return '출현 대기 중...';
    }

    const firstTime = timeArray[0];

    // 2. 당일 출현 컨텐츠인 경우
    if (isToday(firstTime)) {
        return firstTime;
    }

    // 3. 익일 오전 6시 출현 예정인 경우
    if (isNextDay(firstTime)) {
        return `익일 ${firstTime.getHours().toString().padStart(2, '0')}:${firstTime.getMinutes().toString().padStart(2, '0')} 출현 예정`;
    }

    // 4. 이외의 경우
    return '출현 대기 중...';
}

/**
 * 입력된 시간이 오전 6시 기준 당일인지 확인하는 함수
 * 
 * @param {Date} time - 시간 데이터
 * @returns {boolean} 당일 여부
 */
function isToday(time) {
    const now = new Date();

    // 당일 오전 6시 설정
    const nextDay = new Date(now); // 당일 오전 6시
    nextDay.setHours(6, 0, 0, 0);

    // 현재 시간이 오전 6시 이후인 경우
    if (now.getHours() >= 6) {
        nextDay.setDate(now.getDate() + 1); // 익일 오전 6시
    }

    // 입력 시간이 범위 내에 있는지 확인
    return time >= now && time < nextDay;
}

/**
 * 입력된 시간이 오전 6시 기준 다음날인지 확인하는 함수
 * 
 * @param {Date} time - 시간 데이터
 * @returns {boolean} 다음날 여부
 */
function isNextDay(time) {
    const now = new Date();

    // 익일 오전 6시 설정
    const nextDay = new Date(now);
    nextDay.setHours(6, 0, 0, 0);
    nextDay.setDate(nextDay.getDate() + 1);
    const dayAfterTomorrow = new Date(nextDay); // 익일 오전 6시

    // 현재 시간이 오전 6시 이후인 경우
    if (now.getHours() >= 6) {
        dayAfterTomorrow.setDate(nextDay.getDate() + 1); // 모레 오전 6시
    }

    // 입력 시간이 범위 내에 있는지 확인
    return time >= now && time < dayAfterTomorrow;
}

/**
 * ui의 남은 시간을 갱신하는 함수
 * 
 * @param {int} contentId - content의 id
 * @param {String} formattedTime - 00:00:00 형식의 문자열
 * @param {String} selector - 렌더링할 컨테이너의 dom 선택자
 */
function updateContentTime(contentId, formattedTime, selector = '.content-container') {
    console.log(selector + '의 dom 업데이터가 되고 있나요');
    const $remainTimeDom = $(`${selector} #content-${contentId} .remain-time`);
    if ($remainTimeDom) {
        $remainTimeDom.text(formattedTime);
    }
}

/** @type {Function | null} */
let mainTimer = null;
/** @type {Function | null} */
let modalTimer = null;

/**
 *  타이머 함수
 * 
 * @param {Array} contents - 초기 데이터 배열
 * @param {Function} onTick - 매초 호출되는 dom 업데이트 함수
 * @param {Function} onComplete - 타이머 종료 시 호출되는 함수
 * @param {string} [timerName='main'] - 실행하는 타이머의 종류 (초기값: 'main')
 */
function startTimer(contents, onTick, onComplete, timerName) {
    // 기존 타이머 확인 및 중지
    if (timerName === 'main') {
        if (mainTimer) {
            console.log('실행 중인 메인 타이머를 중지합니다.');
            clearInterval(mainTimer);
            mainTimer = null;
        }
    }
    if (modalTimer) {
        console.log('실행 중인 모달 타이머를 중지합니다.');
        clearInterval(modalTimer);
        modalTimer = null;
    }

    const timer = setInterval(() => {
        const now = new Date();
        contents.forEach(content => {
            const diff = content.contentStartTimes - now;

            if (diff > 1000) {
                onTick(content.contentId, formatTime(decrementTime(diff)));
            } else {
                onComplete(content.contentId, '---출현 중---');
            }
        });
    }, 1000);

    // 새로운 타이머 실행
    if (timerName === 'main') {
        console.log('메인 타이머를 실행합니다.');
        mainTimer = timer;
    }
    if (timerName === 'modal') {
        console.log('모달 타이머를 실행합니다.');
        modalTimer = timer;
    }
}

/** 
 * ms 단위 시간을 1초 감소시키는 함수
 * 
 * @param {number} ms - ms 단위 시간
 * @returns {number} 1초 감소된 ms 단위 시간
*/ 
function decrementTime(ms) {
    return ms - 1000;
}

/**
 * ms 단위 시간 데이터를 00:00:00 형식으로 변환하는 함수
 * 
 * @param {ms} ms - ms 단위 시간
 * @returns {String} 00:00:00 형식의 문자열
*/
function formatTime(date) {
    const totalSeconds = Math.floor(date / 1000);
    const hh = Math.floor(totalSeconds / 3600).toString().padStart(2, '0');
    const mm = Math.floor((totalSeconds % 3600) / 60).toString().padStart(2, '0');
    const ss = (totalSeconds % 60).toString().padStart(2, '0');

    return `${hh}:${mm}:${ss}`;
}
