# backend/app/routers/weather.py
from fastapi import APIRouter, HTTPException
from core.config import settings
from httpx import AsyncClient

router = APIRouter(prefix="/weather", tags=["weather"])


@router.get("/")
async def get_weather(city: str):
    """Get current weather for city from OpenWeatherMap"""
    api_key = getattr(settings, "WEATHER_API_KEY", None)
    
    if not api_key or api_key == "your_openweather_api_key_here":
        # Демо-режим, если нет ключа
        return {
            "city": city,
            "temperature": 22.5,
            "condition": "ясно",
            "humidity": 65,
            "wind_speed": 3.2,
            "icon": "01d"
        }
    
    url = f"https://api.openweathermap.org/data/2.5/weather?q={city}&appid={api_key}&units=metric&lang=ru"
    
    async with AsyncClient() as client:
        response = await client.get(url)
        if response.status_code != 200:
            raise HTTPException(404, f"Weather for '{city}' not found")
        
        data = response.json()
        return {
            "city": city,
            "temperature": round(data["main"]["temp"], 1),
            "condition": data["weather"][0]["description"],
            "humidity": data["main"]["humidity"],
            "wind_speed": data["wind"]["speed"],
            "icon": data["weather"][0]["icon"]
        }