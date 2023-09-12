INSERT INTO `user` (user_id, created_at, updated_at, deleted_at, email, last_diary_date,
                    social_code, user_role)
VALUES (1, '2023-03-01T15:20:02.236278', '2023-03-01T15:20:02.236278', null, 'email@gmail.com',
        null, 'GOOGLE',
INSERT INTO `ad_reward` (ad_reward_id, created_at, used_at, user_id)
VALUES (1, '2023-04-01T09:20:02.236278', null, 1);
INSERT INTO `ad_reward` (ad_reward_id, created_at, used_at, user_id)
VALUES (2, '2023-06-30T08:20:02.236278', '2023-07-01T04:20:02.012345', 1);