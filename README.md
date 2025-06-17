# Code ∧ Break

**프로그래머를 위한 GUI 기반 시간 관리 및 알림 시스템**

## 프로젝트 개요

"Code ∧ Break"는 학생들이 코딩에 몰입하여 장시간 앉아있는 문제를 해결하기 위한 프로그램입니다. 사용자가 설정한 시간 간격마다 휴식 알림을 제공하고, 건강한 코딩 습관을 유지하도록 도와주는 GUI 기반 타이머입니다.

## 주요 기능

### 🎯 타이머 기능
- **포모도로 방식**: 25분 작업 + 5분 휴식의 전통적인 포모도로 기법
- **사용자 정의 시간**: 원하는 작업/휴식 시간 설정 가능
- **자동 사이클 전환**: 작업과 휴식이 자동으로 교대

### 🔔 다양한 알림 기능
- **팝업 알림**: 시각적 알림 다이얼로그
- **소리 알림**: 시스템 알림음
- **화면 깜빡임**: 주의를 끌기 위한 화면 효과
- **알림 유형**: 휴식, 스트레칭, 물 마시기, 눈 휴식 등

### ⚙️ 고급 기능
- **시스템 트레이 지원**: 백그라운드에서 실행 가능
- **프로필 관리**: 다양한 타이머 설정 저장/불러오기
- **알림 제어**: 일시 중지, 연기, 무시, 리셋 기능
- **스누즈 기능**: 5분 후 다시 알림

### 🎨 사용자 인터페이스
- **직관적인 GUI**: Java Swing 기반의 깔끔한 인터페이스
- **실시간 진행 표시**: 남은 시간과 진행률 표시
- **상태 표시**: 현재 페이즈(작업/휴식)와 사이클 정보

## 시스템 요구사항

- **Java**: JDK 8 이상
- **운영체제**: Windows, macOS, Linux
- **메모리**: 최소 256MB RAM
- **디스크 공간**: 50MB

## 설치 및 실행

### 1. 소스 코드 다운로드
```bash
git clone [repository-url]
cd CodeBreak
```

### 2. 컴파일 및 실행

#### Windows:
```batch
run.bat
```

#### Unix/Linux/macOS:
```bash
chmod +x run.sh
./run.sh
```

#### 수동 컴파일:
```bash
# 컴파일
mkdir bin
javac -d bin -sourcepath src src/CodeBreakApplication.java src/model/*.java src/view/*.java src/controller/*.java

# 실행
cd bin
java CodeBreakApplication
```

### 3. 명령행 옵션

```bash
java CodeBreakApplication [옵션]

옵션:
  -m, --minimized        최소화된 상태로 시작
  --no-tray              시스템 트레이 비활성화
  -d, --debug            디버그 모드 활성화
  -p, --profile NAME     지정된 프로필로 시작
  --auto-start           타이머 자동 시작
  -h, --help             도움말 표시
  -v, --version          버전 정보 표시
```

## 사용법

### 기본 사용법
1. 애플리케이션 실행
2. 타이머 탭에서 작업/휴식 시간 설정
3. 시작 버튼 클릭
4. 알림이 나타나면 적절히 대응

### 프로필 관리
1. 설정 탭 → 프로필 탭 이동
2. 새 프로필 생성 또는 기존 프로필 선택
3. 원하는 설정으로 조정
4. 저장 버튼 클릭

### 알림 설정
1. 설정 탭 → 알림 탭 이동
2. 원하는 알림 유형 선택
3. 소리, 팝업, 화면 깜빡임 옵션 조정
4. 테스트 탭에서 알림 테스트 가능

### 시스템 트레이 사용
- 트레이 아이콘 더블클릭: 창 표시/숨기기
- 트레이 아이콘 우클릭: 컨텍스트 메뉴
- 트레이에서 타이머 제어 및 프로필 변경 가능

## 프로젝트 구조

```
CodeBreak/
├── src/
│   ├── model/                 # 데이터 모델
│   │   ├── TimerModel.java   # 타이머 로직
│   │   ├── NotificationManager.java  # 알림 관리
│   │   ├── UserProfile.java  # 사용자 프로필
│   │   └── JsonDataManager.java  # 데이터 저장
│   ├── view/                  # GUI 컴포넌트
│   │   ├── MainFrame.java    # 메인 창
│   │   ├── TimerPanel.java   # 타이머 패널
│   │   ├── SettingsPanel.java # 설정 패널
│   │   └── NotificationDialog.java # 알림 다이얼로그
│   ├── controller/            # 제어 로직
│   │   ├── TimerController.java # 타이머 제어
│   │   ├── SettingsController.java # 설정 제어
│   │   └── SystemTrayController.java # 트레이 제어
│   └── CodeBreakApplication.java # 메인 클래스
├── data/                      # 설정 파일 저장 위치
├── bin/                      # 컴파일된 클래스 파일
├── run.bat                   # Windows 실행 스크립트
├── run.sh                    # Unix/Linux/macOS 실행 스크립트
└── README.md                 # 이 파일
```

## 기술 스택

- **언어**: Java
- **GUI 프레임워크**: Java Swing
- **데이터 저장**: JSON 파일
- **아키텍처**: MVC (Model-View-Controller) 패턴
- **빌드 도구**: javac (네이티브 Java 컴파일러)

## 주요 특징

### MVC 아키텍처
- **Model**: 타이머 로직, 알림 설정, 사용자 프로필 관리
- **View**: GUI 컴포넌트, 알림 화면
- **Controller**: 사용자 입력 처리, 모델-뷰 연결

### 확장 가능한 설계
- 플러그인 방식의 알림 유형 추가 가능
- 새로운 타이머 모드 쉽게 추가 가능
- 다양한 데이터 저장 방식 지원 가능

## 트러블슈팅

### 시스템 트레이가 작동하지 않는 경우
```bash
java CodeBreakApplication --no-tray
```

### 알림이 표시되지 않는 경우
1. 설정 → 알림 탭에서 알림이 활성화되어 있는지 확인
2. 운영체제의 알림 권한 설정 확인
3. 테스트 탭에서 알림 테스트

### 컴파일 오류가 발생하는 경우
1. Java 버전 확인 (JDK 8 이상 필요)
2. JAVA_HOME 환경 변수 설정 확인
3. 모든 소스 파일이 올바른 위치에 있는지 확인

## 라이선스

이 프로젝트는 교육 목적으로 개발되었습니다.

## 개발자 정보

- **개발자**: 조민형
- **프로젝트**: Term Project
- **연도**: 2025
- **버전**: 1.0

---

**건강한 코딩 습관을 위해 Code ∧ Break와 함께하세요!** 💻✨
