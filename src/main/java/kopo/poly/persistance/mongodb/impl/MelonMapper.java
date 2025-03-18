package kopo.poly.persistance.mongodb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import kopo.poly.dto.MelonDTO;
import kopo.poly.persistance.mongodb.AbstractMongoDBComon;
import kopo.poly.persistance.mongodb.IMelonMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MelonMapper extends AbstractMongoDBComon implements IMelonMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertSong(List<MelonDTO> pList, String colNm) throws MongoException {

        log.info("{}.insertSong Start!", this.getClass().getName());

        int res;

        // 데이터를 저장할 컬렉션 생성
        if (super.createCollection(mongodb, colNm, "collectTime")) {
            log.info("{} 생성되었습니다.", colNm);
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
    public List<MelonDTO> getSongList(String colNm) throws MongoException {

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
    public List<MelonDTO> getSingerSongCnt(String colNm) throws MongoException {

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
    public List<MelonDTO> getSingerSong(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.getSingerSong Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

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
    public int dropCollection(String colNm) throws MongoException {

        log.info("{}.dropCollection Start!", this.getClass().getName());

        // 컬렉션 삭제하기
        int res = super.dropCollection(mongodb, colNm) ? 1 : 0;

        log.info("{}.dropCollection End!", this.getClass().getName());

        return res;
    }

    @Override
    public int insertManyField(String colNm, List<MelonDTO> pList) throws MongoException {

        log.info("{}.insertManyField Start!", this.getClass().getName());

        int res;

        // 데이터를 저장할 컬렉션 생성
        if (super.createCollection(mongodb, colNm, "collectTime")) {
            log.info("{} 생성되었습니다.", colNm);
        }

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        List<Document> list = new ArrayList<>();

        // 람다식 활용하여 병렬 처리(순서 상관없이 저장) parallelStream과 -> 사용
        pList.parallelStream().forEach(melon ->
                list.add(new Document(new ObjectMapper().convertValue(melon, Map.class))));

        // 람다식 활용하여 싱글 쓰레드
//        pList.forEach(melon ->
//                list.add(new Document(new ObjectMapper().convertValue(melon, Map.class))));

        // List<Document> 파라미터로 사용하며, 레코드 리스트 단위로 한번에 저장하기
        col.insertMany(list);

        res = 1;

        log.info("{}.insertManyField End!", this.getClass().getName());

        return res;
    }

    @Override
    public int updateField(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.updateField Start!", this.getClass().getName());

        int res;

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        String singer = CmmUtil.nvl(pDTO.singer());
        String updateSinger = CmmUtil.nvl(pDTO.updateSinger());

        log.info("pDTO : {}", pDTO);

        // 업데이트할 내용 설정 (singer 값을 updateSinger로 변경)
        Document update = new Document("$set", new Document("singer", updateSinger));

        // UPDATE MELON_20220321 SET singer = 'BTS' WHERE singer ='방탄소년단')
        // Filters.eq : singer ='방탄소년단'
        col.updateMany(Filters.eq("singer", singer), update);

        res = 1;

        log.info("{}.updateField End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<MelonDTO> getUpdateSinger(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.getUpdateSinger Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

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
    public int updateAddField(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.updateAddField Start!", this.getClass().getName());

        int res;

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        String singer = CmmUtil.nvl(pDTO.singer());
        String nickname = CmmUtil.nvl(pDTO.nickname());

        log.info("pDTO : {}", pDTO);

        // 업데이트할 내용 설정 (nickname 문서에 '아미' 추가)
        // 문서 추가는 수정과 동일하게 $set 사용하며, 기존 문서에 값이 없으면 추가함
        // Updates.set("nickname", nickname)
        
        // UPDATE MELON_20220321 SET singer = 'BTS' WHERE singer ='방탄소년단')
        // Filters.eq : singer ='방탄소년단'
        col.updateMany(Filters.eq("singer", singer),
                Updates.set("nickname", nickname));

        res = 1;

        log.info("{}.updateAddField End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<MelonDTO> getSingerSongNickname(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.getSingerSongNickname Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

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
    public int updateAddListField(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.updateAddListField Start!", this.getClass().getName());

        int res;

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        String singer = CmmUtil.nvl(pDTO.singer());
        List<String> member = pDTO.member();

        log.info("pColNm : {}", colNm);
        log.info("pDTO : {}", pDTO);

        // 업데이트할 내용 설정 (nickname 문서에 '아미' 추가)
        // 문서 추가는 수정과 동일하게 $set 사용하며, 기존 문서에 값이 없으면 추가함
        Document update = new Document("$set", new Document("member", member));

        // UPDATE MELON_20220321 SET singer = 'BTS' WHERE singer ='방탄소년단')
        // Filters.eq : singer ='방탄소년단'
        col.updateMany(Filters.eq("singer", singer), update);

        res = 1;

        log.info("{}.updateAddListField End!", this.getClass().getName());

        return res;
    }

    @Override
    public List<MelonDTO> getSingerSongMember(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.getSingerSongMember Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

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
    public int updateFieldAndAddField(String colNm, MelonDTO pDTO) throws MongoException {
        log.info("{}.updateFieldAndAddField Start!", this.getClass().getName());

        int res;

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        String singer = CmmUtil.nvl(pDTO.singer());
        String updateSinger = CmmUtil.nvl(pDTO.updateSinger());
        String addFieldValue = CmmUtil.nvl(pDTO.addFieldValue());

        log.info("pColNm : {}", colNm);
        log.info("pDTO : {}", pDTO);

        // 업데이트할 필드 (기존 필드 수정 & 새로운 필드 추가)
        Document update = new Document("$set", new Document("singer", updateSinger)
                .append("addData", addFieldValue));

        // UPDATE MELON_20220321 SET singer = 'BTS' WHERE singer ='방탄소년단')
        // Filters.eq : singer ='방탄소년단'
        col.updateMany(Filters.eq("singer", singer), update);

        res = 1;

        log.info("{}.updateFieldAndAddField End!", this.getClass().getName());

        return res;

    }

    @Override
    public List<MelonDTO> getSingerSongAddData(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.getSingerSongAddData Start!", this.getClass().getName());

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

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
    public int deleteDocument(String colNm, MelonDTO pDTO) throws MongoException {

        log.info("{}.deleteDocument Start!", this.getClass().getName());

        int res;

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        String singer = CmmUtil.nvl(pDTO.singer());

        log.info("pColNm : {}", colNm);
        log.info("pDTO : {}", pDTO);

        // DELETE FROM MELON_20220321 WHERE singer ='방탄소년단'
        // Filters.eq : singer ='방탄소년단'
        col.deleteMany(Filters.eq("singer", singer));

        res = 1;

        log.info("{}.deleteDocument End!", this.getClass().getName());

        return res;
    }

}


