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

export interface NewsApiResponse {
  status: string;
  totalResults: number;
  articles: Article[];
}
