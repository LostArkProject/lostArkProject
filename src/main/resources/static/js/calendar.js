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
    let calendarDiv = $('#calendar');
    calendarDiv.empty();

    Object.entries(remainTimes).forEach(([key, value]) => {
        calendarDiv.append(`
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

