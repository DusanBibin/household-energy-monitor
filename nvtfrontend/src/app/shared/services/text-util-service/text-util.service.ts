import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TextUtilServiceService {

  constructor() { }

  highlightMatch(text: string, search: string): string {
  
    search = decodeURIComponent(search);

    console.log("highlightmatch")


    const searchWords = search.trim().toLowerCase().split(/\s+/).filter(w => w.length > 0);
    if (searchWords.length === 0) return text;

    return text.split(/\b/).map(word => {
        const lowerWord = word.toLowerCase();
        for (const searchWord of searchWords) {
            if (lowerWord.startsWith(searchWord)) {
                // Highlight only the prefix that matches
                return `<strong>${word.slice(0, searchWord.length)}</strong>${word.slice(searchWord.length)}`;
            }
        }
        return word;
    }).join('');

  }
}
