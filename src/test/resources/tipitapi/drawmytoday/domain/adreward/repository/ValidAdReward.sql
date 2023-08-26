INSERT INTO `user` (user_id, created_at, updated_at, deleted_at, email, last_diary_date,
                    social_code,
                    user_role)
VALUES (1, '2023-03-01T15:20:02.236278', '2023-03-01T15:20:02.236278', null, null, null, 'GOOGLE',
        null);
INSERT INTO `ad_reward` (ad_reward_id, created_at, used_at, user_id)
VALUES (1, '2023-04-01T15:38:02.012342', null, 1);
INSERT INTO `ad_reward` (ad_reward_id, created_at, used_at, user_id)
VALUES (2, '2023-05-03T15:10:02.354232', '2023-05-04T15:20:02.235513', 1);
INSERT INTO `ad_reward` (ad_reward_id, created_at, used_at, user_id)
VALUES (3, '2023-06-20T09:29:02.534499', null, 1);
INSERT INTO `ad_reward` (ad_reward_id, created_at, used_at, user_id)
VALUES (4, '2023-06-28T13:00:02.236278', null, 1);