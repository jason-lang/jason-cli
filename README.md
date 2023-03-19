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
    jason> exit


The shell provides completion and suggestions (using `<TAB>`).

## Installation

Java 17 is required.



    git clone https://github.com/jason-lang/jason-cli.git
    cd jason-cli
    ./gradlew jar
    export PATH=`pwd`/build/scripts:$PATH

These commands will: download JasonCLI, build it, and add `jason` command on the `PATH`. 
Ideally, change the `PATH` in the initialization of your terminal.

## Execution

### Interactive

Type the command `jason`: 

    $ jason
    Jason interactive shell with completion and autosuggestions.
      Hit <TAB> to see available commands.
      Press Ctrl-D to exit.
    jason>

and then `<TAB>` to discover the commands.

### Command Line

In your preferred shell:

    $ jason mas start --console

In another terminal:

    $ jason mas start m1

In another terminal:

    $ jason mas list
    $ jason mas stop --mas-name=m1 --exit


(commands for agents are in ToDo)

### Scripts

Create a script file, for instance, a file called `hello.jcli` with content:

```
mas start --console
agent start bob {        # starts bob with a plan
    +hello[source(A)] <- .print("hello from ",A).
}
agent start alice {
    !s.
    +!s <- .send(bob,tell,hello).
}

echo
echo "beliefs of Bob:"
agent beliefs bob          # show beliefs of bob
mas stop --exit
```

then  run it with

    $ jason < hello.jcli

the output will be:

```
Jason interactive shell with completion and autosuggestions.
      Hit <TAB> to see available commands.
      Press Ctrl-D to exit.
jason> starting MAS mas_1 ...
MAS mas_1 is running (127.0.0.1:51917)
Agent mind inspector is running at http://127.0.0.1:3272
jason> agent start bob {        # starts bob with a plan
add: }>         +hello[source(A)] <- .print("hello from ",A).
add: }>     }agent bob started.
jason> agent start alice {
add: }>         !s.
add: }>         +!s <- .send(bob,tell,hello).
add: }>     }agent alice started.
jason> jason> 

[bob] hello from alice

jason> beliefs of Bob:
jason>     hello[source(alice)]

jason> mas_1 stopped
```
