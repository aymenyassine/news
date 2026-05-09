# NewsApp Backend

Spring Boot 3.x + JWT + RBAC (USER / ADMIN) + NewsAPI.org

## Stack Technique

| Couche | Technologie |
|--------|-------------|
| Backend | Spring Boot 3.3.x (Java 21) |
| Securite | Spring Security + JWT (JJWT 0.12) + @PreAuthorize |
| Persistance | Spring Data JPA + Hibernate + PostgreSQL 15 |
| Cache | Caffeine (TTL 15 min) |
| API Externe | NewsAPI.org |
| Documentation | Springdoc OpenAPI 3 (Swagger UI) |

## Prerequis

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ (ou Docker)
- Cle API NewsAPI.org (gratuit sur https://newsapi.org/register)

## Installation

### 1. Cloner et configurer les variables d'environnement

```bash
cp .env.example .env
# Editer .env avec vos valeurs
```

### 2. Creer la base de donnees PostgreSQL

```sql
CREATE DATABASE newsapp;
CREATE USER newsapp WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE newsapp TO newsapp;
```

### 3. Lancer avec Maven

```bash
export DB_USERNAME=newsapp
export DB_PASSWORD=secret
export JWT_SECRET=your-secret-key-256-bits-minimum
export NEWS_API_KEY=your-newsapi-key
export ADMIN_EMAIL=admin@newsapp.com
export ADMIN_PASSWORD=Admin@1234

mvn spring-boot:run
```

### 4. Lancer avec Docker Compose

```bash
cp .env.example .env
# Editer .env
docker-compose up -d
```

## Acces

| Service | URL |
|---------|-----|
| API Backend | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui |
| API Docs JSON | http://localhost:8080/api-docs |

## Compte Admin par defaut

Au premier demarrage, un compte ADMIN est cree automatiquement :
- Email : valeur de `ADMIN_EMAIL` (defaut : admin@newsapp.com)
- Mot de passe : valeur de `ADMIN_PASSWORD`

> Le role ADMIN ne peut etre attribue que via le DataInitializer ou manuellement en base.

## Endpoints principaux

### Authentification (PUBLIC)
| Methode | Route | Description |
|---------|-------|-------------|
| POST | /api/auth/register | Inscription (role USER) |
| POST | /api/auth/login | Connexion — retourne JWT |
| POST | /api/auth/refresh | Renouvellement token |
| POST | /api/auth/logout | Deconnexion |

### Actualites (USER + ADMIN)
| Methode | Route | Description |
|---------|-------|-------------|
| GET | /api/news/headlines | Top headlines NewsAPI |
| GET | /api/news/search | Recherche full-text |
| GET | /api/news/sources | Sources disponibles |
| GET | /api/news/categories | Categories disponibles |

### Publications (USER + ADMIN)
| Methode | Route | Description |
|---------|-------|-------------|
| GET | /api/posts | Feed public (sans auth) |
| GET | /api/posts/{id} | Detail post (sans auth) |
| GET | /api/posts/my | Mes posts |
| POST | /api/posts | Creer un post |
| PUT | /api/posts/{id} | Modifier un post |
| DELETE | /api/posts/{id} | Supprimer un post |
| POST | /api/posts/{id}/report | Signaler un post |

### Administration (ADMIN uniquement)
| Methode | Route | Description |
|---------|-------|-------------|
| GET | /api/admin/stats | Statistiques globales |
| GET | /api/admin/users | Liste des comptes |
| PUT | /api/admin/users/{id}/ban | Bannir un compte |
| PUT | /api/admin/users/{id}/unban | Debannir un compte |
| GET | /api/admin/posts | Tous les posts |
| DELETE | /api/admin/posts/{id} | Suppression definitive |
| PUT | /api/admin/posts/{id}/status | Changer statut |

## Regles metier RBAC

- Un ADMIN ne peut pas se bannir lui-meme (400 Bad Request)
- Un ADMIN ne peut pas bannir un autre ADMIN (400 Bad Request)
- Les tokens JWT actifs d'un compte banni sont rejetes immediatement
- Le role ADMIN ne peut etre attribue que via DataInitializer ou en base

## Variables d'environnement

| Variable | Description | Defaut |
|----------|-------------|--------|
| DB_USERNAME | Utilisateur PostgreSQL | newsapp |
| DB_PASSWORD | Mot de passe PostgreSQL | secret |
| JWT_SECRET | Secret JWT (256-bit min) | — |
| NEWS_API_KEY | Cle API NewsAPI.org | — |
| ADMIN_EMAIL | Email admin par defaut | admin@newsapp.com |
| ADMIN_PASSWORD | Mot de passe admin | — |
