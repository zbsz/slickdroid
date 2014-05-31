SlickDroid
==========

SlickDroid is an implementation of Android backend for [Slick](https://github.com/slick/slick), it allows you to use Slick in Android project written in Scala.

This library has been developed during [Scaladays Hack Weekend](http://www.meetup.com/Scala-Berlin-Brandenburg/events/182906492/)

## Supported Features

SlickDroid supports all (hopefully) features supported by regular SQLite Slick driver.


## Installation

TBD

## Implementation details

Current version is heavily based on internal Slick JDBCBackend, most code has been copied from it and tweaked to use native Android SQLite api.

This results in significant code duplication and should be improved in future. Some parts can be simplified to better fit Android, and some could be shared with Slick codebase, but this would require changes in Slick itself.
