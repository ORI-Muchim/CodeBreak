package events;

/**
 * 모든 이벤트의 기본 클래스
 * 이벤트 기반 아키텍처를 위한 기본 인터페이스를 제공합니다.
 */
public abstract class Event {
    private final long timestamp;
    private final String eventType;
    
    protected Event(String eventType) {
        this.timestamp = System.currentTimeMillis();
        this.eventType = eventType;
    }
    
    /**
     * 이벤트가 발생한 시각
     * @return 타임스탬프 (밀리초)
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * 이벤트 타입
     * @return 이벤트 타입 문자열
     */
    public String getEventType() {
        return eventType;
    }
    
    @Override
    public String toString() {
        return String.format("%s{timestamp=%d, type='%s'}", 
                           getClass().getSimpleName(), timestamp, eventType);
    }
}
