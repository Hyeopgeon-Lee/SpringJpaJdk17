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

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/movie/v1")
@RequiredArgsConstructor
public class MovieController {

    // 영화 관련 비즈니스 로직을 처리하는 서비스 객체 주입
    private final IMovieService movieService;

    /**
     * 음성 명령을 기반으로 CGV 영화 순위를 가져오는 API
     *
     * @param pDTO          사용자의 음성 명령이 담긴 요청 DTO
     * @param bindingResult 유효성 검사 결과
     * @return 영화 순위 목록 또는 에러 메시지 포함 응답
     */
    @PostMapping("/speechcommand")
    public ResponseEntity<?> getMovie(@Valid @RequestBody MovieDTO pDTO, BindingResult bindingResult) throws Exception {

        log.info("{}.getMovie start!", getClass().getName());

        // 1. 입력값 유효성 검사 결과 확인 (DTO에 정의된 @Valid 기반)
        if (bindingResult.hasErrors()) {
            // 유효성 오류가 있다면 에러 메시지를 포함한 공통 응답 반환
            return CommonResponse.getErrors(bindingResult);
        }

        // 2. 사용자 음성 명령 출력 (디버깅 목적)
        log.info("Received speech command: {}", pDTO.speechCommand());

        // 3. 음성 명령에 포함될 수 있는 '영화'와 유사한 키워드 목록 정의
        List<String> movieKeywords = List.of("영화", "영하", "연하", "연화");

        // 4. 입력된 음성 명령에서 키워드 중 하나라도 포함되어 있는지 확인
        boolean containsKeyword = movieKeywords.stream()
                .anyMatch(keyword -> pDTO.speechCommand().contains(keyword));

        // 5. 영화 관련 명령이 포함되어 있다면 영화 순위 조회, 아니면 빈 리스트 반환
        List<MovieDTO> rList = containsKeyword
                ? Optional.ofNullable(movieService.getMovieRank()).orElse(List.of())
                : List.of();

        log.info("{}.getMovie end!", getClass().getName());

        // 6. 정상 처리된 결과를 공통 응답 객체에 담아 반환
        return ResponseEntity.ok(CommonResponse.of(HttpStatus.OK, "SUCCESS", rList));
    }
}
