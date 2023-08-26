INSERT INTO `user` (user_id, created_at, updated_at, deleted_at, email, last_diary_date,
                    social_code, user_role)
VALUES (1, '2023-03-01T15:20:02.236278', '2023-03-01T15:20:02.236278', null, null,
        '2023-03-14T15:20:02.236278', 'GOOGLE', 'ADMIN');
INSERT INTO `user` (user_id, created_at, updated_at, deleted_at, email, last_diary_date,
                    social_code, user_role)
VALUES (2, '2023-03-02T15:20:02.236278', '2023-03-21T15:20:02.236278', null, null,
        '2023-03-16T15:20:02.236278', 'APPLE', 'USER');
INSERT INTO `user` (user_id, created_at, updated_at, deleted_at, email, last_diary_date,
                    social_code, user_role)
VALUES (3, '2023-04-01T15:20:02.236278', '2023-04-04T15:20:02.236278', null, null,
        '2023-03-18T15:20:02.236278', 'APPLE', 'ADMIN');
INSERT INTO `user` (user_id, created_at, updated_at, deleted_at, email, last_diary_date,
                    social_code, user_role)
VALUES (4, '2023-05-01T15:20:02.236278', '2023-05-02T15:20:02.236278', null, null,
        '2023-03-20T15:20:02.236278', 'GOOGLE', 'USER');

INSERT INTO `emotion` (emotion_id, created_at, color, color_prompt, emotion_prompt, is_active, name)
VALUES (1, '2023-02-01T15:20:02.236278', '#FF0000', 'Blue', 'sadness', 1, '슬픔');
INSERT INTO `emotion` (emotion_id, created_at, color, color_prompt, emotion_prompt, is_active, name)
VALUES (2, '2023-02-02T15:20:02.236278', '#FF0203', 'Pink', 'happiness', 1, '행복');
INSERT INTO `emotion` (emotion_id, created_at, color, color_prompt, emotion_prompt, is_active, name)
VALUES (3, '2023-02-03T15:20:02.236278', '#FF9203', 'Orange', 'joyful', 1, '즐거움');

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (1, '2023-03-13T15:20:02.236278', '2023-03-15T15:20:02.236278', '2023-05-02T07:13:04.971623',
        '2023-03-03T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 1, 1);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (1, '2023-03-13T15:20:02.236278', '/diary/1.png', 1, 1);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (1, '2023-03-13T15:20:02.236278', 1, 'this is prompt of diary 1', 1);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (2, '2023-03-14T15:20:02.236278', '2023-03-15T15:20:02.236278', null,
        '2023-03-14T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 1, 1);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (2, '2023-03-14T15:20:02.236278', '/diary/2.png', 1, 2);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (2, '2023-03-14T15:20:02.236278', 1, 'this is prompt of diary 2', 2);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (3, '2023-03-15T15:20:02.236278', '2023-03-15T15:20:02.236278', null,
        '2023-03-15T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 1, 2);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (3, '2023-03-13T15:20:02.236278', '/diary/3.png', 1, 3);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (3, '2023-03-13T15:20:02.236278', 1, 'this is prompt of diary 3', 3);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (4, '2023-03-16T15:20:02.236278', '2023-03-16T15:20:02.236278', '2023-04-02T07:13:04.971623',
        '2023-03-12T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 1, 2);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (4, '2023-03-16T15:20:02.236278', '/diary/4.png', 1, 4);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (4, '2023-03-16T15:20:02.236278', 1, 'this is prompt of diary 4', 4);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (5, '2023-03-17T15:20:02.236278', '2023-03-17T15:20:02.236278', null,
        '2023-03-17T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 1, 3);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (5, '2023-03-17T15:20:02.236278', '/diary/5.png', 1, 5);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (5, '2023-03-17T15:20:02.236278', 1, 'this is prompt of diary 5', 5);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (6, '2023-03-18T15:20:02.236278', '2023-03-18T15:20:02.236278', null,
        '2023-03-18T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 2, 3);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (6, '2023-03-18T15:20:02.236278', '/diary/6.png', 1, 6);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (6, '2023-03-18T15:20:02.236278', 1, 'this is prompt of diary 6', 6);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (7, '2023-03-19T15:20:02.236278', '2023-03-19T15:20:02.236278', null,
        '2023-03-19T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 2, 4);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (7, '2023-03-19T15:20:02.236278', '/diary/7.png', 1, 7);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (7, '2023-03-19T15:20:02.236278', 1, 'this is prompt of diary 7', 7);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (8, '2023-03-20T15:20:02.236278', '2023-03-20T15:20:02.236278', null,
        '2023-03-20T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 2, 4);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (8, '2023-03-20T15:20:02.236278', '/diary/8.png', 1, 8);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (8, '2023-03-20T15:20:02.236278', 1, 'this is prompt of diary 8', 8);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (9, '2023-03-21T15:20:02.236278', '2023-03-21T15:20:02.236278', '2023-06-02T07:13:04.971623',
        '2023-03-21T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 3, 1);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (9, '2023-03-13T15:20:02.236278', '/diary/9.png', 1, 9);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (9, '2023-03-13T15:20:02.236278', 1, 'this is prompt of diary 9', 9);

INSERT INTO `diary` (diary_id, created_at, updated_at, deleted_at, diary_date, is_ai, notes, review,
                     title, weather, is_test, emotion_id, user_id)
VALUES (10, '2023-03-22T15:20:02.236278', '2023-03-22T15:20:02.236278', null,
        '2023-03-22T15:20:02.236278', 0, 'this is note', 'GOOD', 'title', 'sunny', false, 3, 2);
INSERT INTO `image` (image_id, created_at, image_url, is_selected, diary_id)
VALUES (10, '2023-03-13T15:20:02.236278', '/diary/10.png', 1, 10);
INSERT INTO `prompt` (prompt_id, created_at, is_success, prompt_text, diary_id)
VALUES (10, '2023-03-13T15:20:02.236278', 1, 'this is prompt of diary 10', 10);
