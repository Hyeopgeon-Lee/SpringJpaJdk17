package kopo.poly.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.repository.NoticeFetchRepository;
import kopo.poly.repository.NoticeJoinRepository;
import kopo.poly.repository.NoticeRepository;
import kopo.poly.repository.NoticeSQLRepository;
import kopo.poly.repository.entity.*;
import kopo.poly.service.INoticeJoinService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeJoinService implements INoticeJoinService {

    private final NoticeRepository noticeRepository; // @JoinColumn 적용된 공지사항

    private final NoticeJoinRepository noticeJoinRepository; // @JoinColumn 적용된 공지사항

    private final NoticeSQLRepository noticeSQLRepository; // NativeQuery 사용을 위한 Repository

    private final NoticeFetchRepository noticeFetchRepository; // JPQL 사용을 위한 Repository

    private final JPAQueryFactory queryFactory;

    @Override
    public List<NoticeDTO> getNoticeListUsingJoinColumn() {
        log.info(this.getClass().getName() + ".getNoticeListUsingJoinColumn Start!");

        // 공지사항 전체 리스트 조회하기
        List<NoticeJoinEntity> rList = noticeJoinRepository.findAllByOrderByNoticeYnDescNoticeSeqDesc();

        List<NoticeDTO> list = new LinkedList<>(); // 조회 결과를 List<NoticeDTO> 변환하기 위해 사용

        rList.forEach(rEntity -> { // 람다식 사용

            // Entity 결과를 DTO 저장하기위해 결과 변수를 담기
            long noticeSeq = rEntity.getNoticeSeq(); // 공지사항 순번 PK
            String noticeYn = CmmUtil.nvl(rEntity.getNoticeYn()); // 공지글 여부
            String title = CmmUtil.nvl(rEntity.getTitle()); // 공지사항 제목
            long readCnt = rEntity.getReadCnt(); // 공지사항 조회수
            String userName = CmmUtil.nvl(rEntity.getUserInfoEntity().getUserName()); // 공지사항 작성자(JoinColumn) 활용
            String regDt = CmmUtil.nvl(rEntity.getRegDt()); // 공지사항 작성일시

            // 로그 출력
            log.info("noticeSeq : " + noticeSeq);
            log.info("noticeYn : " + noticeYn);
            log.info("title : " + title);
            log.info("readCnt : " + readCnt);
            log.info("userName : " + userName);
            log.info("regDt : " + regDt);
            log.info("----------------------------");

            // Entity 결과를 DTO 저장하기
            NoticeDTO rDTO = NoticeDTO.builder()
                    .noticeSeq(noticeSeq).noticeYn(noticeYn)
                    .title(title).readCnt(readCnt).userName(userName).regDt(regDt).build();

            list.add(rDTO); // 레코드마다 추가하기

        });

        log.info(this.getClass().getName() + ".getNoticeListUsingJoinColumn End!");

        return list;
    }

    @Override
    public List<NoticeDTO> getNoticeListUsingNativeQuery() {
        log.info(this.getClass().getName() + ".getNoticeListUsingNativeQuery Start!");

        // 공지사항 전체 리스트 조회하기
        List<NoticeSQLEntity> rList = noticeSQLRepository.getNoticeListUsingSQL();

        // 엔티티의 값들을 DTO에 맞게 넣어주기
        List<NoticeDTO> nList = new ObjectMapper().convertValue(rList,
                new TypeReference<List<NoticeDTO>>() {
                });

        log.info(this.getClass().getName() + ".getNoticeListUsingNativeQuery End!");

        return nList;
    }


    @Override
    public List<NoticeDTO> getNoticeListUsingJPQL() {
        log.info(this.getClass().getName() + ".getNoticeListUsingJPQL Start!");

        // 공지사항 전체 리스트 조회하기
        List<NoticeFetchEntity> rList = noticeFetchRepository.getListFetchJoin();

        // 엔티티의 값들을 DTO에 맞게 넣어주기
        List<NoticeDTO> nList = new ArrayList<>();

        rList.forEach(e -> {
                    NoticeDTO rDTO = NoticeDTO.builder().
                            noticeSeq(e.getNoticeSeq()).title(e.getTitle()).noticeYn(e.getNoticeYn())
                            .readCnt(e.getReadCnt()).userId(e.getUserId())
                            .userName(e.getUserInfo().getUserName()).build(); // 회원이름

                    nList.add(rDTO);
                }
        );

        log.info(this.getClass().getName() + ".getNoticeListUsingJPQL End!");

        return nList;
    }

    @Transactional
    @Override
    public List<NoticeDTO> getNoticeListForQueryDSL() {
        log.info(this.getClass().getName() + ".getNoticeListForQueryDSL Start!");

        // QueryDSL 라이브러리를 추가하면, JPA 엔티티들은 Q붙여서 QueryDSL에서 처리가능한 객체를 생성함
        // 예 : NoticeEntity -> QNoticeEntity 객체 생성
        QNoticeFetchEntity ne = QNoticeFetchEntity.noticeFetchEntity;
        QUserInfoEntity ue = QUserInfoEntity.userInfoEntity;

        // 공지사항 전체 리스트 조회하기
        List<NoticeFetchEntity> rList = queryFactory
                .selectFrom(ne) // 조회할 Entity 및 항목 정의
                .join(ne.userInfo, ue) // Inner Joint 적용
                // 공지사항은 위로, 공지사항이 아닌 글들은 아래로 정렬한 뒤, 글 순번이 큰 순서대로 정렬
                .orderBy(ne.noticeYn.desc(), ne.noticeSeq.desc())
                .fetch(); // 결과를 리스트 구조로 반환하기

        List<NoticeDTO> nList = new ArrayList<>();

        rList.forEach(e -> {
                    NoticeDTO rDTO = NoticeDTO.builder().
                            noticeSeq(e.getNoticeSeq()).title(e.getTitle()).noticeYn(e.getNoticeYn())
                            .readCnt(e.getReadCnt()).userId(e.getUserId())
                            .userName(e.getUserInfo().getUserName()).build(); // 회원이름

                    nList.add(rDTO);
                }
        );

        log.info(this.getClass().getName() + ".getNoticeListForQueryDSL End!");

        return nList;
    }

    @Transactional
    @Override
    public NoticeDTO getNoticeInfoForQueryDSL(NoticeDTO pDTO, boolean type) throws Exception {
        log.info(this.getClass().getName() + ".getNoticeInfoForQueryDSL Start!");

        if (type) {
            // 조회수 증가하기
            int res = noticeRepository.updateReadCnt(pDTO.noticeSeq());

            // 조회수 증가 성공여부 체크
            log.info("res : " + res);
        }

        // QueryDSL 라이브러리를 추가하면, JPA 엔티티들은 Q붙여서 QueryDSL에서 처리가능한 객체를 생성함
        // 예 : NoticeEntity -> QNoticeEntity 객체 생성
        QNoticeEntity ne = QNoticeEntity.noticeEntity;

        // 공지사항 상세내역 가져오기
        // 공지사항 전체 리스트 조회하기
        NoticeEntity rEntity = queryFactory
                .selectFrom(ne) // 조회할 Entity 및 항목 정의
                .where(ne.noticeSeq.eq(pDTO.noticeSeq())) // Where 조건 정의 : where notice_seq = 10
                .fetchOne(); // 결과를 리스트 구조로 반환하기

        NoticeDTO rDTO = NoticeDTO.builder().noticeSeq(rEntity.getNoticeSeq())
                .title(rEntity.getTitle())
                .noticeYn(rEntity.getNoticeYn())
                .regDt(rEntity.getRegDt())
                .userId(rEntity.getUserId())
                .readCnt(rEntity.getReadCnt())
                .contents(rEntity.getContents()).build();

        log.info(this.getClass().getName() + ".getNoticeInfoForQueryDSL End!");

        return rDTO;
    }
}
