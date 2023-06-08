### 리팩터링 및 고민해볼 것

- [x] 회의를 통해 변화한 요구사항의 API로 변경
- [x] 각각 쿼리 보내고, 결과물을 `Repository`에서 합치기 vs 테이블을 join해서 쿼리 보내기
- [x] Exception Response 만들어서 반환
- [ ] Controller, Service, Repository 계층에 대한 MockMvc 테스트코드 작성 -> 방학때 해보겠습니당...! 
- [x] Unhappy case에 대한 인수 테스트 작성 (실패하는 것부터 먼저 작성하는게 호흡이 짧아 좋다.)
- [x] `http-request.http`에서 Authentication header를 보내도록 작성
- [x] Order 인수 테스트 작성
- [x] 초기 가입 포인트 입력을 파라미터로 추출
- [x] OrderRequest에 valid 추가하기
- [x] 주문한 상품 장바구니에서 삭제
- [x] order price의 5%만큼 적립
