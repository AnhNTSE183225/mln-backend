# MLN Backend API

FastAPI backend for MLN Analytics application.

## Quick Start

Start the service using Docker Compose:

```bash
docker compose up --build -d
```

## API Endpoints

- `GET /` - API status
- `GET /api/analytics` - Get all analytics data
- `GET /api/analytics/daily-visitors` - Get daily visitors data
- `GET /api/analytics/page-visits` - Get page visits data
- `GET /api/analytics/hourly-traffic` - Get hourly traffic data
- `GET /api/analytics/metrics` - Get general metrics
- `GET /api/analytics/additional-stats` - Get additional statistics
- `POST /api/analytics/track-visit?page={page}` - Track a page visit

## Data Storage

Analytics data is stored in `data/analytics.json` file. The file is created automatically with default data on first run.

## Stop the Service

```bash
docker compose down
```

