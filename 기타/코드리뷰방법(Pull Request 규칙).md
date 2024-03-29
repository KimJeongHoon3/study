코드리뷰방법(Pull Request 규칙)

- PR은 언제 요청할까?
  - 기본적으로 배포를 위한 dev 혹은 master브랜치에 머지를 하기 위한 pull request를 생각하기 쉽지만, 코드 리뷰가 되는 코드의 양을 줄이고, 리뷰를 원활하게 하기 위해 작은 단위의 pr 형태를 지향합니다.
- PR은 언제까지 요청할까?
  - 구글에서는 간단한 리뷰는 제출된 후 1시간 내로, 규모가 큰 리뷰는 제출된 후 4~5시간 이내로 완료해야 하는 rule이 있다. 개발팀의 규모와 진행 중인 프로젝트의 규모에 따라 다르겠지만, 최대한 PR을 가볍게 작성한다는 가정 하에 가능하면 빠른 시점에 코드 리뷰가 이루어 지는 것이 바람직하다.
  - 코드 리뷰의 데드라인은 리뷰를 요청하는 사람이 가장 잘 판단할 수 있다. 본인이 진행중인 개발 프로세스에서 해당 코드가 리뷰되어야 하는 시점의 중요성이 어느정도인지, 스프린트 상 어느 중요도와 우선 순위를 가지고 있는지 판단하여 리뷰 데드라인을 설정할 수 있다.
  - 리뷰 검토자는 놓친 코드리뷰가 존재하는 지 최소한 매일 업무 시작 시 반드시 확인하도록 하며, 요청자의 리뷰 우선순위/데드라인을 확인하고, 해당 데드라인을 지키지 못하는 경우에는 코멘트를 통해 사유를 공유하도록 한다.
  - PR 요청시 우선순위/데드라인설정을 명시해준다(라벨 활용)
    - 리뷰 우선순위 판단을 돕는 D-n 룰(뱅크샐러드)을 통해 적용 가능한 방식의 데드라인 설정 방식
      - ASAP : 즉시 리뷰가 필요합니다. 버그, 에러로 인해 빠른 배포가 필요합니다. (0시간 리뷰)
      - D-0 : 가능하면 당일 빠른 리뷰가 필요합니다. 작업 디펜던시가 존재하여 먼저 리뷰가 요구됩니다.(1시간~4시간 이내 리뷰)
      - D-x : 작업 디펜던시는 없으며, 적당한 시점에 리뷰가 완료되면 됩니다. (1~3일 리뷰)
        - D-x의 x에 숫자를 명시하는 것이 아니며, 코드 리뷰는 가급적 빨리 이루어지는 것이 바람직하다는 전제 하에, 리뷰어의 부담이 없는 형태의 요청입니다.
    - 모든 레파지토리에 동시에 라벨 적용하는 방법
      - https://velog.io/@rimo09/Github-github-label-%ED%95%9C%EB%B2%88%EC%97%90-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0
- 리뷰는 어떻게?
  - 무엇을 확인?
    - 기능의 정상 동작 여부
      - 리뷰 요청에 작성된 기능 변경/추가가 정상적으로 작동하는지
      - 기존 기능이 문제 없이 동작하는지
    - 버그 발견
      - 버그 유발할 수 있는 부분 확인
    - 가독성과 유지보수 편의성
      - 팀 차원의 코드 관리에서 중요하나, 논쟁의 여지가 있음.
      - “주관적인 견해로 옳고 그름을 따지는 비생산적 논쟁으로 변질될 수 있으므로 가급적 기준을 정하고 해당 기준을 기반으로 검토하는 것이 좋습니다. 예를 들어, 들여쓰기(indent)의 크기나, 코드 한 줄의 최대 길이, 메소드 호출의 깊이(depth )등의 표준을 정해두면 좀 더 객관적인 기준으로 코드리뷰를 수행할 수 있습니다.”
      - 자동화와 lint rule을 추가하는 방향으로 불필요한 소모적 논쟁을 줄이기.
    - 개발 표준(CONVENTION)의 준수 여부, 코딩 스타일 ( Indent, Convention )
      - 상황에 맞게 최소한의 기본적인 표준을 정하고, 하나씩 세부적으로 추가해나간다.
      - 정해지지 않은 컨벤션에 대해서 서로 다른 의견을 가지고 있는 경우, 기본적으로 비즈니스 로직이 이상해지지 않는 선에서 작성자의 convention을 따르며, 이후 팀 토크에서 해당 부분에 대한 컨벤션을 정하고(합의), 컨벤션을 적용해나간다.
    - 테스트 코드의 작성 여부
      - 팀에서 테스트 코드의 중요도에 대한 합의, 커버리지와 작성 방식에 대한 합의 필요
        - Ex. service 클래스의 public method는 반드시 테스트코드작성 할 것
    - 재사용 가능한 모듈의 중복 개발, 배울만한 점은 없는지, 코드 자체 개선 가능성이 있는지 제안

  - 리뷰시 커뮤니케이션 비용을 줄이기 위해 요청의 형태를 명확하게 제시
    - [Major-Must]: 에러, 기능 미구현, 오동작이 포함되어 있습니다. 반드시 반영해주세요.
    - [Major-Discussion]: 에러, 기능 미구현, 오동작이 포함되어 있습니다. 논의가 필요합니다. 의견을 듣고 싶습니다.
    - [Major-Request]: 에러, 기능 미구현, 오동작이 포함되어 있습니다. 다음 형태로/다음 기능을 반영해주실 것을 요청합니다.
    - [Recommend-Must]: 에러, 기능 미구현, 오동작은 없습니다. 다음 형태의 개선을 추천하고 반영해주세요.
    - [Recommend-Request]: 에러, 기능 미구현, 오동작은 없습니다. 다음 형태의 개선을 추천합니다만, 반영하지 않아도 좋고 제안해봅니다
    - [Question/Check]: 질문이 있습니다/확인하기 위한 코멘트입니다.
    - [Ok]: 문제 없습니다. 확인하였습니다.
    - [Good]: (어떠한 부분이) 좋습니다
    - ex. 
      ```
          [RECOMMEND-REQUEST] response에 대한 유효성 검사를 메서드로 따로 빼는것도 좋을것 같습니다~ 
          ...
      ```

- [참고사이트](https://medium.com/elecle-bike/%EC%BD%94%EB%93%9C-%EB%A6%AC%EB%B7%B0%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4%EB%A5%BC-%EB%8F%84%EC%9E%85-%EA%B0%9C%EC%84%A0%ED%95%98%EA%B3%A0%EC%9E%90-%ED%95%98%EB%8A%94%EB%8D%B0-%EC%96%B4%EB%96%BB%EA%B2%8C-%ED%95%B4%EC%95%BC%ED%95%A0%EA%B9%8C%EC%9A%94-1e5df5f8949b)