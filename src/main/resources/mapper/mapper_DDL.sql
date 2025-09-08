CREATE
DATABASE LOSTARKPROJECT;
USE
LOSTARKPROJECT;



-- 멤버 테이블
CREATE TABLE MEMBER
(
    MEMBER_ID                         VARCHAR(50) PRIMARY KEY,
    MEMBER_PASSWD                     VARCHAR(255) NOT NULL,
    REGISTRATION_DATE                 DATETIME     NOT NULL DEFAULT NOW(),
    REPRESENTATIVE_CHARACTER_NICKNAME VARCHAR(50)  NOT NULL,
    VERIFIED_NICKNAME                 VARCHAR(50) NULL UNIQUE
);

-- 캐릭터 테이블
CREATE TABLE MEMBER_CHARACTER
(
    CHARACTER_NICKNAME VARCHAR(50) PRIMARY KEY,
    SERVER_NAME        VARCHAR(50) NOT NULL,
    CHARACTER_CLASS    VARCHAR(50) NOT NULL,
    ITEM_LEVEL         VARCHAR(50) NOT NULL,
    ROSTER_LEVEL       VARCHAR(50) NOT NULL,
    MEMBER_ID          VARCHAR(50),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID) ON DELETE CASCADE
);

-- 수집 테이블
CREATE TABLE COLLECTIBLE_POINT
(
    MEMBER_ID              VARCHAR(50),
    COLLECTIBLE_TYPE_NAME  VARCHAR(50)  NOT NULL,
    COLLECTED_POINT        INT          NOT NULL,
    COLLECTIBLE_MAX_POINT  INT          NOT NULL,
    COLLECTIBLE_POINT_NAME VARCHAR(50)  NOT NULL,
    COLLECTIBLE_ICON_LINK  VARCHAR(255) NOT NULL,
    PRIMARY KEY (COLLECTIBLE_TYPE_NAME, COLLECTIBLE_POINT_NAME, MEMBER_ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID) ON DELETE CASCADE
);

-- 컨텐츠 테이블
CREATE TABLE CONTENT
(
    CONTENT_NUMBER    INT AUTO_INCREMENT PRIMARY KEY,
    CONTENT_NAME      VARCHAR(100) NOT NULL,
    CONTENT_ICON_LINK VARCHAR(255) NOT NULL,
    MIN_ITEM_LEVEL    INT          NOT NULL,
    CONTENT_LOCATION  VARCHAR(100) NOT NULL,
    CONTENT_CATEGORY  VARCHAR(50)  NOT NULL
);

-- 알람 설정 테이블
CREATE TABLE ALARM
(
    MEMBER_ID      VARCHAR(50),
    CONTENT_NUMBER INT,
    CONTENT_NAME   VARCHAR(100),
    PRIMARY KEY (MEMBER_ID, CONTENT_NAME),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID) ON DELETE CASCADE
);

-- 컨텐츠 시작시간
CREATE TABLE START_TIME
(
    CONTENT_START_TIME DATETIME,
    CONTENT_NUMBER     INT,
    PRIMARY KEY (CONTENT_START_TIME, CONTENT_NUMBER),
    FOREIGN KEY (CONTENT_NUMBER) REFERENCES CONTENT (CONTENT_NUMBER) ON DELETE CASCADE
);

-- 보상 테이블
CREATE TABLE REWARD
(
    REWARD_NUMBER         INT AUTO_INCREMENT PRIMARY KEY,
    CONTENT_NUMBER        INT          NOT NULL,
    REWARD_ITEM_NAME      VARCHAR(100) NOT NULL,
    REWARD_ITEM_LEVEL     INT          NOT NULL,
    REWARD_ITEM_ICON_LINK VARCHAR(255) NOT NULL,
    REWARD_ITEM_GRADE     VARCHAR(50)  NOT NULL,
    FOREIGN KEY (CONTENT_NUMBER) REFERENCES CONTENT (CONTENT_NUMBER) ON DELETE CASCADE
);

-- 멘토
CREATE TABLE MENTOR
(
    MENTOR_MEMBER_ID   VARCHAR(50),
    MENTOR_DISCORD_ID  VARCHAR(50) NOT NULL,
    MENTOR_WANT_TO_SAY VARCHAR(255),
    PRIMARY KEY (MENTOR_MEMBER_ID),
    FOREIGN KEY (MENTOR_MEMBER_ID) REFERENCES MEMBER (MEMBER_ID) ON DELETE CASCADE
);

