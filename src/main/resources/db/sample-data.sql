-- Sample data for the current RoomMate DB.
-- Run schema-recommendations.sql first if members is missing status/gender/birth_date columns.

INSERT INTO work_types (work_type_name)
SELECT '직장인' WHERE NOT EXISTS (SELECT 1 FROM work_types WHERE work_type_name = '직장인');
INSERT INTO work_types (work_type_name)
SELECT '대학생' WHERE NOT EXISTS (SELECT 1 FROM work_types WHERE work_type_name = '대학생');
INSERT INTO work_types (work_type_name)
SELECT '프리랜서' WHERE NOT EXISTS (SELECT 1 FROM work_types WHERE work_type_name = '프리랜서');

INSERT INTO room_types (room_type_name)
SELECT '원룸' WHERE NOT EXISTS (SELECT 1 FROM room_types WHERE room_type_name = '원룸');
INSERT INTO room_types (room_type_name)
SELECT '투룸' WHERE NOT EXISTS (SELECT 1 FROM room_types WHERE room_type_name = '투룸');
INSERT INTO room_types (room_type_name)
SELECT '쉐어하우스' WHERE NOT EXISTS (SELECT 1 FROM room_types WHERE room_type_name = '쉐어하우스');

INSERT INTO hobbies (hobby_name)
SELECT '요리' WHERE NOT EXISTS (SELECT 1 FROM hobbies WHERE hobby_name = '요리');
INSERT INTO hobbies (hobby_name)
SELECT '러닝' WHERE NOT EXISTS (SELECT 1 FROM hobbies WHERE hobby_name = '러닝');
INSERT INTO hobbies (hobby_name)
SELECT '영화' WHERE NOT EXISTS (SELECT 1 FROM hobbies WHERE hobby_name = '영화');
INSERT INTO hobbies (hobby_name)
SELECT '독서' WHERE NOT EXISTS (SELECT 1 FROM hobbies WHERE hobby_name = '독서');

INSERT INTO preferences (preference_name)
SELECT '조용한 생활' WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE preference_name = '조용한 생활');
INSERT INTO preferences (preference_name)
SELECT '깔끔한 공용공간' WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE preference_name = '깔끔한 공용공간');
INSERT INTO preferences (preference_name)
SELECT '규칙적인 생활' WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE preference_name = '규칙적인 생활');
INSERT INTO preferences (preference_name)
SELECT '게스트 방문 사전 공유' WHERE NOT EXISTS (SELECT 1 FROM preferences WHERE preference_name = '게스트 방문 사전 공유');

INSERT INTO pets (pet_name)
SELECT '반려동물 없음' WHERE NOT EXISTS (SELECT 1 FROM pets WHERE pet_name = '반려동물 없음');
INSERT INTO pets (pet_name)
SELECT '고양이 가능' WHERE NOT EXISTS (SELECT 1 FROM pets WHERE pet_name = '고양이 가능');
INSERT INTO pets (pet_name)
SELECT '강아지 가능' WHERE NOT EXISTS (SELECT 1 FROM pets WHERE pet_name = '강아지 가능');

SET @work_office := (SELECT work_type_id FROM work_types WHERE work_type_name = '직장인' LIMIT 1);
SET @work_student := (SELECT work_type_id FROM work_types WHERE work_type_name = '대학생' LIMIT 1);
SET @work_freelance := (SELECT work_type_id FROM work_types WHERE work_type_name = '프리랜서' LIMIT 1);
SET @room_studio := (SELECT room_type_id FROM room_types WHERE room_type_name = '원룸' LIMIT 1);
SET @room_two := (SELECT room_type_id FROM room_types WHERE room_type_name = '투룸' LIMIT 1);
SET @room_share := (SELECT room_type_id FROM room_types WHERE room_type_name = '쉐어하우스' LIMIT 1);

INSERT INTO members (
    work_type_id, email, password, name, phone, photo_url, role, status,
    banned_until, gender, birth_date, report_count, deleted, sleep_time,
    smoking, drinking, mbti
)
SELECT @work_office, 'sample.jiyoon@roommate.test',
       '$2a$10$n7nZdeSARRvGG0Aq5i3/KO4n6sQWHgr5pZ9x6z3YPgX3cF9dRv30q',
       '김지윤', '010-1000-1001',
       'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=700&q=80',
       'USER', 'ACTIVE', NULL, 'FEMALE', '1998-04-12', 0, 0, '23:30',
       'NON_SMOKER', 'SOCIAL', 'ENFJ'
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'sample.jiyoon@roommate.test');

