# Libabel
Spigot plugin, which allows books to be created from plaintext files on the internet. As well as this, there will also be a local repository of books, which may be published to and pulled from in-game.


Commands:

/libabel
(alias: /lbb)
  - root command which forks into all functions of this plugin with additional arguments.

Arguments:

/libabel geturl \<plaintext file URL\> \<book title\>
  - creates books whose contents are that of the plaintext URL provided, and whose names are the book title provided.

/libabel publish \<description\> 
  - publishes the book in your hand to the local book repository with the given description.

/libabel search \<terms\>
  - provides an indexed list of books matching the terms provided in the local repository, or if there are no terms, a complete list of all books (this may be too long to be shown in full).

/libabel get \<book index\>
  - retrieves a copy of the book at the given index in the local repository.
  
/libabel remove \<book index\>
  - removes the book at the given index in the local repository.
  
/libabel info \<book index\>
  - provides information about the book at the given index in the local repository.
  
