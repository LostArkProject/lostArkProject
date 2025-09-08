import { valid } from './validation.js';
import { getRequest } from './api.js';

// ======================================================== //
//                         초기화                            //
// ======================================================== //
let imageApiTimer = null;
let resetTimer = null;

// 초기 이미지를 페이지에 렌더링
function renderDefaultImage() {
    $('.image-container').empty();

    const imgDom = `
        <img class="w-100 mb-4" src="/img/default_loacon${Math.ceil(Math.random() * 3)}.png" style="max-width:550px; max-height:550px;" />
        <p>닉네임 입력 후에 인증 요청해주세요.</p>
    `;
    $('.image-container').html(imgDom);
}

// ======================================================== //
//                       이미지 요청                         //
// ======================================================== //
// 등급별 배경색 설정
function selectBackground(grade) {
    if(grade === '일반') return '#595959';
    if(grade === '고급') return '#3C5B13';
    if(grade === '희귀') return '#113E5E';
    if(grade === '영웅') return '#4F1464';
    if(grade === '전설') return '#B06900';
    if(grade === '유물') return '#AA4101';
    if(grade === '고대') return '#DFC786';
    if(grade === '에스더') return '#3EF5EB';

    return '';
}

async function checkCertification(nickname) {
    return getRequest(`/member/${nickname}/checkCertification`);
}

// 캐릭터 이미지 불러오기
async function fetchCharacterImage(nickname) {
    return getRequest(`/member/${nickname}/certification`);
}

// 캐릭터 이미지를 페이지에 렌더링
function renderCharacterImage(data) {
    console.log(data);
    
    $('.image-container').empty();
    
    if(data) {
        const characterImage = data.characterImage.CharacterImage;
        const equipment = data.equipment;

        const part = ['무기', '투구', '상의', '하의', '장갑', '어깨', '나침반', '목걸이', '귀걸이1', '귀걸이2', '반지1', '반지2', '팔찌', '어빌리티 스톤'];

        // 캐릭터 이미지 표시
        let imgDomPart = ''
        let imgDom = ''
        for (let i = 0; i < part.length; i++) {
            // top 위치 계산: (i / 8 * 100)%, 필요하다면 소수점 반올림 등 처리
            let topPercent;
            let horizontal;
            if (i>6) {
                topPercent = ((i-6) / 8 * 100).toFixed(2) + '%';
                horizontal = 'calc(50% + 30%)';
            } else {
                topPercent = ((i+1) / 8 * 100).toFixed(2) + '%';
                horizontal = 'calc(50% - 30%)';
            }
            imgDomPart += `
                <div class="position-absolute" style="width:36px; height:36px; left: ${horizontal}; top: ${topPercent}; transform:translate(-50%, -50%);">
                    <img src="${equipment[part[i]].icon}" style="width:100%; height:100%; border-radius:3px; opacity:${equipment[part[i]].unequippedRequired == true ? '0.3' : '1' }; background-color:${selectBackground(equipment[part[i]].grade)}" />
                    ${equipment[part[i]].unequippedRequired
                        ? '<div class="position-absolute" style="left:15px; top:-20px; font-size:30px; font-weight:bold; background:linear-gradient(225deg, #ff3d3d, #ffc7b6); -webkit-background-clip:text; -webkit-text-fill-color:transparent;">✔</div>'
                        : ''
                    }
                </div>
                `;
            if (i === 6) {
              imgDomPart += `
                  <img class="w-100" src="${characterImage}" />
              `;
            }
        }
        imgDom = `
            <div class="position-relative image-wrapper">
                ${imgDomPart}
            </div>
        `;
        $('.image-container').html(imgDom);

        // 해제해야 할 장비 텍스트를 삽입
        const unequippedStr = Object.entries(equipment)
            .filter(([key, value]) => value.unequippedRequired === true)
            .map(([key, value]) => key)
            .join('/');
        $('.certification-equipment').text(unequippedStr);

        // 인증 버튼 보이도록 설정
        $('.certification-container').removeClass('d-none');
    } else {
        // 캐릭터 이미지 대신 에러 이미지 표시
        const imgDom = `
            <img class="w-100 mb-4" src="/img/sad_loacon${Math.ceil(Math.random() * 4)}.png" style="max-width:550px; max-height:550px;" />
            <p>해당 캐릭터의 이미지를 불러오지 못했습니다..</p>
        `;
        $('.image-container').html(imgDom);

        // 인증 버튼 보이지 않도록 설정
        $('.certification-container').addClass('d-none');
    }
}

