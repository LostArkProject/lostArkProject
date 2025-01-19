import { getRequest, postRequest } from './api.js';

/********************
 *    Templates 
 *******************/

/** dom 템플릿 객체입니다. */
const domTemplates = {
    contentDom: (content, startTime, selectorType) => `
        <div class="d-flex border-bottom py-3 ${selectorType}-container">
            <div class="w-100 ms-3">
                <div class="d-flex">
                    <img 
                        class="rounded-circle flex-shrink-0"
                        src="${content.contentIconLink}"
                        alt="${content.contentName}"
                        style="width: 40px; height: 40px;"
                    />
                    <div id="content-${content.contentNumber}" class="text-start ms-3">
                        <h6 class="mb-0">${content.contentName}</h6>
                        <small class="remain-time">${startTime}</small>
                    </div>
                </div>
            </div>
            ${selectorType === 'modal' 
                ? `
                    <input
                        type="checkbox"
                        class="form-check-input align-self-center"
                        id="checkbox-${content.contentNumber}"
                        name="content"
                        value="${content.contentNumber}"
                        aria-label="알림 설정: ${content.contentName}"
                        style="width: 20px;"
                    />
                    <label
                        class="ms-2 me-4"
                        for="checkbox-${content.contentNumber}"
                        style="white-space: nowrap; align-self: center;"
                    >
                        알림 ON
                    </label>
                `
                : ''
            }
        </div>
    `,
};

/** modal 관리 객체입니다. */
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

/** 컨텐츠의 name 테이블입니다. */
const nameMapping = {
    '[습격]리베르탄-[점령]-[습격]프라이겔리': '[습격] 리베르탄',
    '[습격]프라이겔리-[점령]-[습격]리베르탄': '[습격] 프라이겔리',
    '몬테섬': '몬테 섬',
    '배틀 아레나': '태초의 섬',
    '세베크 아툰': '필드보스'
}

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

    $('.modal-container').on('change', 'input[type="checkbox"]', function () {
        const isChecked = $(this).is(':checked'); // 체크박스의 상태 확인
        const contentNumber = $(this).val(); // 체크박스의 value 속성 (contentNumber)
        
        console.log(isChecked);
        console.log(contentNumber);
        updateAlarmSettings(contentNumber);
    });

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
        const renameContents = replaceNames(contents); // 컨텐츠 이름 대체
        const uniqueContents = removeDuplicateContent(renameContents); // 중복 컨텐츠 제거
        console.log(uniqueContents);
        return uniqueContents;
    } catch (e) {
        console.error('데이터를 가져오는 데 실패했습니다.', e.responseText);
        return [];
    }
}

/** 
 * 콘텐츠를 렌더링하고 타이머를 설정하는 함수
 * 
 * @param {string} selector - 렌더링할 컨테이너의 dom 선택자
 * @param {string} [selectorType='main'] - 선택자의 종류 (초기값: 'main')
 */
