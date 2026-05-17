from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from routes import users, hotels, bookings, cities, routes, safe_zones, lost_report, weather

app = FastAPI(title="SoloGo API")

app.mount("/static", StaticFiles(directory="static"), name="static")

# Подключаем роутеры
app.include_router(users.router)
app.include_router(hotels.router)
app.include_router(bookings.router)
app.include_router(cities.router)
app.include_router(routes.router)
app.include_router(safe_zones.router)
app.include_router(lost_report.router)
app.include_router(weather.router)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
async def root():
    return {"message": "SoloGo API is running"}

@app.get("/health")
async def health_check():
    return {"status": "ok"}