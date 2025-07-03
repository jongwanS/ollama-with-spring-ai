### 1. redis stack 실행
````shell
#레디스 설치
docker-compose up -d

#레디스 접속
docker exec -it redis-stack redis-cli

#키 생성 
FT.CREATE spring-ai-index ON JSON PREFIX 1 embedding: SCHEMA $.embedding AS embedding VECTOR FLAT 6 TYPE FLOAT32 DIM 768 DISTANCE_METRIC COSINE $.content AS content TEXT $.metadata.source AS metadata_source TAG
````

- 뭔지는 모르겠지만, 레디스를 vector-store로 사용하기 위해 키를 만드는것...
  - 나중에 다시 확인해봐야지....

| 구성 요소                             | 의미                         |
| --------------------------------- | -------------------------- |
| `FT.CREATE spring-ai-index`       | 인덱스 이름                     |
| `ON JSON`                         | RedisJSON 대상               |
| `PREFIX 1 embedding:`             | `embedding:`으로 시작하는 키만 인덱싱 |
| `$.embedding ... VECTOR`          | 벡터 필드 정의 (유사도 검색의 핵심)      |
| `$.content AS content TEXT`       | 본문 텍스트 필드                  |
| `$.metadata.source AS source TAG` | 메타데이터 태그 필드                |

### 2. redis-insight 접속
- http://localhost:8001

### 3. test
````bash
curl -X POST http://localhost:8080/chat \
     -H "Content-Type: text/plain" \
     --data "face"
     
curl "http://localhost:8080/ask?q=most%20widely%20spoken%20languages%20in%20the%20world"



curl -X POST http://localhost:8080/chat/eng-tutor \
     -H "Content-Type: text/plain" \
     --data "i want to be fluent at englisaah"
````
### 4. 유사도
- 매커니즘을 제대로 모르겠음. 임베딩을 하는데 어떤기준으로 임베딩을 하는지...?
````text
List<Document> result = vectorStore.similaritySearch("How To improve your English");

# 이렇게 나옴
How To improve your English => To improve your English, practice speaking, listening, reading, and writing regularly.

Why is grammar important? => Grammar helps you structure sentences correctly.
````
