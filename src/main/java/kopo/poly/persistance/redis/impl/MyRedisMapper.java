package kopo.poly.persistance.redis.impl;

import kopo.poly.dto.RedisDTO;
import kopo.poly.persistance.redis.IMyRedisMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class MyRedisMapper implements IMyRedisMapper {

    private final RedisTemplate<String, Object> redisDB;

    /**
     * RedisDB 저장된 키 삭제하는 공통 함수
     */
    private void deleteRedisKey(String redisKey) {

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) { // 데이터가 존재하면, 기존 데이터 삭제하기
            redisDB.delete(redisKey); // 삭제하기

            log.info("삭제 성공!");

        }
    }

    @Override
    public int saveString(String redisKey, RedisDTO pDTO) throws DataAccessException {

        log.info("{}.saveString Start!", this.getClass().getName());

        int res;

        String saveData = CmmUtil.nvl(pDTO.text()); // 저장할 값

        /*
         * redis 저장 및 읽기에 대한 데이터 타입 지정(String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입
        redisDB.setValueSerializer(new StringRedisSerializer()); // String 타입

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        // 데이터 저장하기
        redisDB.opsForValue().set(redisKey, saveData);

        // RedisDB에 저장되는 데이터의 유효시간 설정(TTL 설정)
        // 2일이 지나면, 자동으로 데이터가 삭제되도록 설정함
        redisDB.expire(redisKey, 2, TimeUnit.DAYS);

        res = 1;

        log.info("{}.saveString End!", this.getClass().getName());

        return res;
    }

    @Override
    public RedisDTO getString(String redisKey) throws DataAccessException {

        log.info("{}.getString Start!", this.getClass().getName());

        log.info("String redisKey : {}", redisKey);

        /*
         * redis 저장 및 읽기에 대한 데이터 타입 지정(String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입
        redisDB.setValueSerializer(new StringRedisSerializer()); // String 타입

        RedisDTO rDTO = null;

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) { // 데이터가 존재하면, 조회하기
            String res = (String) redisDB.opsForValue().get(redisKey); // redisKey 통해 조회하기

            log.info("res : {}", res); // 조회 결과

            // RedisDB에 저장된 데이터를 DTO에 저장하기
            rDTO = RedisDTO.builder().text(res).build();
        }

        log.info("{}.getString End!", this.getClass().getName());

        return rDTO;
    }

    @Override
    public int saveStringJSON(String redisKey, RedisDTO pDTO) throws DataAccessException {

        log.info("{}.saveStringJSON Start!", this.getClass().getName());

        int res;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        // 데이터 저장하기
        redisDB.opsForValue().set(redisKey, pDTO);

        // RedisDB에 저장되는 데이터의 유효시간 설정(TTL 설정)
        // 2일이 지나면, 자동으로 데이터가 삭제되도록 설정함
        redisDB.expire(redisKey, 2, TimeUnit.DAYS);

        res = 1;

        log.info("{}.saveStringJSON End!", this.getClass().getName());

        return res;
    }

    @Override
    public RedisDTO getStringJSON(String redisKey) throws DataAccessException {
        log.info("{}.getStringJSON Start!", this.getClass().getName());

        log.info("String redisKey : {}", redisKey);

        RedisDTO rDTO = null;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) { // 데이터가 존재하면, 조회하기
            rDTO = (RedisDTO) redisDB.opsForValue().get(redisKey); // redisKey 통해 조회하기

        }

        log.info("{}.getStringJSON End!", this.getClass().getName());

        return rDTO;
    }

    @Override
    public int saveList(String redisKey, List<RedisDTO> pList) throws DataAccessException {

        log.info("{}.saveList Start!", this.getClass().getName());

        int res;
        /*
         * redis 저장 및 읽기에 대한 데이터 타입 지정(String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입
        redisDB.setValueSerializer(new StringRedisSerializer()); // String 타입

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        pList.forEach(dto -> {
//            오름차순으로 저장하기
//                    redisDB.opsForList().rightPush(redisKey, CmmUtil.nvl(dto.text()));

                    // 내림차순으로 저장하기
                    redisDB.opsForList().leftPush(redisKey, CmmUtil.nvl(dto.text()));
                }
        );

        // 저장되는 데이터의 유효기간(TTL)은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        res = 1;

        log.info("{}.saveList End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<String> getList(String redisKey) throws DataAccessException {

        log.info("{}.getRedisList Start!", this.getClass().getName());

        List<String> rList = null;
        /*
         * redis 저장 및 읽기에 대한 데이터 타입 지정(String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입
        redisDB.setValueSerializer(new StringRedisSerializer()); // String 타입

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) {
            rList = (List) redisDB.opsForList().range(redisKey, 0, -1);

        }

        log.info("{}.getRedisList End!", this.getClass().getName());

        return rList;
    }

    @Override
    public int saveListJSON(String redisKey, List<RedisDTO> pList) throws DataAccessException {

        log.info("{}.saveListJSON Start!", this.getClass().getName());

        int res;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        // 람다식 사용하여 데이터 저장
        pList.forEach(dto -> redisDB.opsForList().rightPush(redisKey, dto));

        // 저장되는 데이터의 유효기간(TTL)은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        res = 1;

        log.info("{}.saveListJSON End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<RedisDTO> getListJSON(String redisKey) throws DataAccessException {

        log.info("{}.getListJSON Start!", this.getClass().getName());

        // 결과 값 저장할 객체
        List<RedisDTO> rList = null;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) {
            rList = (List) redisDB.opsForList().range(redisKey, 0, -1);

        }

        log.info("{}.getListJSON End!", this.getClass().getName());

        return rList;
    }

    @Override
    public int saveHash(String redisKey, RedisDTO pDTO) throws DataAccessException {

        log.info("{}.saveHash Start!", this.getClass().getName());

        int res;

        /*
         * redis 저장 및 읽기에 대한 데이터 타입 지정(String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입
        redisDB.setHashKeySerializer(new StringRedisSerializer()); // Hash 구조의 키 타입, String 타입
        redisDB.setHashValueSerializer(new StringRedisSerializer()); // Hash 구조의 값 타입, String 타입

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        redisDB.opsForHash().put(redisKey, "name", CmmUtil.nvl(pDTO.name()));
        redisDB.opsForHash().put(redisKey, "email", CmmUtil.nvl(pDTO.email()));
        redisDB.opsForHash().put(redisKey, "addr", CmmUtil.nvl(pDTO.addr()));

        // 저장되는 데이터의 유효기간(TTL)은 100분으로 정의
        redisDB.expire(redisKey, 100, TimeUnit.MINUTES);

        res = 1;

        log.info("{}.saveHash End!", this.getClass().getName());

        return res;
    }

    @Override
    public RedisDTO getHash(String redisKey) throws DataAccessException {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info("{}.getHash Start!", this.getClass().getName());

        // 결과값 전달할 객체
        RedisDTO rDTO = null;

        /*
         * redis 저장 및 읽기에 대한 데이터 타입 지정(String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입
        redisDB.setHashKeySerializer(new StringRedisSerializer()); // Hash 구조의 키 타입, String 타입
        redisDB.setHashValueSerializer(new StringRedisSerializer()); // Hash 구조의 값 타입, String 타입

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) {
            String name = CmmUtil.nvl((String) redisDB.opsForHash().get(redisKey, "name"));
            String email = CmmUtil.nvl((String) redisDB.opsForHash().get(redisKey, "email"));
            String addr = CmmUtil.nvl((String) redisDB.opsForHash().get(redisKey, "addr"));

            log.info("name : {}", name);
            log.info("email : {}", email);
            log.info("addr : {}", addr);

            rDTO = RedisDTO.builder().name(name).email(email).addr(addr).build();

        }

        log.info("{}.getHash End!", this.getClass().getName());

        return rDTO;
    }

    @Override
    public int saveSetJSON(String redisKey, List<RedisDTO> pList) throws DataAccessException {

        log.info("{}.saveSetJSON Start!", this.getClass().getName());

        int res;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        log.info("입력받은 데이터 수 : {}", pList.size());

        // Set 구조는 저장 순서에 상관없이 저장하기 떄문에 List 구조와 달리 방향이 존재하지 않음
        pList.forEach(dto -> redisDB.opsForSet().add(redisKey, dto));

        // 저장되는 데이터의 유효기간(TTL)은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        res = 1;

        log.info("{}.saveSetJSON End!", this.getClass().getName());

        return res;
    }

    @Override
    public Set<RedisDTO> getSetJSON(String redisKey) throws DataAccessException {

        log.info("{}.getSetJSON Start!", this.getClass().getName());

        // 결과값 전달할 객체
        Set<RedisDTO> rSet = null;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) {
            rSet = (Set) redisDB.opsForSet().members(redisKey); // RedisDB 데이터 조회하기

        }

        log.info("{}.getSetJSON End!", this.getClass().getName());

        return rSet;
    }

    @Override
    public int saveZSetJSON(String redisKey, List<RedisDTO> pList) throws DataAccessException {

        log.info("{}.saveZSetJSON Start!", this.getClass().getName());

        int res;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        this.deleteRedisKey(redisKey); // RedisDB 저장된 키 삭제

        pList.forEach(dto -> {
            redisDB.opsForZSet().add(redisKey, dto, dto.order()); // 저장순서는 order 값에 따름

        });

        // 저장되는 데이터의 유효기간(TTL)은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        res = 1;

        log.info("{}.saveZSetJSON End!", this.getClass().getName());

        return res;
    }

    @Override
    public Set<RedisDTO> getZSetJSON(String redisKey) throws DataAccessException {

        log.info("{}.getZSetJSON Start!", this.getClass().getName());

        // 결과값 전달할 객체
        Set<RedisDTO> rSet = null;

        // redisDB의 키의 데이터 타입을 String으로 정의(항상 String으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer()); // String 타입

        // RedisDTO에 저장된 데이터를 자동으로 JSON으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

        if (Boolean.TRUE.equals(redisDB.hasKey(redisKey))) {
            rSet = (Set) redisDB.opsForZSet().range(redisKey, 0, -1);

        }

        log.info("{}.getZSetJSON End!", this.getClass().getName());

        return rSet;
    }

}