INSERT INTO members (
    work_type_id, email, password, name, phone, photo_url, role, status,
    banned_until, gender, birth_date, report_count, deleted, sleep_time,
    smoking, drinking, mbti
)
SELECT @work_student, 'sample.minho@roommate.test',
       '$2a$10$n7nZdeSARRvGG0Aq5i3/KO4n6sQWHgr5pZ9x6z3YPgX3cF9dRv30q',
       '박민호', '010-1000-1002',
       'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=700&q=80',
       'USER', 'ACTIVE', NULL, 'MALE', '2000-09-03', 0, 0, '01:00',
       'NON_SMOKER', 'NONE', 'ISTJ'
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'sample.minho@roommate.test');

INSERT INTO members (
    work_type_id, email, password, name, phone, photo_url, role, status,
    banned_until, gender, birth_date, report_count, deleted, sleep_time,
    smoking, drinking, mbti
)
SELECT @work_freelance, 'sample.seoyeon@roommate.test',
       '$2a$10$n7nZdeSARRvGG0Aq5i3/KO4n6sQWHgr5pZ9x6z3YPgX3cF9dRv30q',
       '이서연', '010-1000-1003',
       'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=700&q=80',
       'USER', 'ACTIVE', NULL, 'FEMALE', '1996-12-20', 0, 0, '00:30',
       'NON_SMOKER', 'OFTEN', 'INFP'
WHERE NOT EXISTS (SELECT 1 FROM members WHERE email = 'sample.seoyeon@roommate.test');

SET @member_jiyoon := (SELECT member_id FROM members WHERE email = 'sample.jiyoon@roommate.test' LIMIT 1);
SET @member_minho := (SELECT member_id FROM members WHERE email = 'sample.minho@roommate.test' LIMIT 1);
SET @member_seoyeon := (SELECT member_id FROM members WHERE email = 'sample.seoyeon@roommate.test' LIMIT 1);

INSERT INTO rooms (
    member_id, room_title, room_content, room_type_id, monthly_rent, deposit,
    area_m2, floor, address, legal_dong, land_number, lat, lng,
    available_from, max_roommates, status
)
SELECT @member_jiyoon, '마포역 도보 5분 조용한 투룸', '채광이 좋고 공용공간을 깔끔하게 쓰는 룸메이트를 찾습니다.',
       @room_two, 58, 1000, 42.0, 5, '서울특별시 마포구 도화동', '마포구 도화동',
       'sample-1', 37.5412, 126.9498, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 2, 'OPEN'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE member_id = @member_jiyoon AND room_title = '마포역 도보 5분 조용한 투룸');

INSERT INTO rooms (
    member_id, room_title, room_content, room_type_id, monthly_rent, deposit,
    area_m2, floor, address, legal_dong, land_number, lat, lng,
    available_from, max_roommates, status
)
SELECT @member_minho, '신촌역 근처 대학생 쉐어하우스', '학교와 지하철이 가까운 쉐어하우스입니다. 생활 패턴이 규칙적인 분을 선호합니다.',
       @room_share, 42, 500, 56.0, 3, '서울특별시 서대문구 창천동', '서대문구 창천동',
       'sample-2', 37.5577, 126.9369, DATE_ADD(CURDATE(), INTERVAL 14 DAY), 3, 'OPEN'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE member_id = @member_minho AND room_title = '신촌역 근처 대학생 쉐어하우스');

INSERT INTO rooms (
    member_id, room_title, room_content, room_type_id, monthly_rent, deposit,
    area_m2, floor, address, legal_dong, land_number, lat, lng,
    available_from, max_roommates, status
)
SELECT @member_seoyeon, '강남구청역 조용한 원룸', '재택근무가 잦아 조용한 생활을 함께 지킬 룸메이트를 찾습니다.',
       @room_studio, 78, 1500, 29.5, 8, '서울특별시 강남구 논현동', '강남구 논현동',
       'sample-3', 37.5172, 127.0413, DATE_ADD(CURDATE(), INTERVAL 21 DAY), 2, 'RESERVED'
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE member_id = @member_seoyeon AND room_title = '강남구청역 조용한 원룸');

