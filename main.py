from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import json
import asyncio
from pathlib import Path
from typing import Dict, Any
from datetime import datetime

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

# Initialize with default data if file doesn't exist
DEFAULT_DATA = {
    "dailyVisitors": [
        {"date": "01/03", "visitors": 245, "pageViews": 892},
        {"date": "02/03", "visitors": 312, "pageViews": 1043},
        {"date": "03/03", "visitors": 289, "pageViews": 967},
        {"date": "04/03", "visitors": 401, "pageViews": 1234},
        {"date": "05/03", "visitors": 378, "pageViews": 1156},
        {"date": "06/03", "visitors": 423, "pageViews": 1389},
        {"date": "07/03", "visitors": 467, "pageViews": 1523},
        {"date": "08/03", "visitors": 501, "pageViews": 1678},
        {"date": "09/03", "visitors": 478, "pageViews": 1567},
        {"date": "10/03", "visitors": 523, "pageViews": 1789},
        {"date": "11/03", "visitors": 589, "pageViews": 1923},
        {"date": "12/03", "visitors": 612, "pageViews": 2045},
        {"date": "13/03", "visitors": 634, "pageViews": 2178},
        {"date": "14/03", "visitors": 701, "pageViews": 2334},
    ],
    "pageVisits": [
        {"page": "Trang Chủ", "visits": 3245},
        {"page": "Trắc Nghiệm", "visits": 2156},
        {"page": "Nhận Xét", "visits": 1789},
        {"page": "Thống Kê", "visits": 967},
    ],
    "hourlyTraffic": [
        {"hour": "00:00", "users": 23},
        {"hour": "02:00", "users": 12},
        {"hour": "04:00", "users": 8},
        {"hour": "06:00", "users": 34},
        {"hour": "08:00", "users": 156},
        {"hour": "10:00", "users": 234},
        {"hour": "12:00", "users": 289},
        {"hour": "14:00", "users": 312},
        {"hour": "16:00", "users": 267},
        {"hour": "18:00", "users": 201},
        {"hour": "20:00", "users": 178},
        {"hour": "22:00", "users": 89},
    ],
    "metrics": {
        "avgTimeOnSite": "4:32",
        "bounceRate": "32%",
        "growthMetrics": {
            "visitorsGrowth": "+12.5%",
            "pageViewsGrowth": "+8.3%",
            "timeGrowth": "+0:23",
            "bounceRateChange": "-3.2%"
        }
    },
    "additionalStats": {
        "devices": {
            "mobile": "62%",
            "desktop": "32%",
            "tablet": "6%"
        },
        "users": {
            "newUsers": 4234,
            "returningUsers": 2123,
            "returnRate": "33.4%"
        },
        "sources": {
            "search": "45%",
            "direct": "38%",
            "social": "17%"
        }
    }
}


async def read_data() -> Dict[str, Any]:
    """Read analytics data from JSON file"""
    async with file_lock:
        if not DATA_FILE.exists():
            await write_data(DEFAULT_DATA)
            return DEFAULT_DATA
        
        with open(DATA_FILE, 'r', encoding='utf-8') as f:
            return json.load(f)


async def write_data(data: Dict[str, Any]) -> None:
    """Write analytics data to JSON file asynchronously"""
    async with file_lock:
        with open(DATA_FILE, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)


@app.get("/")
async def root():
    return {"message": "MLN Analytics API", "status": "running"}


@app.get("/api/analytics")
async def get_analytics():
    """Get all analytics data"""
    data = await read_data()
    return data


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
async def track_visit(page: str):
    """Track a page visit (example endpoint for future use)"""
    data = await read_data()
    
    # Update page visits
    page_visits = data.get("pageVisits", [])
    for pv in page_visits:
        if pv["page"] == page:
            pv["visits"] += 1
            break
    
    await write_data(data)
    return {"status": "success", "page": page}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)

