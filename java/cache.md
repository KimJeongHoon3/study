cache

- springboot + ehcache
    - 내용정리 굿 
    - https://jojoldu.tistory.com/57
    - https://055055.tistory.com/15

- ehcache에 대한 상세한 설명 : https://javacan.tistory.com/entry/133

- ehcache.xml 의 설정
    - name	/ 캐시의 이름 / 필수
    - maxElementsInMemory /메모리에 저장될 수 있는 객체의 최대 개수 / 필수
    - eternal / 이 값이 true이면 timeout 관련 설정은 무시되고, Element가 캐시에서 삭제되지 않는다. / 필수
    - overflowToDisk	/ 메모리에 저장된 객체 개수가 maxElementsInMemory에서 지정한 값에 다다를 경우 디스크에 오버플로우 되는 객체는 저장할 지의 여부를 지정한다. / 필수
    - timeToIdleSeconds / Element가 지정한 시간 동안 사용(조회)되지 않으면 캐시에서 제거된다. 이 값이 0인 경우 조회 관련 만료 시간을 지정하지 않는다. 기본값은 0이다. / 선택
    - timeToLiveSeconds / Element가 존재하는 시간. 이 시간이 지나면 캐시에서 제거된다. 이 시간이 0이면 만료 시간을 지정하지 않는다. 기본값은 0이다. / 선택
    - diskPersistent / VM이 재 가동할 때 디스크 저장소에 캐싱된 객체를 저장할지의 여부를 지정한다. 기본값은 false이다. / 선택
    - diskExpiryThreadIntervalSeconds / Disk Expiry 쓰레드의 수행 시간 간격을 초 단위로 지정한다. 기본값은 120 이다. / 선택
    - memoryStoreEvictionPolicy / 객체의 개수가 maxElementsInMemory에 도달했을 때,메모리에서 객체를 어떻게 제거할 지에 대한 정책을 지정한다. 기본값은 LRU(least recently used) 이다. FIFO와 LFU(least frequently used)도 지정할 수 있다. / 선택
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
    updateCheck="false">
    <diskStore path="java.io.tmpdir" />

    <cache name="findMemberCache"
           maxEntriesLocalHeap="10000"
           maxEntriesLocalDisk="1000"
           eternal="false"
           diskSpoolBufferSizeMB="20"
           timeToIdleSeconds="300" timeToLiveSeconds="600"
           memoryStoreEvictionPolicy="LFU"
           transactionalMode="off">
        <persistence strategy="localTempSwap" />
    </cache>

</ehcache>
```