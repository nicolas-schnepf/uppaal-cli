# Uppaal command line interface

A command line interface for uppaal.

## Requirements

uppaal-cli relies on uppaal64-4.1.20-stratego-7 that can be downloaded [here](https://people.cs.aau.dk/~marius/stratego/download.html). Please make sure that it is installed on your computer and that the variable UPPAALPATH points to the root of the uppaal repository before running uppaal-cli. All dependencies of uppaal-cli are included in the lib folder of this repository, please include them in your class path before building uppaal-cli.

## Installation

To install simply run the following commands in this repository:

make
sudo -E make install

It will copy all dependencies of uppaal-cli in the folder $UPPAALPATH/lib so please make sure that $UPPAALPATH is set before running these commands.

## Usage

You can start uppaal-cli by typing the following command in your prompt:

uppaal-cli

Once you started the uppaal command line interface you are invited to enter a command or to type help to get more information, for instance you can enter one of the following commands:

help

help COMMANDS

help ENVIRONMENTS

You can also start the uppaal command line interface with an uppaal script as argument, two examples are provided in the folder examples the script train-gate.upl that implements the basic train gate model of uppaal and the script set-test.upl that provides some examples of affectations. To run one of them please execute one of the following commands in your bash:

./examples/train-gate.upl

uppaal-cli examples/train-gate.upl

./examples/set_test.upl

uppaal-cli examples/set_test.upl

## Contributing

You are free to make or suggest any update to the uppaal command line interface. The source code is structured according to a MVC architecture adapted to the command line:

- the package org.uppaal.cli.context contains the classes related to the model, namely the class Context and different experts for the different types of objects handled in an uppaal document;

- the package org.uppaal.cli.commands contains all classes related to the execution of commands, it corresponds to the controller of the architecture MVC;

- the package org.uppaal.cli.frontend contains all classes related to the interaction with the command line, it corresponds to the view + some elements of the controller.

Finally the package org.uppaal.cli.exceptions contains the exception classes of the uppaal command line interface and org.uppaal.cli.test contains some classes of test for the model.