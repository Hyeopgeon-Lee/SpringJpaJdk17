package kopo.poly.controller;

import jakarta.validation.Valid;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.MovieDTO;
import kopo.poly.service.IMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/movie/v1")
@RequiredArgsConstructor
@RestController
public class MovieController {

    private final IMovieService movieService;

    /**
     * CGV 영화 순위 가져오기
     */
    @PostMapping(value = "speechcommand")
    public ResponseEntity getMovie(@Valid @RequestBody MovieDTO pDTO, BindingResult bindingResult) throws Exception {

        log.info("{}.getMovie start!", this.getClass().getName());

        if (bindingResult.hasErrors()) { // Spring Validation 맞춰 잘 바인딩되었는지 체크
            return CommonResponse.getErrors(bindingResult); // 유효성 검증 결과에 따른 에러 메시지 전달

        }

        List<MovieDTO> rList = null;

        log.info("pDTO : {}", pDTO);

        // 영화와 비슷한 단어가 존재하면 CGV 영화 순위 가져오기 수행
        if ((pDTO.speechCommand().contains("영화")) || (pDTO.speechCommand().contains("영하"))
                || (pDTO.speechCommand().contains("연하")) || (pDTO.speechCommand().contains("연화"))) {

            // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
            rList = Optional.ofNullable(movieService.getMovieRank()).orElseGet(ArrayList::new);

        }

        log.info("{}.getMovie end!", this.getClass().getName());

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }
}
