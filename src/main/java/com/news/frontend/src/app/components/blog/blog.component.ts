import { Component, OnInit, inject } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../models/post.models';
import { NgFor, NgIf, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, RouterLink],
  templateUrl: './blog.component.html',
  styleUrl: './blog.component.css'
})
export class BlogComponent implements OnInit {
  posts: Post[] = [];
  loading = true;
  
  postService = inject(PostService);
  authService = inject(AuthService);

  ngOnInit() {
    this.loadPosts();
  }

  loadPosts() {
    this.postService.getPosts().subscribe({
      next: (data) => {
        this.posts = data.content; // Spring Boot Page object
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
