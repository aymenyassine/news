import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './create-post.component.html',
  styleUrl: './create-post.component.css'
})
export class CreatePostComponent {
  postData = {
    title: '',
    content: '',
    category: 'Général',
    imageUrl: ''
  };
  errorMessage = '';
  
  router = inject(Router);
  postService = inject(PostService);

  onSubmit() {
    if (!this.postData.title || !this.postData.content) {
      this.errorMessage = 'Le titre et le contenu sont obligatoires';
      return;
    }

    this.postService.createPost(this.postData).subscribe({
      next: () => {
        this.router.navigate(['/blog']);
      },
      error: (err) => {
        this.errorMessage = "Erreur lors de la création du post. Vérifiez votre connexion.";
      }
    });
  }

  cancel() {
    this.router.navigate(['/blog']);
  }
}