SET @room_jiyoon := (SELECT room_id FROM rooms WHERE member_id = @member_jiyoon AND room_title = '마포역 도보 5분 조용한 투룸' LIMIT 1);
SET @room_minho := (SELECT room_id FROM rooms WHERE member_id = @member_minho AND room_title = '신촌역 근처 대학생 쉐어하우스' LIMIT 1);
SET @room_seoyeon := (SELECT room_id FROM rooms WHERE member_id = @member_seoyeon AND room_title = '강남구청역 조용한 원룸' LIMIT 1);

INSERT INTO room_image (room_id, image_url, sort_order, deleted)
SELECT @room_jiyoon, 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM room_image WHERE room_id = @room_jiyoon AND sort_order = 0);
INSERT INTO room_image (room_id, image_url, sort_order, deleted)
SELECT @room_minho, 'https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=900&q=80', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM room_image WHERE room_id = @room_minho AND sort_order = 0);
INSERT INTO room_image (room_id, image_url, sort_order, deleted)
SELECT @room_seoyeon, 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=900&q=80', 0, 0
WHERE NOT EXISTS (SELECT 1 FROM room_image WHERE room_id = @room_seoyeon AND sort_order = 0);

INSERT INTO member_hobbies (member_id, hobby_id)
SELECT @member_jiyoon, hobby_id FROM hobbies WHERE hobby_name IN ('요리', '영화')
AND NOT EXISTS (SELECT 1 FROM member_hobbies WHERE member_id = @member_jiyoon AND hobby_id = hobbies.hobby_id);
INSERT INTO member_hobbies (member_id, hobby_id)
SELECT @member_minho, hobby_id FROM hobbies WHERE hobby_name IN ('러닝', '독서')
AND NOT EXISTS (SELECT 1 FROM member_hobbies WHERE member_id = @member_minho AND hobby_id = hobbies.hobby_id);
INSERT INTO member_hobbies (member_id, hobby_id)
SELECT @member_seoyeon, hobby_id FROM hobbies WHERE hobby_name IN ('영화', '독서')
AND NOT EXISTS (SELECT 1 FROM member_hobbies WHERE member_id = @member_seoyeon AND hobby_id = hobbies.hobby_id);

INSERT INTO member_preferences (member_id, preference_id)
SELECT @member_jiyoon, preference_id FROM preferences WHERE preference_name IN ('깔끔한 공용공간', '게스트 방문 사전 공유')
AND NOT EXISTS (SELECT 1 FROM member_preferences WHERE member_id = @member_jiyoon AND preference_id = preferences.preference_id);
INSERT INTO member_preferences (member_id, preference_id)
SELECT @member_minho, preference_id FROM preferences WHERE preference_name IN ('규칙적인 생활', '조용한 생활')
AND NOT EXISTS (SELECT 1 FROM member_preferences WHERE member_id = @member_minho AND preference_id = preferences.preference_id);
INSERT INTO member_preferences (member_id, preference_id)
SELECT @member_seoyeon, preference_id FROM preferences WHERE preference_name IN ('조용한 생활', '깔끔한 공용공간')
AND NOT EXISTS (SELECT 1 FROM member_preferences WHERE member_id = @member_seoyeon AND preference_id = preferences.preference_id);

INSERT INTO member_pets (member_id, pet_id)
SELECT @member_jiyoon, pet_id FROM pets WHERE pet_name = '반려동물 없음'
AND NOT EXISTS (SELECT 1 FROM member_pets WHERE member_id = @member_jiyoon AND pet_id = pets.pet_id);
INSERT INTO member_pets (member_id, pet_id)
SELECT @member_minho, pet_id FROM pets WHERE pet_name = '고양이 가능'
AND NOT EXISTS (SELECT 1 FROM member_pets WHERE member_id = @member_minho AND pet_id = pets.pet_id);
INSERT INTO member_pets (member_id, pet_id)
SELECT @member_seoyeon, pet_id FROM pets WHERE pet_name = '반려동물 없음'
AND NOT EXISTS (SELECT 1 FROM member_pets WHERE member_id = @member_seoyeon AND pet_id = pets.pet_id);
