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

// 웹페이지 로드 후에 실행되는 코드
// $(() => {
//     initFunction();
// });

// // 초기화 함수 (비동기 함수의 순서를 제어)
// async function initFunction() {
//     try {
//         await fetchContent(); // 초기 캘린더 데이터 로드
//     } catch(e) {
//         console.error('initFunction() Error', e);
//     }
// }

// 외부 api에서 데이터를 받아와서 db에 저장하라고 서버에 명령하는 함수
function fetchContent() {
    try {
        $.ajax({
            url: '/content/fetch',
            type: 'GET',
        })
        .success(function(data) {
            console.log(`data: ${data}`);
        });
        // await fetchContent();
    } catch(e) {
        console.error('fetchContent() Error', e);
    }
}

// 서버에서 캘린더 데이터를 받아오는 함수
function getContent() {
    try {
        $.ajax({
            url: '/content/all',
            method: 'GET',
            success: (res) => {
                console.log(res);
            },
            error: (xhr, status, error) => {
                console.error(xhr.statusText + '\n\n' + xhr.responseText);
            }
        });
    } catch (e) {
        console.error('Error get content', e);
        return [];
    }
};

// 캘린더 데이터로 DOM을 구성하는 함수
function addContentHTML(data) {
    const $contentDiv = $('#content');
    const $remainTimeBody = $('#remain-time-modal-body');

    $contentDiv.empty();
    $remainTimeBody.empty();

    const contentHTML = data.map(content => {
        let itemsHTML = content.items.map(item => `
                <p>name === ${item.name}</p>
                <p>icon === ${item.icon}</p>
                <img src="${item.icon}" alt="itemIcon" />
                <p>grade === ${item.grade}</p>
        `).join('');

        return `
            <div class="d-flex border-bottom py-3">
                <div class="w-100 ms-3">
                    <div class="d-flex">
                        <img class="rounded-circle flex-shrink-0" src="${content.contentsIcon}" alt="" style="width: 40px; height: 40px;">
                        <div class="text-start ms-3">
                            <h6 class="mb-0">${content.contentsName}</h6>
                            <small id="remain-time-${content.sanitizedContentsName}"></small>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    $contentDiv.append(contentHTML);
    $remainTimeBody.append(contentHTML);
};

// 남은 시간을 1초마다 갱신하는 함수
function updateRemainTime(content) {
    const clientTime = new Date().getTime();                        // 클라이언트 시간
    const serverTime = new Date(content.serverTime).getTime();  // 서버 시간
    let timeOffset = clientTime - serverTime;           // 서버와 클라이언트의 시간 차이

    setInterval(() => {
        const currentclientTime = new Date().getTime();       // 클라이언트 현재 시간
        const adjustedTime = currentclientTime - timeOffset;  // 서버 시간에 맞춘 현재 시간
        const nextStartTime = getValidStartTime(currentclientTime, content.startTimes);  // 현재 시간과 가장 가까운 일정
        const remainTime = Math.floor((nextStartTime - adjustedTime) / 1000);  // 남은 시간 계산

        // 각 컨텐츠의 남은 시간 태그를 갱신
        $(`#remain-time-${content.sanitizedContentsName}`).text(`Remain Time: ${convertRemainTime(remainTime)}`);
    }, 1000);
}

// 가장 가까운 시작 시간을 반환
function getValidStartTime(currentclientTime, startTimes) {
    for (let element of startTimes) {
        let startTime = new Date(element).getTime();

        if(startTime - currentclientTime > 0) {
            return startTime;
        }
    }

    return 0;
}

// 초 단위의 시간을 "hh:mm:ss"로 변환
function convertRemainTime(totalSeconds) {
    if (totalSeconds > 60 * 60 * 24 || totalSeconds < 0) {
        return `출현 대기 중`;
    };

    let hours = Math.floor(totalSeconds / 3600);
    totalSeconds %= 3600;
    let minutes = Math.floor(totalSeconds / 60);
    totalSeconds %= 60;
    let seconds = Math.floor(totalSeconds);

    // 각 단위를 2자리로 포맷팅
    // ex) 5:3:20이라면 05:03:20으로 포맷팅
    const formattedHours = String(hours).padStart(2, '0');
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(seconds).padStart(2, '0');

    return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
}

// let timeOffset;   // 서버와 클라이언트의 시간 차이

// // 1초마다 갱신
// function updateContent(content) {
//     setInterval(() => {
//         addContentHTML(content);
//     }, 1000);
// }

// // 웹페이지 로드 후에 실행되는 코드
// $(() => {
//     $.ajax({
//         url: '/content/fetch',
//         type: 'GET',
//         success: function() {
//             getContent();     // 필요한 데이터만 담는 dto 객체를 통해 데이터를 받아오는 함수
//         },
//         error: function(xhr, status, error) {
//             console.log(error);
//         }
//     });
// });

// function getContent() {
//     $.ajax({
//         url: '/content', // API 엔드포인트
//         method: 'GET',
//         success: function(content) {
//             const clientTime = new Date().getTime();                        // 클라이언트 시간
//             const serverTime = new Date(content[0].serverTime).getTime();  // 서버 시간
//             timeOffset = clientTime - serverTime;           // 서버와 클라이언트의 시간 차이

//             addContentHTML(content); // 캘린더 데이터로 DOM 구성
//             updateContent(content);  // 1초마다 캘린더를 갱신하는 함수
//             updateNotice(content);
//         },
//         error: function(err) {
//             console.error("Error fetching content", err);
//         }
//     });
// };

// function addContentHTML(data) {
//     const $contentDiv = $('#content');
//     const $remainTimeBody = $('#remain-time-modal-body');

