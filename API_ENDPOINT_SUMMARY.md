# API Endpoint Summary

## Endpoint Details

**Base URL:** `https://newsapi.org/v2/`

**Full Endpoint:** `https://newsapi.org/v2/top-headlines`

**HTTP Method:** GET

## Query Parameters

- `country`: `us` (United States)

## Headers

- `X-Api-Key`: `9f6482a584804376874b848980b7a044`
- `User-Agent`: `ABC`

## Complete URL for Browser Testing

```
https://newsapi.org/v2/top-headlines?country=us&apiKey=9f6482a584804376874b848980b7a044
```

## Response Details

**Status:** 200 OK

**Response Time:** ~563ms to 1456ms

**Content-Type:** application/json; charset=utf-8

**Total Results:** 34 articles

## Sample Response Structure

```json
{
  "status": "ok",
  "totalResults": 34,
  "articles": [
    {
      "source": {
        "id": null,
        "name": "SFGate"
      },
      "author": "Gabe Fernandez",
      "title": "Article Title",
      "description": "Article description",
      "url": "https://...",
      "urlToImage": "https://...",
      "publishedAt": "2026-03-26T01:32:35Z",
      "content": "Article content..."
    }
    // ... more articles
  ]
}
```

## API Provider

**News API** - https://newsapi.org/

This is a popular news aggregation API that provides breaking news headlines and articles from various sources.

## Notes

- The app fetches top headlines for the US region
- The API key is embedded in the app
- The response includes 34 news articles with title, description, image, and content
- Response headers indicate the data is not cached (`x-cached-result: false`)
- The API is served through Cloudflare CDN

## How the App Uses This Data

1. **TopHeadlineActivity** makes the API call on launch
2. **ViewModel** processes the response through the Repository
3. **RecyclerView** displays the list of news articles
4. Each article shows:
   - Title
   - Description
   - Image (loaded via Glide)
   - Source name
   - Published date

---

**Last Updated:** March 27, 2026

