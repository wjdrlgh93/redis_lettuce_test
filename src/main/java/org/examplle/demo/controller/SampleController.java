package org.examplle.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.examplle.demo.DTO.HashInVo;
import org.examplle.demo.DTO.HashOutVo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.UnifiedJedis;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

//@Slf4j
@RestController
public class SampleController {

    @Autowired
    UnifiedJedis unifiedJedis;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/jedis/hash")
    public String getJedisHash() {
        Map<String, String> hashmap = unifiedJedis.hgetAll("hashtest2");
        for (Map.Entry<String, String> outer : hashmap.entrySet()) {
            String redisKey = outer.getKey();
            String redisValue = outer.getValue();

            System.out.println("Redis Key: " + redisKey);
            System.out.println("Redis Value: " + redisValue);

        }
        return "OK";
    }
//    @GetMapping("/jedis/hash/field")
//    public String getJedisHashField() {
//
//        String fieldValue = unifiedJedis.hget("hashtest:2", "name");
//
//        System.out.println("Redis value: " + fieldValue);
//
//        return "OK";
//    }
//    public void sendKeepAlivePing() {
//        try {
//            String response = unifiedJedis.ping();
//            log.info("Redis Keepalive PING sent. Response: {}", response);
//        } catch (Exception e) {
//            // 문제가 발생 시 jedis의 로컬 캐시를 비워서 redis 호출 되도록
//            unifiedJedis.getCache().flush();
//            log.error("Redis Keepalive PING failed", e);
//        }
//    }

    // String RedisTemplate SETKEY
    @GetMapping("/set/data")
    public String setData() {
        String key = "key1";
        String value = "value1";
        ValueOperations<String, String> valueOperations =
                redisTemplate.opsForValue();
        valueOperations.set(key, value);

        return valueOperations.get(key);
    }

    // GETKEY
    @GetMapping("/get/data")
    public String getData() {
        return (String) redisTemplate.opsForValue().get("key1");
    }

    // setList
    @GetMapping("/set/list")
    public String setList() {
        ListOperations<String, String> listOpt = redisTemplate.opsForList();
        listOpt.rightPush("key", "value");
        listOpt.leftPush("key", "value");
        listOpt.rightPop("key");
        listOpt.leftPop("key");
        return "OK-rightPush-leftPush-rightPop-leftPop";
    }

    // RedisSet
    @GetMapping("/sets/data")
    public String setSets() {
        SetOperations<String, String> setOpt = redisTemplate.opsForSet();
        // 키 이름에 {user1} 처럼 중괄호를 넣어줍니다.
        // 이러면 중괄호 안의 'user1'만 보고 해시를 생성해서 같은 노드로 보냅니다.
        String key1 = "{user1}:setKey";
        String key2 = "{user1}:setKey2";

        setOpt.add(key1, "value1", "value2");
        setOpt.add(key2, "value2", "value3");

        // 이제 두 키는 같은 노드에 있으므로 교집합 연산이 가능합니다!
        Set<String> result = setOpt.intersect(key1, key2);

        return "Intersection Result: " + result; // [value2] 가 나올 거예요.
    }

    //////////
    //      //
    // HASH //
    //      //
    /// ///////
    @GetMapping("/set/hash")
    public String setHash() {
        HashInVo hashIn = new HashInVo();
        hashIn.setName("test");
        hashIn.setCompany("테스트");
        hashIn.setAge(20);

        HashOutVo hashOut = new HashOutVo();
        hashOut.setTest("test");
        hashOut.setTest2("test");

        HashOperations<String, String, Object> hashOps =
                redisTemplate.opsForHash();
        hashOps.put("hashtest:0", "in", hashIn);
        hashOps.put("hashtest:0", "out", hashOut);

        return "OK";
    }

    @GetMapping("/get/hash")
    public Object getRedisHash() {

        Object object = redisTemplate.opsForHash().get("hashtest:0", "in");

        HashInVo hashInVo = objectMapper.convertValue(object, HashInVo.class);

        return hashInVo;
    }

