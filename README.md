# [history-of-fishing][] [![ci-badge][]][ci] [![gitter-badge][]][gitter]

[history-of-fishing]: https://github.com/2m/history-of-fishing
[ci]:                 https://github.com/2m/history-of-fishing/actions
[ci-badge]:           https://github.com/2m/history-of-fishing/workflows/ci/badge.svg
[gitter]:             https://gitter.im/2m/general
[gitter-badge]:       https://badges.gitter.im/2m/general.svg

History of Fishing (or `hof`) is a CLI application that works with the [Fish Shell][] history files.

[Fish Shell]: https://fishshell.com

## Features

* merges arbitrary number of Fish Shell history files
* verifies Fish Shell history file that it contains only increasing timestamps

## Usage

[![asciicast-badge][]][asciicast]

[asciicast]:       https://asciinema.org/a/NMsCJaq3yd9fJuxnWoIP9cRHu
[asciicast-badge]: https://asciinema.org/a/NMsCJaq3yd9fJuxnWoIP9cRHu.svg

## Acknowledgments

The usage of [decline][] library and setting up [native-image] build was inspired by a very informative [@note][] blogpost [Writing native CLI applications in Scala with GraalVM][].

[decline]:      https://github.com/bkirwi/decline
[native-image]: https://www.graalvm.org/docs/reference-manual/native-image/
[@note]:        https://github.com/note
[Writing native CLI applications in Scala with GraalVM]: https://msitko.pl/blog/2020/03/10/writing-native-cli-applications-in-scala-with-graalvm.html