//     $contentDiv.empty();
//     $remainTimeBody.empty();

//     data.forEach(content => {
//         let itemsHTML = '';

//         content.items.forEach(item => {
//             itemsHTML += `
//                 <p>name === ${item.name}</p>
//                 <p>icon === ${item.icon}</p>
//                 <img src="${item.icon}" alt="itemIcon" />
//                 <p>grade === ${item.grade}</p>
//             `;
//         });

//         const currentclientTime = new Date().getTime();       // 클라이언트 현재 시간
//         const adjustedTime = currentclientTime - timeOffset;  // 서버 시간에 맞춘 현재 시간
//         const nextStartTime = getValidStartTime(currentclientTime, content.startTimes);  // 현재 시간과 가장 가까운 일정
//         const remainTime = Math.floor((nextStartTime - adjustedTime) / 1000);  // 남은 시간 계산

//         let contentHTML = `
//             <div class="d-flex border-bottom py-3">
//                 <div class="w-100 ms-3">
//                     <div class="d-flex">
//                         <img class="rounded-circle flex-shrink-0" src="${content.contentsIcon}" alt="" style="width: 40px; height: 40px;">
//                         <div class="text-start ms-3">
//                             <h6 class="mb-0">${content.contentsName}</h6>
//                             <small>Remain Time: ${convertRemainTime(remainTime)}</small>
//                         </div>
//                     </div>
//                 </div>
//             </div>
//         `;
//         $contentDiv.append(contentHTML);
//         $remainTimeBody.append(contentHTML);
//     });
// };

// // 초 단위의 시간을 "hh:mm:ss"로 변환
// function convertRemainTime(totalSeconds) {
//     let isOver24 = totalSeconds > 60 * 60 * 24;
//     let convertTime;

//     if(!isOver24) {
//         let hours = Math.floor(totalSeconds / 3600);
//         totalSeconds %= 3600;
//         let minutes = Math.floor(totalSeconds / 60);
//         totalSeconds %= 60;
//         let seconds = Math.floor(totalSeconds);

//         convertTime = `${hours}:${minutes}:${seconds}`;
//     } else {
//         convertTime = `출현 대기 중`;
//     }

//     return convertTime;
// }

// //  가장 빠른 날짜를 받아오는 함수
// function getValidStartTime(currentclientTime, startTimes) {
//     let prop;
//     let i = 0;

//     for (prop in startTimes) {
//         let startTime = new Date(startTimes[prop]).getTime();

//         if(startTime - currentclientTime > 0) {
//             return startTime;
//         }

//         i++;
//     }

//     return 0;
// }

// // 알림 설정 함수
// function updateNotice(remainTimes) {
//     if (remainTimes['카오스게이트']) {
//         $('span:contains("카오스게이트")')
//         .next().text(remainTimes['카오스게이트'].remainTime);
//     }

//     if (remainTimes['필드보스']) {
//         $('span:contains("필드보스")')
//         .next().text(remainTimes['필드보스'].remainTime);
//     }

//     if (remainTimes['모험 섬']) {
//         $('span:contains("모험 섬")')
//         .text('모험 섬 (' + remainTimes['모험 섬'].ContentsName + ')')
//         .next().text(remainTimes['모험 섬'].remainTime);
//     }
// }
    



// $(document).ready(function() {
//     fetchContent();

//     // 1초마다 데이터 갱신
//     setInterval(fetchContent, 1000);
// });

// function fetchContent() {
//     $.ajax({
//         url: '/getcal-with-remaintimes', // API 엔드포인트
//         method: 'GET',

//         // 데이터를 잘 받아오면 success 실행, 실패하면 error 실행
//         success: function(data) {
//             updateRemainTime(data);
//             updateNotice(data);
//         },
//         error: function(err) {
//             console.error("Error fetching content", err);
//         }
//     });
// }

// // Remain Time 함수
// function updateRemainTime(remainTimes) {
//     // 메인 페이지의 Remain Time 칸에 출력되는 남은 시간
//     const $contentDiv = $('#content');
//     $contentDiv.empty();

//     Object.entries(remainTimes).forEach(([key, value]) => {
//         $contentDiv.append(`
//             <div class="d-flex border-bottom py-3">
//                 <div class="w-100 ms-3">
//                     <div class="d-flex">
//                         <img class="rounded-circle flex-shrink-0" src="${value.ContentsIcon}" alt="" style="width: 40px; height: 40px;">
//                         <div class="text-start ms-3">
//                             <h6 class="mb-0">${key} - ${value.ContentsName}</h6>
//                             <small>Remain Time: ${value.remainTime}</small>
//                         </div>
//                     </div>
//                 </div>
//             </div>
//         `);
//     });

//     // Remain Time의 Show All 클릭하면 나오는 modal에 출력되는 남은 시간
//     const $remainTimeBody = $('#remain-time-modal-body');
//     $remainTimeBody.empty();

//     Object.entries(remainTimes).forEach(([key, value]) => {
//         $remainTimeBody.append(`
//             <div class="d-flex border-bottom py-3">
//                 <div class="w-100 ms-3">
//                     <div class="d-flex">
//                         <img class="rounded-circle flex-shrink-0" src="${value.ContentsIcon}" alt="" style="width: 40px; height: 40px;">
//                         <div class="text-start ms-3">
//                             <h6 class="mb-0">${key} - ${value.ContentsName}</h6>
//                             <small>Remain Time: ${value.remainTime}</small>
//                         </div>
//                     </div>
//                 </div>
//             </div>
//         `);
//     });
// };