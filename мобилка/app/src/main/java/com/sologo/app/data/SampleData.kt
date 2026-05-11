package com.sologo.app.data

import com.sologo.app.R

object SampleData {

    val routes = listOf(
        RouteIdea(
            id = "r1",
            title = "Утро у набережной",
            description = "Кофе, прогулка вдоль воды, лёгкий завтрак в кафе без спешки.",
            mood = Mood.CALM,
            city = "Калининград",
            imageRes = R.drawable.route_utro_u_naberezhnoi,
        ),
        RouteIdea(
            id = "r2",
            title = "Парк и чтение",
            description = "Найти тихий парк, посидеть на скамейке с книгой или подкастом.",
            mood = Mood.CALM,
            city = "Казань",
            imageRes = R.drawable.route_park_i_chtenie,
        ),
        RouteIdea(
            id = "r3",
            title = "Город на велосипеде",
            description = "Аренда велика на полдня, объехать набережные и смотровые точки.",
            mood = Mood.ACTIVE,
            city = "Сочи",
            imageRes = R.drawable.route_gorod_na_velosipede,
        ),
        RouteIdea(
            id = "r4",
            title = "Треккинг лёгкий",
            description = "Маршрут 8–12 км с перекусом, без экстрима — только движение и виды.",
            mood = Mood.ACTIVE,
            city = "Кисловодск",
            imageRes = R.drawable.route_trekking_legkii,
        ),
        RouteIdea(
            id = "r5",
            title = "Музейный день",
            description = "Один главный музей + кофе, без плотного графика.",
            mood = Mood.CULTURAL,
            city = "Санкт-Петербург",
            imageRes = R.drawable.route_muzeinyi_den,
        ),
        RouteIdea(
            id = "r6",
            title = "Пешеходный центр",
            description = "Исторический квартал, аудиогид в наушниках, ужин в одном месте.",
            mood = Mood.CULTURAL,
            city = "Нижний Новгород",
            imageRes = R.drawable.route_peshehodnyi_centr,
        ),
    )

    val hotels = listOf(
        Hotel(
            id = "h1",
            name = "Отель «Северное небо»",
            city = "Санкт-Петербург",
            pricePerNight = 4200,
            avgCityPrice = 5500,
            description = "Тихий дворик, завтрак, рядом метро.",
            mainImageRes = R.drawable.hotel_1_main,
            roomImageRes = listOf(
                R.drawable.hotel_1_room_1,
                R.drawable.hotel_1_room_2,
                R.drawable.hotel_1_room_3,
            ),
        ),
        Hotel(
            id = "h2",
            name = "Хостел «Соло-лаунж»",
            city = "Санкт-Петербург",
            pricePerNight = 1900,
            avgCityPrice = 5500,
            description = "Капсулы и общая кухня — для знакомств с другими путешественниками.",
            mainImageRes = R.drawable.hotel_2_main,
            roomImageRes = listOf(
                R.drawable.hotel_2_room_1,
                R.drawable.hotel_2_room_2,
                R.drawable.hotel_2_room_3,
            ),
        ),
        Hotel(
            id = "h3",
            name = "Апарт «Казанская набережная»",
            city = "Казань",
            pricePerNight = 3100,
            avgCityPrice = 4000,
            description = "Квартира-студия, вид на воду, самостоятельное заселение.",
            mainImageRes = R.drawable.hotel_3_main,
            roomImageRes = listOf(
                R.drawable.hotel_3_room_1,
            ),
        ),
        Hotel(
            id = "h4",
            name = "Отель «Горный ветер»",
            city = "Сочи",
            pricePerNight = 6800,
            avgCityPrice = 5200,
            description = "Выше средней цены по городу — премиум-зона и бассейн.",
            mainImageRes = R.drawable.hotel_4_main,
            roomImageRes = listOf(
                R.drawable.hotel_4_room_1,
                R.drawable.hotel_4_room_2,
                R.drawable.hotel_4_room_3,
                R.drawable.hotel_4_room_4,
            ),
        ),
        Hotel(
            id = "h5",
            name = "Мини-отель «Тихий двор»",
            city = "Калининград",
            pricePerNight = 2800,
            avgCityPrice = 4500,
            description = "Небольшой семейный формат, завтраки по расписанию.",
            mainImageRes = R.drawable.hotel_5_main,
            roomImageRes = listOf(
                R.drawable.hotel_5_room_1,
                R.drawable.hotel_5_room_2,
            ),
        ),
    )

    val safeZones = listOf(
        SafeZone(
            district = "Центр, Дворцовая набережная",
            city = "Санкт-Петербург",
            level = SafetyLevel.HIGH,
            note = "Людно вечером, следите за личными вещами.",
        ),
        SafeZone(
            district = "Казанский Кремль",
            city = "Казань",
            level = SafetyLevel.HIGH,
            note = "Туристы и патрули — ориентир для первого дня в городе.",
        ),
        SafeZone(
            district = "Промзона у вокзала",
            city = "Условный город",
            level = SafetyLevel.LOW,
            note = "Пример «осторожной» зоны: лучше не гулять поздно в одиночку.",
        ),
    )

    val faq = listOf(
        FaqItem(
            question = "С чего начать первую соло-поездку?",
            answer = "Выберите город с хорошей транспортной доступностью, забронируйте жильё заранее и оставьте кому-то маршрут прибытия.",
        ),
        FaqItem(
            question = "Как не переплачивать за отель?",
            answer = "Сравнивайте цену ночи со средней по городу в приложении и смотрите отзывы о чистоте и расположении.",
        ),
        FaqItem(
            question = "Что делать, если тревожно вечером?",
            answer = "Зайдите в людное кафе, напишите близким, используйте раздел безопасных зон как ориентир.",
        ),
        FaqItem(
            question = "Какие документы нужны для поездки в одиночку?",
            answer = "Важно взять с собой загранпаспорт, визы (если требуются), медицинскую страховку, билеты, бронь жилья. Копии документов стоит хранить отдельно от оригиналов (в облаке, на почте и т. д.). ",
        ),
    )

    val phrasebook = mapOf(
        "где вокзал" to "where is the train station",
        "сколько стоит" to "how much does it cost",
        "один билет, пожалуйста" to "one ticket, please",
        "я заблудился" to "I am lost",
        "помогите" to "help me, please",
        "спасибо" to "thank you",
    )

    fun hotelById(id: String): Hotel? = hotels.find { it.id == id }
}
