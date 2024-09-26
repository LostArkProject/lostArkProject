/********************
 * modal 관련 로직 
 *******************/
// 웹페이지 로드 후 알림 설정 modal 로직을 동적으로 추가
(($) => {
    'use strict';
    const modalHTML = `
        <div class="modal fade" id="staticBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content" style="background-color: #4f4f4f;">
                    <div class="modal-header">
                        <h5 class="modal-title" id="staticBackdropLabel">알림 설정</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="remain-time-modal-body">
                        ...
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                        <button type="button" class="btn btn-primary">저장</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    $('body').append(modalHTML);
})(jQuery);

// Show all 버튼 클릭할 시 modal 창 띄움
$('#remain-time-list').on('click', (event) => {
    event.preventDefault();
    $('#staticBackdrop').modal('show');
});


/**************************
 * remain time 관련 로직
 *************************/
$(document).ready(function() {
    fetchCalendar();

    // 1초마다 데이터 갱신
    setInterval(fetchCalendar, 1000);
});

function fetchCalendar() {
    $.ajax({
        url: '/remaintimes', // API 엔드포인트
        method: 'GET',

        // 데이터를 잘 받아오면 success 실행, 실패하면 error 실행
        success: function(data) {
            updateRemainTime(data);
            updateNotice(data);
        },
        error: function(err) {
            console.error("Error fetching calendar", err);
        }
    });
}

// Remain Time 함수
function updateRemainTime(remainTimes) {
    // 메인 페이지의 Remain Time 칸에 출력되는 남은 시간
    const $calendarDiv = $('#calendar');
    $calendarDiv.empty();

    Object.entries(remainTimes).forEach(([key, value]) => {
        $calendarDiv.append(`
            <div class="d-flex border-bottom py-3">
                <div class="w-100 ms-3">
                    <div class="d-flex">
                        <img class="rounded-circle flex-shrink-0" src="${value.ContentsIcon}" alt="" style="width: 40px; height: 40px;">
                        <div class="text-start ms-3">
                            <h6 class="mb-0">${key} - ${value.ContentsName}</h6>
                            <small>Remain Time: ${value.remainTime}</small>
                        </div>
                    </div>
                </div>
            </div>
        `);
    });

    // Remain Time의 Show All 클릭하면 나오는 modal에 출력되는 남은 시간
    const $remainTimeBody = $('#remain-time-modal-body');
    $remainTimeBody.empty();

    Object.entries(remainTimes).forEach(([key, value]) => {
        $remainTimeBody.append(`
            <div class="d-flex border-bottom py-3">
                <div class="w-100 ms-3">
                    <div class="d-flex">
                        <img class="rounded-circle flex-shrink-0" src="${value.ContentsIcon}" alt="" style="width: 40px; height: 40px;">
                        <div class="text-start ms-3">
                            <h6 class="mb-0">${key} - ${value.ContentsName}</h6>
                            <small>Remain Time: ${value.remainTime}</small>
                        </div>
                    </div>
                </div>
            </div>
        `);
    });
};

// 알림 설정 함수
function updateNotice(remainTimes) {
    if (remainTimes['카오스게이트']) {
        $('span:contains("카오스게이트")')
        .next().text(remainTimes['카오스게이트'].remainTime);
    }

    if (remainTimes['필드보스']) {
        $('span:contains("필드보스")')
        .next().text(remainTimes['필드보스'].remainTime);
    }

    if (remainTimes['모험 섬']) {
        $('span:contains("모험 섬")')
        .text('모험 섬 (' + remainTimes['모험 섬'].ContentsName + ')')
        .next().text(remainTimes['모험 섬'].remainTime);
    }
}

