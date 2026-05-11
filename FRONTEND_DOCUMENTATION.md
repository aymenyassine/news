# Documentation Frontend — NewsApp

> Guide complet d'intégration pour la création du frontend Angular avec Lovable pour l'application NewsApp (Backend Spring Boot).

---

## 1. Architecture du Système

| Propriété | Valeur |
|---|---|
| **Base URL** | `http://localhost:8080` |
| **Préfixe API** | `/api` |
| **Auth** | JWT Bearer Tokens (Header `Authorization: Bearer <token>`) |
| **Token Expiry** | 1 heure (Access) / 7 jours (Refresh) |
| **Format** | JSON |

### Rôles Utilisateurs
- `USER` : Utilisateur standard (peut lire, poster, commenter, gérer ses favoris).
- `ADMIN` : Administrateur (accès aux statistiques, modération des posts, bannissement d'utilisateurs).

---

## 2. Authentification & Sécurité

Le flux d'authentification utilise deux tokens : un **Access Token** pour les requêtes API et un **Refresh Token** pour renouveler la session.

### Endpoints d'Authentification (`/api/auth`)

| Méthode | Endpoint | Description | Payload (Request) | Réponse (Success) |
|---|---|---|---|---|
| `POST` | `/api/auth/register` | Création de compte | `{email, password, name}` | `AuthResponse` |
| `POST` | `/api/auth/login` | Connexion | `{email, password}` | `AuthResponse` |
| `POST` | `/api/auth/refresh` | Rafraîchir le token | Header `Authorization: Bearer <refreshToken>` | `AuthResponse` |
| `POST` | `/api/auth/logout` | Déconnexion | - | `200 OK` |

---

## 3. Actualités (NewsAPI) (`/api/news`)

Ce module récupère des articles en temps réel via l'intégration NewsAPI du backend.

| Méthode | Endpoint | Description | Query Params |
|---|---|---|---|
| `GET` | `/api/news/headlines` | Top actualités | `country`, `category`, `page`, `pageSize` |
| `GET` | `/api/news/search` | Recherche articles | `q`, `language`, `sortBy`, `from`, `page`, `pageSize` |
| `GET` | `/api/news/sources` | Liste des sources | `country`, `category`, `language` |
| `GET` | `/api/news/categories` | Liste des catégories | - |

---

## 4. Publications & Communauté (`/api/posts`)

Gestion des articles créés par les utilisateurs (blog/news interne).

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/api/posts` | Feed public (paginé) | Non |
| `GET` | `/api/posts/{id}` | Détail d'un post | Non |
| `GET` | `/api/posts/my` | Mes publications | Oui |
| `POST` | `/api/posts` | Créer un post | Oui |
| `PUT` | `/api/posts/{id}` | Modifier son post | Oui (Auteur) |
| `DELETE` | `/api/posts/{id}` | Supprimer son post | Oui (Auteur/Admin) |
| `POST` | `/api/posts/{id}/report` | Signaler un post | Oui |

### Commentaires (`/api/posts/{postId}/comments`)

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `.../comments` | Liste des commentaires |
| `POST` | `.../comments` | Ajouter un commentaire |
| `PUT` | `.../comments/{id}` | Modifier son commentaire |
| `DELETE` | `.../comments/{id}` | Supprimer un commentaire |

---

## 5. Favoris & Historique (`/api/favorites` & `/api/users`)

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/favorites` | Liste de mes favoris |
| `POST` | `/api/favorites` | Ajouter aux favoris |
| `DELETE` | `/api/favorites/{id}` | Retirer des favoris |
| `GET` | `/api/favorites/check?url=...` | Vérifier si une URL est déjà en favori |
| `GET` | `/api/users/history` | Historique de consultation |
| `POST` | `/api/users/history` | Ajouter à l'historique |

---

## 6. Profil Utilisateur (`/api/users`)

| Méthode | Endpoint | Description | Payload |
|---|---|---|---|
| `GET` | `/api/users/profile` | Voir mon profil | - |
| `PUT` | `/api/users/profile` | Mettre à jour profil | `{name, avatarUrl, preferences}` |

---

## 7. Administration (`/api/admin`)

*Réservé aux utilisateurs avec le rôle `ADMIN`.*

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/admin/stats` | Statistiques globales (users, posts, etc.) |
| `GET` | `/api/admin/users` | Gestion des comptes (recherche, filtres) |
| `PUT` | `/api/admin/users/{id}/ban` | Bannir un utilisateur |
| `PUT` | `/api/admin/users/{id}/unban` | Débannir |
| `GET` | `/api/admin/posts` | Modération des posts (inclut signalés) |
| `PUT` | `/api/admin/posts/{id}/status` | Changer statut (PUBLISHED/DELETED) |

---

## 8. Modèles TypeScript (Interfaces)

```typescript
// Authentication
export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: UserInfo;
}

export interface UserInfo {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  avatarUrl?: string;
}

// News
export interface Article {
  title: string;
  description: string;
  url: string;
  urlToImage: string;
  publishedAt: string;
  content: string;
  source: { id: string; name: string };
  author: string;
}

// Posts
export interface Post {
  id: number;
  title: string;
  content: string;
  imageUrl: string;
  category: string;
  status: 'PUBLISHED' | 'DELETED';
  reportCount: number;
  createdAt: string;
  author: { id: number; name: string; avatarUrl: string };
}

// Admin Stats
export interface AdminStats {
  totalUsers: number;
  bannedUsers: number;
  totalPosts: number;
  reportedPosts: number;
  publishedPosts: number;
  deletedPosts: number;
}
```

---

## 9. Instructions pour Lovable / Angular

1.  **Environnement** : Configurer `apiUrl: 'http://localhost:8080/api'` dans `environment.ts`.
2.  **Intercepteur JWT** : Créer un `HttpInterceptor` pour injecter automatiquement le header `Authorization: Bearer <token>` si l'utilisateur est connecté.
3.  **Gestion des Erreurs** :
    - `401 Unauthorized` : Rediriger vers `/login`.
    - `403 Forbidden` : Afficher une alerte (Accès refusé ou Compte Banni).
    - `400 Validation` : Afficher les messages d'erreur du backend.
4.  **Guard** : Utiliser des `CanActivate` guards pour protéger les routes `/profile`, `/admin`, et `/create-post`.
5.  **Refresh Token** : Implémenter une logique de "Silent Refresh" si l'Access Token expire (intercepter le code 401 et tenter un refresh avant d'abandonner).

---

## 10. Identifiants de Test (Développement)

| Compte | Email | Mot de passe |
|---|---|---|
| **Administrateur** | `admin@newsapp.com` | `Admin@1234` |
| **Utilisateur standard** | `user@newsapp.com` | `User@1234` |