-- 멘토 컨텐츠 다중선택 위한 테이블
CREATE TABLE MENTOR_CONTENT
(
    MENTOR_MEMBER_ID  VARCHAR(50),
    MENTOR_CONTENT_ID VARCHAR(100),
    PRIMARY KEY (MENTOR_MEMBER_ID, MENTOR_CONTENT_ID),
    FOREIGN KEY (MENTOR_MEMBER_ID) REFERENCES MENTOR (MENTOR_MEMBER_ID) ON DELETE CASCADE
);


-- 멘티
CREATE TABLE MENTEE_APPLY
(
    apply_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    MENTOR_MEMBER_ID VARCHAR(50),
    MENTEE_MEMBER_ID VARCHAR(50),
    apply_status     VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    rejected_reason   VARCHAR(255),
    FOREIGN KEY (MENTOR_MEMBER_ID) REFERENCES MENTOR (MENTOR_MEMBER_ID) ON DELETE CASCADE,
    FOREIGN KEY (MENTEE_MEMBER_ID) REFERENCES MEMBER (MEMBER_ID) ON DELETE CASCADE
);

-- 멘티 신청 컨텐츠(멘토에게 전송되는 페이지)
CREATE TABLE MENTEE
(
    MENTEE_MEMBER_ID  VARCHAR(50),
    MENTEE_CONTENT_ID VARCHAR(100),
    PRIMARY KEY (MENTEE_MEMBER_ID, MENTEE_CONTENT_ID),
    FOREIGN KEY (MENTEE_MEMBER_ID) REFERENCES MENTEE_APPLY (MENTEE_MEMBER_ID) ON DELETE CASCADE
);


-- 멘토에게 차단당한 멘티 목록 테이블
CREATE TABLE DISABLE_MENTEE (
    MENTOR_MEMBER_ID VARCHAR(50) NOT NULL,
    MENTEE_MEMBER_ID VARCHAR(50) NOT NULL,
    DISABLED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MENTOR_MEMBER_ID, MENTEE_MEMBER_ID),
    FOREIGN KEY (MENTOR_MEMBER_ID) REFERENCES MENTOR(MENTOR_MEMBER_ID) ON DELETE CASCADE,
    FOREIGN KEY (MENTEE_MEMBER_ID) REFERENCES MEMBER(MEMBER_ID) ON DELETE CASCADE
);




-- 학원 게시글
CREATE TABLE ACADEMY_BOARD
(
    ACADEMY_BOARD_NUMBER INT AUTO_INCREMENT PRIMARY KEY,
    WRITER               VARCHAR(50)  NOT NULL,
    TITLE                VARCHAR(100) NOT NULL,
    CONTENT              VARCHAR(500) NOT NULL,
    RAID                 VARCHAR(50)  NOT NULL,
    IMAGE                VARCHAR(100),
    CREATED_AT           DATETIME     NOT NULL DEFAULT NOW(),
    FOREIGN KEY (WRITER) REFERENCES MEMBER (VERIFIED_NICKNAME) ON UPDATE CASCADE
);

-- 어비스 던전 목록
CREATE TABLE ABYSS_DUNGEON_LIST
(
    `CODE`       VARCHAR(20) PRIMARY KEY COMMENT "aby-6",
    ABYSS_NAME   VARCHAR(20) NOT NULL COMMENT "카양겔",
    BOSS_NAME    VARCHAR(20) COMMENT "라우리엘",
    LOCATION     VARCHAR(30) COMMENT "영원한 빛의 요람",
    LEVEL_SINGLE INT COMMENT "1540",
    LEVEL_NORMAL INT COMMENT "1540",
    LEVEL_HARD   INT COMMENT "1580"
);

-- 레이드 목록
CREATE TABLE RAID_LIST
(
    `CODE`       VARCHAR(20) PRIMARY KEY COMMENT "com-1",
    BOSS_NAME    VARCHAR(20) NOT NULL COMMENT "발탄",
    BOSS_ROLE    VARCHAR(20) COMMENT "마수군단장",
    LOCATION     VARCHAR(30) COMMENT "부활한 마수의 심장",
    LEVEL_SINGLE INT COMMENT "1415",
    LEVEL_NORMAL INT COMMENT "1415",
    LEVEL_HARD   INT COMMENT "1445"
);

