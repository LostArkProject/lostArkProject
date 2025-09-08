$(function(){
  // 모달이 화면에 표시되기 직전에
  $('#collectibleModal').on('show.bs.modal', function () {
    loadFullList();
  });
});

window.toggleCleared = function(checkbox) {

    const id      = $(checkbox).data('id');       // data-id 속성 읽기
    const cleared = checkbox.checked;

    //추천 내실 달성 상태 변경
    $.ajax({
      url: '/collectible/clear-status',
      method: 'PATCH',
      contentType: 'application/json; charset=UTF-8',
      data: JSON.stringify({
        recommendCollectibleID: id,
        cleared: cleared
      }),
      success: function() {
        // 성공 시 따로 할 일 없으면 그냥 리턴
      },
      error: function() {
        // 실패하면 원래 상태로 되돌리고 알림
        checkbox.checked = !cleared;
        alert('상태 업데이트에 실패했습니다.');
      }
    });
  }


$(function(){
    // 페이지 로딩 완료 후에 실행
    var modalEl = document.getElementById('collectibleModal');
    if (modalEl) {
      modalEl.addEventListener('hidden.bs.modal', function () {
        changeCollectible();
      });
    }
  });

// 내실 API 요청
$.ajax({
    url: '/collectibles',
    type: 'GET',
    success: function(data) {
        collectibleChart(data);
    },
    error: function(xhr, status, error) {
        console.error(xhr.statusText + '\n\n' + xhr.responseText);
    }
});

//내실 차트
function collectibleChart(collectibleItemList) {
    // collectibleItemList에서 Labels와 데이터 추출 (Type과 비율 계산)
    window.collectibleLabels = collectibleItemList.map(item => item.collectibleTypeName);
    window.collectibleData = collectibleItemList.map(item => (item.totalCollectedTypePoint / item.totalCollectibleTypePoint) * 100);

    // 콘솔에 전역 변수 출력
    console.log("Labels:", window.collectibleLabels);
    console.log("Data:", window.collectibleData);

    var ctx1 = $("#collectable-percent").get(0).getContext("2d");
    var myChart1 = new Chart(ctx1, {
        type: "bar",
        data: {
            // HTML에서 전달받은 전역 변수로 설정된 데이터를 사용
            labels: window.collectibleLabels || ["No Data"], // 데이터가 없을 경우 기본값 "No Data"
            datasets: [{
                label: "%",
                data: window.collectibleData || [0], // 데이터가 없으면 기본값 0
                backgroundColor: "rgba(235, 22, 22, .7)"
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100 // 100%까지 표시
                }
            }
        }
    });
}


window.escapeHtml = function(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
};

window.changeCollectible = function() {

    //추천 내실 목록 일부 조회
    $.ajax({
        url: '/collectible/getRecommendCollectibleList',
        type: 'GET',
        contentType: 'application/json',
        success: function(recommendCollectibleList) {
            const tbody = document.getElementById('recommendTbody');
            let rows = '';
            for (let i = 0; i < recommendCollectibleList.length; i++) {
                const item = recommendCollectibleList[i];
                // URL이 null이면 disabled 클래스 추가
                const disabled = item.recommendCollectibleURL ? '' : ' disabled';
                rows += `
                  <tr>
                    <td>${escapeHtml(item.recommendCollectibleName)}</td>
                    <td>
                      <a
                        class="btn btn-sm btn-primary${disabled}"
                        href="${item.recommendCollectibleURL || '#'}"
                        ${!item.recommendCollectibleURL ? 'tabindex="-1" aria-disabled="true"' : ''}
                      >URL</a>
                    </td>
                    <td>
                      <button
                          class="btn btn-sm btn-primary"
                          type="button"
                          name="collectibleId"
                          data-id="${item.recommendCollectibleID}"
                          onclick="collectibleClear(this)"
                      >완료</button>
                    </td>
                  </tr>`;
            }
            tbody.innerHTML = rows;
        }
    });
}

window.collectibleClear = function(btn) {
        const id = btn.dataset.id;
        //추천 내실 달성시
        $.ajax({
            url: '/collectible/clear',
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify({ collectibleId: id }),
            success: function() {
                changeCollectible();
            }
        });
    }

window.loadFullList = function() {
  //추천내실 전체 목록 조회
  $.ajax({
    url: '/collectible/getRecommendFullCollectibleList',   // JSON 반환용 컨트롤러 엔드포인트
    type: 'GET',
    dataType: 'json',
    success: function(recommendFullCollectibleList) {

      const tbody = document.getElementById('recommendFullTbody');
      let rows = '';
      for (let i = 0; i < recommendFullCollectibleList.length; i++) {
        const item = recommendFullCollectibleList[i];
        // URL이 null이면 disabled 클래스 추가
        const disabled = item.recommendCollectibleURL ? '' : ' disabled';
        // name, URL, ID 값을 그대로 넣으면 XSS 위험 → 반드시 escape 처리하세요!
        rows += `
          <tr>
            <td>${item.recommendCollectibleID}</td>
            <td>${item.recommendCollectibleName}</td>
            <td>
              <a class="btn btn-sm btn-primary${disabled}"
                 href="${item.recommendCollectibleURL || '#'}"
                 ${!item.recommendCollectibleURL ? 'tabindex="-1" aria-disabled="true"' : ''}>
                URL
              </a>
            </td>
            <td>
              <input type="checkbox"
                     data-id="${item.recommendCollectibleID}"
                     ${item.cleared ? 'checked' : '' }
                     onchange="toggleCleared(this)"/>
            </td>
          </tr>`;
      }
      tbody.innerHTML = rows;
    },
    error: function() {
      console.error('목록을 불러오는 중 오류 발생');
    }
  });
}