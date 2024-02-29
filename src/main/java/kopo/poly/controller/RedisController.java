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
    public ResponseEntity saveString(@RequestBody RedisDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".saveString Start!");

        log.info("pDTO : " + pDTO); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveString(pDTO))
                .orElseGet(() -> RedisDTO.builder().build());

        log.info(this.getClass().getName() + ".saveString End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }


    /**
     * Redis 문자열을 JSON으로 저장 실습
     */
    @PostMapping(value = "saveStringJSON")
    public ResponseEntity saveStringJSON(@RequestBody RedisDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".saveStringJSON Start!");

        log.info("pDTO : " + pDTO); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveStringJSON(pDTO))
                .orElseGet(() -> RedisDTO.builder().build());

        log.info(this.getClass().getName() + ".saveStringJSON End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }


    /**
     * List타입에 여러 문자열로 저장하기(동기화)
     */
    @PostMapping(value = "saveList")
    public ResponseEntity saveList(@RequestBody List<RedisDTO> pList) throws Exception {

        log.info(this.getClass().getName() + ".saveList Start!");

        log.info("pList : " + pList); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<String> rList = Optional.ofNullable(myRedisService.saveList(pList))
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".saveList End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }


    /**
     * List타입에 JSON 형태로 저장하기(동기화)
     */
    @PostMapping(value = "saveListJSON")
    public ResponseEntity saveListJSON(@RequestBody List<RedisDTO> pList) throws Exception {

        log.info(this.getClass().getName() + "saveListJSON. Start!");

        log.info("pList : " + pList); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<RedisDTO> rList = Optional.ofNullable(myRedisService.saveListJSON(pList))
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".saveListJSON End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }

    /**
     * Hash 타입에 문자열 형태로 저장하기
     */
    @PostMapping(value = "saveHash")
    public ResponseEntity saveHash(@RequestBody RedisDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".saveHash Start!");

        log.info("pDTO : " + pDTO); // 전달받은 값 로그로 확인하기!(반드시 작성하기)

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveHash(pDTO))
                .orElseGet(() -> RedisDTO.builder().build());

        log.info(this.getClass().getName() + ".saveHash End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rDTO));
    }

    /**
     * Set타입에 JSON 형태로 람다식을 이용하여 저장하기
     */
    @PostMapping(value = "saveSetJSON")
    public ResponseEntity saveSetJSON(@RequestBody List<RedisDTO> pList) throws Exception {

        log.info(this.getClass().getName() + ".saveSetJSON Start!");

        log.info("pList : " + pList);

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        Set<RedisDTO> rSet = Optional.ofNullable(myRedisService.saveSetJSON(pList))
                .orElseGet(HashSet::new);

        log.info(this.getClass().getName() + ".saveSetJSON End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rSet));
    }

    /**
     * ZSet타입에 JSON 형태로 저장하기
     */
    @PostMapping(value = "saveZSetJSON")
    public ResponseEntity saveRedisZSetJSON(@RequestBody List<RedisDTO> pList) throws Exception {

        log.info(this.getClass().getName() + ".saveZSetJSON Start!");

        log.info("pList : " + pList);

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        Set<RedisDTO> rSet = Optional.ofNullable(myRedisService.saveZSetJSON(pList))
                .orElseGet(HashSet::new);

        log.info(this.getClass().getName() + ".saveZSetJSON End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rSet));
    }
}
