export interface Author {
  id: number;
  name: string;
  avatarUrl: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  imageUrl: string;
  category: string;
  status: 'PUBLISHED' | 'DELETED';
  reportCount: number;
  createdAt: string;
  updatedAt: string;
  author: Author;
}

export interface Comment {
  id: number;
  content: string;
  createdAt: string;
  author: Author;
}
