# Command Line Interface (CLI) for Jason

This project provides a CLI for Jason user. 

Example of use (`jason>` is the prompt for JasonCLI):

    jason> mas start
    jason> agent start bob
    jason> agent start alice {
        !start.
        +!start <- .send(bob,tell,hello).
    }
    jason> agent beliefs bob
    hello[source(alice)]

The shell provides completion and suggestions (using `<TAB>`).

## Installation




