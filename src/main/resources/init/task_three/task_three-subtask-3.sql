select * from users
where
        ID in (
        select USER_ID from likes
        where DATE(CREATED_DATE) between '2025-03-01' and '2025-03-31'
        group by POST_ID
        having count(USER_ID) > 9
    )
  AND
        ID in (
        select USER_ID1 from friendships
        where DATE(CREATED_DATE) between '2025-03-01' and '2025-03-31'
        group by USER_ID1
        having count(USER_ID2) > 10
    )
order by ID;