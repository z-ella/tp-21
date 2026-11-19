# TP Microservices : Eureka + Spring Boot + WebClient

Trois projets Maven indépendants :

| Projet | Port | Rôle |
|---|---|---|
| `eureka-server` | 8761 | Registre de services (Eureka) |
| `service-client` | 8081 | CRUD clients, base MySQL `clientservicedb` |
| `service-car` | 8082 | CRUD voitures, base MySQL `carservicedb`, appelle `service-client` via `WebClient` |

## Prérequis
- Java 17+
- Maven
- MySQL local (port 3306), utilisateur `root` sans mot de passe (modifiable dans chaque `application.yml`)
- Postman, curl, ou l'extension REST Client / IntelliJ HTTP (voir `requests.http`)

Versions : Spring Boot 3.3.5, Spring Cloud 2023.0.3.

## Lancement (dans cet ordre)
```bash
cd eureka-server  && mvn spring-boot:run   # http://localhost:8761
cd service-client && mvn spring-boot:run   # 8081
cd service-car    && mvn spring-boot:run   # 8082
```
Le dashboard Eureka doit lister `SERVICE-CLIENT` et `SERVICE-CAR`.

## Scénario de test
1. `POST http://localhost:8081/api/clients` avec `{ "nom": "Salma", "age": 22 }`
2. `POST http://localhost:8082/api/cars` avec `{ "marque": "Toyota", "modele": "Yaris", "clientId": 1 }`
3. `GET http://localhost:8082/api/cars` :

```json
[
  {
    "id": 1,
    "marque": "Toyota",
    "modele": "Yaris",
    "clientId": 1,
    "client": { "id": 1, "nom": "Salma", "age": 22.0 }
  }
]
```

## Points clés
- `@LoadBalanced` sur `WebClient.Builder` permet d'utiliser `http://SERVICE-CLIENT/...` (résolution via Eureka).
- Pas de relation JPA entre services : `Car` stocke `clientId`, et le client est récupéré par appel HTTP (`@Transient`).
- `block()` est utilisé pour simplifier le TP ; en production, rester réactif (`Mono`/`Flux`).
- `ClientApi` renvoie `null` si le client est introuvable (404), au lieu de faire échouer toute la liste.

## Dépannage
- `No instances available for SERVICE-CLIENT` : `@LoadBalanced` manquant, dépendance LoadBalancer absente, ou service-client non enregistré dans Eureka.
- Erreur MySQL au démarrage : MySQL arrêté, mauvais mot de passe, droits insuffisants.
- 404 : vérifier le chemin (`/api/clients`, `/api/cars`) et le port (8081 vs 8082).
