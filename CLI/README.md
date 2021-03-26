# Uppaal command line interface

A command line interface for uppaal.

## Installation

Before running the uppaal cli you should have uppaal64-4.1.20-stratego-7 installed somewhere on your system and a variable UPPAALPATH must point to the root of this directory in your bash environment. All required libraries for uppaal cli are included in this repository, before running it please also make sure to set the variable UPPAALCLIPATH pointing to the repository where the jar cli.jar is located. To compile the project simply execute the command make in this repository, it should compile the archive and generate the javadoc.

## Usage

The simplest way to start the uppaal cli is to start the script uppaal.sh provided in this repository, this script uses the variable UPPAALCLIPATH to locate the archive cli.jar so make sure to set it before running this script. the uppaal cli then uses the variable UPPAALPATH to locate the binary to execute so also make sure that it is well set before starting the uppaal cli. Once you started the uppaal command line interface you are invited to enter a command or to type help to get more information, for instance you can enter one of the following commands:

help

help COMMANDS

help ENVIRONMENTS

You can also start the uppaal command line interface with an uppaal script as argument, two examples are provided in this repository, the script train-gate.upl that implements the basic train gate model of uppaal and the script set-test.upl that provides some examples of affectations. To run one of them please execute one of the following commands in your bash:

./train-gate.upl

./uppaal.sh train-gate.upl

./set_test.upl

uppaal.sh set_test.upl

## Contributing

You are free to make or suggest any update to the uppaal command line interface. The source code is structured according to a MVC architecture adapted to the command line:

- the package org.uppaal.cli.context contains the classes related to the model, namely the class Context and different experts for the different types of objects handled in an uppaal document;

- the package org.uppaal.cli.commands contains all classes related to the execution of commands, it corresponds to the controller of the architecture MVC;

- the package org.uppaal.cli.frontend contains all classes related to the interaction with the command line, it corresponds to the view + some elements of the controller.

Finally the package org.uppaal.cli.exceptions contains the exception classes of the uppaal command line interface and org.uppaal.cli.test contains some classes of test for the model.