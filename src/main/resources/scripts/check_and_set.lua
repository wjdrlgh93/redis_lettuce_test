-- KEYS[1]: 확인할 키 이름
-- ARGV[1]: 비교할 값
-- ARGV[2]: 새로 설정할 값

local current = redis.call('get', KEYS[1])

if current == ARGV[1] then
    redis.call('set', KEYS[1], ARGV[2])
    return "SUCCESS"
else
    return "FAILURE"
end