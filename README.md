클린 아키텍처의 핵심 규칙: '의존성 규칙(The Dependency Rule)'

클린 아키텍처의 가장 중요한 규칙은 의존성의 방향이 항상 안쪽으로만 향해야 한다는 것입니다.
**내부 계층(Entities, Use Cases)**은 외부 계층(Databases, Web)에 대해 아무것도 몰라야 합니다.
반면, 외부 계층은 내부 계층에 대해 알고 있어야 합니다.

이 규칙 덕분에 내부 계층의 변경은 외부 계층에 영향을 주지 않지만, 외부 계층의 변경은 내부 계층에 영향을 미치지 않습니다. 위 예제에서 UserUseCase가 UserRepository라는 인터페이스에 의존하고, 구체적인 DatabaseUserRepository가 그 인터페이스를 구현함으로써 이 규칙을 완벽하게 따르고 있습니다. 이것이 바로 의존성 역전 원칙이 클린 아키텍처의 핵심 기반이 되는 이유입니다.


<img width="721" height="711" alt="image" src="https://github.com/user-attachments/assets/f4c09ddc-337f-47ce-97d7-cdc755e5e0de" />

<img width="721" height="706" alt="image" src="https://github.com/user-attachments/assets/b9a731d5-23d0-40fe-a805-232068564f51" />

