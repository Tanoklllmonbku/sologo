from pydantic_settings import BaseSettings
from typing import Optional

from dotenv import load_dotenv
import os

load_dotenv()  # принудительно загружаем .env
print("DB URL из .env:", os.getenv("DATABASE_URL"))

class Settings(BaseSettings):
    DATABASE_URL: str = "postgresql+asyncpg://postgres:123123F@localhost:5432/yachts"  # ← значение по умолчанию
    SECRET_KEY: str = "your-secret-key"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    
    class Config:
        env_file = ".env"  # ← должен быть указан
        env_file_encoding = "utf-8"

settings = Settings()