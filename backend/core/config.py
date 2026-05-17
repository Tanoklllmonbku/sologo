from pydantic_settings import BaseSettings
from typing import Optional

from dotenv import load_dotenv
import os

load_dotenv()
print("DB URL из .env:", os.getenv("DATABASE_URL"))

class Settings(BaseSettings):
    DATABASE_URL: str = os.getenv("DATABASE_URL", "postgresql+asyncpg://postgres:123@localhost:5432/sologo")
    SECRET_KEY: str = os.getenv("SECRET_KEY ", "supersecretkey_sologo_2024_change_me_in_production")
    ALGORITHM: str = os.getenv("ALGORITHM", "HS256")
    ACCESS_TOKEN_EXPIRE_MINUTES: int = os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", 60)
    WEATHER_API_KEY: str = os.getenv("WEATHER_API_KEY", "your_openweather_api_key_here")
    HOST: str = os.getenv("HOST", "localhost")
    PORT: int = os.getenv("PORT", 8080)
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"

settings = Settings()