-- 공지사항
CREATE TABLE NOTICE
(
    NOTICE_NUMBER INT AUTO_INCREMENT PRIMARY KEY,
    TITLE         VARCHAR(50)   NOT NULL,
    CONTENT       VARCHAR(1000) NOT NULL,
    IMAGE         VARCHAR(100),
    CREATED_AT    DATETIME      NOT NULL DEFAULT NOW()
);

-- 어드민
CREATE TABLE ADMIN
(
    ADMIN_ID     VARCHAR(50) PRIMARY KEY,
    ADMIN_PASSWD VARCHAR(255)
);

-- 추천 내실
CREATE TABLE RECOMMEND_COLLECTIBLE
(
    RECOMMEND_COLLECTIBLE_ID   INT AUTO_INCREMENT PRIMARY KEY,
    RECOMMEND_COLLECTIBLE_NAME VARCHAR(50),
    RECOMMEND_COLLECTIBLE_URL  VARCHAR(255)
);

-- 해결 내실 목록
CREATE TABLE CLEAR_COLLECTIBLE
(
    MEMBER_ID                VARCHAR(50),
    RECOMMEND_COLLECTIBLE_ID INT,
    PRIMARY KEY (MEMBER_ID, RECOMMEND_COLLECTIBLE_ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID) ON DELETE CASCADE,
    FOREIGN KEY (RECOMMEND_COLLECTIBLE_ID) REFERENCES RECOMMEND_COLLECTIBLE (RECOMMEND_COLLECTIBLE_ID)
);

-- 인증번호 저장
CREATE TABLE AUTH_CODE
(
    MEMBER_ID                VARCHAR(50)        PRIMARY KEY,
    CODE                     VARCHAR(50)
);

INSERT INTO ABYSS_DUNGEON_LIST (CODE, ABYSS_NAME, BOSS_NAME, LOCATION, LEVEL_SINGLE, LEVEL_NORMAL, LEVEL_HARD)
VALUES
    -- 어비스 던전 (Abyss Dungeon)
    ('aby-1', '고대 유적 엘베리아', NULL, NULL, NULL, 500, NULL),
    ('aby-2', '몽환의 궁전', NULL, NULL, NULL, 635, NULL),
    ('aby-3', '오만의 방주', NULL, NULL, NULL, 805, NULL),
    ('aby-4', '낙원의 문', NULL, NULL, NULL, 960, NULL),
    ('aby-5', '오레하의 우물', '알비온', NULL, NULL, 1340, 1370),
    ('aby-6', '카양겔', '라우리엘', '영원한 빛의 요람', 1540, 1540, 1580),
    ('aby-7', '혼돈의 상아탑', '라자람', '짓밟힌 정원', 1600, 1600, 1620);


INSERT INTO RAID_LIST (CODE, BOSS_NAME, BOSS_ROLE, LOCATION, LEVEL_SINGLE, LEVEL_NORMAL, LEVEL_HARD)
VALUES
    -- 군단장 레이드 (Commander Legion Raid)
    ('com-1', '발탄', '마수군단장', '부활한 마수의 심장', 1415, 1415, 1445),
    ('com-2', '비아키스', '욕망군단장', '목마른 쾌락의 정원', 1430, 1430, 1460),
    ('com-3', '쿠크세이튼', '광기군단장', '한밤중의 서커스', 1475, 1475, NULL),
    ('com-4', '아브렐슈드', '몽환군단장', '몽환의 아스탤지어', 1490, 1490, 1540),
    ('com-5', '일리아칸', '질병군단장', '부패한 군주의 판데모니움', 1580, 1580, 1600),
    ('com-6', '카멘', '어둠군단장', '어둠의 바라트론', 1610, 1610, 1630),

    -- 에픽 레이드 (Epic Raid)
    ('epi-1', '베히모스', '', '사일락 동굴', NULL, 1640, NULL),

    -- 카제로스 레이드 (Kazeros Raid)
    ('kaz-1', '에키드나', '서막: 붉어진 백야의 나선', '카르가 고원', 1620, 1620, 1640),
    ('kaz-2', '에기르', '1막 : 대지를 부수는 업화의 궤적', '아베크 골짜기', NULL, 1660, 1680),
    ('kaz-3', '아브렐슈드', '2막 : 부유하는 악몽의 진혼곡', '센나르 분지', NULL, 1670, 1690),
    ('kaz-4', '모르둠', '3막 : 칠흑, 폭풍의 밤', '안타레스 산맥', NULL, 1680, 1700);

