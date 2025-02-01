import { deleteRequest, getRequest, postRequest } from './api.js';

/********************
 *    Templates 
 *******************/
/** dom 템플릿 객체입니다. */
const domTemplates = {
    contentDom: (content, startTime) => `
        <div class="d-flex border-bottom py-3">
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
        </div>
    `,
    modalDom: (content, startTime) => `
        <div class="d-flex border-bottom py-3">
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
            <div class="modal-checkbox-wrapper">
                <input
                    type="checkbox"
                    class="form-check-input align-self-center"
                    id="checkbox-${content.contentNumber}"
                    name="content"
                    value="${content.contentNumber}"
                    data-content-name="${content.contentName}"
                    aria-label="알림 설정: ${content.contentName}"
                />
                <label
                    class="form-check-label ms-2 me-4"
                    for="checkbox-${content.contentNumber}"
                    style="white-space: nowrap; align-self: center;"
                ></label>
            </div>
        </div>
    `,
    alarmDom: (alarm) => `
        <div class="d-flex align-items-center border-bottom py-2">
            <div class="w-100 ms-3">
                <div class="d-flex align-items-center justify-content-between">
                    <span class="flex-grow-1">${alarm.contentName}</span>
                    <span>${alarm.memberId}</span>
                    <button class="btn btn-sm" data-content-name="${alarm.contentName}">
                        <i class="fa fa-times"></i>
                    </button>
                </div>
            </div>
        </div>
    `,
    loadingDom: () => `
        <div class="d-flex align-items-center my-4">
            <div class="spinner-border text-light m-2 mx-4" role="status">
                <span class="sr-only">Loading...</span>
            </div>
            <p class="my-0">Loading...</p>
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
        await renderContent();
        await renderAlarm();
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
const memberId = loggedInMember ? loggedInMember.memberId : null;

const dataManager = (() => {
    let contentCache = null;

    /** 컨텐츠 데이터를 가져오는 함수 */
    async function fetchContentData() {
        try {
            const response = await getRequest('/contents/start-time');
            console.log('원본 컨텐츠: ', response);
            return response;
        } catch (e) {
            console.error('데이터를 가져오는 데 실패했습니다.', e.responseText);
            return [];
        }
    }

    /** 로그인한 유저의 알림 설정 데이터를 가져오는 함수 */
    async function fetchAlarmSettingsData() {
        try {
            const response = await getRequest(`/alarm`);
            console.log('원본 알림 설정 데이터: ', response);
            return response;
        } catch (e) {
            console.error('유저의 알림 설정 데이터을 가져오는 데 실패했습니다.', e.responseText);
            return [];
        }
    }

    /** 컨텐츠 데이터를 전처리하는 함수 */
    function processContentData(contents) {
        const validContents = getValidStartTime(contents); // 유효 시간 데이터 필터링
        const renamedContents = renameContentNames(validContents); // 컨텐츠 이름 대체
        const uniqueContents = removeDuplicateContent(renamedContents); // 중복 컨텐츠 제거
        console.log('전처리된 컨텐츠 데이터: ', uniqueContents);
        return uniqueContents;
    }

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
     * 컨텐츠 이름을 매핑 테이블 데이터로 대체하는 함수
     * 
     * @param {Array} contents - contents 배열
     * @returns 컨텐츠 이름이 대체된 contents 배열
     */
    function renameContentNames(contents) {
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

    /** 알림 설정 데이터를 전처리하는 함수 */
    async function processAlarmSettingsData(alarms) {
    }

    async function fetchContent() {
        if (contentCache) {
            console.log('캐싱된 컨텐츠 데이터를 반환합니다.');
            return contentCache;
        }
        const contents = await fetchContentData();
        contentCache = processContentData(contents);
        console.log('컨텐츠 데이터를 캐싱 후 반환합니다.');
        return contentCache;
    }

    async function fetchAlarmSettings() {
        const alarms = await fetchAlarmSettingsData();
        console.log('알림 설정 데이터를 반환합니다.');
        return alarms;
    }

    function clearContentCache() {
        contentCache = null;
        console.log('컨텐츠 캐시 데이터를 초기화하였습니다.');
    }

    return {
        fetchContent,
        fetchAlarmSettings,
        clearContentCache,
    };
})();

// 페이지 초기화
$(() => {
    initFunction();
    $('.content-modal-link').on('click', handleModalOpen);
    $('[data-bs-dismiss="modal"]').on('click', modalManager.closeModal);
});

// 컨테이너 렌더링 함수
async function initFunction() {
    await renderContent();
    await renderAlarm();
}

// modal 링크(전체) 클릭 시 실행되는 함수
async function handleModalOpen(event) {
    event.preventDefault();

    await renderModal();

    $('.modal-checkbox-wrapper input[type="checkbox"]').each((idx, checkbox) => {
        const $checkbox = $(checkbox);
        const contentNumber = $checkbox.val();
        const contentName = $checkbox.data('content-name');
        const $label = $(checkbox).next();

        // 로그인과 비로그인의 텍스트를 동적으로 업데이트
        if (checkbox.disabled) {
            $label.text('');
        } else {
            $label.text($checkbox.is(':checked') ? '알림 ON' : '알림 OFF');
        }
    
        // 체크박스 상태 변경 시 동적으로 업데이트
        $checkbox.on('change', function() {
            if ($checkbox.is(':checked')) {
                $checkbox.prop('checked', true);
                $label.text('알림 ON');
                updateAlarmSetting(contentNumber, contentName);
            } else {
                $checkbox.prop('checked', false);
                $label.text('알림 OFF');
                deleteAlarmSetting(contentName);
            }
        });
    });

    modalManager.openModal();
}

// 알림 삭제 버튼 클릭 시 실행되는 함수
$(document).on('click', '.btn[data-content-name]', async function() {
    const contentName = $(this).data('content-name');
    console.log('삭제 버튼 클릭: ' + contentName);
    await deleteAlarmSetting(contentName);
    await renderAlarm();
});

/******************************
 *     Business functions
 ******************************/

/** 컨텐츠 컨테이너 렌더링 함수 */
async function renderContent() {
    const $contentContainer = $('.content-container');
    $contentContainer.html(domTemplates.loadingDom());

    // 데이터 가져오기
    const contents = await dataManager.fetchContent();

    // 컨텐츠 데이터 페이징
    const firstFiveContents = getFirstNElements(contents, 5);

    // dom 생성
    const contentsDom = firstFiveContents
        .map(content => domTemplates.contentDom(content, domTemplates.loadingDom()))
        .join('');

    // 컨테이너 렌더링
    $contentContainer.html(contentsDom);

    // 시작 시간이 Date 타입인 데이터만 필터링한 배열을 생성
    const validContents = firstFiveContents.filter(content => {
        if (!(content.contentStartTimes instanceof Date)) {
            updateContentTime(content.contentNumber, content.contentStartTimes);
            return false;
        }
        return true;
    });

    // 타이머 시작
    setContentCountdownTimer(
        validContents,
        (id, formattedTime) => updateContentTime(id, formattedTime),
        (id, finalTime) => updateContentTime(id, finalTime),
    );
}

/** 모달 컨테이너 렌더링 함수 */
async function renderModal() {
    const $modalContainer = $('.modal-container');
    $modalContainer.html(domTemplates.loadingDom());

    // 데이터 가져오기
    const contents = await dataManager.fetchContent();
    const alarms = await dataManager.fetchAlarmSettings();

    // 알림 설정된 컨텐츠명 배열 생성
    const alarmContentNames = alarms.map(alarm => alarm.contentName);

    // dom 생성
    const contentsDom = contents.map(content => {
        const isChecked = alarmContentNames.includes(content.contentName);
        return domTemplates.modalDom(content, domTemplates.loadingDom())
            .replace(
                `type="checkbox"`,
                `type="checkbox" ${memberId ? '' : 'disabled'} ${isChecked ? 'checked' : ''}`
            )
    }).join('');

    // 컨테이너 렌더링
    $modalContainer.html(contentsDom);

    // 유효한 데이터만 필터링
    const validContents = contents.filter(content => {
        if (!(content.contentStartTimes instanceof Date)) {
            updateContentTime(content.contentNumber, content.contentStartTimes);
            return false;
        }
        return true;
    });

    // 타이머 시작
    setContentCountdownTimer(
        validContents,
        (id, formattedTime) => updateContentTime(id, formattedTime),
        (id, finalTime) => updateContentTime(id, finalTime),
    );
}

/** 알림 컨테이너 렌더링 함수 */
async function renderAlarm() {
    const $alarmWrapper = $('.alarm-wrapper');
    $alarmWrapper.html(domTemplates.loadingDom());
    
    try {
        const alarms = await dataManager.fetchAlarmSettings();
        
        if (!memberId) {
            $alarmWrapper.html('<p class="text-center">로그인이 필요합니다.</p>');
            return;
        } else if (!alarms || alarms.length === 0) {
            $alarmWrapper.html('<p class="text-center">알림 설정한 컨텐츠가 존재하지 않습니다.</p>');
        } else {
            const alarmHTML = alarms.map(alarm => domTemplates.alarmDom(alarm)).join('');
            $alarmWrapper.html(alarmHTML);
        }
    } catch (e) {
        console.error('알림 컨테이너를 렌더링하는 데 실패했습니다: ', e);
    }
}

/**
 * 컨텐츠명으로 알림 설정을 갱신하는 함수
 * 
 * @param {int} contentNumber - 컨텐츠 대리 키
 * @param {string} contentName - 컨텐츠명
 */
async function updateAlarmSetting(contentNumber, contentName) {
    try {
        const response = await postRequest(`/alarm`, {
            contentNumber: contentNumber,
            contentName: contentName,
        });
        console.log(response);
    } catch (e) {
        console.error('알림 설정 실패: ', e.responseText);
    }
}

/**
 * 특정 컨텐츠의 알람 설정을 해제하는 함수
 * 
 * @param {int} contentName - 컨텐츠명
 */
async function deleteAlarmSetting(contentName) {
    try {
        const response = await deleteRequest(`/alarm/${contentName}`);
        console.log(response);
    } catch (e) {
        console.error('알림 설정 해제 실패: ', e.responseText);
    }
}

/**********************
 *  Utility functions
**********************/





/**
 * ui의 남은 시간을 갱신하는 함수
 * 
 * @param {int} contentNumber - content의 number
 * @param {String} formattedTime - 00:00:00 형식의 문자열
 * @param {String} selector - 렌더링할 컨테이너의 dom 선택자
 */
function updateContentTime(contentNumber, formattedTime) {
    const $remainTimeDom = $(`#content-${contentNumber} .remain-time`);
    if ($remainTimeDom) {
        $remainTimeDom.text(formattedTime);
    }
}

/**
 * 특정 개수의 원소를 가지는 contents 배열을 반환하는 함수
 * 
 * @param {Array} array - 배열
 * @param {number} n - 배열의 원소 개수
 * @returns 앞 n개의 데이터를 가진 배열
 */
function getFirstNElements(array, n) {
    return array.slice(0, n);
}

/** @type {Function | null} */
let contentTimer = null;

/**
 *  컨텐츠 타이머 함수
 * 
 * @param {Array} contents - 초기 컨텐츠 데이터
 * @param {Function} onTick - 매초 호출되는 함수
 * @param {Function} onComplete - 타이머 종료 시 호출되는 함수
 */
function setContentCountdownTimer(contents, onTick, onComplete) {
    // 초기화
    clearInterval(contentTimer);
    contentTimer = null;

    // 타이머 이벤트 설정
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

    // 타이머 실행
    if (!contentTimer) {
        console.log('컨텐츠 타이머를 실행합니다.');
        contentTimer = timer;
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
