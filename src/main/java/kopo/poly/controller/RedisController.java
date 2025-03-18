package kopo.poly.controller;

import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.RedisDTO;
import kopo.poly.service.IMyRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RequestMapping(value = "/redis/v1")
@RequiredArgsConstructor
@RestController
public class RedisController {

    private final IMyRedisService myRedisService;

    /**
     * Redis 문자열 저장 실습
     */
    @PostMapping(value = "saveString")
    public ResponseEntity<CommonResponse<RedisDTO>> saveString(@RequestBody RedisDTO pDTO) throws Exception {

        log.info("{}.saveString Start!", this.getClass().getName());

        log.info("pDTO : {}", pDTO); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveString(pDTO))
                .orElseGet(() -> RedisDTO.builder().build());

        log.info("{}.saveString End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }


    /**
     * Redis 문자열을 JSON으로 저장 실습
     */
    @PostMapping(value = "saveStringJSON")
    public ResponseEntity<CommonResponse<RedisDTO>> saveStringJSON(@RequestBody RedisDTO pDTO) throws Exception {

        log.info("{}.saveStringJSON Start!", this.getClass().getName());

        log.info("pDTO : {}", pDTO); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveStringJSON(pDTO))
                .orElseGet(() -> RedisDTO.builder().build());

        log.info("{}.saveStringJSON End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }


    /**
     * List타입에 여러 문자열로 저장하기(동기화)
     */
    @PostMapping(value = "saveList")
    public ResponseEntity<CommonResponse<List<String>>> saveList(@RequestBody List<RedisDTO> pList) throws Exception {

        log.info("{}.saveList Start!", this.getClass().getName());

        log.info("pList : {}", pList); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<String> rList = Optional.ofNullable(myRedisService.saveList(pList))
                .orElseGet(ArrayList::new);

        log.info("{}.saveList End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }


    /**
     * List타입에 JSON 형태로 저장하기(동기화)
     */
    @PostMapping(value = "saveListJSON")
    public ResponseEntity<CommonResponse<List<RedisDTO>>> saveListJSON(@RequestBody List<RedisDTO> pList)
            throws Exception {

        log.info("{}saveListJSON. Start!", this.getClass().getName());

        log.info("pList : {}", pList); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<RedisDTO> rList = Optional.ofNullable(myRedisService.saveListJSON(pList))
                .orElseGet(ArrayList::new);

        log.info("{}.saveListJSON End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }

    /**
     * Hash 타입에 문자열 형태로 저장하기
     */
    @PostMapping(value = "saveHash")
    public ResponseEntity<CommonResponse<RedisDTO>> saveHash(@RequestBody RedisDTO pDTO) throws Exception {

        log.info("{}.saveHash Start!", this.getClass().getName());

        log.info("pDTO : {}", pDTO); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveHash(pDTO))
                .orElseGet(() -> RedisDTO.builder().build());

        log.info("{}.saveHash End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }

    /**
     * Set타입에 JSON 형태로 람다식을 이용하여 저장하기
     */
    @PostMapping(value = "saveSetJSON")
    public ResponseEntity<CommonResponse<Set<RedisDTO>>> saveSetJSON(@RequestBody List<RedisDTO> pList)
            throws Exception {

        log.info("{}.saveSetJSON Start!", this.getClass().getName());

        log.info("pList : {}", pList);

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        Set<RedisDTO> rSet = Optional.ofNullable(myRedisService.saveSetJSON(pList))
                .orElseGet(HashSet::new);

        log.info("{}.saveSetJSON End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rSet));
    }

    /**
     * ZSet타입에 JSON 형태로 저장하기
     */
    @PostMapping(value = "saveZSetJSON")
    public ResponseEntity<CommonResponse<Set<RedisDTO>>> saveRedisZSetJSON(@RequestBody List<RedisDTO> pList)
            throws Exception {

        log.info("{}.saveZSetJSON Start!", this.getClass().getName());

        log.info("pList : {}", pList);

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        Set<RedisDTO> rSet = Optional.ofNullable(myRedisService.saveZSetJSON(pList))
                .orElseGet(HashSet::new);

        log.info("{}.saveZSetJSON End!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rSet));
    }
}
