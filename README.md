# GitHub Api Task

Aplikacja Spring Boot, która pobiera z publicznego API GitHub listę repozytoriów użytkownika (które nie są forkami) oraz informacje o ich brancach.

## Technologie
- Java 21
- Spring Boot 3.5
- Maven

## Uruchomienie

Wymagania:
- Java 21
- Maven

Aby uruchomić aplikację:
```bash
mvn clean spring-boot:run
```
Po uruchomieniu aplikacja działa domyślnie pod adresem:
http://localhost:8080

## Endpoint
Aby pobrać listę repozytoriów użytkownika (bez forków) i ich branche:
```bash
GET /api/github/users/{username}/repos
```
Zamień {username} na login użytkownika GitHub. 

**Przykład:** http://localhost:8080/api/github/users/octocat/repos

Aplikacja zwróci listę repozytoriów, które nie są forkami, wraz z ich branchami i ostatnim commit SHA.


**Przykład odpowiedzi:**
```json
[
  {
    "repositoryName": "main-repo",
    "ownerLogin": "exampleuser",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "sha123"
      },
      {
        "name": "dev",
        "lastCommitSha": "sha456"
      }
    ]
  }
]
```
**Obsługa błędów:**

Jeśli użytkownik nie istnieje, API zwraca odpowiedź:
```json
{
  "status": 404,
  "message": "User exampleuser not found"
}
```
## Testy
Aby uruchomić testy:
```bash
mvn test
```
