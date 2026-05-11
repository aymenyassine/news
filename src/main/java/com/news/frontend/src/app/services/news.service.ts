import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Article, NewsApiResponse } from '../models/news.models';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private apiUrl = 'http://localhost:8080/api/news';
  private http = inject(HttpClient);

  getHeadlines(country: string = 'us', category: string = '', page: number = 1): Observable<NewsApiResponse> {
    let params = new HttpParams()
      .set('country', country)
      .set('page', page.toString());
    
    if (category) {
      params = params.set('category', category);
    }

    return this.http.get<NewsApiResponse>(`${this.apiUrl}/headlines`, { params });
  }

  search(query: string, page: number = 1): Observable<NewsApiResponse> {
    const params = new HttpParams()
      .set('q', query)
      .set('page', page.toString());

    return this.http.get<NewsApiResponse>(`${this.apiUrl}/search`, { params });
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }
}
