from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
import json
import asyncio
from pathlib import Path
from typing import Dict, Any
from datetime import datetime, timedelta
from collections import defaultdict

app = FastAPI(title="MLN Analytics API")

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, specify your frontend URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Path to JSON data file
DATA_FILE = Path("data/analytics.json")
DATA_FILE.parent.mkdir(exist_ok=True)

# Lock for async file operations
file_lock = asyncio.Lock()

# Rate limiting: track visits per IP to prevent spam
# Format: {ip: {page: last_tracked_time}}
rate_limit_cache: Dict[str, Dict[str, datetime]] = defaultdict(dict)
# Minimum time between tracking from same IP (in seconds)
RATE_LIMIT_SECONDS = 30

# Initialize with default data if file doesn't exist
DEFAULT_DATA = {
    "dailyVisitors": [
        {"date": "01/03", "visitors": 0, "pageViews": 0},
        {"date": "02/03", "visitors": 0, "pageViews": 0},
        {"date": "03/03", "visitors": 0, "pageViews": 0},
        {"date": "04/03", "visitors": 0, "pageViews": 0},
        {"date": "05/03", "visitors": 0, "pageViews": 0},
        {"date": "06/03", "visitors": 0, "pageViews": 0},
        {"date": "07/03", "visitors": 0, "pageViews": 0},
        {"date": "08/03", "visitors": 0, "pageViews": 0},
        {"date": "09/03", "visitors": 0, "pageViews": 0},
        {"date": "10/03", "visitors": 0, "pageViews": 0},
        {"date": "11/03", "visitors": 0, "pageViews": 0},
        {"date": "12/03", "visitors": 0, "pageViews": 0},
        {"date": "13/03", "visitors": 0, "pageViews": 0},
        {"date": "14/03", "visitors": 0, "pageViews": 0},
    ],
    "pageVisits": [
        {"page": "Trang Chủ", "visits": 0},
        {"page": "Trắc Nghiệm", "visits": 0},
        {"page": "Nhận Xét", "visits": 0},
        {"page": "Thống Kê", "visits": 0},
    ],
    "hourlyTraffic": [
        {"hour": "00:00", "users": 0},
        {"hour": "02:00", "users": 0},
        {"hour": "04:00", "users": 0},
        {"hour": "06:00", "users": 0},
        {"hour": "08:00", "users": 0},
        {"hour": "10:00", "users": 0},
        {"hour": "12:00", "users": 0},
        {"hour": "14:00", "users": 0},
        {"hour": "16:00", "users": 0},
        {"hour": "18:00", "users": 0},
        {"hour": "20:00", "users": 0},
        {"hour": "22:00", "users": 0},
    ],
    "metrics": {
        "avgTimeOnSite": "0:00",
        "bounceRate": "0%",
        "growthMetrics": {
            "visitorsGrowth": "+0%",
            "pageViewsGrowth": "+0%",
            "timeGrowth": "+0:00",
            "bounceRateChange": "0%"
        }
    },
    "additionalStats": {
        "devices": {
            "mobile": "0%",
            "desktop": "0%",
            "tablet": "0%"
        },
        "users": {
            "newUsers": 0,
            "returningUsers": 0,
            "returnRate": "0%"
        },
        "sources": {
            "search": "0%",
            "direct": "0%",
            "social": "0%"
        }
    }
}


def _read_data_sync() -> Dict[str, Any]:
    """Synchronous file read operation"""
    if not DATA_FILE.exists():
        _write_data_sync(DEFAULT_DATA)
        return DEFAULT_DATA
    
    with open(DATA_FILE, 'r', encoding='utf-8') as f:
        return json.load(f)


def _write_data_sync(data: Dict[str, Any]) -> None:
    """Synchronous file write operation"""
    with open(DATA_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)


async def read_data() -> Dict[str, Any]:
    """Read analytics data from JSON file (async wrapper)"""
    async with file_lock:
        # Run file I/O in thread pool to avoid blocking event loop
        try:
            loop = asyncio.get_running_loop()
        except RuntimeError:
            loop = asyncio.get_event_loop()
        return await loop.run_in_executor(None, _read_data_sync)


