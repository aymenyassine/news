import { Component, OnInit, inject } from '@angular/core';
import { NewsService } from '../../services/news.service';
import { Article } from '../../models/news.models';
import { NgFor, NgIf, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule, DatePipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  articles: Article[] = [];
  loading = true;
  searchQuery = '';
  currentPage = 1;
  
  newsService = inject(NewsService);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    if (this.searchQuery.trim()) {
      this.onSearch();
    } else {
      this.loadHeadlines();
    }
  }

  loadHeadlines() {
    this.loading = true;
    this.newsService.getHeadlines('us', '', this.currentPage).subscribe({
      next: (data) => {
        this.articles = data.articles;
        setTimeout(() => this.loading = false);
      },
      error: () => setTimeout(() => this.loading = false)
    });
  }

  onSearch() {
    if (!this.searchQuery.trim()) {
      this.currentPage = 1;
      this.loadHeadlines();
      return;
    }
    this.loading = true;
    this.newsService.search(this.searchQuery, this.currentPage).subscribe({
      next: (data) => {
        this.articles = data.articles;
        setTimeout(() => this.loading = false);
      },
      error: () => setTimeout(() => this.loading = false)
    });
  }

  nextPage() {
    this.currentPage++;
    this.loadData();
    window.scrollTo(0, 0);
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadData();
      window.scrollTo(0, 0);
    }
  }

  resetSearch() {
    this.currentPage = 1;
    this.onSearch();
  }
}