async function initializeContentContainer(selector, selectorType = 'main') {
    const contents = await fetchContentData('/contents/start-time');

    /** @deprecated */
    const reductContents = selector === '.content-container' 
        ? getFirstFiveContents(contents, 5)
        : contents;

    // 컨테이너 렌더링
    const contentsDom = reductContents.map(content =>
        domTemplates.contentDom(content, 'loading...', selectorType)
    ).join('');
    $(selector).html(contentsDom);

    // 유효한 데이터만 필터링
    const validContents = reductContents.filter(content => {
        if (!(content.contentStartTimes instanceof Date)) {
            updateContentTime(content.contentNumber, content.contentStartTimes, selector);
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
        selectorType
    );
}

/**
 * 특정 유저의 컨텐츠별 알림 설정 여부를 반환하는 함수
 * 
 * @returns {Object} 유저의 알림 설정 여부 객체
 */
async function fetchAlarmSettings() {
    try {
        const memberId = loggedInMember.memberId;
        const response = await getRequest(`/alarm/member/${memberId}`);
        console.log('알림 설정 데이터');
        console.log(response);
        return response;
    } catch (e) {
        console.error('유저의 알림 설정 데이터을 가져오는 데 실패했습니다.', e.responseText);
        return false;
    }
}

/**
 * contentNumber로 알림 설정을 갱신하는 함수
 * 
 * @param {number} contentNumber - 특정 컨텐츠의 number
 */
async function updateAlarmSettings(contentNumber) {
    try {
        const memberId = loggedInMember.memberId;
        const response = await postRequest(`/alarm/member/${memberId}/${contentNumber}`);
        console.log('알림 설정 갱신: ');
        console.log(response);
    } catch (e) {
        console.error('유저의 알림 설정 갱신 실패', e.responseText);
    }
}

/**********************
 *  Utility functions
**********************/

/**
 * 유효 시간을 정제해서 content 배열을 반환하는 함수
 * 
 * @param {Array} contents - content 배열
 * @returns {Array} 유효 시간으로 오름차순 정렬된 contents 배열
*/ 
function getValidStartTime(contents) {
    // 1. 배열에서 유효 시간 중 첫 번째 값을 반환
    return contents.map(content => {
        const validStartTimes = content.contentStartTimes
        .map(contentStartTime => new Date(contentStartTime.contentStartTime)) // Date 객체 변환
        .filter(contentStartTime => contentStartTime > new Date()) // 유효 시간 필터링
        
        return {
            ...content,
            contentStartTimes: validStartTimes[0],
        };
    // 2. 시간을 오름차순 정렬
    }).sort((a, b) => {
        const timeA = a.contentStartTimes instanceof Date ? a.contentStartTimes : Infinity;
        const timeB = b.contentStartTimes instanceof Date ? b.contentStartTimes : Infinity;
        return timeA - timeB;
    // 3. 일정에 따라 메시지 처리
    }).map(content => {
        return {
            ...content,
            contentStartTimes: formatRemainingTime(content.contentStartTimes),
        }
    });
}

/**
 * 남은 시간에 따른 데이터를 반환하는 함수
 * 
 * @param {Date} startTime - 시작 시간
 * @returns {String | Date} '출현 대기 중...' || 유효 시간
 */
function formatRemainingTime(startTime) {
    // 1. 남은 일정이 없을 경우
    if (!startTime || startTime.length === 0) {
        return '출현 대기 중...';
    }

    // 2. 당일 출현 컨텐츠인 경우
    if (isToday(startTime)) {
        return startTime;
    }

    // 3. 익일 오전 6시 출현 예정인 경우
    if (isNextDay(startTime)) {
        return `익일 ${startTime.getHours().toString().padStart(2, '0')}:${startTime.getMinutes().toString().padStart(2, '0')} 출현 예정`;
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
 * @param {int} contentNumber - content의 number
 * @param {String} formattedTime - 00:00:00 형식의 문자열
 * @param {String} selector - 렌더링할 컨테이너의 dom 선택자
 */
function updateContentTime(contentNumber, formattedTime, selector = '.content-container') {
    const $remainTimeDom = $(`${selector} #content-${contentNumber} .remain-time`);
    if ($remainTimeDom) {
        $remainTimeDom.text(formattedTime);
    }
}

/**
 * 컨텐츠 이름을 매핑 테이블 데이터로 대체하는 함수
 * 
 * @param {Array} contents - contents 배열
 * @returns 컨텐츠 이름이 대체된 contents 배열
 */
function replaceNames(contents) {
    return contents.map(content => ({
        ...content,
        contentName: nameMapping[content.contentName] || content.contentName,
    }))
}

/**
 * 중복 컨텐츠를 제거한 후 컨텐츠명을 변환하는 함수
 * 
 * @param {Array} contents - contents 배열
 * @returns {Array} 수정된 contents 배열
 */
function removeDuplicateContent(contents) {
    // 1. 중복 확인 플래그 생성
    let duplicateChaosGate = false;

    // 2. 중복 컨텐츠 제거
    return contents.filter(content => {
        if (content.contentCategory === '카오스게이트') {
            if (!duplicateChaosGate) {
                duplicateChaosGate = true;
                return true;
            }
            return false;
        }
        return true;
    })
    // 3. 컨텐츠명 변환
    .map(content => {
        if (content.contentCategory === '카오스게이트') {
            return { ...content, contentName: '카오스게이트' }; // 새 객체로 반환
        }
        return content;
    });
}

/**
 * 특정 개수의 원소를 가지는 contents 배열을 반환하는 함수
 * 
 * @param {Array} contents - contents 배열
 * @param {number} amount - 배열의 원소 개수
 * @returns 특정 개수의 원소를 가진 contents 배열
 */
function getFirstFiveContents(contents, amount) {
    return contents.slice(0, amount);
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
 * @param {string} [selectorType='main'] - 선택자의 종류 (초기값: 'main')
 */
function startTimer(contents, onTick, onComplete, selectorType) {
    // 기존 타이머 확인 및 중지
    if (selectorType === 'main') {
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
                onTick(content.contentNumber, formatTime(decrementTime(diff)));
            } else {
                onComplete(content.contentNumber, '---출현 중---');
            }
        });
    }, 1000);

    // 새로운 타이머 실행
    if (selectorType === 'main') {
        console.log('메인 타이머를 실행합니다.');
        mainTimer = timer;
    }
    if (selectorType === 'modal') {
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
