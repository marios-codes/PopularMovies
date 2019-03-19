# PopularMovies

Please replace ```BuildConfig.ApiKey``` in ```public static final String API_KEY``` field constant,
found in ```MainActivity.java```, with your own api key from [The MovieDB](https://www.themoviedb.org).

This app fetches two different movie lists according to the user sorting preference (Most Popular and Top Rated). It also lets
the user save to their device a movie as favorite and access all their favorite movies offline. The user can also read reviews
about a movie inside the movie's details activity and watch its trailer.

## It was submitted to Udacity's Android Developer Nanodegree and passed successfully, which was tested among others in the following aspects:

- Checks for Internet Connectivity (not just network connectivity) by pinging a server
- Consume a RESTful API and populate a RecyclerView with data
- Correct implementation of activity lifecycle and Saved Instance State on orientation changes
- Minimize network calls during an orientation change and prevent app from crashing
- MVVM architectural pattern

## Libraries and Android Architecture Components Used: 
- Picasso for image handling
- Retrofit for RESTful API consumption
- Butterknife for View Injection
- Room as an abstraction layer of sqlite database and data persistence
- LiveData and Android Lifecycle Jetpack Libraries for best practice regarding
lifecycle handling and network requests consumption

## Screenshots
![First Screen](https://raw.githubusercontent.com/marioszou/PopularMovies/master/screenshots/portrait-popular-movies.png)
![Movie Details Screen](https://raw.githubusercontent.com/marioszou/PopularMovies/master/screenshots/portrait-movie-details.png)
![Details with Trailer and Reviews](https://raw.githubusercontent.com/marioszou/PopularMovies/master/screenshots/portrait-movie-trailer-reviews.png)
