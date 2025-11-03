# MLN Backend - Spring Boot with PostgreSQL

Spring Boot backend for MLN Analytics application with PostgreSQL database.

## Technology Stack

- **Spring Boot 3.2.0** - Java framework
- **PostgreSQL 16** - Database
- **Maven** - Build tool
- **Java 21** - Runtime
- **Docker** - Containerization

## Quick Start

### Start with Docker Compose

```bash
docker compose up --build -d
```

This will:
- Build the Spring Boot application
- Start PostgreSQL database
- Create necessary tables automatically
- Start the backend on `http://localhost:8000`

### Verify Backend is Running

Visit `http://localhost:8000` in your browser. You should see:

```json
{"message": "MLN Analytics API", "status": "running"}
```

Visit `http://localhost:8000/api/analytics` to see analytics data.

## API Endpoints

All endpoints maintain the same API contract as the FastAPI version:

- `GET /` - API status
- `GET /api/analytics` - Get all analytics data
- `GET /api/analytics/daily-visitors` - Get daily visitors data
- `GET /api/analytics/page-visits` - Get page visits data
- `GET /api/analytics/hourly-traffic` - Get hourly traffic data
- `GET /api/analytics/metrics` - Get general metrics
- `GET /api/analytics/additional-stats` - Get additional statistics
- `POST /api/analytics/track-visit?page={page}` - Track a page visit

## Database Schema

The application automatically creates the following tables:
- `daily_visitors` - Daily visitor and page view statistics
- `page_visits` - Page visit counts
- `hourly_traffic` - Hourly user activity
- `analytics_metrics` - General metrics (bounce rate, time on site, etc.)
- `additional_stats` - Additional statistics (devices, users, sources)

## Configuration

Database configuration is set via environment variables:
- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: mln_analytics)
- `DB_USER` - Database user (default: mln_user)
- `DB_PASSWORD` - Database password (default: mln_password)

## Managing the Service

### View logs
```bash
docker compose logs -f api
```

### View database logs
```bash
docker compose logs -f postgres
```

### Stop the service
```bash
docker compose down
```

### Stop and remove volumes (clean database)
```bash
docker compose down -v
```

### Restart the service
```bash
docker compose restart
```

### Rebuild after code changes
```bash
docker compose up --build -d
```

## Database Access

To connect to PostgreSQL directly:

```bash
docker compose exec postgres psql -U mln_user -d mln_analytics
```

## Features

- ✅ Same API contract as FastAPI version (no frontend changes needed)
- ✅ Rate limiting (30-second cooldown per IP)
- ✅ Automatic table creation via JPA
- ✅ Health checks and dependency management
- ✅ Persistent data storage in PostgreSQL
- ✅ Transaction management for data consistency