    @GetMapping("/set/hash2")
    public String setHash2() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "wesome3");
        map.put("company", "위썸3");
        map.put("age", "31");

        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        hashOps.putAll("hashtest:1", map);

        return "HASH2 OK";
    }

    @GetMapping("/hdel")
    public String hdel() {
        redisTemplate.opsForHash().delete("user1", "age");
        return "OK";
    }

    @GetMapping("/hexpire")
    public String hexpire() {
        redisTemplate.opsForHash().expire("user:1", Duration.ofSeconds(60), Arrays.asList("age", "city"));
        return "OK";

    }

    ////////////////
    //            //
    // Sorted-set //
    //            //
    /// /////////////
    @GetMapping("/sset/data")
    public String setzSets() {
        ZSetOperations<String, String> zsetOps = redisTemplate.opsForZSet();
        zsetOps.add("racer_scores", "Norem", 10);
        zsetOps.add("racer_scores", "Castilla", 12);
        zsetOps.addIfAbsent("racer_scores", "Castilla", 12);
        zsetOps.range("racer_scores", 0, 10);
        zsetOps.rank("racer_scores", "Norem");
        zsetOps.reverseRank("racer_scores", "Norem");

        return "OK - Sorted Set";
    }

    ////////////////
    //            //
    // Geospatial //
    //            //
    /// /////////////
    String geoKey = "geoKey";
    double geoLen = 3.1394349;
    double geoLat = 56.49450932;
    String geoStation = "tempDummyStation";
    double radius = 5.0;

    @GetMapping("/set/geo")
    public String setGeo() {

        redisTemplate.opsForGeo()
                .add(geoKey, new Point(geoLen, geoLat), geoStation);

        return "OK Geo";
    }

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/get/geo")
    public List<Map<String, Object>> searchGeo() {
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeCoordinates()
                        .sortAscending();

        GeoResults<RedisGeoCommands.GeoLocation<String>> re =
                stringRedisTemplate.opsForGeo()
                        .radius(geoKey, new Circle(new Point(geoLen, geoLat), new Distance(radius, Metrics.KILOMETERS)), args);

        if (re == null) {
            return Collections.emptyList();
        }
        return re.getContent().stream().map(res -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", res.getContent().getName());
            data.put("dist", res.getDistance().getValue());
            data.put("lat", res.getContent().getPoint().getY());
            data.put("lon", res.getContent().getPoint().getX());
            return data;
        }).collect(Collectors.toList());
    }
    ////////////////
    //            //
    //  Bitmaps   //
    //            //
    /// /////////////
    String bKey = "bitmapsKey";
    long boffset = 0;
    boolean bvalue = true;

    @GetMapping("/set/bitmap")
    public String setBitmap() {
        redisTemplate.opsForValue().setBit(bKey, boffset, bvalue);
        return "OK Bitmaps";
    }

    @GetMapping("/get/bitmap")
    public int getBitmap() {
        Boolean bit = stringRedisTemplate.opsForValue().getBit(bKey, boffset);
        return bit != null && bit ? 1 : 0;
    }

    @GetMapping("/get/bitcount")
    public long getBitCount() {
        String pingKey = "pings:2024-01-01-00:00";
        stringRedisTemplate.opsForValue().setBit(pingKey, 10, true);
        return stringRedisTemplate.execute((RedisCallback<Long>) con ->
                con.bitCount(pingKey.getBytes()));
    }

    // 서버에 bitmap 형식으로 가져오는 방식
    @GetMapping("/get/bit")
    public String getBit() {
        byte[] value = stringRedisTemplate.execute((RedisCallback<byte[]>) con ->
                con.get(bKey.getBytes(StandardCharsets.UTF_8)));
        if (value == null) return null;
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            sb.append(String.format("\\x%02x", b));
        }
        return sb.toString();
    }

    private byte[] toBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/set/bitop")
    public String setBitOp() {
        String op = "AND";
        // JAVA에서 Cluster 상태에서는 여러 슬롯에 데이터가 나뉘어져 있어 {...}를 붙여서 연산실행
        String hashTag = "{group}";
        stringRedisTemplate.opsForValue().setBit(hashTag + "A", 5, true);
        stringRedisTemplate.opsForValue().setBit(hashTag + "B", 5, true);
        stringRedisTemplate.opsForValue().setBit(hashTag + "C", 5, true);
        String destKey = hashTag + "R";

        List<String> sourceKeys = Arrays.asList(hashTag + "A", hashTag + "B", hashTag + "C");
        byte[][] source = sourceKeys.stream()
                .map(this::toBytes)
                .toArray(byte[][]::new);
        Long result = stringRedisTemplate.execute((RedisCallback<Long>) con -> {
            switch (op.toUpperCase()) {
                case "AND":
                    return con.bitOp(RedisStringCommands.BitOperation.AND, toBytes(destKey), source);
                case "OR":
                    return con.bitOp(RedisStringCommands.BitOperation.OR, toBytes(destKey), source);
                case "XOR":
                    return con.bitOp(RedisStringCommands.BitOperation.XOR, toBytes(destKey), source);
                case "NOT":
                    if (source.length != 1)
                        throw new IllegalArgumentException("NOT require excatly 1 source Key");
                    return con.bitOp(RedisStringCommands.BitOperation.NOT, toBytes(destKey), source[0]);
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + op);
            }
        });
        // BITOP후 GET
        Object value = stringRedisTemplate.execute((RedisCallback<Object>) con ->
                con.get(toBytes(destKey))
        );
        String getResult = (value != null) ? Arrays.toString((byte[]) value) : null;
        return "BitOp " + op + " result size:" + result + ", GET " + destKey + " = " + getResult;
    }

    ////////////////
    //            //
    //  Bitfield  //
    //            //
    /// /////////////
    @GetMapping("/set/bitfield")
    public String setBitField() {
        final String key = "bike:1:stats";
        List<Long> result;
        String op = "SET";

        switch (op) {
            case "SET":
                //'unsigned()' 안에 비트 타입 <type>,
                //'valueAt()' 안에 저장된 값의 비트 자리수 위치 값 <offset>,
                // 'to()' 안에 저장된 값의 증가/감소시킬 수를 지정합니다 <increment>
                result = stringRedisTemplate.opsForValue().bitField(
                        key,
                        BitFieldSubCommands.create()
                                .set(BitFieldSubCommands.BitFieldType.unsigned(32)).valueAt(0).to(1000));
                break;
            case "INCRBY":
                result = stringRedisTemplate.opsForValue().bitField(
                        key,
                        BitFieldSubCommands.create()
                                .incr(BitFieldSubCommands.BitFieldType.unsigned(32)).valueAt(0).by(500)
                );
                break;
            case "GET":
                result = stringRedisTemplate.opsForValue().bitField(
                        key,
                        BitFieldSubCommands.create()
                                .get(BitFieldSubCommands.BitFieldType.unsigned(32)).valueAt(0)
                );
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);

        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.size(); i++) {
            sb.append(i + 1).append(") (integer) ").append(result.get(i)).append("\n");
        }

        return "BITFIELD " + op + " 결과:\n" + sb;
    }
    /////////////
    //  TTL   //
    ////////////
    @GetMapping("/set/ttl")
    public String setTtl() {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public List<Object> execute(RedisOperations operations) {
                // 트랜젝션 시작
                operations.multi();

                try{
                    operations.opsForValue().set("test","value");
                    operations.expire("test", Duration.ofSeconds(60));


                    // 트랜젝션 실행
                    return operations.exec();
                } catch (Exception e){
                    operations.discard();
                    throw new RuntimeException("Transaction Failed", e);
                }
            }
        });
        return "ttl-OK";
    }

    // Pub/Sub Connect

}

