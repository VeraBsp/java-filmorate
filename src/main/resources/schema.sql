CREATE TABLE IF NOT EXISTS users (
	user_id INT PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	email VARCHAR (255) UNIQUE NOT NULL,
	login  VARCHAR(100) UNIQUE NOT NULL,
	birthday DATE
);

CREATE TABLE IF NOT EXISTS friends_status (
	friend_status_id INT PRIMARY KEY AUTO_INCREMENT,
	friend_status_title VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
	user_id INT NOT NULL,
	friend_id INT NOT NULL,
	friend_status_id INT NOT NULL,
	PRIMARY KEY (user_id, friend_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (friend_status_id) REFERENCES friends_status(friend_status_id),
	CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS genres (
	genre_id INT PRIMARY KEY AUTO_INCREMENT,
	genre_title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS rating (
	rating_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	rating_title VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS directors (
	director_id INT PRIMARY KEY AUTO_INCREMENT,
	director_name VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id INT PRIMARY KEY AUTO_INCREMENT,
	film_name VARCHAR(255) NOT NULL,
	description VARCHAR(255) NOT NULL,
	duration INT NOT NULL,
	release_date DATE NOT NULL,
    rating_id INT NOT NULL,
	FOREIGN KEY (rating_id) REFERENCES rating(rating_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id INT NOT NULL,
    director_id INT NOT NULL,
    PRIMARY KEY (film_id, director_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (director_id) REFERENCES directors(director_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_like (
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    PRIMARY KEY (user_id, film_id),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
);