async def write_data(data: Dict[str, Any]) -> None:
    """Write analytics data to JSON file asynchronously"""
    async with file_lock:
        # Run file I/O in thread pool to avoid blocking event loop
        try:
            loop = asyncio.get_running_loop()
        except RuntimeError:
            loop = asyncio.get_event_loop()
        await loop.run_in_executor(None, _write_data_sync, data)


@app.get("/")
async def root():
    return {"message": "MLN Analytics API", "status": "running"}


@app.get("/api/analytics")
async def get_analytics():
    """Get all analytics data"""
    try:
        data = await read_data()
        return data
    except Exception as e:
        # Fallback to default data if read fails
        import logging
        logging.error(f"Error reading analytics data: {e}")
        return DEFAULT_DATA


@app.get("/api/analytics/daily-visitors")
async def get_daily_visitors():
    """Get daily visitors data"""
    data = await read_data()
    return data.get("dailyVisitors", [])


@app.get("/api/analytics/page-visits")
async def get_page_visits():
    """Get page visits data"""
    data = await read_data()
    return data.get("pageVisits", [])


@app.get("/api/analytics/hourly-traffic")
async def get_hourly_traffic():
    """Get hourly traffic data"""
    data = await read_data()
    return data.get("hourlyTraffic", [])


@app.get("/api/analytics/metrics")
async def get_metrics():
    """Get general metrics"""
    data = await read_data()
    return data.get("metrics", {})


@app.get("/api/analytics/additional-stats")
async def get_additional_stats():
    """Get additional statistics"""
    data = await read_data()
    return data.get("additionalStats", {})


@app.post("/api/analytics/track-visit")
async def track_visit(page: str, request: Request):
    """Track a page visit with comprehensive analytics"""
    # Rate limiting: prevent spam from same IP
    client_ip = request.client.host if request.client else "unknown"
    now = datetime.now()
    
    # Check if this IP tracked this page recently
    if client_ip in rate_limit_cache:
        if page in rate_limit_cache[client_ip]:
            last_tracked = rate_limit_cache[client_ip][page]
            time_since = (now - last_tracked).total_seconds()
            if time_since < RATE_LIMIT_SECONDS:
                # Too soon, return success but don't increment
                return {"status": "success", "page": page, "skipped": "rate_limited"}
    
    # Update rate limit cache
    rate_limit_cache[client_ip][page] = now
    
    # Clean up old entries (older than 1 hour) to prevent memory bloat
    if len(rate_limit_cache) > 1000:  # Only clean if cache is large
        cutoff_time = now - timedelta(hours=1)
        for ip in list(rate_limit_cache.keys()):
            rate_limit_cache[ip] = {
                k: v for k, v in rate_limit_cache[ip].items()
                if v > cutoff_time
            }
            if not rate_limit_cache[ip]:
                del rate_limit_cache[ip]
    
    data = await read_data()
    
    # Get current date and time (reuse 'now' from rate limiting check above)
    current_date = now.strftime("%d/%m")
    current_hour = now.strftime("%H:00")
    
    # Update page visits
    page_visits = data.get("pageVisits", [])
    for pv in page_visits:
        if pv["page"] == page:
            pv["visits"] += 1
            break
    
    # Update daily visitors
    daily_visitors = data.get("dailyVisitors", [])
    found_date = False
    for dv in daily_visitors:
        if dv["date"] == current_date:
            dv["visitors"] += 1
            dv["pageViews"] += 1
            found_date = True
            break
    
    # If date doesn't exist, add it (keep only last 30 days)
    if not found_date:
        daily_visitors.append({
            "date": current_date,
            "visitors": 1,
            "pageViews": 1
        })
        # Keep only last 30 days
        if len(daily_visitors) > 30:
            daily_visitors.pop(0)
    
    # Update hourly traffic
    hourly_traffic = data.get("hourlyTraffic", [])
    found_hour = False
    for ht in hourly_traffic:
        if ht["hour"] == current_hour:
            ht["users"] += 1
            found_hour = True
            break
    
    # If hour doesn't exist, add it
    if not found_hour:
        hourly_traffic.append({
            "hour": current_hour,
            "users": 1
        })
        # Sort by hour
        hourly_traffic.sort(key=lambda x: x["hour"])
    
    # Update data
    data["dailyVisitors"] = daily_visitors
    data["pageVisits"] = page_visits
    data["hourlyTraffic"] = hourly_traffic
    
    await write_data(data)
    return {"status": "success", "page": page}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

