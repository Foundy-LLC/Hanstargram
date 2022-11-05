# Hanstargram

## 폴더 구조

```console
root
    - base
        ViewBindingActivity
    - repository
        AuthRepository
    - source
        DataSource
    - util 
        DateTimeExtension
    - view
        - login
            LoginActivity or Fragment
            LoginViewModel
            LoginUiState
```

## 작업 순서

- 해당 이슈 본인에게 할당(없으면 생성)
- 브런치 생성(feature/foo-bar)
- 커밋, 커밋, 커밋
- PR 생성
    - Resolve 혹은 Fix 키워드를 본문에 삽입하여 자동 이슈 닫도록 함
- CI 통과
- 머지 into main
- 브런치 제거