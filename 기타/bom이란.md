bom이란
- BOM (Bill of Materials)은 라이브러리나 프로젝트에서 사용되는 다양한 컴포넌트, 라이브러리, 플러그인 및 기타 종속성의 정확한 버전 정보를 기록한 목록입니다. BOM은 프로젝트의 일관성과 안정성을 유지하기 위해 주로 사용됩니다.
- 소프트웨어 프로젝트에서 BOM의 목적은 다음과 같습니다
  - 종속성 관리: BOM을 사용하면 프로젝트의 모든 종속성 버전을 한 곳에서 관리할 수 있습니다. 이를 통해 중복된 버전 정의를 제거하고, 프로젝트 전반에서 일관된 종속성 버전을 사용할 수 있습니다.
  - 버전 충돌 해결: 프로젝트의 여러 모듈이 동일한 라이브러리의 다른 버전에 의존할 때 버전 충돌이 발생할 수 있습니다. BOM을 사용하면, 전체 프로젝트에 대한 공통 종속성을 정의하고 버전 충돌을 해결할 수 있습니다.
  - 업데이트 및 유지 보수 용이성: BOM에서 종속성 버전을 업데이트하면, 프로젝트 전체의 해당 종속성이 자동으로 업데이트됩니다. 이렇게 하면 일일이 모든 모듈의 종속성을 검토하고 업데이트할 필요가 없어 유지 보수가 쉬워집니다.

- BOM은 메이븐(Maven)이나 그레이들(Gradle)과 같은 빌드 도구에서 종속성 관리에 사용됩니다. 이러한 빌드 도구에서 BOM을 설정하면 프로젝트의 모든 모듈이 해당 BOM의 종속성 버전을 사용하도록 강제할 수 있습니다.

- maven에서 어떻게 사용하나?
  - `<dependencyManagement>` 섹션에 BOM을 추가해야 합니다.
    ```xml
        <dependencyManagement>
            <dependencies>
                <dependency>
                <groupId>com.example</groupId>
                <artifactId>my-bom</artifactId>
                <version>1.0.0</version>
                <type>pom</type>
                <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>
    ```
  - 프로젝트의 다른 모듈에서 BOM에 정의된 종속성을 사용하려면, 해당 종속성을 `<dependencies>` 섹션에 추가하면 됩니다. 이때 버전 정보를 생략해도 BOM에서 관리되는 버전이 자동으로 적용됩니다.
    ```xml
        <dependencies>
            <dependency>
                <groupId>com.example</groupId>
                <artifactId>some-dependency</artifactId>
            </dependency>
        </dependencies>
    ```