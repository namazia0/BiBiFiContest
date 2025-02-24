# BiBiFiContest

This repo shows one solution for the [Build it Break it Fix it (BiBiFi)](https://builditbreakit.org/) challenge from 2023/2024 for the Secure Sofware Engineering lecture.

## Task Description

This project was divided into three parts.

### Build it
Implement a secure log to describe the state of an art gallery: the guests and employees who have entered and left, and persons that are in rooms. The log will be used by two programs. One program, logappend, will append new information to this file, and the other, logread, will read from the file and display the state of the art gallery according to a given query over the log. Both programs will use an authentication token, supplied as a command-line argument, to authenticate each other.

### Break it
In this phase we need to find security violations in the other group's code. There are four break submission types:
- Correctness (the implementation violates the specification)
- Crash (the implementation terminates unexplectedly)
- Confidentiality (an attacker is able to learn contents from log files without authorisation)
- Integrity (an attacker is able to fool logappend or logread into accepting their manipulated log file)

### Fix it
In this phase this code received the breaks the other teams have maade against this implementation. We were supposed to fix the bugs.


## How to test locally

1. Install docker on your system

2. Build docker image

```sh
podman build -t bibifi . --network=host
```

3. Run docker image with local `build` folder source mounted as `bibifi_build`
        
```sh
podman run --rm --network=host -i -t -v ${PWD}/build:/bibifi_build bibifi bash
```

4. Try building sample project

```sh
cd bibifi_build/ && make
```

5. Run `logappend` and `logread`

```sh
./logappend
./logread
```

6. Clean generated files

```sh
make clean
```