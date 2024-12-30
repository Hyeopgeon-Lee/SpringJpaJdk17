package kopo.poly.persistance.mongodb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.MongoDTO;
import kopo.poly.persistance.mongodb.AbstractMongoDBComon;
import kopo.poly.persistance.mongodb.IMongoMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class MongoMapper extends AbstractMongoDBComon implements IMongoMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertData(@NonNull MongoDTO pDTO, @NonNull String colNm) throws Exception {

        log.info("{}.insertData Start!", this.getClass().getName());

        // 데이터를 저장할 컬렉션 생성
        if (super.createCollection(mongodb, colNm)) {
            log.info("Create {} Collection!", colNm);
        }

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // DTO를 Document로 변환 후 저장
        Document doc = new Document(new ObjectMapper().convertValue(pDTO, Map.class));
        col.insertOne(doc);

        log.info("{}.insertData End!", this.getClass().getName());

        return 1;
    }

}

