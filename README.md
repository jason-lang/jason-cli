# Command Line Interface (CLI) for Jason

This project provides a CLI for Jason users. 

Example of use in a terminal (`jason>` is the prompt for JasonCLI):

    jason> mas start
    jason> agent start bob
    jason> agent start alice {
        !start.
        +!start <- .send(bob,tell,hello).
    }
    jason> agent mind bob
        hello[source(alice)]
    jason> agent run-as bob { .send(alice,tell,hello) }
    jason> exit


In this example, 

* an (empty) MAS is created in the first command, 
* agent bob is created (second command) -- with no beliefs or plans; 
* agent alice is created (third command) -- with an initial goal and plan. 
* alice achieves the goal `!start` by sending a message to  bob
* the beliefs of bob are shown (fourth command). 
* bob also send a hello message to alice (fifth command).
* the MAS is finished (last command).

The shell provides completion and suggestions (using `<TAB>`).

![screen show](docs/figs/s1.png)

## Installation

Java 17 is required.

    git clone https://github.com/jason-lang/jason-cli.git
    cd jason-cli
    ./gradlew build
    export PATH=`pwd`/build/scripts:$PATH

These commands will: download JasonCLI, build it, and add `jason` command in the `PATH`. 
Ideally, change the `PATH` in the initialization of your terminal.

## Uses

### Interactive Shell

Type the command `jason`: 

    $ jason
    Jason interactive shell with completion and autosuggestions.
      Hit <TAB> to see available commands.
      Press Ctrl-D to exit.
    jason>

the `<TAB>` key is your new 'mouse' to explore the system.

### Scripts

Create a script file, for instance, a file called `hello.jcli` with content:

```
mas start

# starts bob with a plan
agent start bob    { +hello[source(A)] <- .print("hello from ",A). }

agent start alice
agent run-as alice { .send(bob,tell,hello) }  # alice executes the .send...

echo
echo "beliefs of Bob:"
agent mind bob         # show beliefs of bob
```

then  run it with

    $ jason < hello.jcli

the output in the _MAS Console_ will be:

```
[alice] done
[bob] hello from alice
```

and the output in the terminal is:

```
starting MAS mas_1 ...
MAS mas_1 is running (127.0.0.1:59052).
agent bob started.
agent alice started.
beliefs of Bob:
    hello[source(alice)]
<end of script>
```

### Command Line

In your preferred terminal:

    $ jason mas start --console

In another terminal:

    $ jason mas start m1

In another terminal:

    $ jason mas list
    $ jason mas stop --mas-name=m1 --exit

Agent commands:

    $ jason agent start  bob   --mas-name=mas_1
    $ echo "\!s. +\!s <- .send(bob,tell,hello)." > x.asl
    $ jason agent start  alice --mas-name=mas_1 --source=x.asl
    $ jason agent list         --mas-name=mas_1
    $ jason agent status bob   --mas-name=mas_1
    $ jason agent mind   bob   --mas-name=mas_1
    $ jason agent mind alice   --mas-name=mas_1 --plans

