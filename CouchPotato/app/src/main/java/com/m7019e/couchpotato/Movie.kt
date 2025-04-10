package com.m7019e.couchpotato

data class Movie(
    val adult: Boolean,
    val backdrop_path: String?,
    val budget: Long,
    val genres: List<Genre>,
    val homepage: String?,
    val id: Int,
    val imdb_id: String?,
    val original_language: String,
    val original_title: String,
    val overview: String?,
    val popularity: Double,
    val poster_path: String?,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val release_date: String,
    val revenue: Long,
    val runtime: Int?,
    val spoken_languages: List<SpokenLanguage>,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)

data class Genre(
    val id: Int,
    val name: String
)

data class ProductionCompany(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

data class ProductionCountry(
    val iso_3166_1: String,
    val name: String
)

data class SpokenLanguage(
    val english_name: String,
    val iso_639_1: String,
    val name: String
)

object MovieDatabase {
    val movies = listOf(
        Movie(
            adult = false,
            backdrop_path = "/9GvhICpapmgHMqkoLniA1DAMwOt.jpg",
            budget = 63000000,
            genres = listOf(Genre(18, "Drama"), Genre(80, "Crime")),
            homepage = "https://www.warnerbros.com/movies/shawshank-redemption",
            id = 278,
            imdb_id = "tt0111161",
            original_language = "en",
            original_title = "The Shawshank Redemption",
            overview = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
            popularity = 51.645,
            poster_path = "/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
            production_companies = listOf(
                ProductionCompany(97, "/path/to/logo.png", "Castle Rock Entertainment", "US")
            ),
            production_countries = listOf(ProductionCountry("US", "United States of America")),
            release_date = "1994-09-23",
            revenue = 58800000,
            runtime = 142,
            spoken_languages = listOf(SpokenLanguage("English", "en", "English")),
            status = "Released",
            tagline = "Fear can hold you prisoner. Hope can set you free.",
            title = "The Shawshank Redemption",
            video = false,
            vote_average = 8.7,
            vote_count = 21000
        ),
        Movie(
            adult = false,
            backdrop_path = "/3fylLsj1k5fNjaQJMMODZpIgvYQ.jpg",
            budget = 25000000,
            genres = listOf(Genre(18, "Drama"), Genre(80, "Crime")),
            homepage = "https://www.paramountmovies.com/movies/the-godfather",
            id = 238,
            imdb_id = "tt0068646",
            original_language = "en",
            original_title = "The Godfather",
            overview = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
            popularity = 45.123,
            poster_path = "/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
            production_companies = listOf(
                ProductionCompany(4, "/path/to/paramount.png", "Paramount Pictures", "US")
            ),
            production_countries = listOf(ProductionCountry("US", "United States of America")),
            release_date = "1972-03-14",
            revenue = 245066411,
            runtime = 175,
            spoken_languages = listOf(SpokenLanguage("English", "en", "English")),
            status = "Released",
            tagline = "An offer you can't refuse.",
            title = "The Godfather",
            video = false,
            vote_average = 8.7,
            vote_count = 19000
        ),
        Movie(
            adult = false,
            backdrop_path = "/8ZTVqvKDybL0i41TN9JeR4FovW.jpg",
            budget = 160000000,
            genres = listOf(Genre(28, "Action"), Genre(878, "Science Fiction"), Genre(53, "Thriller")),
            homepage = "https://www.warnerbros.com/movies/inception",
            id = 27205,
            imdb_id = "tt1375666",
            original_language = "en",
            original_title = "Inception",
            overview = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            popularity = 38.456,
            poster_path = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg",
            production_companies = listOf(
                ProductionCompany(174, "/path/to/wb.png", "Warner Bros. Pictures", "US")
            ),
            production_countries = listOf(ProductionCountry("US", "United States of America")),
            release_date = "2010-07-15",
            revenue = 829895144,
            runtime = 148,
            spoken_languages = listOf(SpokenLanguage("English", "en", "English")),
            status = "Released",
            tagline = "Your mind is the scene of the crime.",
            title = "Inception",
            video = false,
            vote_average = 8.4,
            vote_count = 30000
        ),
        Movie(
            adult = false,
            backdrop_path = "/4XDAFzI9AyV2EWP1KvlS2ZbsATr.jpg",
            budget = 8000000,
            genres = listOf(Genre(53, "Thriller"), Genre(80, "Crime")),
            homepage = "https://www.miramax.com/movie/pulp-fiction",
            id = 680,
            imdb_id = "tt0110912",
            original_language = "en",
            original_title = "Pulp Fiction",
            overview = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
            popularity = 42.789,
            poster_path = "/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg",
            production_companies = listOf(
                ProductionCompany(14, "/path/to/miramax.png", "Miramax", "US")
            ),
            production_countries = listOf(ProductionCountry("US", "United States of America")),
            release_date = "1994-10-14",
            revenue = 213928762,
            runtime = 154,
            spoken_languages = listOf(SpokenLanguage("English", "en", "English")),
            status = "Released",
            tagline = "Just because you are a character doesn't mean you have character.",
            title = "Pulp Fiction",
            video = false,
            vote_average = 8.5,
            vote_count = 23000
        ),
        Movie(
            adult = false,
            backdrop_path = "/1XVaZS8aJTwX8fX3C9XmsfKNGsx.jpg",
            budget = 185000000,
            genres = listOf(Genre(18, "Drama"), Genre(28, "Action"), Genre(80, "Crime"), Genre(53, "Thriller")),
            homepage = "https://www.warnerbros.com/movies/dark-knight",
            id = 155,
            imdb_id = "tt0468569",
            original_language = "en",
            original_title = "The Dark Knight",
            overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            popularity = 49.876,
            poster_path = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            production_companies = listOf(
                ProductionCompany(174, "/path/to/wb.png", "Warner Bros. Pictures", "US")
            ),
            production_countries = listOf(ProductionCountry("US", "United States of America")),
            release_date = "2008-07-16",
            vote_average = 8.5,
            revenue = 1004558444,
            runtime = 152,
            spoken_languages = listOf(SpokenLanguage("English", "en", "English")),
            status = "Released",
            tagline = "Why so serious?",
            title = "The Dark Knight",
            video = false,
            vote_count = 28000
        )
    )
}