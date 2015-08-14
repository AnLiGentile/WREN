
### Datasets With Internal XPath-Value Representation ###

This folder contains evaluation datasets already converted in WREN internal representation, provided as compressed files (zip or 7z).
Converted datasets include:
- REX, provided as one archive file for each website-concept:
  * [espnfc-player](./WEIR/espnfc-player-index.7z)
  * [espnfc-team](./WEIR/espnfc-team-index-new.7z)
  * [goodreads-author](./WEIR/goodreads-author-index.7z)
  * [goodreads-book](./WEIR/goodreads-book-index.7z)
  * [imdb-name](./WEIR/imdb-name-index.7z)
  * [imdb-title](./WEIR/imdb-title-index.7z)

- WEIR, provided as one archive file for each domain:
  * [book](./WEIR/book.zip)
  * [finance](./WEIR/finance.zip)
  * [soccer](./WEIR/soccer.zip)
  * [videogame](./WEIR/videogame.7z)

- SWDE, provided as one archive file for each domain:
  * [auto](./swde-17477/auto.7z)
  * [book](./swde-17477/book.7z)
  * [camera](./swde-17477/camera.7z)
  * [job](./swde-17477/job.7z)
  * [movie](./swde-17477/movie.7z)
  * [nbaplayer](./swde-17477/nbaplayer.7z)
  * [restaurant](./swde-17477/restaurant.7z)
  * [university](./swde-17477/university.7z)

To convert additional datasets use the class uk.ac.shef.dcs.oak.xpath.processors.ReducePagesToXpath
