// 멘토/멘티 관련 JavaScript 코드
document.addEventListener('DOMContentLoaded', function() {
    const messageDropdown = document.getElementById("messageDropdownMenu");
    const messageBadge = document.getElementById('messageBadge');

    if (!messageDropdown) {
        console.log("Message dropdown not found");
        return;
    }

    const shownRequests = new Set(); // 중복 방지용 캐시

    // 고정 메뉴 초기화 함수 (한 번만 실행)
    function initializeFixedMenu() {
        try {
            // 고정 메뉴가 이미 있는지 확인
            if (messageDropdown.querySelector('.fixed-divider')) {
                return; // 이미 초기화됨
            }

            // 동적 메시지와 고정 메뉴 사이 구분선
            const fixedDivider = document.createElement('hr');
            fixedDivider.className = 'dropdown-divider fixed-divider';
            
            // 고정 메뉴 두 개를 새로 생성해서 추가
            const link1 = document.createElement('a');
            link1.href = '/message/list';
            link1.className = 'dropdown-item text-center';
            link1.textContent = '받은 멘토링 신청 전부보기';

            const link2 = document.createElement('a');
            link2.href = '/message/myRequest';
            link2.className = 'dropdown-item text-center';
            link2.textContent = '내가 보낸 멘토링 신청 결과';

            // 구분선 + 고정 메뉴 추가
            // messageDropdown.appendChild(fixedDivider);
            messageDropdown.appendChild(link1);
            messageDropdown.appendChild(link2);
        } catch (error) {
            console.error("Error initializing fixed menu:", error);
        }
    }

    // 동적 메시지만 제거하는 함수
    function clearDynamicMessages() {
        try {
            const fixedDivider = messageDropdown.querySelector('.fixed-divider');
            if (fixedDivider) {
                // 고정 구분선 이전의 모든 동적 메시지 제거
                while (messageDropdown.firstChild && messageDropdown.firstChild !== fixedDivider) {
                    messageDropdown.removeChild(messageDropdown.firstChild);
                }
            } else {
                // 고정 구분선이 없으면 모든 메시지 제거
                while (messageDropdown.firstChild) {
                    messageDropdown.removeChild(messageDropdown.firstChild);
                }
            }
        } catch (error) {
            console.error("Error clearing dynamic messages:", error);
        }
    }

    function updateMessages(data) {
        try {
            initializeFixedMenu(); // 고정 메뉴 초기화 (한 번만 실행)
            clearDynamicMessages(); // 동적 메시지만 제거
            shownRequests.clear(); // 상태 캐시 초기화

            if (!data || data.length === 0) {
                console.log("No requests found");
                // 알림 배지 숨기기
                if (messageBadge) {
                    messageBadge.style.display = 'none';
                    console.log("Hiding badge - no messages");
                }
                return;
            }

            console.log("Processing requests:", data);

            // REQUESTED 상태만 필터링
            const requestedMessages = data.filter(request => {
                return request && request.mentee_member_id && request.apply_status === 'REQUESTED';
            });

            console.log("Filtered REQUESTED messages:", requestedMessages.length);

            // 알림 배지 업데이트 (REQUESTED 개수만)
            if (messageBadge) {
                if (requestedMessages.length > 0) {
                    messageBadge.textContent = requestedMessages.length;
                    messageBadge.style.display = 'inline-block';
                    console.log("Updating badge with REQUESTED count:", requestedMessages.length);
                } else {
                    messageBadge.style.display = 'none';
                    console.log("Hiding badge - no REQUESTED messages");
                }
            } else {
                console.log("Message badge element not found");
            }

            if (requestedMessages.length === 0) {
                console.log("No REQUESTED messages found");
                return;
            }

            // 고정 메뉴(구분선) 앞에 동적 메시지 추가
            const fixedDivider = messageDropdown.querySelector('.fixed-divider');

            requestedMessages.forEach(request => {
                // 이미 필터링된 데이터이므로 추가 검증 불필요
                // if (!request || !request.mentee_member_id) {
                //     console.log("Invalid request data:", request);
                //     return;
                // }

                // // REQUESTED 상태인 경우만 표시
                // if (request.apply_status !== 'REQUESTED') {
                //     console.log("Skipping non-requested status:", request.apply_status);
                //     return;
                // }

                const key = request.mentee_member_id;
                if (shownRequests.has(key)) {
                    console.log("Skipping duplicate request:", key);
                    return;
                }
                shownRequests.add(key);

                // 새 메시지 항목 생성
                const newMessage = document.createElement("a");
                newMessage.href = `/message/newMessageDetail?menteeMemberId=${request.mentee_member_id}`;
                newMessage.className = "dropdown-item";

                let statusMessage = '';
                let statusColor = '';
                // REQUESTED 상태만 처리
                statusMessage = '멘토링 요청이 도착했습니다.';
                statusColor = '';

                // 다른 상태 처리 (주석처리)
                /*
                switch (request.apply_status) {
                    case 'ACCEPTED':
                        statusMessage = '멘토링이 수락되었습니다.';
                        statusColor = 'text-success';
                        break;
                    case 'REJECTED':
                        statusMessage = '멘토링이 거절당했습니다.';
                        statusColor = 'text-danger';
                        break;
                    default:
                        statusMessage = '멘토링 요청이 도착했습니다.';
                        statusColor = '';
                }
                */

                newMessage.innerHTML = `
                    <div class="d-flex align-items-center">
                        <div class="flex-grow-1">
                            <span class="fw-bold">${request.mentee_nickname || '알 수 없음'}</span>
                            <div class="small text-gray-500 ${statusColor}">
                                ${statusMessage}
                            </div>
                        </div>
                    </div>
                `;

                // 고정 구분선(fixed-divider) 앞에 동적 메시지 삽입
                if (fixedDivider) {
                    messageDropdown.insertBefore(newMessage, fixedDivider);
                } else {
                    messageDropdown.appendChild(newMessage);
                }
            });
        } catch (error) {
            console.error("Error updating messages:", error);
        }
    }

    function checkMentorRequests() {
        console.log("Checking mentor requests...");
        fetch('/message/all-applies')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Received data:", data);
                updateMessages(data);
            })
            .catch(error => {
                console.error('메시지 상태 확인 중 오류 발생:', error);
            });
    }

    // 초기 로딩 시 고정 메뉴 초기화 후 메시지 표시
    initializeFixedMenu();
    checkMentorRequests();

    // 30초마다 상태 확인
    setInterval(checkMentorRequests, 30000);
}); 