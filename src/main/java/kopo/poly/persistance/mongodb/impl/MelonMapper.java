package kopo.poly.persistance.mongodb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.MelonDTO;
import kopo.poly.persistance.mongodb.AbstractMongoDBComon;
import kopo.poly.persistance.mongodb.IMelonMapper;
import kopo.poly.util.CmmUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mongodb.client.model.Updates.set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MelonMapper extends AbstractMongoDBComon implements IMelonMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertSong(@NonNull List<MelonDTO> pList, @NonNull String colNm) throws Exception {

        log.info("{}.insertSong Start!", this.getClass().getName());

        int res;

        // 데이터를 저장할 컬렉션 생성
        // MongoDB에 지정된 컬렉션 이름(colNm)을 사용하여 새로운 컬렉션을 생성합니다.
        // "collectTime"은 시간 기반으로 데이터를 정렬하거나 관리하기 위해 추가되는 인덱스입니다.
        if (super.createCollection(mongodb, colNm, "collectTime")) {
            log.info("Create {} Collection!", colNm);

        }

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        for (MelonDTO pDTO : pList) {
            col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class))); // 레코드 한개씩 저장하기
        }

        res = 1;

        log.info("{}.insertSong End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<MelonDTO> getSongList(@NonNull String colNm) throws Exception {

        log.info("{}.getSongList Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(new Document()).projection(projection);

        for (Document doc : rs) {
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

            log.info("song : {}/ singer : {}", song, singer);

            MelonDTO rDTO = MelonDTO.builder().song(song).singer(singer).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }
        log.info("{}.getSongList End!", this.getClass().getName());

        return rList;
    }

    @Override
    public List<MelonDTO> getSingerSongCnt(@NonNull String colNm) throws Exception {

        log.info("{}.getSingerSongCnt Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        // MongoDB 조회 쿼리
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().append("$group",
                        new Document().append("_id", new Document().append("singer", "$singer")).append("COUNT(singer)",
                                new Document().append("$sum", 1))),
                new Document()
                        .append("$project",
                                new Document().append("singer", "$_id.singer").append("singerCnt", "$COUNT(singer)")
                                        .append("_id", 0)),
                new Document().append("$sort", new Document().append("singerCnt", -1)));

        MongoCollection<Document> col = mongodb.getCollection(colNm);
        AggregateIterable<Document> rs = col.aggregate(pipeline).allowDiskUse(true);

        for (Document doc : rs) {
            String singer = doc.getString("singer");
            int singerCnt = doc.getInteger("singerCnt", 0);

            log.info("singer : {}/ singerCnt : {}", singer, singerCnt);

            MelonDTO rDTO = MelonDTO.builder().singer(singer).singerCnt(singerCnt).build();

            rList.add(rDTO);

        }

        log.info("{}.getSingerSongCnt End!", this.getClass().getName());

        return rList;
    }

    @Override
    public List<MelonDTO> getSingerSong(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.getSingerSong Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 조회할 조건(SQL의 WHERE 역할 /  SELECT song, singer FROM MELON_20220321 where singer ='방탄소년단')
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.singer()));

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

            MelonDTO rDTO = MelonDTO.builder().song(song).singer(singer).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }
        log.info("{}.getSingerSong End!", this.getClass().getName());

        return rList;

    }

    @Override
    public int dropCollection(@NonNull String colNm) throws Exception {

        log.info("{}.dropCollection Start!", this.getClass().getName());

        if (super.dropCollection(mongodb, colNm)) {
            log.info("Drop {} Collection", colNm);
        }

        log.info("{}.dropCollection End!", this.getClass().getName());

        return 1;
    }

    @Override
    public int insertManyField(@NonNull String colNm, @NonNull List<MelonDTO> pList) throws Exception {

        log.info("{}.insertManyField Start!", this.getClass().getName());

        // 데이터를 저장할 컬렉션 생성
        // MongoDB에 지정된 컬렉션 이름(colNm)을 사용하여 새로운 컬렉션을 생성합니다.
        // "collectTime"은 시간 기반으로 데이터를 정렬하거나 관리하기 위해 추가되는 인덱스입니다.
        if (super.createCollection(mongodb, colNm, "collectTime")) {
            log.info("Create {} Collection!", colNm);

        }

        // 저장할 컬렉션 객체 생성
        // MongoDB에서 colNm에 해당하는 컬렉션을 가져옵니다.
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // List<Document> 변환 및 저장
        // 입력받은 pList(MelonDTO 객체 리스트)를 병렬 스트림(parallelStream)으로 처리하여
        // 각각의 MelonDTO 객체를 Map 형태로 변환한 뒤, MongoDB Document로 매핑합니다.
        List<Document> list = pList.parallelStream()
                .map(melon -> new Document(new ObjectMapper().convertValue(melon, Map.class)))
                .toList();

        // 레코드 리스트 단위로 한번에 저장
        // 변환된 Document 리스트를 MongoDB 컬렉션에 한 번에 삽입합니다.
        col.insertMany(list);

        log.info("{}.insertManyField End!", this.getClass().getName());

        // 작업이 성공적으로 완료되었음을 나타내는 값 반환
        return 1;
    }

    @Override
    public int updateField(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.updateField Start!", this.getClass().getName());

        // MongoDB 컬렉션 가져오기
        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 입력받은 필드 값 확인
        String singer = CmmUtil.nvl(pDTO.singer());
        String updateSinger = CmmUtil.nvl(pDTO.updateSinger());

        log.info("pColNm: {}, singer: {}, updateSinger: {}", pColNm, singer, updateSinger);

        // 수정 조건 설정 (SQL의 WHERE 절과 유사)
        Document query = new Document("singer", singer);

        // 수정할 데이터 설정
        Document update = new Document("$set", new Document("singer", updateSinger));

        // 조건에 맞는 모든 데이터 수정
        col.updateMany(query, update);

        log.info("{}.updateField End!", this.getClass().getName());

        return 1;
    }

    @Override
    public List<MelonDTO> getUpdateSinger(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.getUpdateSinger Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 조회할 조건(SQL의 WHERE 역할 /  SELECT song, singer FROM MELON_20220321 where singer ='방탄소년단')
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.updateSinger()));

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {

            // MongoDB 조회 결과를 MelonDTO 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

            log.info("song : {}/ singer : {}", song, singer);

            MelonDTO rDTO = MelonDTO.builder().song(song).singer(singer).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }
        log.info("{}.getUpdateSinger End!", this.getClass().getName());

        return rList;
    }

    @Override
    public int updateAddField(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.updateAddField Start!", this.getClass().getName());

        int res;

        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        String singer = CmmUtil.nvl(pDTO.singer());
        String nickname = CmmUtil.nvl(pDTO.nickname());

        log.info("pColNm : {}", pColNm);
        log.info("singer : {}", singer);
        log.info("nickname : {}", nickname);

        // 조회할 조건(SQL의 WHERE 역할 /  SELECT * FROM MELON_20220321 where singer ='방탄소년단')
        Document query = new Document();
        query.append("singer", singer);

        // MongoDB 데이터 삭제는 반드시 컬렉션을 조회하고, 조회된 ObjectID를 기반으로 데이터를 삭제함
        // MongoDB 환경은 분산환경(Sharding)으로 구성될 수 있기 때문에 정확한 PK에 매핑하기 위해서임
        FindIterable<Document> rs = col.find(query);

        // 람다식 활용하여 컬렉션에 조회된 데이터들을 수정하기
        // MongoDB Driver는 MongoDB의 "$set" 함수를 대신할 자바 함수를 구현함
        rs.forEach(doc -> col.updateOne(doc, set("nickname", nickname)));

        res = 1;

        log.info("{}.updateAddField End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<MelonDTO> getSingerSongNickname(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.getSingerSongNickname Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 조회할 조건(SQL의 WHERE 역할 /  SELECT song, singer FROM MELON_20220321 where singer ='방탄소년단')
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.singer()));

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");
        projection.append("nickname", "$nickname");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {

            // MongoDB 조회 결과를 MelonDTO 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));
            String nickname = CmmUtil.nvl(doc.getString("nickname"));

            log.info("song : {}/ singer : {}/ nickname : {}", song, singer, nickname);

            MelonDTO rDTO = MelonDTO.builder().song(song).singer(singer).nickname(nickname).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }
        log.info("{}.getSingerSongNickname End!", this.getClass().getName());

        return rList;
    }

    @Override
    public int updateAddListField(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.updateAddListField Start!", this.getClass().getName());

        // MongoDB 컬렉션 가져오기
        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 입력받은 필드 값 확인
        String singer = CmmUtil.nvl(pDTO.singer());
        List<String> member = pDTO.member();

        log.info("pColNm: {}, singer: {}, member: {}", pColNm, singer, member);

        // 수정 조건 설정 (SQL의 WHERE 절과 유사)
        Document query = new Document("singer", singer);

        // 수정할 데이터 설정
        Document update = new Document("$set", new Document("member", member));

        // 조건에 맞는 모든 데이터 수정
        col.updateMany(query, update);

        log.info("{}.updateAddListField End!", this.getClass().getName());

        return 1;
    }

    @Override
    public List<MelonDTO> getSingerSongMember(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.getSingerSongMember Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 조회할 조건(SQL의 WHERE 역할 /  SELECT song, singer FROM MELON_20220321 where singer ='방탄소년단')
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.singer()));

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");
        projection.append("member", "$member");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {

            // MongoDB 조회 결과를 MelonDTO 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));
            List<String> member = doc.getList("member", String.class, new ArrayList<>());

            log.info("song : {}/ singer : {}/ member : {}", song, singer, member);

            MelonDTO rDTO = MelonDTO.builder().song(song).singer(singer).member(member).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }
        log.info("{}.getSingerSongMember End!", this.getClass().getName());

        return rList;

    }

    @Override
    public int updateFieldAndAddField(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {
        log.info("{}.updateFieldAndAddField Start!", this.getClass().getName());

        // MongoDB 컬렉션 가져오기
        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 입력받은 필드 값 확인
        String singer = CmmUtil.nvl(pDTO.singer());
        String updateSinger = CmmUtil.nvl(pDTO.updateSinger());
        String addFieldValue = CmmUtil.nvl(pDTO.addFieldValue());

        log.info("pColNm: {}, singer: {}, updateSinger: {}, addFieldValue: {}",
                pColNm, singer, updateSinger, addFieldValue);

        // 수정 조건 설정 (SQL의 WHERE 절과 유사)
        Document query = new Document("singer", singer);

        // 수정할 데이터 설정 (기존 필드 수정 및 신규 필드 추가)
        Document update = new Document("$set", new Document()
                .append("singer", updateSinger)
                .append("addData", addFieldValue));

        // 조건에 맞는 모든 데이터 수정
        col.updateMany(query, update);

        log.info("{}.updateFieldAndAddField End!", this.getClass().getName());

        return 1;
    }

    @Override
    public List<MelonDTO> getSingerSongAddData(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.getSingerSongAddData Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 조회할 조건(SQL의 WHERE 역할 /  SELECT song, singer FROM MELON_20220321 where singer ='방탄소년단')
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.updateSinger())); // 이전 실행에서 가수이름이 변경되어 변경시킬 값으로 적용

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");
        projection.append("addData", "$addData");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {

            // MongoDB 조회 결과를 MelonDTO 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));
            String addData = CmmUtil.nvl(doc.getString("addData"));

            log.info("song : {}/ singer : {}/ addData : {}", song, singer, addData);

            MelonDTO rDTO = MelonDTO.builder().song(song).singer(singer).addFieldValue(addData).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }
        log.info("{}.getSingerSongAddData End!", this.getClass().getName());

        return rList;
    }

    @Override
    public int deleteDocument(@NonNull String pColNm, @NonNull MelonDTO pDTO) throws Exception {

        log.info("{}.deleteDocument Start!", this.getClass().getName());

        // MongoDB 컬렉션 가져오기
        MongoCollection<Document> col = mongodb.getCollection(pColNm);

        // 입력받은 필드 값 확인
        String singer = CmmUtil.nvl(pDTO.singer());

        log.info("pColNm: {}, singer: {}", pColNm, singer);

        // 삭제 조건 설정 (SQL의 WHERE 절과 유사)
        Document query = new Document("singer", singer);

        // 조건에 맞는 데이터 삭제
        col.deleteMany(query);

        log.info("{}.deleteDocument End!", this.getClass().getName());

        return 1;
    }


}