// 캐릭터 인증을 위한 데이터 요청 함수
async function requestCertification() {
    // 1. 과도한 api 요청 방지를 위한 스로틀링
    if(imageApiTimer) {
        alert('잠시 후 다시 시도해주세요.');
        return;
    }
    
    imageApiTimer = setTimeout(() => {
        console.log('인증요청 가능');
        imageApiTimer = null;
    }, 5000);
    
    // 2. 캐릭터 닉네임 검증
    const $nickname = $('#certificationCharacter');
    if(!valid('nickname', $nickname.val())) {
        renderCharacterImage(null);
        $nickname.focus();
        return;
    }

    // 3. 캐릭터 이미지 요청
    const data = await fetchCharacterImage($nickname.val().trim());
    if (!data.characterImage || !data.characterImage.CharacterImage || data.characterImage.CharacterImage.trim() === '') {
        renderCharacterImage(null);
        $nickname.focus();
        return;
    }

    // 4. 캐릭터 이미지를 페이지에 렌더링
    renderCharacterImage(data);
}

// ======================================================== //
//                       인증 요청                           //
// ======================================================== //
// 장비 해제 인증하기 함수
async function submitCertification() {
    // nickname 값을 꺼내고
    const nickname = $('#certificationCharacter').val().trim();

    try {
        // async/await 로 API 호출
        const data = await checkCertification(nickname);

        if (data === true) {
            alert("성공적으로 인증을 마쳤습니다.");
            $.ajax({
                    url: '/member/finishCertification',
                    type: 'Patch',
                    contentType: 'application/json',
            });
            window.location.href = "/"
        } else {
            alert("인증에 실패했습니다.");
        }
    } catch (err) {
        console.error(err);
        alert("인증 중 오류가 발생했습니다.");
    }
}

// ======================================================== //
//                       인증 초기화                         //
// ======================================================== //
// 캐릭터 인증 상태 초기화 (세션 초기화)
function resetCertificationState() {
    // 1. 과도한 api 요청 방지를 위한 스로틀링
    if(resetTimer) {
        alert('잠시 후 다시 시도해주세요.');
        return;
    }
    
    resetTimer = setTimeout(() => {
        console.log('인증 초기화 가능');
        resetTimer = null;
    }, 5000);

    // 2. 인증 데이터 초기화 요청
    $.ajax({
        url: '/member/certification/reset',
        method: 'DELETE',
        success: function(data) {
            renderDefaultImage();    // 기본 이미지 출력
            setTimeout(() => alert(data), 20);  // 이미지 렌더링 함수 실행을 기다린 후 (0.02초) 메시지 출력
        },
        error: (xhr) => {
            console.error("error: ", xhr);
        }
    });

    // 3. 인증 dom 제거
    $('.certification-container').addClass('d-none');
}

// ======================================================== //
//                        최초 실행                          //
// ======================================================== //
$(() => {
    // 페이지 처음 접근하면 인증 상태 초기화 (세션 삭제)
    $.ajax({
        url: '/member/certification/reset',
        method: 'DELETE',
        success: function(data) {
            renderDefaultImage();
        },
        error: (xhr) => {
            console.error("error: ", xhr);
        }
    });

    // 이벤트 바인딩
    $('#request-certification-button').click(requestCertification);
    $('#submit-certification-button').click(submitCertification);
    $('#reset-certification-button').click(resetCertificationState);
    $('#nickname').keydown((e) => {
        if(e.key === 'Enter') {
            requestCertification();
        }
    });
});