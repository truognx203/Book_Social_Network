import {Component, Input} from '@angular/core';
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss']
})
export class BookCardComponent {
  private _book_cover: string | undefined;

  get book_cover(): string | undefined {
    if(this._book.cover){
      return 'data:image/png;base64, ' + this._book_cover;
    }
    return this._book_cover;
  }

  private _book: BookResponse = {};

  get book(): BookResponse {
    return this._book;
  }

  @Input()
  set book(value: BookResponse) {
    this._book = value;
  }
}
