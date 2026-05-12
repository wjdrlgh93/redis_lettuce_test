local current = redis.call('GET', KEYS[1])

if current == ARGV[1] then
    redis.call('SET', KEYS[1], ARGV[2])
    return true  -- 성공하면 true 반환
end

return false -- 값이 다르면 실패(false) 반환