INSERT INTO NOTICE (TITLE, CONTENT, IMAGE, CREATED_AT)
VALUES ('사이트 관련 공지', '사이트 이용에 대한 공지사항입니다.', NULL, '2024-12-02 10:30:00'),
       ('내실 가이드 추가', '내실을 효율적으로 올릴 수 있는 내실추천 기능이 업데이트되었습니다.', NULL, '2024-12-28 14:15:00'),
       ('멘토/멘티 기능 추가', '이제 사이트 내에서 멘토/멘티를 직접 구하실 수 있습니다.', NULL, '2025-01-16 08:45:00'),
       ('학원 탭 신설', '학원 파티 구인/구직을 위한 학원 탭이 개설되었습니다.', NULL, '2025-02-25 16:25:00'),
       ('계정 인증 절차 추가', '닉네임 도용으로 인한 악용 가능성이 있다고 판단되어 본인 캐릭터 인증을 개선하였습니다.', NULL, '2025-03-20 12:00:00');

INSERT INTO RECOMMEND_COLLECTIBLE (RECOMMEND_COLLECTIBLE_NAME, RECOMMEND_COLLECTIBLE_URL)
VALUES ('내실 익스프레스 (스포)', 'https://doyulv.tistory.com/46'),
       ('지혜의 섬 보조 사서 (스포)', 'https://www.inven.co.kr/board/lostark/4821/95622'),
       ('거심 12개 (스포)', 'https://www.inven.co.kr/board/lostark/4821/95622'),
       ('파푸니카 80% (스포)', 'https://www.inven.co.kr/board/lostark/4821/95622'),
       ('이그네아의 징표 8개 (스포)', 'https://www.inven.co.kr/board/lostark/4821/95622'),
       ('타워 오브 데스티니 15층 (스포)', 'https://blog.naver.com/bledel90/222525736538'),
       ('타워 오브 데스티니 50층 (스포)', 'https://blog.naver.com/bledel90/222525736538'),
       ('세베크 아툰 (상깨물)', 'https://www.inven.co.kr/webzine/news/?news=297450&site=lostark'),
       ('평판: 끝나지 않은 싸움 (깨물)', 'https://vortexgaming.io/postdetail/369504'),
       ('쿠르잔 북부 70% (깨물)', 'https://gam3.tistory.com/58'),
       ('크림스네일의 해도 2개 (깨물)', 'https://gopenguin.tistory.com/382'),
       ('이그네아의 징표 9개 (비프로스트)', 'https://www.inven.co.kr/board/lostark/4821/95622'),
       ('모험물 34개 (영웅 풍요 룬)', 'https://www.inven.co.kr/board/lostark/4821/100483'),
       ('이그네아의 징표 15개 (전설 정화 룬)', 'https://arca.live/b/lostark/83541704'),
       ('전설 단죄 구매 (영지)', 'https://www.inven.co.kr/board/lostark/4821/73762'),
       ('전설 심판 구매 (영지)', 'https://www.inven.co.kr/board/lostark/4821/73762'),
       ('미술품 36개 (웨이 카드)', 'https://canfactory.tistory.com/1241'),
       ('거인의 심장 13개 (영웅 집중)', 'https://inty.kr/entry/lostark-hearts'),
       ('항해 모험물 42개 (전설 집중)', 'https://oksk.tistory.com/24'),
       ('미술품 44개 (전설 심판)', 'https://canfactory.tistory.com/1241'),
       ('오르페우스의 별 7개 (전설 수호)', 'https://m.blog.naver.com/aaccq123/223169503483'),
       ('미술품 58개 (전설 철벽)', 'https://canfactory.tistory.com/1241'),
       ('기억의 오르골 10개 (전설 카드팩 2개)', 'https://www.inven.co.kr/board/lostark/4821/88649'),
       ('기억의 오르골 14개 (도약의 전설 카드 선택팩)', 'https://www.inven.co.kr/board/lostark/4821/94727'),
       ('아스트레이 7렙 (쾌속항해)', 'https://blog.naver.com/jej0572/222646100193');
